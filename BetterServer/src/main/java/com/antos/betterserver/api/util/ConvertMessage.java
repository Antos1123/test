package com.antos.betterserver.api.util;

import org.bukkit.entity.Player;

import java.util.Random;

public class ConvertMessage {
    public static String convertMessage(String message, Player player) {
        String text = message;
        if (text.contains("%player%")) {
            text = text.replaceAll("%player%", player.getName());
        }

        if (text.contains("%uuid of player%")) {
            text = text.replaceAll("%uuid of player%", player.getUniqueId().toString());
        }

        if (text.contains("%random(") && text.contains(")%")) {
            String s = text;
            text = message.replaceFirst("%random\\(", "");
            text = text.substring(0, text.lastIndexOf(")%"));
            String[] num = text.split(",");
            if (num.length == 2) {
                Random random = new Random();
                int num1 = Integer.parseInt(num[0].trim());
                int num2 = Integer.parseInt(num[1].trim());
                int minNum = Math.min(num1, num2);
                int maxNum = Math.max(num1, num2);
                int randomNum = random.nextInt(maxNum - minNum + 1) + minNum;
                text = s.replaceFirst("%random\\(" + num[0] + "," + num[1] + "\\)%",  Integer.toString(randomNum));
            }
        }

        if (text.contains("＆")) {
            text = text.replaceAll("＆", "§");
        }

        if (!WarningConfigReader.variables.isEmpty()) {
            for (String var : WarningConfigReader.variables.keySet()) {
                if (text.contains("%" + var + "%")) {
                    text = text.replaceAll("%" + var + "%", WarningConfigReader.variables.get(var));
                }
            }
        }

        return text;
    }
}
