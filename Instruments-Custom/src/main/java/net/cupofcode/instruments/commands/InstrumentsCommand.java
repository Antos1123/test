package net.cupofcode.instruments.commands;

import net.cupofcode.instruments.InstrumentType;
import net.cupofcode.instruments.Instruments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InstrumentsCommand implements CommandExecutor {

	private Instruments instance = Instruments.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			// player-only commands
			Player p = (Player) sender;

			if (cmd.getName().equalsIgnoreCase("instruments")) {
				if (args.length == 0) {
					this.sendUsageMessage(p);
					return true;
				}

				if (args[0].equalsIgnoreCase("list")) {
					if (instance.getConfig().getBoolean("settings.instruments.permissions")
							&& !p.hasPermission("instruments.list"))
						return false;

					String instrumentString = "";
					for (InstrumentType instrumentType : InstrumentType.values()) {
						instrumentString += instrumentType.getName().replace(" ", "_") + ", ";
					}

					instrumentString += "ALL";

					p.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "지원하는 악기:");
					p.sendMessage(ChatColor.GREEN + instrumentString);
					return true;
				}

				if (args[0].equalsIgnoreCase("reload")) {
					if (!p.hasPermission("instruments.reload")) {return false;}

					try {
						instance.getConfig().load(instance.getConfigFile());
					} catch (Exception e) {
						e.printStackTrace();
					}

					p.sendMessage("§a성공적으로 콘피그를 리로드했습니다");
					return true;
				}

				if (args.length == 3 && args[0].equalsIgnoreCase("give")) { //TODO: re-used code cuz I'm lazy, might want to clean it up
					if (instance.getConfig().getBoolean("settings.instruments.permissions")
							&& !p.hasPermission("instruments.give"))
						return false;

					String selectedInstrument = args[2];
					String playerName = args[1];

					Player givePlayer = Bukkit.getPlayer(playerName);

					if (givePlayer == null) {
						p.sendMessage(ChatColor.RED + playerName + "님은 현재 온라인이 아니십니다");
						return true;
					}

					if (selectedInstrument.equalsIgnoreCase("all")) {
						for (InstrumentType instrumentType : InstrumentType.values()) {
							givePlayer.getInventory().addItem(instrumentType.getItemStack());
						}
						return true;
					}

					InstrumentType instrumentType = InstrumentType.getInstrumentTypeByName(selectedInstrument);

					if (instrumentType == null) {
						p.sendMessage(ChatColor.RED +selectedInstrument + "(이)라는 악기를 찾을 수 없습니다");
						p.sendMessage(ChatColor.RED + "사용 가능한 악기 목록을 보실려면 '/악기 목록'을 입력해주세요");
						return true;
					}

					givePlayer.getInventory().addItem(instrumentType.getItemStack());
					return true;
				}

				this.sendUsageMessage(p);
				return true;
			}
			return false;
		} else {
			//any sender command
			if (cmd.getName().equalsIgnoreCase("instruments")) {
				if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
					if (instance.getConfig().getBoolean("settings.instruments.permissions")
							&& !sender.hasPermission("instruments.give"))
						return false;

					String selectedInstrument = args[2];
					String playerName = args[1];

					Player givePlayer = Bukkit.getPlayer(playerName);

					if (givePlayer == null) {
						sender.sendMessage(ChatColor.RED+ playerName + "님은 현재 온라인이 아니십니다");
						return true;
					}

					if (selectedInstrument.equalsIgnoreCase("all")) {
						for (InstrumentType instrumentType : InstrumentType.values()) {
							givePlayer.getInventory().addItem(instrumentType.getItemStack());
						}
						return true;
					}

					InstrumentType instrumentType = InstrumentType.getInstrumentTypeByKey(selectedInstrument);

					if (instrumentType == null) {
						sender.sendMessage(ChatColor.RED  + selectedInstrument + "(이)라는 악기를 찾을 수 없습니다");
						sender.sendMessage(ChatColor.RED + "악기 목록을 확인 하시고 싶으시면 '/악기 목록'을 입력해주세요.");
						return true;
					}

					givePlayer.getInventory().addItem(instrumentType.getItemStack());
					return true;
				}
			}
		}
		return false;
	}

	private void sendUsageMessage(Player player) {
		player.sendMessage(
				ChatColor.RED + "" + ChatColor.BOLD + "Instruments-Custom v" + instance.getDescription().getVersion());
		player.sendMessage(ChatColor.RED + "/instruments give [player] [instrument] §7- 플레이어에게 악기를 지급합니다");
		player.sendMessage(ChatColor.RED + "/instruments list §7- 악기 목록을 확인합니다/");
		player.sendMessage("§c/instruments reload §7- 콘피그를 리로드합니다.");
	}

}
