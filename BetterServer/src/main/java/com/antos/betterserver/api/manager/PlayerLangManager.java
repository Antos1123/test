package com.antos.betterserver.api.manager;

import com.antos.betterserver.BetterServer;
import com.antos.betterserver.api.annotations.Manager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

@Manager("PlayerLang")
public interface PlayerLangManager {
    Map<UUID, String> PLAYER_LANG = new HashMap<>();
    List<String> LANG_LIST = new ArrayList<>();

    void loadAllPlayerLangConfig();

    void setPlayerLang(@NotNull OfflinePlayer player, @NotNull String lang);

    void saveLangConfig(@NotNull OfflinePlayer player, @NotNull String lang);

    @NotNull
    String getPlayerLang(@NotNull OfflinePlayer player);

    void reloadPlayerLangConfig(@NotNull YamlConfiguration configuration);

    boolean isLanguage(String lang);

    List<String> getLanguageList();

    YamlConfiguration getLangConfig(String name);

    String getMessage(OfflinePlayer player ,String path);

    String getDefaultLangName();

    YamlConfiguration getDefaultLangConfig();
}