package com.antos.betterserver.command;


import com.antos.betterserver.BetterServer;
import com.antos.betterserver.api.Warning;
import com.antos.betterserver.api.event.PlayerWarningEvent;
import com.antos.betterserver.api.type.WarningType;
import com.antos.betterserver.api.util.*;
import com.antos.betterserver.api.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WarningCommand implements TabExecutor {
    private PlayerLangManager playerLangManager = BetterServer.getPlayerLangManager();
    private WarningManager warningManager = BetterServer.getWarningManager();
    private final BetterServer plugin;
    public WarningCommand(@NotNull BetterServer plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("warning");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (args.length == 0) {
                player.sendMessage( "");
                player.sendMessage("§e§m                                                    ");
                player.sendMessage("");
                if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                    player.sendMessage("  §2[] §f= optional argument");
                    player.sendMessage("  §6<> §f= required arguments");
                    player.sendMessage("");
                    player.sendMessage(" /warning add §6<player> §6<num> §2[reason] §7- Add the player's warning.");
                    player.sendMessage(" /warning remove §6<player> §6<num> §2[reason] §7- Subtract the player's warning");
                    player.sendMessage(" /warning set §6<player> §6<num> §2[reason] §7- Set the player's warning");
                    player.sendMessage(" /warning reload §7- Reload all warning configuration file");
                    player.sendMessage(" /warning check §2[player] §7- Check the player's warning");
                } else if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                    player.sendMessage("  §2[] §f= 선택 인자");
                    player.sendMessage("  §6<> §f= 필수 인자");
                    player.sendMessage("");
                    player.sendMessage(" /warning add §6<player> §6<num> §2[reason] §7- 경고를 추가합니다.");
                    player.sendMessage(" /warning remove §6<player> §6<num> §2[reason] §7- 경고를 제거합니다.");
                    player.sendMessage(" /warning set §6<player> §6<num> §2[reason] §7- 경고를 설정합니다.");
                    player.sendMessage(" /warning reload §7- Warning과 관련된 모든 구성 파일을 리로드합니다.");
                    player.sendMessage(" /warning check §2[player] §7- 경고를 확인합니다.");
                }
            } else if (args[0].equals("add")) {
                if (args.length == 1) {
                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§c플레이어를 입력해주세요.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§cPlease enter a player");
                    }
                } else if (args.length == 2) {
                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§c경고 추가 횟수를 입력해주세요.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§cPlease enter the number of warnings to add");
                    }
                } else {
                    int num;
                    try {
                        num = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                            player.sendMessage("§c" + args[2] + "을(를) 숫자로 변환할 수 없습니다.");
                        } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                            player.sendMessage("§c" + args[2] + " cannot be converted to a number.");
                        }
                        return false;
                    }

                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
                    Warning warning = null;
                    if (warningManager.getWarning(targetPlayer) != null) {
                        warning = warningManager.getWarning(targetPlayer);
                        warning.addWaring(num);
                    } else {
                        warning = new Warning(targetPlayer, num);
                    }

                    plugin.getServer().getPluginManager().callEvent(new PlayerWarningEvent(targetPlayer, player, WarningType.ADD, num));

                    String reason = "Administrator discretion";
                    if (args.length >= 4) {
                        reason = "";
                        for (int i = 3; i < args.length; i++) {
                            reason = reason + " " + args[i];
                        }
                    }

                    File file = new File(plugin.getDataFolder(), "warning.yml");

                    if (YamlConfiguration.loadConfiguration(file) != null) {
                        if (YamlConfiguration.loadConfiguration(file).getString("Warning-Info.Warning-Message").equals("broadcast"))  {
                            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                                player.sendMessage("");
                                player.sendMessage("§c§m                                                   ");
                                if (playerLangManager.getPlayerLang(onlinePlayer).equals("default-english")) {
                                    onlinePlayer.sendMessage("Player: §7" + warning.getPlayer().getName());
                                    onlinePlayer.sendMessage("Number of additions: §7" + num);
                                    onlinePlayer.sendMessage("Reason: §7" + reason);
                                } else if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                                    onlinePlayer.sendMessage("플레이어: §7" + warning.getPlayer().getName());
                                    onlinePlayer.sendMessage("경고 추가 횟수:§7 " + num);
                                    if (reason.equals("Administrator discretion")) {
                                        onlinePlayer.sendMessage("사유: §7관리자 재량");
                                    } else {
                                        onlinePlayer.sendMessage("사유: §7" + reason);
                                    }
                                }
                                player.sendMessage("");
                                player.sendMessage("§c§m                                                   ");
                            }
                        } else if (YamlConfiguration.loadConfiguration(file).getString("Warning-Info.Warning-Message").equals("message"))  {
                            if (targetPlayer.isOnline()) {
                                targetPlayer.getPlayer().sendMessage("");
                                targetPlayer.getPlayer().sendMessage("§c§m                                                   ");
                                if (playerLangManager.getPlayerLang(targetPlayer).equals("default-korean")) {
                                    targetPlayer.getPlayer().sendMessage(targetPlayer.getName() + "님의 경고가 " + num + "회 추가되었습니다.");
                                    if (reason.equals("Administrator discretion")) {
                                        targetPlayer.getPlayer().sendMessage("경고 사유: §7관리자 재량");
                                    } else {
                                        targetPlayer.getPlayer().sendMessage("경고 사유: §7" + reason);
                                    }
                                } else if (playerLangManager.getPlayerLang(targetPlayer.getPlayer()).equals("default-english")) {
                                    targetPlayer.getPlayer().sendMessage( targetPlayer.getName() + "'s warning has been added " + num + "time");
                                    targetPlayer.getPlayer().sendMessage("Reason: §7" + reason);
                                }
                                targetPlayer.getPlayer().sendMessage("");
                                targetPlayer.getPlayer().sendMessage("§c§m                                                   ");
                            }
                        }
                    }

                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§a성공적으로 " + targetPlayer.getName() + "님의 경고를 " + num + "회 추가하였습니다.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§aSuccessfully added " + targetPlayer.getName() + "'s warning " + num + " times.");
                    }

                    if (targetPlayer.isOnline()) {
                        WarningConfigReader.readWCData(plugin.getServer().getPlayer(args[1]));
                    }

                    warning.saveConfig();
                }
            } else if (args[0].equals("remove")) {
                if (args.length == 1) {
                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§c플레이어를 입력해주세요.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§cPlease enter a player");
                    }
                } else if (args.length == 2) {
                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§c경고 제거 횟수를 입력해주세요.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§cPlease enter the number of warnings to subtract");
                    }
                } else {
                    int num;
                    try {
                        num = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                            player.sendMessage("§c" + args[2] + "을(를) 숫자로 변환할 수 없습니다.");
                        } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                            player.sendMessage("§c" + args[2] + " cannot be converted to a number.");
                        }
                        return false;
                    }

                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
                    Warning warning = null;
                    if (warningManager.getWarning(targetPlayer) != null) {
                        warning = warningManager.getWarning(targetPlayer);
                        warning.subtractWaring(num);
                    } else {
                        warning = new Warning(targetPlayer);
                    }

                    String reason = "Administrator discretion";
                    if (args.length >= 4) {
                        reason = "";
                        for (int i = 3; i < args.length; i++) {
                            reason = reason + " " + args[i];
                        }
                    }

                    File file = new File(plugin.getDataFolder(), "warning.yml");

                    if (YamlConfiguration.loadConfiguration(file) != null) {
                        if (YamlConfiguration.loadConfiguration(file).getString("Warning-Info.Warning-Message").equals("broadcast"))  {
                            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                                player.sendMessage("");
                                player.sendMessage("§c§m                                                   ");
                                if (playerLangManager.getPlayerLang(onlinePlayer).equals("default-english")) {
                                    onlinePlayer.sendMessage("Player: §7" + warning.getPlayer().getName());
                                    onlinePlayer.sendMessage("Number of subtraction: §7" + num);
                                    onlinePlayer.sendMessage("Reason: §7" + reason);
                                } else if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                                    onlinePlayer.sendMessage("플레이어: §7" + warning.getPlayer().getName());
                                    onlinePlayer.sendMessage("경고 제거 횟수:§7 " + num);
                                    if (reason.equals("Administrator discretion")) {
                                        onlinePlayer.sendMessage("사유: §7관리자 재량");
                                    } else {
                                        onlinePlayer.sendMessage("사유: §7" + reason);
                                    }
                                }
                                player.sendMessage("");
                                player.sendMessage("§c§m                                                   ");
                            }
                        } else if (YamlConfiguration.loadConfiguration(file).getString("Warning-Info.Warning-Message").equals("message"))  {
                            if (targetPlayer.isOnline()) {
                                targetPlayer.getPlayer().sendMessage("");
                                targetPlayer.getPlayer().sendMessage("§c§m                                                   ");
                                if (playerLangManager.getPlayerLang(targetPlayer.getPlayer()).equals("default-korean")) {
                                    targetPlayer.getPlayer().sendMessage(targetPlayer.getName() + "님의 경고가 " + num + "회 차감되었습니다.");
                                    if (reason.equals("Administrator discretion")) {
                                        targetPlayer.getPlayer().sendMessage("경고 제거 사유: §7관리자 재량");
                                    } else {
                                        targetPlayer.getPlayer().sendMessage("경고 제거 사유: §7" + reason);
                                    }
                                } else if (playerLangManager.getPlayerLang(targetPlayer.getPlayer()).equals("default-english")) {
                                    targetPlayer.getPlayer().sendMessage( targetPlayer.getName() + "'s warning has been subtracted " + num + "time");
                                    targetPlayer.getPlayer().sendMessage("Reason: §7" + reason);
                                }
                                targetPlayer.getPlayer().sendMessage("");
                                targetPlayer.getPlayer().sendMessage("§c§m                                                   ");
                            }
                        }
                    }

                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§a성공적으로 " + targetPlayer.getName() + "님의 경고를 " + num + "회 제거하였습니다.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§aSuccessfully subtracted " + targetPlayer.getName() + "'s warning " + num + " times.");
                    }

                    warning.saveConfig();
                }
            } else if (args[0].equals("set")) {
                if (args.length == 1) {
                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§c플레이어를 입력해주세요.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§cPlease enter a player");
                    }
                } else if (args.length == 2) {
                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§c경고 설정 횟수를 입력해주세요.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§cPlease enter the number of warnings to set");
                    }
                } else {
                    int num;
                    try {
                        num = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                            player.sendMessage("§c" + args[2] + "을(를) 숫자로 변환할 수 없습니다.");
                        } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                            player.sendMessage("§c" + args[2] + " cannot be converted to a number.");
                        }
                        return false;
                    }

                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
                    Warning warning = null;

                    if (warningManager.getWarning(targetPlayer) != null) {
                        warning = warningManager.getWarning(targetPlayer);
                        warning.setWaring(num);
                    } else {
                        warning = new Warning(targetPlayer, num);
                    }

                    String reason = "Administrator discretion";
                    if (args.length >= 4) {
                        reason = "";
                        for (int i = 3; i < args.length; i++) {
                            reason = reason + " " + args[i];
                        }
                    }

                    File file = new File(plugin.getDataFolder(), "warning.yml");

                    if (YamlConfiguration.loadConfiguration(file) != null) {
                        if (YamlConfiguration.loadConfiguration(file).getString("Warning-Info.Warning-Message").equals("broadcast"))  {
                            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                                player.sendMessage("");
                                player.sendMessage("§c§m                                                   ");
                                if (playerLangManager.getPlayerLang(onlinePlayer).equals("default-english")) {
                                    onlinePlayer.sendMessage("Player: §7" + warning.getPlayer().getName());
                                    onlinePlayer.sendMessage("Number of setting: §7" + num);
                                    onlinePlayer.sendMessage("Reason: §7" + reason);
                                } else if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                                    onlinePlayer.sendMessage("플레이어: §7" + warning.getPlayer().getName());
                                    onlinePlayer.sendMessage("경고 설정 횟수:§7 " + num);
                                    if (reason.equals("Administrator discretion")) {
                                        onlinePlayer.sendMessage("사유: §7관리자 재량");
                                    } else {
                                        onlinePlayer.sendMessage("사유: §7" + reason);
                                    }
                                }
                                player.sendMessage("");
                                player.sendMessage("§c§m                                                   ");
                            }
                        } else if (YamlConfiguration.loadConfiguration(file).getString("Warning-Info.Warning-Message").equals("message"))  {
                            if (targetPlayer.isOnline()) {
                                targetPlayer.getPlayer().sendMessage("");
                                targetPlayer.getPlayer().sendMessage("§c§m                                                   ");
                                if (playerLangManager.getPlayerLang(targetPlayer.getPlayer()).equals("default-korean")) {
                                    targetPlayer.getPlayer().sendMessage(targetPlayer.getName() + "님의 경고가 " + num + "회로 설정되었습니다.");
                                    if (reason.equals("Administrator discretion")) {
                                        targetPlayer.getPlayer().sendMessage("경고 설정 사유: §7관리자 재량");
                                    } else {
                                        targetPlayer.getPlayer().sendMessage("경고 설정 사유: §7" + reason);
                                    }
                                } else if (playerLangManager.getPlayerLang(targetPlayer.getPlayer()).equals("default-english")) {
                                    targetPlayer.getPlayer().sendMessage( targetPlayer.getName() + "'s warning has been set " + num + "time");
                                    targetPlayer.getPlayer().sendMessage("Reason: §7" + reason);
                                }
                                targetPlayer.getPlayer().sendMessage("");
                                targetPlayer.getPlayer().sendMessage("§c§m                                                   ");
                            }
                        }
                    }

                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        player.sendMessage("§a성공적으로 " + targetPlayer.getName() + "님의 경고를 " + num + "회로 설정하였습니다.");
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        player.sendMessage("§aSuccessfully set " + targetPlayer.getName() + "'s warning " + num + " times.");
                    }

                    warning.saveConfig();
                }
            } else if (args[0].equals("reload")) {
                warningManager.loadAllWarningConfig();

                if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                    player.sendMessage("§a성공적으로 경고 관련 구성 파일이 리로드되었습니다.");
                } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                    player.sendMessage("§aWarning-related configuration files have been successfully reloaded.");
                }
            } else if (args[0].equals("check")) {
                if (args.length == 1) {
                    if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                        Warning warning = warningManager.getWarning(player);
                        if (warning != null) {
                            player.sendMessage("§a" + player.getName() + "님은 " + warning.getWarningNum() + "번의 경고를 받았습니다.");
                        } else {
                            player.sendMessage("§c" + player.getName() + "님이 경고를 받은 기록이 없습니다.");
                        }
                    } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                        Warning warning = warningManager.getWarning(player);
                        if (warning != null) {
                            player.sendMessage("§a" +  player.getName() +" received " + warning.getWarningNum() +" warnings.");
                        } else {
                            player.sendMessage("§cThere is no record of " + player.getName() + " receiving a warning.");
                        }
                    }
                } else if (args.length == 2) {
                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
                    if (targetPlayer != null) {
                        if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                            Warning warning = warningManager.getWarning(targetPlayer);
                            if (warning != null) {
                                player.sendMessage("§a" + targetPlayer.getName() + "님은 " + warning.getWarningNum() + "번의 경고를 받았습니다.");
                            } else {
                                player.sendMessage("§c" + targetPlayer.getName() + "님이 경고를 받은 기록이 없습니다.");
                            }
                        } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                            Warning warning = warningManager.getWarning(targetPlayer);
                            if (warning != null) {
                                player.sendMessage("§a" +  targetPlayer.getName() +" received " + warning.getWarningNum() +" warnings.");
                            } else {
                                player.sendMessage("§cThere is no record of " + targetPlayer.getName() + " receiving a warning.");
                            }
                        }
                    }
                }
            } else if (args[0].equals("test")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "warning.yml"));
                if (!config.getStringList("Warning-Event." + "Kick" + ".messages").isEmpty()) {
                    for (String message : config.getStringList("Warning-Event." + "Kick" + ".messages")) {
                        if (message.contains("message{m=\"") && message.contains("}")) {
                            message = message.replaceFirst("message\\{m=\"", "");
                            message = message.substring(0, (message.length()-2));
                            player.sendMessage(message);
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> tab = new ArrayList<>();
        if (strings.length == 1) {
            tab.add("add");
            tab.add("remove");
            tab.add("set");
            tab.add("reload");
            tab.add("check");
        } else if (strings.length == 2 && !strings[0].equals("reload")) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                tab.add(player.getName());
            }
        } else if (strings.length == 3 && !(strings[0].equals("reload") && strings[0].equals("check"))) {
            tab.add("1");
        } else if (strings.length == 4 &&!(strings[0].equals("reload") && strings[0].equals("check")) ) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (playerLangManager.getPlayerLang(player).equals("default-korean")) {
                    tab.add("규칙 위반");
                } else if (playerLangManager.getPlayerLang(player).equals("default-english")) {
                    tab.add("rule violation");
                }
            }
        } else {
            tab.add("");
        }
        return tab;
    }
}