package com.antos.betterserver;

import com.antos.betterserver.api.Mute;
import com.antos.betterserver.api.Warning;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.antos.betterserver.api.manager.*;
import com.antos.betterserver.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
public final class BetterServer extends JavaPlugin {
    // manager
    static WarningManager warningManager = null;
    static MuteManager muteManager = null;
    static PlayerLangManager playerLangManager = null;

    //betterServer
    static BetterServer betterServer;
    @Override
    public void onEnable() {
        betterServer = this;

        getServer().getConsoleSender().sendMessage("§e __   ___ ___ ___  ___  __   __   ___  __        ___  __      §aBetterServer §r" + this.getDescription().getVersion());
        getServer().getConsoleSender().sendMessage("§e|__) |__   |   |  |__  |__) /__` |__  |__) \\  / |__  |__)     §aAuthor §rAntos112");
        getServer().getConsoleSender().sendMessage("§e|__) |___  |   |  |___ |  \\ .__/ |___ |  \\  \\/  |___ |  \\     §7" + this.getServer().getName() + "" + this.getServer().getVersion());

        getConfig().options().copyDefaults(true);
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        reloadConfig();

        if (!new File(getDataFolder(), "warning.yml").exists()) {
            saveResource("warning.yml", false);
        }
        YamlConfiguration.loadConfiguration(new File(getDataFolder(), "warning.yml")).options().copyDefaults(true);

        YamlConfiguration.loadConfiguration(new File(getDataFolder(), "warning-event.yml")).options().copyDefaults(true);
        if (!new File(getDataFolder(), "warning-event.yml").exists()) {
            saveResource("warning-event.yml", false);
        }

        new File(new File(getDataFolder(), "Player-Data"), "Player-Lang");

        //manager set up

        warningManager = new WarningManager() {
            @Override
            public @Nullable Warning getWarning(@NotNull OfflinePlayer player) {
                return PLAYER_WARNING.get(player.getUniqueId());
            }

            @Override
            public void reloadWarningConfig(@NotNull YamlConfiguration configuration) {
                new Warning(Bukkit.getOfflinePlayer(UUID.fromString(configuration.getString("Warning.Player-UUID"))), Integer.parseInt(configuration.getString("Warning.Warning-Num")));
            }

            @Override
            public void loadAllWarningConfig() {
                if (new File(new File(betterServer.getDataFolder(), "Player-Data"), "Player-Lang").listFiles() != null) {
                    for (File file : new File(new File(BetterServer.getInstance().getDataFolder(), "Player-Data"), "Player-Warning").listFiles()) {
                        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                        new Warning(getServer().getOfflinePlayer(UUID.fromString(configuration.getString("Warning.Player-UUID"))), configuration.getInt("Warning.Warning-Num"));
                    }
                }
            }
        };

        muteManager = new MuteManager() {
            @Override
            public @Nullable Mute getMute(OfflinePlayer player) {
                return mutes.get(player.getUniqueId());
            }

            @Override
            public void reloadMuteConfig(YamlConfiguration configuration) {
                new Mute(Bukkit.getOfflinePlayer(configuration.getString("Mute.Player-UUID")), configuration.getInt("Mute.Player-Second"));
            }

            @Override
            public @NotNull List<OfflinePlayer> getMutedPlayers() {
                List<OfflinePlayer> players = new ArrayList<>();
                for (UUID uuid : mutes.keySet()) {
                    players.add(Bukkit.getOfflinePlayer(uuid));
                }

                return players;
            }
        };


        playerLangManager = new PlayerLangManager() {
            @Override
            public void loadAllPlayerLangConfig() {
                if (new File(new File(betterServer.getDataFolder(), "Player-Data"), "Player-Lang").listFiles() != null) {
                    for (File file : new File(new File(betterServer.getDataFolder(), "Player-Data"), "Player-Lang").listFiles()) {
                        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                        PLAYER_LANG.put(UUID.fromString(configuration.getString("Lang.Player-UUID")), configuration.getString("Lang.File-Name"));
                    }
                }
            }

            @Override
            public void setPlayerLang(@NotNull OfflinePlayer player, @NotNull String lang) {
                if (LANG_LIST.contains(lang) || lang.equals("default-english") || lang.equals("default-korean")) {
                    PLAYER_LANG.put(player.getUniqueId(), lang);
                    saveLangConfig(player, lang);
                }
            }

            @Override
            public void saveLangConfig(@NotNull OfflinePlayer player, @NotNull String lang) {
                File langFile = new File(new File(new File(betterServer.getDataFolder(), "Player-Data"), "Player-Lang"), player.getUniqueId() + ".yml");
                YamlConfiguration config = new YamlConfiguration();
                config.set("Lang.Player-UUID", player.getUniqueId() + "");
                config.set("Lang.Player-Name", player.getName());
                config.set("Lang.File-Name", lang);
                try {
                    config.save(langFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PLAYER_LANG.put(player.getUniqueId(), lang);
            }

            @Override
            public @NotNull String getPlayerLang(@NotNull OfflinePlayer player) {
                if (!PLAYER_LANG.containsKey(player.getUniqueId())) {
                    saveLangConfig(player, betterServer.getConfig().getString("BetterServer.default-lang"));
                    return betterServer.getConfig().getString("BetterServer.default-lang");
                } else {
                    return PLAYER_LANG.get(player.getUniqueId());
                }
            }

            @Override
            public void reloadPlayerLangConfig(@NotNull YamlConfiguration configuration) {
                PLAYER_LANG.put(UUID.fromString(configuration.getString("Lang.Player-UUID")), configuration.getString("Lang.File-Name"));
            }

            @Override
            public boolean isLanguage(String lang) {
                return LANG_LIST.contains(lang);
            }

            @Override
            public List<String> getLanguageList() {
                return LANG_LIST;
            }

            @Override
            public YamlConfiguration getLangConfig(String name) {
                return YamlConfiguration.loadConfiguration(new File(new File(betterServer.getDataFolder(), "Lang"), name + ".yml"));
            }

            @Override
            public String getMessage(OfflinePlayer player ,String path) {
                YamlConfiguration configuration = getLangConfig(getPlayerLang(player));
                if (configuration != null) {
                    if (configuration.getString(path) != null) {
                        return configuration.getString(path);
                    }
                }

                return "";
            }

            @Override
            public String getDefaultLangName() {
                return betterServer.getConfig().getString("BetterServer.default-lang");
            }

            @Override
            public YamlConfiguration getDefaultLangConfig() {
                if ((getDefaultLangName().equals("default-english") && getDefaultLangName().equals("default-korean"))) {
                    if (isLanguage(getDefaultLangName())) {
                        return getLangConfig(getDefaultLangName());
                    }
                }
                return null;
            }
        };


        // Command declaration
        new BetterServerCommand(this);
        new MuteCommand(this);
        new WarningCommand(this);

        // load Data
        warningManager.loadAllWarningConfig();
        playerLangManager.loadAllPlayerLangConfig();

        // console info
        getServer().getConsoleSender().sendMessage("[BetterServer] §a[Info] BetterServer enabled.");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("[BetterServer] §b[Info] BetterServer disabled.");
    }

    public static void error(String message) {
        getInstance().getLogger().severe(message);
    }

    public static void warning(String message) {
        getInstance().getLogger().warning(message);
    }

    @NotNull
    public static BetterServer getInstance() {
        return betterServer;
    }

    @NotNull
    public static WarningManager getWarningManager() {
        return warningManager;
    }

    @NotNull
    public static MuteManager getMuteManager() {
        return muteManager;
    }

    @NotNull
    public static PlayerLangManager getPlayerLangManager() {
        return playerLangManager;
    }
}