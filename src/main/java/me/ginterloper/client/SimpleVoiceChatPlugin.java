package me.ginterloper.client;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;

public class SimpleVoiceChatPlugin implements VoicechatPlugin{
    private static VoicechatApi voicechatApi;

    public String getPluginId() {
        return "voicemouth";
    }

    public void initialize(VoicechatApi api) {
        voicechatApi = api;
    }

    public VoicechatApi getVoicechatApi() {
        return voicechatApi;
    }

    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(ClientReceiveSoundEvent.EntitySound.class, this::onAnotherPlayerSoundEvent);
        registration.registerEvent(ClientReceiveSoundEvent.StaticSound.class, this::onAnotherPlayerSoundEvent);
        registration.registerEvent(ClientReceiveSoundEvent.LocationalSound.class, this::onAnotherPlayerSoundEvent);
        registration.registerEvent(ClientSoundEvent.class, this::onClientPlayerSoundEvent);
    }

    private void onClientPlayerSoundEvent(ClientSoundEvent event) {
        if (MinecraftClient.getInstance().player != null) {
            UUID senderUuid = MinecraftClient.getInstance().player.getUuid();
            if (senderUuid != null) {
                VoiceStateManager.setTalking(senderUuid);
            }
        }
    }

    private void onAnotherPlayerSoundEvent(ClientReceiveSoundEvent event) {
        if (event instanceof ClientReceiveSoundEvent.EntitySound entitySound) {
            UUID senderUuid = entitySound.getEntityId();
            if (senderUuid != null) {
                VoiceStateManager.setTalking(senderUuid);
            }
        }
    }
}
