package com.github.otbproject.otbproject.config;

public class GeneralConfigHelper {
    public static boolean permanentlyEnable(GeneralConfig config, String command) {
        if (!config.permanently_enabled_commands.contains(command)) {
            config.permanently_enabled_commands.add(command);
            return true;
        }
        return false;
    }

    public static boolean impermanentlyEnable(GeneralConfig config, String command) {
        return config.permanently_enabled_commands.remove(command);
    }

    public static void clearPermanentlyEnabled(GeneralConfig config) {
        config.permanently_enabled_commands.clear();
    }

    public static boolean isPermanentlyEnabled(GeneralConfig config, String command) {
        return config.permanently_enabled_commands.contains(command);
    }
}
