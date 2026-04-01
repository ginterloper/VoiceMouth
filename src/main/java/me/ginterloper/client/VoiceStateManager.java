package me.ginterloper.client;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Отслеживает состояние \"говорит/не говорит\" и последний известный уровень громкости для игроков.
 * Ожидаемый диапазон громкости: 0..1, где 1 означает \"крик\".
 */
public class VoiceStateManager {

    private static final Set<UUID> TALKING_PLAYERS = ConcurrentHashMap.newKeySet();
    private static final long TALK_TIMEOUT_MS = 100;

    private static final ConcurrentHashMap<UUID, Long> LAST_TALK_TIME = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Float> LAST_VOLUME = new ConcurrentHashMap<>();

    /**
     * Обновляет состояние \"игрок говорит\" и сохраняет нормализованную громкость (0..1).
     */
    public static void setTalking(UUID uuid, float volume) {
        LAST_TALK_TIME.put(uuid, System.currentTimeMillis());
        TALKING_PLAYERS.add(uuid);
        LAST_VOLUME.put(uuid, clampVolume(volume));
    }

    /**
     * Возвращает true, если игрок недавно говорил (с учётом таймаута).
     */
    public static boolean isTalking(UUID uuid) {
        Long last = LAST_TALK_TIME.get(uuid);
        if (last == null) return false;

        if (System.currentTimeMillis() - last > TALK_TIMEOUT_MS) {
            TALKING_PLAYERS.remove(uuid);
            LAST_TALK_TIME.remove(uuid);
            LAST_VOLUME.remove(uuid);
            return false;
        }

        return true;
    }

    /**
     * Возвращает последнюю известную нормализованную громкость (0..1) для игрока.
     * Если игрок давно не говорил, возвращает 0.
     */
    public static float getVolume(UUID uuid) {
        Long last = LAST_TALK_TIME.get(uuid);
        if (last == null) {
            return 0F;
        }
        if (System.currentTimeMillis() - last > TALK_TIMEOUT_MS) {
            TALKING_PLAYERS.remove(uuid);
            LAST_TALK_TIME.remove(uuid);
            LAST_VOLUME.remove(uuid);
            return 0F;
        }
        return LAST_VOLUME.getOrDefault(uuid, 0F);
    }

    private static float clampVolume(float volume) {
        if (Float.isNaN(volume) || Float.isInfinite(volume)) {
            return 0F;
        }
        if (volume < 0F) return 0F;
        if (volume > 1F) return 1F;
        return volume;
    }
}