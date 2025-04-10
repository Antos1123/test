package com.antos.betterserver.command;

import com.antos.betterserver.BetterServer;
import com.antos.betterserver.api.Mute;
import com.antos.betterserver.api.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MuteCommand implements TabExecutor {
    private final MuteManager muteManager = BetterServer.getMuteManager();
    private final PlayerLangManager playerLangManager = BetterServer.getPlayerLangManager();
    private final JavaPlugin plugin;


    public MuteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("mute");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("mute")) {
                if (strings.length == 0) {
                    player.sendMessage( "");
                    player.sendMessage("§e§m                                                    ");
                    player.sendMessage("");
                    if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("  §2[] §f= optional argument");
                        player.sendMessage("  §6<> §f= required arguments");
                        player.sendMessage("");
                        player.sendMessage(" /mute add §6<player> §6<time> §2[reason] §7- Add the player's mute time.");
                        player.sendMessage(" /mute remove §6<player> §6<time> §2[reason] §7- Subtract the player's mute time.");
                        player.sendMessage(" /mute set §6<player> §6<time> §2[reason] §7- Set the player's mute time.");
                        player.sendMessage(" /mute cancel §6<player> §2[reason] §7- Cancel the player's mute");
                        player.sendMessage(" /mute reload §7- Reload all mute configuration file");
                        player.sendMessage(" /mute list §7- Get a list of mute players.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("  §2[] §f= 선택 인자");
                        player.sendMessage("  §6<> §f= 필수 인자");
                        player.sendMessage("");
                        player.sendMessage(" /mute add §6<player> §6<time> §2[reason] §7- 뮤트 시간을 추가합니다.");
                        player.sendMessage(" /mute remove §6<player> §6<time> §2[reason] §7- 뮤트 시간을 차감합니다.");
                        player.sendMessage(" /mute set §6<player> §6<time> §2[reason] §7- 뮤트 시간을 설정합니다.");
                        player.sendMessage(" /mute cancel §6<player> §7- 뮤트를 취소합니다.");
                        player.sendMessage(" /mute reload §7- mute와 관련된 모든 구성파일을 리로드합니다.");
                        player.sendMessage(" /mute list §7- 뮤트 중인 플레이어 목록을 얻습니다.");
                    } else if (plugin.getConfig().getBoolean("BetterServer.console-debug")) {
                        plugin.getLogger().severe("Could not read 'BetterServer.default-lang' or player's BetterServer lang in config");
                    }
                } else if (strings[0].equals("add")) {
                    if (strings.length == 1) {
                        if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                            player.sendMessage("§c플레이어를 입력해주세요.");
                        } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                            player.sendMessage("§cPlease enter a player");
                        }
                    } else if (strings.length == 2) {
                        if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                            player.sendMessage("§c뮤트 추가 시간을 입력해주세요.");
                        } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                            player.sendMessage("§cPlease enter additional mute time.");
                        }
                    } else {
                        int second = 0;
                        int minute = 0;
                        int hour = 0;
                        int day = 0;



                        if (strings[2].contains("s")) {
                            try {
                                second = Integer.parseInt(strings[2].charAt((strings[2].indexOf("s")-1)) + "");
                            } catch (Exception e) {
                                if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                                    player.sendMessage("§c숫자로 변환할 수 없는 인수가 있습니다.");
                                } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                                    player.sendMessage("§cContains an argument that cannot be converted to a number.");
                                }
                                return false;
                            }
                        }

                        if (strings[2].contains("m")) {
                            try {
                                minute = Integer.parseInt(strings[2].charAt((strings[2].indexOf("m")-1)) + "");
                            } catch (Exception e) {
                                if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                                    player.sendMessage("§c숫자로 변환할 수 없는 인수가 있습니다.");
                                } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                                    player.sendMessage("§cContains an argument that cannot be converted to a number.");
                                }
                                return false;
                            }
                        }

                        if (strings[2].contains("h")) {
                            try {
                                hour = Integer.parseInt(strings[2].charAt((strings[2].indexOf("h")-1)) + "");
                            } catch (Exception e) {
                                if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                                    player.sendMessage("§c숫자로 변환할 수 없는 인수가 있습니다.");
                                } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                                    player.sendMessage("§cContains an argument that cannot be converted to a number.");
                                }
                                return false;
                            }
                        }

                        if (strings[2].contains("d")) {
                            try {
                                day = Integer.parseInt(strings[2].charAt((strings[2].indexOf("d")-1)) + "");
                            } catch (Exception e) {
                                if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                                    player.sendMessage("§c숫자로 변환할 수 없는 인수가 있습니다.");
                                } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                                    player.sendMessage("§cContains an argument that cannot be converted to a number.");
                                }
                                return false;
                            }
                        }
                        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(strings[1]);
                        int value = ( (day*86400) + (hour*3600) + (minute*60) + second );

                        if (muteManager.getMute(targetPlayer) != null) {
                            muteManager.getMute(targetPlayer).addSecond(value);
                        } else {
                            new Mute(targetPlayer, value);
                        }
                    }
                }

                else if (strings[0].equals("remove")) {

                }


                else if (strings[0].equals("set")) {

                }


                else if (strings[0].equals("cancel")) {

                }


                else if (strings[0].equals("list")) {

                }

                else if (strings[0].equals("reload")) {
                    BetterServer.getMuteManager().loadAllMuteConfig();

                    if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§aAll Mute configuration files have been loaded successfully.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§a모든 뮤트 관련 구성파일이 성공적으로 로드되었습니다.");
                    } else if (plugin.getConfig().getBoolean("BetterServer.console-debug")) {
                        plugin.getLogger().severe("Could not read " + player.getName() + "'s BetterServer lang");
                    }
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            tab.add("add");
            tab.add("remove");
            tab.add("set");
            tab.add("cancel");
            tab.add("list");
            tab.add("reload");
        } else if (args.length == 2 && !(args[0].equals("list") && args[0].equals("reload"))) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                tab.add(player.getName());
            }
        } else {
            tab.add("");
        }
        return tab;
    }
}