package net.masa3mc.pvp2.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.masa3mc.pvp2.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class KitUtils {

	private static final Main ins = Main.getInstance();

	public static void kit(Player p, String team, String kit) {
		if (p != null) {
			if (!hasKit(p, kit)) {
				p.sendMessage(c("&6" + kit + "&cは所持していません"));
				return;
			}
			Inventory in = p.getInventory();
			in.clear();
			YamlConfiguration kitY = YamlConfiguration.loadConfiguration(kitFile(kit));
			team = team.toLowerCase();
			for (int i = 0; 35 >= i;) {
				in.setItem(i, convCannotDrop(kitY.getItemStack(team + "." + i)));
				i++;
			}
			p.getEquipment().setBoots(convCannotDrop(kitY.getItemStack(team + ".boots")));
			p.getEquipment().setLeggings(convCannotDrop(kitY.getItemStack(team + ".leggings")));
			p.getEquipment().setChestplate(convCannotDrop(kitY.getItemStack(team + ".chestplate")));
			p.getEquipment().setHelmet(convCannotDrop(kitY.getItemStack(team + ".helmet")));
			p.updateInventory();
		}
	}

	public static ItemStack convCannotDrop(ItemStack item) {
		if (item == null) {
			return null;
		}
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null || !lore.contains(c("&cドロップで削除"))) {
			List<String> list = new ArrayList<String>();
			if (lore != null) {
				list = lore;
			}
			list.add(c("&cドロップで削除"));
			meta.setLore(list);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static boolean isCannotDrop(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null || meta.getLore() == null) {
			return false;
		}
		return meta.getLore().contains(c("&cドロップで削除"));
	}

	public static void saveKit(Player p, String team, String kit) {
		YamlConfiguration kitY = YamlConfiguration.loadConfiguration(kitFile(kit));
		if (Bukkit.getPlayer(p.getName()).isOnline()) {
			Inventory in = p.getInventory();
			for (int i = 0; 35 >= i;) {
				kitY.set(team + "." + i, in.getItem(i));
				i++;
			}
			kitY.set(team + ".helmet", p.getEquipment().getHelmet());
			kitY.set(team + ".chestplate", p.getEquipment().getChestplate());
			kitY.set(team + ".leggings", p.getEquipment().getLeggings());
			kitY.set(team + ".boots", p.getEquipment().getBoots());
			try {
				kitY.save(kitFile(kit));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void kitMenu(Player p) {
		YamlConfiguration kitY = YamlConfiguration.loadConfiguration(new File(ins.getDataFolder() + "/gui/kit.yml"));
		Inventory inv = Bukkit.createInventory(p, 9, "SelectKit");
		for (int i = 0; 9 > i; i++) {
			if (!kitY.getString("item" + i + ".type").equalsIgnoreCase("AIR")) {
				ItemStack item = new ItemStack(Material.valueOf(kitY.getString("item" + i + ".type").toUpperCase()));
				ItemMeta m = item.getItemMeta();
				m.setDisplayName(c(kitY.getString("item" + i + ".name")));
				boolean has = hasKit(p, kitY.getString("item" + i + ".kit"));
				m.setLore(Arrays.asList(has ? c("&6所持しています") : c("&c購入してください")));
				item.setItemMeta(m);
				inv.setItem(i, item);
			}
		}
		p.openInventory(inv);
	}

	public static void buyMenu(Player p) {
		YamlConfiguration buy = YamlConfiguration.loadConfiguration(new File(ins.getDataFolder() + "/gui/kit.yml"));
		Inventory inv = Bukkit.createInventory(p, 9, "BuyKit");
		for (int i = 0; 9 > i; i++) {
			if (!buy.getString("item" + i + ".type").equalsIgnoreCase("AIR")) {
				ItemStack item = new ItemStack(Material.valueOf(buy.getString("item" + i + ".type").toUpperCase()));
				ItemMeta m = item.getItemMeta();
				m.setDisplayName(c(buy.getString("item" + i + ".name")));

				String price = c("&6Price: " + kitYml(buy.getString("item" + i + ".kit")).getInt("price"));
				boolean has = hasKit(p, buy.getString("item" + i + ".kit"));
				m.setLore(Arrays.asList(has ? c("&6所持しています") : c("&c購入する"), price));
				item.setItemMeta(m);
				inv.setItem(i, item);
			}
		}
		p.openInventory(inv);
	}

	public static int buyKit(Player p, String kit) {
		// return 0 - OK
		// return 1 - NotEnoughMoney
		// return 2 - Already bought
		// return 3 - IOException
		// return 4 - Transaction failure
		// return 5 - Invalid argument
		if (p == null || kit == null || kit.isEmpty()) {
			return 5;
		}
		if (hasKit(p, kit)) {
			return 2;
		}
		Economy e = Main.getEconomy();
		double balance = e.getBalance(p);
		double price = YamlConfiguration.loadConfiguration(kitFile(kit)).getDouble("price");
		if (balance < price) {
			return 1;
		}
		EconomyResponse r = e.depositPlayer(p, -price);
		if (r.transactionSuccess()) {
			YamlConfiguration data = playerKitData(p);
			data.set(kit, true);
			try {
				data.save(playerKitDataFile(p));
			} catch (IOException e1) {
				e1.printStackTrace();
				return 3;
			}
			return 0;
		}
		return 4;
	}

	public static boolean hasKit(Player p, String kit) {
		if (p == null || kit == null || kit.isEmpty())
			return false;
		return playerKitData(p).getBoolean(kit);
	}

	public static File kitFile(String kit) {
		return new File(ins.getDataFolder() + "/kit/" + kit + ".yml");
	}

	public static YamlConfiguration kitYml(String kit) {
		return YamlConfiguration.loadConfiguration(kitFile(kit));
	}

	public static YamlConfiguration playerKitData(Player p) {
		return YamlConfiguration.loadConfiguration(playerKitDataFile(p));
	}

	public static File playerKitDataFile(Player p) {
		File dir = new File(ins.getDataFolder() + "/kit/player-data/");
		File file = new File(dir, p.getUniqueId() + ".yml");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
			YamlConfiguration yml = playerKitData(p);
			yml.set("none", true);
			yml.set("soldier", true);
			yml.set("archer", true);
			try {
				yml.save(file);
			} catch (IOException e) {
			}
		}
		return file;
	}

	private static String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

}
