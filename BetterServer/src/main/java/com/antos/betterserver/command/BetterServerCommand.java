package com.antos.betterserver.command;

import com.antos.betterserver.BetterServer;
import com.antos.betterserver.api.manager.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BetterServerCommand implements TabExecutor {
    private final PlayerLangManager playerLangManager = BetterServer.getPlayerLangManager();
    private final JavaPlugin plugin;
    public BetterServerCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("AntosPlugin");
        PluginCommand pluginCommand = plugin.getCommand("bl");
        if (command != null && pluginCommand != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("AntosPlugin")) {
            String plugins = "p";
            int num = 0;
            for (Plugin p : this.plugin.getServer().getPluginManager().getPlugins()) {
                if (p.getDescription().getAuthors().contains("Antos112")) {
                    if (plugins.equals("p")) {
                        plugins = "§a" + p.getName();
                    } else {
                        plugins = plugins + "§r, §a" + p.getName();
                    }
                    num++;
                }
            }

            commandSender.sendMessage("§eAntos Plugins: (" + num + "):");
            commandSender.sendMessage(" §7- " + plugins);
        } else if (command.getName().equalsIgnoreCase("bl") && commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (strings.length == 0) {
                player.sendMessage( "");
                player.sendMessage("§e§m                                                    ");
                player.sendMessage("");

                if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                    player.sendMessage("  §2[] §f= optional argument");
                    player.sendMessage("  §6<> §f= required arguments");
                    player.sendMessage("");
                    player.sendMessage(" /bl set §6<lang> §2[player] §7- Set the player's Lang");
                    player.sendMessage(" /bl create §6<name> §2[base] §7- Create new lang");
                    player.sendMessage(" /bl remove §6<lang> §7- Remove the lang");

                } else if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                    player.sendMessage("  §2[] §f= 선택 인자");
                    player.sendMessage("  §6<> §f= 필수 인자");
                    player.sendMessage("");
                    player.sendMessage(" /bl set §6<lang> §2[player] §7- 플레이어의 언어를 설정합니다.");
                    player.sendMessage(" /bl create §6<name> §2[base] §7- 새로운 언어를 만듭니다.");
                    player.sendMessage(" /bl remove §6<lang> §7- 언어를 삭제 합니다.");

                }
            } else if (strings[0].equals("reload")) {
                plugin.reloadConfig();
                playerLangManager.loadAllPlayerLangConfig();
            } else if (strings[0].equals("set")) {
                if (strings.length == 1) {
                    if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§ePlease, enter a lang");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§e변경할 언어를 입력해주세요.");
                    }
                } else if (playerLangManager.isLanguage(strings[1]) || strings[1].equals("default-korean") || strings[1].equals("default-english")) {
                    playerLangManager.setPlayerLang(player, strings[1]);
                    if (strings[1].equals("default-english")) {
                        player.sendMessage("§aThe language has been changed successfully.");
                    } else if (strings[1].equals("default-korean")) {
                        player.sendMessage("§a언어가 성공적으로 변경되었습니다.");
                    } else {
                        player.sendMessage(playerLangManager.getMessage(player , "Lang.lang-change"));
                    }
                } else {
                    player.sendMessage(playerLangManager.getDefaultLangConfig().getString("Lang.lang-change"));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> tab = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("bl")) {
            if (strings.length == 1) {
                tab.add("set");
                tab.add("reload");
            } else  if (strings.length > 1 && strings[0].equalsIgnoreCase("set")){
                tab.add("default-korean");
                tab.add("default-english");
            } else {
                tab.add("");
            }
        }
        return tab;
    }
}
