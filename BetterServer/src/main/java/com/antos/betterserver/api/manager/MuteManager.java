package com.antos.betterserver.api.manager;

import com.antos.betterserver.BetterServer;
import com.antos.betterserver.api.Mute;
import com.antos.betterserver.api.annotations.Manager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@Manager("Mute")
public interface MuteManager {
    Map<UUID, Mute> mutes = new HashMap<>();

    @Nullable
    Mute getMute(OfflinePlayer player);

    void reloadMuteConfig(YamlConfiguration configuration);

    default void loadAllMuteConfig() {
        for (File file : Objects.requireNonNull(new File(new File(BetterServer.getInstance().getDataFolder(), "Player-Data"), "Player-Mute").listFiles())) {
            reloadMuteConfig(YamlConfiguration.loadConfiguration(file));
        }
    }

    @NotNull
    List<OfflinePlayer> getMutedPlayers();
}