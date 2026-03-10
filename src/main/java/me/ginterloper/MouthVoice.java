package me.ginterloper;

import me.ginterloper.network.SelectMouthC2SPayload;
import me.ginterloper.network.SyncMouthS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MouthVoice implements ModInitializer {

	/** Серверное хранилище: UUID игрока → идентификатор текстуры рта (строка). */
	private static final Map<UUID, String> SERVER_MOUTH_MAP = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(SelectMouthC2SPayload.TYPE, SelectMouthC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SyncMouthS2CPayload.TYPE, SyncMouthS2CPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(SelectMouthC2SPayload.TYPE, (payload, context) -> {
			String mouthId = payload.mouthId();
			if (mouthId == null || mouthId.length() > 256) return;
			ServerPlayerEntity player = context.player();
			UUID uuid = player.getUuid();
			SERVER_MOUTH_MAP.put(uuid, mouthId);
			for (ServerPlayerEntity other : net.fabricmc.fabric.api.networking.v1.PlayerLookup.world((ServerWorld) player.getEntityWorld())) {
				ServerPlayNetworking.send(other, new SyncMouthS2CPayload(uuid, mouthId));
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity joining = handler.getPlayer();
			UUID joiningUuid = joining.getUuid();
			for (Map.Entry<UUID, String> e : SERVER_MOUTH_MAP.entrySet()) {
				ServerPlayNetworking.send(joining, new SyncMouthS2CPayload(e.getKey(), e.getValue()));
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			SERVER_MOUTH_MAP.remove(handler.getPlayer().getUuid());
		});
	}
}