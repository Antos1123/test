package com.antos.betterserver.api.util;

import com.antos.betterserver.BetterServer;
import com.antos.betterserver.api.Warning;
import com.antos.betterserver.api.manager.WarningManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class WarningConfigReader {
    private static WarningManager warningManager = BetterServer.getWarningManager();
    private static Plugin plugin = Bukkit.getPluginManager().getPlugin("BetterServer");
    public static Map<String, String> variables = new HashMap<>();

    public static void readWCData(Player player) {
        variables = new HashMap<>();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "warning-event.yml"));
        if (warningManager.getWarning(player) != null && warningManager.getWarning(player).getWarningNum() >= 1) {
            Warning warning = warningManager.getWarning(player);
            int warnNum = warning.getWarningNum();

            for (String key : ConfigReader.readConfigData(config, "Warning-Event")) {
                if (!config.getStringList("Warning-Event." + key + ".variables").isEmpty()) {
                    for (String var : config.getStringList("Warning-Event." + key + ".variables")) {
                        String variable = var;
                        if (variable.contains("=")) {
                            String[] vars = variable.split("=");
                            String varName = convertOperatorMessage(vars[0].trim(), player, false);
                            String varValue = convertOperatorMessage(vars[1].trim(), player);
                            variables.put(varName, varValue);
                        } else {
                            variables.put(convertOperatorMessage(variable.trim(), player) ,"");
                        }
                    }
                }
            }

            for (String key : ConfigReader.readConfigData(config, "Warning-Event")) {
                if (config.getString("Warning-Event." + key + ".Type") != null) {
                    String type = config.getString("Warning-Event." + key + ".Type");
                    if (type.equalsIgnoreCase("WARNING_" + warnNum)) {
                        if (!config.getStringList("Warning-Event." + key + ".execution").isEmpty()) {
                            for (String message : config.getStringList("Warning-Event." + key + ".execution")) {
                                if (message.contains("@cast:") && (message.contains(">") || message.contains("<") || message.contains("="))) {
                                    String[] operatorText = message.split("@cast:");
                                    if (operatorText.length == 2) {
                                        if (operatorText[0].contains("<=")) {
                                          String[] executeText = operatorText[0].split("<=");
                                          if (executeText.length == 2) {
                                              if (Integer.parseInt(convertOperatorMessage(executeText[0].trim(), player)) <= Integer.parseInt(convertOperatorMessage(executeText[1].trim(), player))) {
                                                  executeMessage(operatorText[1], player);
                                              }
                                          }
                                        } else if (operatorText[0].contains("=<")) {
                                            String[] executeText = operatorText[0].split("<=");
                                            if (executeText.length == 2) {
                                                if (Integer.parseInt(convertOperatorMessage(executeText[0].trim(), player)) <= Integer.parseInt(convertOperatorMessage(executeText[1].trim(), player))) {
                                                    executeMessage(operatorText[1], player);
                                                }
                                            }
                                        } else if (operatorText[0].contains(">=")) {
                                            String[] executeText = operatorText[0].split(">=");
                                            if (executeText.length == 2) {
                                                if (Integer.parseInt(convertOperatorMessage(executeText[0].trim(), player)) >= Integer.parseInt(convertOperatorMessage(executeText[1].trim(), player))) {
                                                    executeMessage(operatorText[1], player);
                                                }
                                            }
                                        } else if (operatorText[0].contains("=>")) {
                                            String[] executeText = operatorText[0].split("<=");
                                            if (executeText.length == 2) {
                                                if (Integer.parseInt(convertOperatorMessage(executeText[0].trim(), player)) >= Integer.parseInt(convertOperatorMessage(executeText[1].trim(), player))) {
                                                    executeMessage(operatorText[1], player);
                                                }
                                            }
                                        } else if (operatorText[0].contains("==")) {
                                            String[] excuteText = operatorText[0].split("==");
                                            if (excuteText.length == 2) {
                                                if (convertOperatorMessage(excuteText[0].trim(), player).equals(convertOperatorMessage(excuteText[1].trim(), player))) {
                                                    executeMessage(operatorText[1], player);
                                                }
                                            }
                                        } else if (operatorText[0].contains("!=")) {
                                            String[] executeText = operatorText[0].split("!=");
                                            if (executeText.length == 2) {
                                                if (!convertOperatorMessage(executeText[0].trim(), player).equals(convertOperatorMessage(executeText[1].trim(), player))) {
                                                    executeMessage(operatorText[1], player);
                                                }
                                            }
                                        } else if (operatorText[0].contains("<")) {
                                            String[] executeText = operatorText[0].split("<");
                                            if (executeText.length == 2) {
                                                if (Integer.parseInt(convertOperatorMessage(executeText[0].trim(), player)) < Integer.parseInt(convertOperatorMessage(executeText[1].trim(), player))) {
                                                    executeMessage(operatorText[1], player);
                                                }
                                            }
                                        } else if (operatorText[0].contains(">")) {
                                            String[] executeText = operatorText[0].split(">");
                                            if (executeText.length == 2) {
                                                if (Integer.parseInt(convertOperatorMessage(executeText[0].trim(), player)) > Integer.parseInt(convertOperatorMessage(executeText[1].trim(), player))) {
                                                    executeMessage(operatorText[1], player);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    executeMessage(message, player);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static String convertOperatorMessage(String message, Player player, boolean b) {
        String text = message;
        for (int i = 0; i < message.length(); i++) {
            boolean a = false;
            if (text.contains("player")) {
                text = text.replace("player", player.getName());
                a = true;
            }

            if (text.contains("uuid of player")) {
                text = text.replaceAll("uuid of player", player.getUniqueId().toString());
                a = true;
            }

            if (text.contains("random(") && text.contains(")")) {
                String s = text;
                text = message.replaceFirst("random\\(", "");
                text = text.substring(0, text.lastIndexOf(")"));
                String[] num = text.split(",");
                if (num.length == 2) {
                    Random random = new Random();
                    int num1 = Integer.parseInt(num[0].trim());
                    int num2 = Integer.parseInt(num[1].trim());
                    int minNum = Math.min(num1, num2);
                    int maxNum = Math.max(num1, num2);
                    int randomNum = random.nextInt(maxNum - minNum + 1) + minNum;
                    text = s.replaceFirst("random\\(" + num[0] + "," + num[1] + "\\)",  Integer.toString(randomNum));
                }
                a = true;
            }

            /*
            if (text.contains("skript_variable(") && text.contains(")")) {
                String s = text;
                text = message.replaceFirst("skript_variable\\(", "");
                text = text.substring(0, text.lastIndexOf(")"));
                String n = text;
                text =  text.replaceFirst("\"", "");
                text = text.substring(0, (text.length()-1));

                if (text.contains("%uuid of player%")) {
                    text = text.replaceAll("%uuid of player%", player.getUniqueId().toString());
                }

                if (text.contains("%player%")) {
                    text = text.replaceAll("%player%", player.getName());
                }

                String value = SkriptManager.getVar(text, null, false).toString();

                text = s.replaceFirst("skript_variable\\(" + n + "\\)", value);
                a = true;
            }

             */

            if (b) {
                if (!WarningConfigReader.variables.isEmpty()) {
                    for (String var : WarningConfigReader.variables.keySet()) {
                        if (text.contains(var)) {
                            text = text.replaceAll(var, WarningConfigReader.variables.get(var));
                            a = true;
                        }
                    }
                }
            }

            if (!a) {
                break;
            }
        }
        return text;
    }

    static String convertOperatorMessage(String message, Player player) {
        return convertOperatorMessage(message, player, true);
    }

    static void executeMessage(String message, Player player) {
        if (message.contains("set{") && message.contains("}")) {
            String variable = message.replaceFirst("set\\{", "");
            variable = variable.substring(0, variable.lastIndexOf("}"));
            String[] var = variable.split("=");
            variables.put(convertOperatorMessage(var[0].trim(), player, false), convertOperatorMessage(var[1].trim(), player));
        } else if (message.contains("function{f=\"") && message.contains("}")) {
            message = message.replaceFirst("function\\{f=\"", "");
            message = message.substring(0, (message.length()-2));
            String[] function = message.split(" ");
            if (function.length == 2) {
                if (function[0].trim().equals("kick")) {
                    player.kickPlayer(function[1]);
                }
            }
        } else if (message.contains("command{c=\"") && message.contains("}")) {
            message = message.replaceFirst("command\\{c=\"", "");
            message = message.substring(0, (message.length()-1));
            if(message.contains("\",")) {
                String[] command = message.split("\",");
                if (command.length == 2) {
                    command[0] = command[0].trim();
                    command[1] = command[1].trim();
                    if (command[1].equals("player")) {
                        player.performCommand(ConvertMessage.convertMessage(command[0], player));
                    } else if (command[1].equals("console")) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), ConvertMessage.convertMessage(command[0], player));
                    }
                    else if (command[1].equals("op")) {
                        if (player.isOp()) {
                            player.performCommand(ConvertMessage.convertMessage(command[0], player));
                        } else {
                            player.setOp(true);
                            player.performCommand(ConvertMessage.convertMessage(command[0], player));
                            player.setOp(false);
                        }
                    }
                } else {
                    player.performCommand(ConvertMessage.convertMessage(command[0], player));
                }
            } else {
                message = message.trim();
                message = message.substring(0 ,(message.length()-1));
                player.sendMessage(ConvertMessage.convertMessage(message, player));
                player.performCommand(ConvertMessage.convertMessage(message, player));
            }
        } else if (message.contains("message{m=\"") && message.contains("}")) {
            message = message.replaceFirst("message\\{m=\"", "");
            message = message.substring(0, (message.length()-2));
            plugin.getServer().getPlayer(player.getName()).sendMessage(ConvertMessage.convertMessage(message.trim(), player));
        } else if (message.contains("title{t=\"") && message.contains("}")) {
            message = message.replaceFirst("title\\{t=", "");
            message = message.substring(0, (message.length()-1));
            if (message.contains("\",")) {
                String[] title = message.split("\",");
                if (title.length == 2) {
                    if (title[1].contains(",")) {
                        String[] times = title[1].split(",");
                        if (times.length == 3) {
                            try {
                                plugin.getServer().getPlayer(player.getName()).sendTitle(ConvertMessage.convertMessage(title[0].replaceFirst("\"", ""), player), "", Integer.parseInt(times[0]), Integer.parseInt(times[1]), Integer.parseInt(times[2]));
                            } catch (Exception e) {
                                plugin.getLogger().severe("[WarningEventReader] Error while reading title: " + e.getMessage());
                            }
                        } else {
                            try {
                                plugin.getServer().getPlayer(player.getName()).sendTitle(ConvertMessage.convertMessage(title[0].replaceFirst("\"", ""), player), "", 20, 20, 20);
                            } catch (Exception e) {
                                plugin.getLogger().severe("[WarningEventReader] Error while reading title: " + e.getMessage());
                            }
                        }
                    } else {
                        try {
                            title[1] = title[1].replaceFirst("\"", "");
                            plugin.getServer().getPlayer(player.getName()).sendTitle(ConvertMessage.convertMessage(title[0].replaceFirst("\"", ""), player), ConvertMessage.convertMessage(title[1].substring(0, (title[1].length()-1)), player), 20, 20, 20);
                        } catch (Exception e) {
                            plugin.getLogger().severe("[WarningEventReader] Error while reading title: " + e.getMessage());
                        }
                    }
                } else if (title.length == 3) {
                    if (title[2].contains(",")) {
                        String[] times = title[2].split(",");
                        if (times.length == 3) {
                            try {
                                plugin.getServer().getPlayer(player.getName()).sendTitle(ConvertMessage.convertMessage(title[0].replaceFirst("\"", ""), player), ConvertMessage.convertMessage(title[1].replaceFirst("\"", ""), player), Integer.parseInt(times[0]), Integer.parseInt(times[1]), Integer.parseInt(times[2]));
                            } catch (Exception e) {
                                plugin.getLogger().severe("[WarningEventReader] Error while reading title: " + e.getMessage());
                            }
                        } else {
                            try {
                                plugin.getServer().getPlayer(player.getName()).sendTitle(ConvertMessage.convertMessage(title[0].replaceFirst("\"", ""), player), ConvertMessage.convertMessage(title[1].replaceFirst("\"", ""), player), 20, 20, 20);
                            } catch (Exception e) {
                                plugin.getLogger().severe("[WarningEventReader] Error while reading title: " + e.getMessage());
                            }
                        }
                    } else {
                        try {
                            plugin.getServer().getPlayer(player.getName()).sendTitle(ConvertMessage.convertMessage(title[0].replaceFirst("\"", ""), player), ConvertMessage.convertMessage(title[1].replaceFirst("\"", ""), player), 20, 20, 20);
                        } catch (Exception e) {
                            plugin.getLogger().severe("[WarningEventReader] Error while reading title: " + e.getMessage());
                        }
                    }
                }
            } else {
                message = message.replaceFirst("\"", "");
                message = message.substring(0, (message.length()-1));
                plugin.getServer().getPlayer(player.getName()).sendTitle(ConvertMessage.convertMessage(message, player), "", 20, 20, 20);
            }
        } else if (message.contains("sound{s=\"")) {
            message = message.replaceFirst("sound\\{s=\"", "");
            message = message.substring(0, (message.length()-1));
            if (message.contains("\",")) {
                String[] sounds = message.split("\",");
                if (sounds.length == 1) {
                    try {
                        plugin.getServer().getPlayer(player.getName()).playSound(player.getLocation(), sounds[0], 1, 1);
                    } catch (Exception e) {
                        plugin.getLogger().severe("[WarningEventReader] Error while reading sound: " + e.getMessage());
                    }
                } else if (sounds.length == 2) {
                    if (sounds[1].contains(",")) {
                        String[] volume  = sounds[1].split(",");
                        if (volume.length == 1) {
                            try {
                                plugin.getServer().getPlayer(player.getName()).playSound(player.getLocation(), sounds[0], Integer.parseInt(volume[0]), 1);
                            } catch (Exception e) {
                                plugin.getLogger().severe("[WarningEventReader] Error while reading sound: " + e.getMessage());
                            }
                        } else if (volume.length == 2) {
                            try {
                                plugin.getServer().getPlayer(player.getName()).playSound(player.getLocation(), sounds[0], Integer.parseInt(volume[0]), Integer.parseInt(volume[1]));
                            } catch (Exception e) {
                                plugin.getLogger().severe("[WarningEventReader] Error while reading sound: " + e.getMessage());
                            }
                        }
                    } else {
                        try {
                            plugin.getServer().getPlayer(player.getName()).playSound(player.getLocation(), sounds[0], 1, 1);
                        } catch (Exception e) {
                            plugin.getLogger().severe("[WarningEventReader] Error while reading sound: " + e.getMessage());
                        }
                    }
                }
            }
        } else if (message.contains("broadcast{m=\"") && message.contains("}")) {
            message = message.replaceFirst("broadcast\\{m=\"", "");
            message = message.substring(0, (message.length()-2));
            plugin.getServer().broadcastMessage(message);
        }
    }
}