package me.ginterloper.client;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatClientApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.config.ConfigAccessor;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import me.ginterloper.core.ModConstants;
import me.ginterloper.voice.VolumeAnalyzer;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;

public class SimpleVoiceChatPlugin implements VoicechatPlugin{
    private static VoicechatClientApi voicechatApi;

    public String getPluginId() {
        return ModConstants.MOD_ID;
    }

    public void initialize(VoicechatApi api) {
        if (api instanceof VoicechatClientApi clientApi) {
            voicechatApi = clientApi;
        }
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
                float volume = calculateVolume(event.getRawAudio());
                VoiceStateManager.setTalking(senderUuid, volume);
            }
        }
    }

    private void onAnotherPlayerSoundEvent(ClientReceiveSoundEvent event) {
        if (event instanceof ClientReceiveSoundEvent.EntitySound entitySound) {
            UUID senderUuid = entitySound.getEntityId();
            if (senderUuid != null) {
                float volume = calculateVolume(event.getRawAudio());
                VoiceStateManager.setTalking(senderUuid, volume);
            }
        }
    }

    private float calculateVolume(short[] rawAudio) {
        double activationThresholdDb = getVoiceActivationThresholdDb();
        boolean isShout = VolumeAnalyzer.isShout(rawAudio, activationThresholdDb);
        if (isShout) {
            return 1F;
        }
        double normalized = VolumeAnalyzer.calculateNormalizedRms(rawAudio);
        return (float) normalized;
    }

    private double getVoiceActivationThresholdDb() {
        if (voicechatApi == null) {
            return -50.0D;
        }
        ConfigAccessor config = voicechatApi.getClientConfig();
        if (config == null) {
            return -50.0D;
        }
        return config.getDouble("voice_activation_threshold", -50.0D);
    }
}
