package net.masa3mc.pvp2.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import net.masa3mc.pvp2.Main;

public class ChestUtils {

	private static final Main main = Main.getInstance();

	public static void restoreAllChests() {
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/chests.yml"));
		List<String> list = yml.getStringList("locations");
		if (list == null || list.isEmpty()) {
			return;
		}
		list.forEach(loc -> {
			String[] split = loc.split(",");
			World w = Bukkit.getWorld(split[0]);
			int x = Integer.parseInt(split[1]);
			int y = Integer.parseInt(split[2]);
			int z = Integer.parseInt(split[3]);
			Location location = new Location(w, x, y, z);
			if (location.getBlock().getType().equals(Material.CHEST)) {
				restoreChests((Chest) location.getBlock().getState());
			}
		});
	}

	public static void restoreChests(Chest chest) {
		Location l = chest.getLocation();
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/chests.yml"));
		String pos = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
		for (int i = 0; i < chest.getInventory().getSize(); i++) {
			ItemStack item = yml.getItemStack(pos + "." + i);
			if (item == null) {
				chest.getInventory().setItem(i, new ItemStack(Material.AIR));
			} else {
				chest.getInventory().setItem(i, item);
			}
		}
	}

	public static boolean saveChest(Chest chest) {
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/chests.yml"));
		Location l = chest.getLocation();
		String pos = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
		List<String> locations = yml.getStringList("locations");
		locations.add(pos);
		yml.set("locations", locations);
		for (int i = 0; i < chest.getInventory().getSize(); i++) {
			ItemStack item = chest.getInventory().getItem(i);
			if (item == null) {
				continue;
			}
			yml.set(pos + "." + i, item);
		}
		try {
			yml.save(new File(main.getDataFolder() + "/chests.yml"));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
