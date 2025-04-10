package com.antos.betterserver.api;

import com.antos.betterserver.BetterServer;
import com.antos.betterserver.api.manager.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Mute {
    private final OfflinePlayer player;
    private long second;
    private File path = new File(new File(Bukkit.getPluginManager().getPlugin("BetterServer").getDataFolder(), "Player-Data"), "Player-Mute");

    public Mute(OfflinePlayer player, long second) {
        this.player = player;
        this.second = second;
        BetterServer.getMuteManager().mutes.put(player.getUniqueId(), this);
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public long getSecond() {
        return this.second;
    }

    public void setSecond(long second) {
        this.second = second;
        BetterServer.getMuteManager().mutes.put(player.getUniqueId(), this);
    }

    public void addSecond(long second) {
        this.second += second;
        BetterServer.getMuteManager().mutes.put(player.getUniqueId(), this);
    }

    public void removeSecond(long second) {
        this.second -= second;
        BetterServer.getMuteManager().mutes.put(player.getUniqueId(), this);
    }

    public void saveConfig() {
        File file = new File(path, player.getUniqueId() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("Mute.Player-UUID", player.getUniqueId() + "");
        config.set("Mute.Player-Name", player.getName());
        config.set("Mute.Player-Second", second);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMute() {
        if (this.second > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Mute)) return false;
        return ((Mute) obj).getPlayer().getUniqueId().equals(this.player.getUniqueId()) && ((Mute) obj).getSecond() == this.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player.getUniqueId(), second);
    }
}