package me.ginterloper.network;

import me.ginterloper.core.ModConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/**
 * Пакет клиент → сервер: игрок выбрал текстуру рта.
 */
public record SelectMouthC2SPayload(String mouthId) implements CustomPayload {

    public static final net.minecraft.util.Identifier ID =
            net.minecraft.util.Identifier.of(ModConstants.MOD_ID, "select_mouth_c2s");
    public static final CustomPayload.Id<SelectMouthC2SPayload> TYPE =
            new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, SelectMouthC2SPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING,
                    SelectMouthC2SPayload::mouthId,
                    SelectMouthC2SPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
