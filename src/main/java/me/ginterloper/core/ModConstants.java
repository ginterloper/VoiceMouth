package me.ginterloper.core;

public final class ModConstants {

    private ModConstants() {
    }

    public static final String MOD_ID = "voicemouth";

    public static final String CONFIG_DIR = "config";
    public static final String CLIENT_CONFIG_FILE = "voicemouth.json";

    public static String id(String path) {
        return MOD_ID + ":" + path;
    }
}

