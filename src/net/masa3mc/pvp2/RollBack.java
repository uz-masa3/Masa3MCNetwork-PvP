package net.masa3mc.pvp2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

public class RollBack {

	private static LinkedList<String> lists = new LinkedList<>();
	private static HashMap<String, List<String>> saves = new HashMap<>();

	@SuppressWarnings("deprecation")
	public static String save(Location l1, Location l2, String name, String filename) {
		if (!l1.getWorld().getName().equals(l2.getWorld().getName())) {
			return "";
		}
		// max
		int mxx = Math.max(l1.getBlockX(), l2.getBlockX());
		int mxy = Math.max(l1.getBlockY(), l2.getBlockY());
		int mxz = Math.max(l1.getBlockZ(), l2.getBlockZ());
		// min
		int mnx = Math.min(l1.getBlockX(), l2.getBlockX());
		int mny = Math.min(l1.getBlockY(), l2.getBlockY());
		int mnz = Math.min(l1.getBlockZ(), l2.getBlockZ());
		World w = l1.getWorld();
		List<String> datas = new ArrayList<>();
		for (int x = mnx; x <= mxx; x++) {
			for (int y = mny; y <= mxy; y++) {
				for (int z = mnz; z <= mxz; z++) {
					StringBuilder sb = new StringBuilder();// Location@Material@Data
					sb.append(w.getName()).append(",");
					sb.append(x + "").append(",");
					sb.append(y + "").append(",");
					sb.append(z + "");
					sb.append("@");
					Block b = new Location(w, x, y, z).getBlock();
					sb.append(b.getType().name());
					sb.append("@");
					sb.append(b.getData());
					datas.add(sb.toString());
				}
			}
		}
		if (filename != null && !filename.isEmpty()) {
			File file = new File(Main.getInstance().getDataFolder() + "/rollback/" + filename);
			YamlConfiguration savey = YamlConfiguration.loadConfiguration(file);
			savey.set("data", datas);
			try {
				savey.save(file);
			} catch (IOException e) {
				Bukkit.getLogger().warning("RollbackData can't saved (IOException)");
				e.printStackTrace();
			}
		}
		if (name != null && !name.isEmpty()) {
			saves.put(name, datas);
			lists.add(name);
			return name;
		}
		String uuids = UUID.randomUUID().toString();
		saves.put(uuids, datas);
		lists.add(uuids);
		return uuids;
	}

	@SuppressWarnings("deprecation")
	public static void load(String name) {
		List<String> datas = saves.get(name);
		if (datas == null) {
			Logger l = Bukkit.getLogger();
			l.warning("-----------------------------------");
			l.warning("Rollback[" + name + "] is not found.");
			l.warning("-----------------------------------");
			return;
		}
		for (String s : datas) {
			String[] split = s.split("@");
			Location l = parsel(split[0]);
			Block b = l.getBlock();
			b.setType(Material.valueOf(split[1]));
			b.setData((byte) i(split[2]));
		}
	}

	public static String loadData() {
		File file = new File(Main.getInstance().getDataFolder(), "rollback");
		if (!file.exists()) {
			file.mkdir();
			return "";
		}
		lists.clear();
		StringBuilder names = new StringBuilder();
		for (File files : file.listFiles()) {
			if (files.getName().endsWith(".yml")) {
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(files);
				names.append(files.getName() + "," + names.toString());
				saves.put(files.getName(), yml.getStringList("data"));
				lists.add(files.getName());
			}
		}
		return names.toString();
	}

	public static LinkedList<String> getList() {
		return lists;
	}

	private static Location parsel(String location) {
		String[] s = location.split(",");
		Location l = new Location(Bukkit.getWorld(s[0]), i(s[1]), i(s[2]), i(s[3]));
		return l;
	}

	private static int i(String s) {
		return Integer.parseInt(s);
	}

}