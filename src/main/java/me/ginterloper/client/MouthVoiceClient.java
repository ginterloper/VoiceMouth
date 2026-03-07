package me.ginterloper.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

import me.ginterloper.renderer.MouthRenderer;

public class MouthVoiceClient implements ClientModInitializer {

    public void onInitializeClient() {
        MouthConfig.load();

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
