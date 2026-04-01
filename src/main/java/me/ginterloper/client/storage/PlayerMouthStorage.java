package me.ginterloper.client.storage;

import me.ginterloper.client.config.MouthConfig;
import me.ginterloper.core.ModConstants;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище на клиенте: выбор текстуры рта для других игроков (по UUID).
 * Для локального игрока используется {@link MouthConfig}.
 */
public final class PlayerMouthStorage {

    private static final Identifier DEFAULT_MOUTH =
            Identifier.of(ModConstants.MOD_ID, "textures/entity/mouth_standard.png");

    private static final Map<UUID, Identifier> MOUTH_BY_UUID = new ConcurrentHashMap<>();

    public static void setMouth(UUID playerUuid, String mouthId) {
        if (mouthId == null || mouthId.isEmpty()) {
            MOUTH_BY_UUID.remove(playerUuid);
            return;
        }
        try {
            MOUTH_BY_UUID.put(playerUuid, Identifier.of(mouthId));
        } catch (Exception ignored) {
            MOUTH_BY_UUID.put(playerUuid, DEFAULT_MOUTH);
        }
    }

    /** Возвращает текстуру рта для игрока (не локального). Если неизвестна — дефолт. */
    public static Identifier getMouth(UUID playerUuid) {
        return MOUTH_BY_UUID.getOrDefault(playerUuid, DEFAULT_MOUTH);
    }

    /** Очистить при отключении от сервера. */
    public static void clear() {
        MOUTH_BY_UUID.clear();
    }
}
