package me.ginterloper.client;

import me.ginterloper.network.SelectMouthC2SPayload;
import me.ginterloper.network.SyncMouthS2CPayload;
import me.ginterloper.renderer.MouthRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

public class VoiceMouthClient implements ClientModInitializer {

    public void onInitializeClient() {
        MouthConfig.initializeMouths();
        MouthConfig.load();

        ClientPlayNetworking.registerGlobalReceiver(SyncMouthS2CPayload.TYPE, (payload, context) -> PlayerMouthStorage.setMouth(payload.playerUuid(), payload.mouthId()));

        ClientPlayConnectionEvents.JOIN.register((handler, client, join) -> {
            if (join.player != null) {
                syncSelectedMouthToServer();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> PlayerMouthStorage.clear());

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
}
