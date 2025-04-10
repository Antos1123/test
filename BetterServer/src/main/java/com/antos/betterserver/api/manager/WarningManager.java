package com.antos.betterserver.api.manager;

import com.antos.betterserver.BetterServer;
import com.antos.betterserver.api.Warning;
import com.antos.betterserver.api.annotations.Manager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Manager("Warning")
public interface WarningManager {
    Map<UUID, Warning> PLAYER_WARNING = new HashMap<>();

    @Nullable
    Warning getWarning(@NotNull OfflinePlayer player);

    default void loadAllWarningConfig() {
        for (File file : Objects.requireNonNull(new File(new File(BetterServer.getInstance().getDataFolder(), "Player-Data"), "Player-Warning").listFiles())) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            reloadWarningConfig(config);
        }
    }

    void reloadWarningConfig(@NotNull YamlConfiguration configuration);
}
