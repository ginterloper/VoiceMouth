package me.ginterloper;

import me.ginterloper.network.SelectMouthC2SPayload;
import me.ginterloper.network.SelectPositionC2SPayload;
import me.ginterloper.network.SyncMouthS2CPayload;
import me.ginterloper.network.SyncPositionS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceMouth implements ModInitializer {

	/** Серверное хранилище: UUID игрока → идентификатор текстуры рта (строка). */
	private static final Map<UUID, String> SERVER_MOUTH_MAP = new ConcurrentHashMap<>();
	/** Серверное хранилище: UUID игрока → {offsetX, offsetY} для позиции рта. */
	private static final Map<UUID, float[]> SERVER_POSITION_MAP = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(SelectMouthC2SPayload.TYPE, SelectMouthC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SelectPositionC2SPayload.TYPE, SelectPositionC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SyncMouthS2CPayload.TYPE, SyncMouthS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SyncPositionS2CPayload.TYPE, SyncPositionS2CPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(SelectMouthC2SPayload.TYPE, (payload, context) -> {
			String mouthId = payload.mouthId();
			if (mouthId == null || mouthId.length() > 256) return;
			ServerPlayerEntity player = context.player();
			UUID uuid = player.getUuid();
			SERVER_MOUTH_MAP.put(uuid, mouthId);
			for (ServerPlayerEntity other : net.fabricmc.fabric.api.networking.v1.PlayerLookup.world(player.getEntityWorld())) {
				ServerPlayNetworking.send(other, new SyncMouthS2CPayload(uuid, mouthId));
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(SelectPositionC2SPayload.TYPE, (payload, context) -> {
			float offsetX = payload.offsetX();
			float offsetY = payload.offsetY();
			ServerPlayerEntity player = context.player();
			UUID uuid = player.getUuid();
			SERVER_POSITION_MAP.put(uuid, new float[]{offsetX, offsetY});
			for (ServerPlayerEntity other : net.fabricmc.fabric.api.networking.v1.PlayerLookup.world(player.getEntityWorld())) {
				ServerPlayNetworking.send(other, new SyncPositionS2CPayload(uuid, offsetX, offsetY));
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity joining = handler.getPlayer();
			for (Map.Entry<UUID, String> e : SERVER_MOUTH_MAP.entrySet()) {
				ServerPlayNetworking.send(joining, new SyncMouthS2CPayload(e.getKey(), e.getValue()));
			}
			for (Map.Entry<UUID, float[]> e : SERVER_POSITION_MAP.entrySet()) {
				float[] pos = e.getValue();
				ServerPlayNetworking.send(joining, new SyncPositionS2CPayload(e.getKey(), pos[0], pos[1]));
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			UUID uuid = handler.getPlayer().getUuid();
			SERVER_MOUTH_MAP.remove(uuid);
			SERVER_POSITION_MAP.remove(uuid);
		});
	}
}