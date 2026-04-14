package me.ginterloper.network;

import me.ginterloper.core.ModConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

/**
 * Пакет сервер → клиент: смещение x и y для рта игрока с данным UUID.
 */
public record SyncPositionS2CPayload(UUID playerUuid, float offsetX, float offsetY) implements CustomPayload {

    public static final net.minecraft.util.Identifier ID =
            net.minecraft.util.Identifier.of(ModConstants.MOD_ID, "sync_position_s2c");
    public static final CustomPayload.Id<SyncPositionS2CPayload> TYPE =
            new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, SyncPositionS2CPayload> CODEC =
            PacketCodec.tuple(
                    Uuids.PACKET_CODEC,
                    SyncPositionS2CPayload::playerUuid,
                    PacketCodecs.FLOAT,
                    SyncPositionS2CPayload::offsetX,
                    PacketCodecs.FLOAT,
                    SyncPositionS2CPayload::offsetY,
                    SyncPositionS2CPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}

