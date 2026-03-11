package me.ginterloper.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MouthConfig {

    private static final Logger LOGGER = LogManager.getLogger(MouthConfig.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/voicemouth.json");
    private static final ExecutorService SAVE_EXECUTOR =
            Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "voicemouth-config-writer");
                t.setDaemon(true);
                return t;
            });

    private static Identifier currentMouth = Identifier.of("voicemouth", "textures/entity/mouth_standard.png");
    private static final float DEFAULT_SCALE = 4F;
    private static final Map<Identifier, Float> SCALE_BY_MOUTH = new ConcurrentHashMap<>();

    private static float offsetX = 0F;
    private static float offsetY = 0F;

    public record MouthDefinition(String translationKey, Identifier texture, int textureHeight, float scale) {}

    private static final List<MouthDefinition> REGISTERED_MOUTHS = List.of(
            new MouthDefinition("gui.voicemouth.standard", Identifier.of("voicemouth", "textures/entity/mouth_standard.png"), 48, 3F),
            new MouthDefinition("gui.voicemouth.realism", Identifier.of("voicemouth", "textures/entity/mouth_realism.png"), 48, 2F),
            new MouthDefinition("gui.voicemouth.lipped", Identifier.of("voicemouth", "textures/entity/mouth_lipped.png"), 48, 2F),
            new MouthDefinition("gui.voicemouth.classic", Identifier.of("voicemouth", "textures/entity/mouth_classic.png"), 48, 3F),
            new MouthDefinition("gui.voicemouth.minimal", Identifier.of("voicemouth", "textures/entity/mouth_minimal.png"), 48, 3F)
    );

    public static void registerMouth(Identifier id, float scale) {
        if (id == null) return;
        SCALE_BY_MOUTH.put(id, scale);
    }

    public static void initializeMouths() {
        for (MouthDefinition mouth : REGISTERED_MOUTHS) {
            registerMouth(mouth.texture(), mouth.scale());
        }
    }

    public static List<MouthDefinition> getRegisteredMouths() {
        return REGISTERED_MOUTHS;
    }

    public static float getMouthScale(Identifier id) {
        if (id == null) return DEFAULT_SCALE;
        return SCALE_BY_MOUTH.getOrDefault(id, DEFAULT_SCALE);
    }

    public static void setMouth(Identifier id) {
        currentMouth = id;
        SAVE_EXECUTOR.execute(MouthConfig::save);
    }

    public static Identifier getMouth() {
        return currentMouth;
    }

    public static float getOffsetX() {
        return offsetX;
    }

    public static void setOffsetX(float value) {
        offsetX = value;
        SAVE_EXECUTOR.execute(MouthConfig::save);
    }

    public static float getOffsetY() {
        return offsetY;
    }

    public static void setOffsetY(float value) {
        offsetY = value;
        SAVE_EXECUTOR.execute(MouthConfig::save);
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                if (data.selectedMouth != null) {
                    currentMouth = Identifier.of(data.selectedMouth);
                }
                offsetX = data.offsetX;
                offsetY = data.offsetY;
            }
        } catch (IOException e) {
            LOGGER.error("Не удалось загрузить конфиг voicemouth", e);
        }
    }

    private static void save() {
        ConfigData data = new ConfigData();
        data.selectedMouth = currentMouth.toString();
        data.offsetX = offsetX;
        data.offsetY = offsetY;

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            LOGGER.error("Не удалось сохранить конфиг voicemouth", e);
        }
    }

    private static class ConfigData {
        String selectedMouth;
        float offsetX;
        float offsetY;
    }
}