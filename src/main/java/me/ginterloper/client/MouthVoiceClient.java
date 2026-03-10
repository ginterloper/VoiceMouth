package me.ginterloper.client;

import me.ginterloper.network.SelectMouthC2SPayload;
import me.ginterloper.network.SyncMouthS2CPayload;
import me.ginterloper.renderer.MouthRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

public class MouthVoiceClient implements ClientModInitializer {

    public void onInitializeClient() {
        MouthConfig.load();

        ClientPlayNetworking.registerGlobalReceiver(SyncMouthS2CPayload.TYPE, (payload, context) -> {
            PlayerMouthStorage.setMouth(payload.playerUuid(), payload.mouthId());
        });

        ClientPlayConnectionEvents.JOIN.register((handler, client, join) -> {
            // При входе на сервер сообщаем свой текущий выбор рта
            if (join.player != null) {
                ClientPlayNetworking.send(new SelectMouthC2SPayload(MouthConfig.getMouth().toString()));
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            PlayerMouthStorage.clear();
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
}
