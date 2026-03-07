package com.example.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MouthConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/mouth-voice.json");

    private static Identifier currentMouth = Identifier.of("mouth-voice", "textures/entity/mouth-standard.png");

    public static void setMouth(Identifier id) {
        currentMouth = id;
        save();
    }

    public static Identifier getMouth() {
        return currentMouth;
    }

    // Загрузка при запуске мода (вызвать в onInitializeClient)
    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save(); // создаём файл с дефолтом
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null && data.selectedMouth != null) {
                currentMouth = Identifier.of(data.selectedMouth);
            }
        } catch (IOException e) {
            System.err.println("Не удалось загрузить конфиг mouth-voice: " + e.getMessage());
        }
    }

    // Сохранение при выборе
    private static void save() {
        ConfigData data = new ConfigData();
        data.selectedMouth = currentMouth.toString();

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Не удалось сохранить конфиг mouth-voice: " + e.getMessage());
        }
    }

    // Внутренний класс для JSON
    private static class ConfigData {
        String selectedMouth;
    }
}