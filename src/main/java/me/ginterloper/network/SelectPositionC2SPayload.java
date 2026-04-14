package me.ginterloper.network;

import me.ginterloper.core.ModConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/**
 * Пакет клиент → сервер: игрок выбрал смещение x и y для рта.
 */
public record SelectPositionC2SPayload(float offsetX, float offsetY) implements CustomPayload {

    public static final net.minecraft.util.Identifier ID =
            net.minecraft.util.Identifier.of(ModConstants.MOD_ID, "select_position_c2s");
    public static final CustomPayload.Id<SelectPositionC2SPayload> TYPE =
            new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, SelectPositionC2SPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.FLOAT,
                    SelectPositionC2SPayload::offsetX,
                    PacketCodecs.FLOAT,
                    SelectPositionC2SPayload::offsetY,
                    SelectPositionC2SPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}

