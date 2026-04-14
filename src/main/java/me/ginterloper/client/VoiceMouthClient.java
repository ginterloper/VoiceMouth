package me.ginterloper.client;

import me.ginterloper.client.config.MouthConfig;
import me.ginterloper.client.render.MouthRenderer;
import me.ginterloper.client.storage.PlayerMouthStorage;
import me.ginterloper.client.storage.PlayerPositionStorage;
import me.ginterloper.network.SelectMouthC2SPayload;
import me.ginterloper.network.SelectPositionC2SPayload;
import me.ginterloper.network.SyncMouthS2CPayload;
import me.ginterloper.network.SyncPositionS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

public class VoiceMouthClient implements ClientModInitializer {

    public void onInitializeClient() {
        MouthConfig.initializeMouths();
        MouthConfig.load();

        ClientPlayNetworking.registerGlobalReceiver(SyncMouthS2CPayload.TYPE, (payload, context) -> PlayerMouthStorage.setMouth(payload.playerUuid(), payload.mouthId()));
        ClientPlayNetworking.registerGlobalReceiver(SyncPositionS2CPayload.TYPE, (payload, context) -> PlayerPositionStorage.setOffset(payload.playerUuid(), payload.offsetX(), payload.offsetY()));

        ClientPlayConnectionEvents.JOIN.register((handler, client, join) -> {
            if (join.player != null) {
                syncSelectedMouthToServer();
                syncSelectedPositionToServer();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            PlayerMouthStorage.clear();
            PlayerPositionStorage.clear();
        });

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                (entityType, entityRenderer, registrationHelper, context) -> {
                    if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
                        registrationHelper.register(
                                new MouthRenderer(playerRenderer)
                        );
                    }
                }
        );
    }

    public static void syncSelectedMouthToServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null || client.player == null) {
            return;
        }
        ClientPlayNetworking.send(new SelectMouthC2SPayload(MouthConfig.getMouth().toString()));
    }

    public static void syncSelectedPositionToServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null || client.player == null) {
            return;
        }
        ClientPlayNetworking.send(new SelectPositionC2SPayload(MouthConfig.getOffsetX(), MouthConfig.getOffsetY()));
    }
}
