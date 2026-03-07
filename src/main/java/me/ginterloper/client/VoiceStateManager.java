package me.ginterloper.client;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceStateManager {

    private static final Set<UUID> TALKING_PLAYERS = ConcurrentHashMap.newKeySet();
    private static final long TALK_TIMEOUT_MS = 100;

    private static final ConcurrentHashMap<UUID, Long> LAST_TALK_TIME = new ConcurrentHashMap<>();

    public static void setTalking(UUID uuid) {
        LAST_TALK_TIME.put(uuid, System.currentTimeMillis());
        TALKING_PLAYERS.add(uuid);
    }

    public static boolean isTalking(UUID uuid) {
        Long last = LAST_TALK_TIME.get(uuid);
        if (last == null) return false;

        if (System.currentTimeMillis() - last > TALK_TIMEOUT_MS) {
            TALKING_PLAYERS.remove(uuid);
            return false;
        }

        return true;
    }
}