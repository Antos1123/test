package com.antos.betterserver.api.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class ConfigReader {

    public static @Nullable Set<String> readConfigData(@NotNull YamlConfiguration configuration, @NotNull String path) {
        ConfigurationSection configSection = configuration.getConfigurationSection(path);
        if (configSection != null) {
            return configSection.getKeys(false);
        }
        return null;
    }
}
