package kr.antos112.animation.command;

import kr.antos112.animation.SDAnimation;
import kr.antos112.animation.api.SchoolDoor;
import kr.antos112.animation.api.manager.SDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static kr.antos112.animation.SDAnimation.doors;

public class SDCommand implements TabExecutor {
    SDManager manager = SDAnimation.getSDManager();

    public SDCommand() {
        PluginCommand command = SDAnimation.getInstance().getServer().getPluginCommand("교문");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("");
            commandSender.sendMessage("§e§m                                                    ");
            commandSender.sendMessage("");
            commandSender.sendMessage("  §2[] §f= 선택 인자");
            commandSender.sendMessage("  §6<> §f= 필수 인자");
            commandSender.sendMessage("");
            commandSender.sendMessage(" /교문 설치 §6<name> §7- 교문을 설치합니다");
            commandSender.sendMessage(" /교문 제거 §6<name>");
            commandSender.sendMessage(" /교문 리로드 §2[name] §7- 콘피그를 리로드합니다");
            commandSender.sendMessage(" /교문 재설치 §7- 모든 교문을 재설치합니다");
            commandSender.sendMessage("");
            commandSender.sendMessage("§e§m                                                    ");
        } else if (strings[0].equals("설치")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("§c해당 명령어는 플레이어만 입력가능합니다");
                return true;
            }

            if (strings.length == 1) {
                commandSender.sendMessage("§c교문 이름을 설정해주세요");
                return true;
            }

            if (manager.getSchoolDoor(strings[1]) != null) {
                commandSender.sendMessage("§c이미 존재하는 이름에 교문입니다");
                return true;
            }

            SchoolDoor sc = new SchoolDoor(strings[1], ((Player) commandSender).getLocation());
            sc.place();

            commandSender.sendMessage("§a성공적으로 교문이 설치되었습니다");
        } else if (strings[0].equals("제거")) {
            if (strings.length == 1) {
                commandSender.sendMessage("§c교문 이름을 입력해주세요");
                return true;
            }

            if (manager.getSchoolDoor(strings[1]) == null) {
                commandSender.sendMessage("§c존재하지않는 교문입니다");
                return true;
            }

            manager.getSchoolDoor(strings[1]).delete();
            commandSender.sendMessage("§a교문을 성공적으로 제거하였습니다");
        } else if (strings[0].equals("리로드")) {
            if (strings.length == 1) {
                SDAnimation.getInstance().reloadConfig();
                manager.loadAllData();
            } else if (strings.length == 2) {
                SchoolDoor sd = manager.getSchoolDoor(strings[1]);

                if (sd == null) {
                    commandSender.sendMessage("§c존재하지않는 이름에 교문입니다");
                    return true;
                }

                sd.reload();
            }

            commandSender.sendMessage("§a성공적으로 데이터가 리로드되었습니다");
        } else if (strings[0].equals("재설치")) {
            manager.replaceAll();
            commandSender.sendMessage("§a모든 교문을 재설치하였습니다");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> tab = new ArrayList<>();

        if (strings.length == 1) {
            tab.add("설치");
            tab.add("제거");
            tab.add("리로드");
            tab.add("재설치");
        } else if (strings.length == 2) {
            if (strings[0].equals("제거") || strings[0].equals("리로드")) {
                if (!manager.getSchoolDoors().isEmpty()) {
                    for (SchoolDoor door : manager.getSchoolDoors()) {
                        tab.add(door.getName());
                    }
                }
            }
        }

        return tab;
    }
}
