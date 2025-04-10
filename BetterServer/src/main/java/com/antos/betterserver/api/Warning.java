package com.antos.betterserver.api;

import com.antos.betterserver.BetterServer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import com.antos.betterserver.api.manager.WarningManager;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Warning {
    WarningManager warningManager = BetterServer.getWarningManager();

    private int WarningNum;
    private final OfflinePlayer player;
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("BetterServer");

    public Warning(@NotNull OfflinePlayer player, @NotNull int warnNum) {
        this.player = player;
        if (warnNum < 0) {
            this.WarningNum = 0;
        } else {
            this.WarningNum = warnNum;
        }

        warningManager.PLAYER_WARNING.put(player.getUniqueId(), this);
    }

    public Warning(@NotNull OfflinePlayer player) {
        this.player = player;
        this.WarningNum = 0;
        warningManager.PLAYER_WARNING.put(player.getUniqueId(), this);
    }

    public void addWaring(@NotNull int n) {
        this.WarningNum += n;
        warningManager.PLAYER_WARNING.put(player.getUniqueId(), this);
    }

    public void subtractWaring(@NotNull int n) {
        this.WarningNum -= n;
        if (this.WarningNum <= 0) {
            this.WarningNum = 0;
        }
        warningManager.PLAYER_WARNING.put(player.getUniqueId(), this);
    }

    public void setWaring(@NotNull int n) {
        this.WarningNum = n;
        if (this.WarningNum <= 0) {
            this.WarningNum = 0;
        }
        warningManager.PLAYER_WARNING.put(player.getUniqueId(), this);
    }

    public int getWarningNum() {
        warningManager.PLAYER_WARNING.put(player.getUniqueId(), this);
        return this.WarningNum;
    }

    public OfflinePlayer getPlayer() {
        warningManager.PLAYER_WARNING.put(player.getUniqueId(), this);
        return this.player;
    }

    public void saveConfig() {
        File file = new File(new File(new File(plugin.getDataFolder(), "Player-Data"), "Player-Warning"), player.getUniqueId() + ".yml");
        YamlConfiguration configuration = new YamlConfiguration();

        configuration.set("Warning.Player-UUID", player.getUniqueId() + "");
        configuration.set("Warning.Player-Name", player.getName());
        configuration.set("Warning.Warning-Num", WarningNum);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Warning)) return false;
        return ((Warning) o).getPlayer().getUniqueId() == this.player.getUniqueId() && ((Warning) o).getWarningNum() == this.WarningNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(WarningNum, player.getUniqueId());
    }
}
