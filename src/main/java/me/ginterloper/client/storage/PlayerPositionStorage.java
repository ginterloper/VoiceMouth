package me.ginterloper.client.storage;

import me.ginterloper.client.config.MouthConfig;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище на клиенте: смещение x и y для отображения рта других игроков (по UUID).
 * Для локального игрока используется {@link MouthConfig}.
 */
public final class PlayerPositionStorage {
    
    private static final float DEFAULT_OFFSET_X = 0F;
    private static final float DEFAULT_OFFSET_Y = 0F;
    
    private static final Map<UUID, Float> OFFSET_X_BY_UUID = new ConcurrentHashMap<>();
    private static final Map<UUID, Float> OFFSET_Y_BY_UUID = new ConcurrentHashMap<>();
    
    /**
     * Устанавливает смещение X для игрока.
     */
    public static void setOffsetX(UUID playerUuid, float offsetX) {
        OFFSET_X_BY_UUID.put(playerUuid, offsetX);
    }
    
    /**
     * Устанавливает смещение Y для игрока.
     */
    public static void setOffsetY(UUID playerUuid, float offsetY) {
        OFFSET_Y_BY_UUID.put(playerUuid, offsetY);
    }
    
    /**
     * Устанавливает оба смещения для игрока.
     */
    public static void setOffset(UUID playerUuid, float offsetX, float offsetY) {
        OFFSET_X_BY_UUID.put(playerUuid, offsetX);
        OFFSET_Y_BY_UUID.put(playerUuid, offsetY);
    }
    
    /**
     * Возвращает смещение X для игрока (не локального).
     */
    public static float getOffsetX(UUID playerUuid) {
        return OFFSET_X_BY_UUID.getOrDefault(playerUuid, DEFAULT_OFFSET_X);
    }
    
    /**
     * Возвращает смещение Y для игрока (не локального).
     */
    public static float getOffsetY(UUID playerUuid) {
        return OFFSET_Y_BY_UUID.getOrDefault(playerUuid, DEFAULT_OFFSET_Y);
    }
    
    /**
     * Очистить при отключении от сервера.
     */
    public static void clear() {
        OFFSET_X_BY_UUID.clear();
        OFFSET_Y_BY_UUID.clear();
    }
}

