package me.ginterloper.network;

import me.ginterloper.core.ModConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

/**
 * Пакет сервер → клиент: у игрока с данным UUID выбран такой mouthId.
 */
public record SyncMouthS2CPayload(UUID playerUuid, String mouthId) implements CustomPayload {

    public static final net.minecraft.util.Identifier ID =
            net.minecraft.util.Identifier.of(ModConstants.MOD_ID, "sync_mouth_s2c");
    public static final CustomPayload.Id<SyncMouthS2CPayload> TYPE =
            new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, SyncMouthS2CPayload> CODEC =
            PacketCodec.tuple(
                    Uuids.PACKET_CODEC,
                    SyncMouthS2CPayload::playerUuid,
                    net.minecraft.network.codec.PacketCodecs.STRING,
                    SyncMouthS2CPayload::mouthId,
                    SyncMouthS2CPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
