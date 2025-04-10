package net.cupofcode.instruments;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Utils {

	public static HashMap<Player, HashMap<Integer, ItemStack>> inventoryMap = new HashMap<>();

	private static String[] notes = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

	public static void storeInventory(Player player) {
		Inventory inventory = player.getInventory();
		HashMap<Integer, ItemStack> items = new HashMap<>();
		for (int i = 0; i < inventory.getSize(); i++) {
			if (inventory.getItem(i) != null) {
				items.put(i, inventory.getItem(i));
			}
		}

		inventoryMap.put(player, items);
	}

	public static void loadInventory(Player player) {
		if (!inventoryMap.containsKey(player))
			return;

		clearInventory(player);

		Inventory inventory = player.getInventory();
		HashMap<Integer, ItemStack> items = inventoryMap.get(player);
		for (Integer i : items.keySet()) {
			inventory.setItem(i, items.get(i));
		}

		inventoryMap.remove(player);
	}

	public static void clearInventory(Player player) {
		for (int i = 0; i < 36; i++) {
			if (player.getInventory().getItem(i) != null) {
				player.getInventory().setItem(i, new ItemStack(Material.AIR));
			}
		}
	}

	public static String[] getMajorTriad(String note) {
		int position = getIndexOfNote(note);

		return new String[] { notes[position], notes[(position + 4) % notes.length],
				notes[(position + 8) % notes.length] };
	}

	public static String[] getMinorTriad(String note) {
		int position = getIndexOfNote(note);

		return new String[] { notes[position], notes[(position + 4) % notes.length],
				notes[(position + 7) % notes.length] };
	}

	public static int getIndexOfNote(String note) {
		for (int i = 0; i < notes.length; i++) {
			if (notes[i].equals(note)) {
				return i;
			}
		}
		return -1;
	}

	public static String formatString(String key) {
		String newKey = key.toLowerCase().replaceAll("_", " ");
		newKey = StringUtils.capitalize(newKey);
		return ChatColor.RESET + newKey;
	}

	public static boolean isWorldGuard(String s) {
		if (!Instruments.getInstance().getConfig().getStringList("worldguard.place").isEmpty()) {
			for (String worldguard : Instruments.getInstance().getConfig().getStringList("worldguard.place")) {
				if (s.contains(worldguard)) {
					return true;
				}
			}
		}
		return false;
	}

	public static ApplicableRegionSet getRegions(Player player) {
		org.bukkit.Location location = player.getLocation(); // Bukkit Location
		World world = location.getWorld();

		if (world == null) return null;

		com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();

		com.sk89q.worldedit.util.Location weLocation = new com.sk89q.worldedit.util.Location(weWorld, location.getX(), location.getY(), location.getZ());

		ApplicableRegionSet regions = query.getApplicableRegions(weLocation);

		return regions;
	}

}
