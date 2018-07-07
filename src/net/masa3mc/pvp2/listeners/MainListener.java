package net.masa3mc.pvp2.listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.masa3mc.pvp2.GameManager;
import net.masa3mc.pvp2.Main;
import net.masa3mc.pvp2.utils.ChestUtils;
import net.masa3mc.pvp2.utils.KitUtils;
import net.masa3mc.pvp2.utils.PlayerUtils;
import net.masa3mc.pvp2.utils.PointUtils;
import net.masa3mc.pvp2.utils.SidebarUtils;
import net.milkbowl.vault.economy.EconomyResponse;

public class MainListener implements Listener {

	private List<String> inbase = new ArrayList<String>();

	private Main main = null;

	public MainListener(Main main) {
		this.main = main;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void bbreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		Location loc = event.getBlock().getLocation();
		String w = loc.getWorld().getName();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		Block b = event.getBlock();
		String pos = w + "," + x + "," + y + "," + z;
		int ticks = GameManager.canbreaks.getInt("Arena" + GameManager.gamenumber + ".canbreaks." + pos);
		if (ticks > 0) {
			if (!GameManager.breaking.containsKey(loc)) {
				event.setCancelled(true);
				player.getInventory().addItem(new ItemStack(b.getType()));
				player.updateInventory();
				GameManager.breaking.put(loc, b.getType());
				if (ticks >= 20) {
					new BukkitRunnable() {
						public void run() {
							loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.OBSIDIAN.getId());
							loc.getWorld().playSound(loc, Sound.DIG_STONE, 1, 1);
							b.setType(Material.OBSIDIAN);
							cancel();
						}
					}.runTaskLater(main, 3);
				}
				new BukkitRunnable() {
					public void run() {
						b.setType(GameManager.breaking.get(loc));
						loc.getWorld().playEffect(loc, Effect.STEP_SOUND, b.getTypeId());
						loc.getWorld().playSound(loc, Sound.DIG_STONE, 1, 1);
						GameManager.breaking.remove(loc);
						cancel();
					}
				}.runTaskLater(main, ticks);
			} else {
				player.sendMessage(c("&c再設置されるまでお待ちください"));
				event.setCancelled(true);
			}
		} else {
			player.sendMessage(c("&cそこは壊せません"));
			event.setCancelled(true);
		}

		if (player.isOp() && player.getGameMode().equals(GameMode.CREATIVE)) {
			if (player.getItemInHand() == null) {
				return;
			}
			if (player.getItemInHand().getType().equals(Material.ARROW) && !GameManager.ingame) {
				Block block = event.getBlock();
				event.setCancelled(true);
				if (block.getType().equals(Material.CHEST)) {
					if (ChestUtils.saveChest((Chest) block.getState())) {
						player.sendMessage(c("&6チェストの登録が正常に完了しました"));
					} else {
						player.sendMessage(c("&cチェストの登録中にエラーが発生しました"));
					}
				} else {
					player.sendMessage(c("&cチェスト以外は登録できません"));
				}
			}
		}
	}

	@EventHandler
	public void bucketfill(PlayerBucketFillEvent event) {
		event.setCancelled(true);
		Block b = event.getBlockClicked();
		if (b != null) {
			if (b.getType().equals(Material.WATER)) {
				event.getPlayer().sendMessage(c("&c水を汲むことはできません"));
			} else if (b.getType().equals(Material.LAVA)) {
				event.getPlayer().sendMessage(c("&c溶岩を汲むことはできません"));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(null);
		GameManager.CTWRed.removePlayer(player);
		GameManager.CTWBlue.removePlayer(player);
		GameManager.TDMRed.removePlayer(player);
		GameManager.TDMBlue.removePlayer(player);
		Location spawn = player.getWorld().getSpawnLocation();
		player.teleport(spawn);
		player.setBedSpawnLocation(spawn);
		if (GameManager.playerkit.containsKey(player.getUniqueId())) {
			GameManager.playerkit.remove(player.getUniqueId());
		}
		if (!GameManager.ingame) {
			SidebarUtils.SidebarUnregist();
		}

		new BukkitRunnable() {
			public void run() {
				GameManager.addPlayer(player);
			}
		}.runTaskLater(main, 20);
		new Thread() {
			public void run() {
				YamlConfiguration y = KitUtils.playerKitData(player);
				if (!y.contains("soldier")) {
					y.set("soldier", true);
				}
				if (!y.contains("archer")) {
					y.set("archer", true);
				}
				try {
					y.save(KitUtils.playerKitDataFile(player));
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.stop();
			}
		}.start();
	}

	@SuppressWarnings({ "deprecation", "unlikely-arg-type" })
	@EventHandler
	public void quit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		event.setQuitMessage(null);
		GameManager.CTWRed.removePlayer(player);
		GameManager.CTWBlue.removePlayer(player);
		GameManager.TDMRed.removePlayer(player);
		GameManager.TDMBlue.removePlayer(player);
		GameManager.entried.remove(player);
		event.getPlayer().getInventory().clear();
		event.getPlayer().updateInventory();
	}

	@EventHandler
	public void damage(EntityDamageEvent event) {
		if (!GameManager.ingame) {
			event.setCancelled(true);
			if (event.getCause() == DamageCause.VOID) {
				Player player = (Player) event.getEntity();
				player.teleport(player.getWorld().getSpawnLocation());
			}
		}
	}

	@EventHandler
	public void hit(ProjectileHitEvent event) {
		GameManager.delentities.add(event.getEntity());
	}

	@EventHandler
	public void FoodLevelChange(FoodLevelChangeEvent event) {
		if (!GameManager.ingame) {
			try {
				Player player = (Player) event.getEntity();
				player.setFoodLevel(20);
			} catch (Exception e) {

			}
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getClickedBlock() != null) {
			Material type = event.getClickedBlock().getType();
			if (type.equals(Material.CHEST) || type.equals(Material.FURNACE)) {
				Location l = event.getClickedBlock().getLocation();
				YamlConfiguration yml = GameManager.chests;
				String pos = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
				if (yml.getString(pos) == null) {
					player.sendMessage(c("&cその" + (type.name().equals("CHEST") ? "チェスト" : "かまど") + "は開けれません"));
					event.setCancelled(true);
				}
			}
		}
		if (player.getItemInHand().hasItemMeta() && event.getAction().name().contains("RIGHT")) {
			if (player.getItemInHand().getType().equals(Material.DIAMOND_SWORD)) {
				event.setCancelled(true);
				KitUtils.kitMenu(event.getPlayer());
			} else if (player.getItemInHand().getType().equals(Material.CHEST)) {
				event.setCancelled(true);
				KitUtils.buyMenu(player);
			}
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		if (GameManager.ingame) {
			GameManager.delentities.add((Entity) event.getItemDrop());
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void kitSelect(InventoryClickEvent event) {
		if (event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
			Inventory eventinv = event.getClickedInventory();
			String invname = eventinv.getName();
			Player p = (Player) event.getWhoClicked();
			int r = event.getRawSlot();
			if (invname.equals("SelectKit")) {
				event.setCancelled(true);
				p.closeInventory();
				YamlConfiguration gui = YamlConfiguration
						.loadConfiguration(new File(main.getDataFolder() + "/gui/kit.yml"));
				String kit = gui.getString("item" + r + ".kit");
				if (KitUtils.hasKit(p, kit)) {
					p.sendMessage(c("&a" + kit + "&6を選択しました"));
					GameManager.playerkit.put(p.getUniqueId(), kit);
					if (GameManager.ingame) {
						GameManager.addGamePlayer(p);
					}
				} else {
					p.sendMessage(c("&6" + kit + "&cは所持していません"));
				}
			} else if (invname.equals("BuyKit")) {
				event.setCancelled(true);
				p.closeInventory();
				YamlConfiguration kitY = YamlConfiguration
						.loadConfiguration(new File(main.getDataFolder() + "/gui/kit.yml"));
				String kit = kitY.getString("item" + r + ".kit");
				if (KitUtils.hasKit(p, kit)) {
					p.sendMessage(c("&6" + kit + "&cは既に持っています"));
				} else {
					YamlConfiguration confirm = YamlConfiguration
							.loadConfiguration(new File(main.getDataFolder() + "/gui/confirm.yml"));
					Inventory inv = Bukkit.createInventory(p, 27, "BuyConfirm");
					for (String str : Arrays.asList("confirmyes", "confirmno")) {
						ItemStack item = new ItemStack(
								Material.valueOf(confirm.getString(str + ".type").toUpperCase()));
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(c(confirm.getString(str + ".name")));
						item.setItemMeta(meta);
						confirm.getIntegerList(str + ".location").forEach(i -> {
							inv.setItem(i, item);
						});
					}
					ItemStack kitI = new ItemStack(
							Material.valueOf(kitY.getString("item" + r + ".type").toUpperCase()));
					ItemMeta kitM = kitI.getItemMeta();
					kitM.setDisplayName(c(kitY.getString("item" + r + ".name") + "(ID" + r + ")"));
					kitI.setItemMeta(kitM);
					inv.setItem(confirm.getInt("kit.location"), kitI);
					p.openInventory(inv);
				}
			} else if (invname.equals("BuyConfirm")) {
				event.setCancelled(true);
				YamlConfiguration confirm = YamlConfiguration
						.loadConfiguration(new File(main.getDataFolder() + "/gui/confirm.yml"));
				Material item = event.getCurrentItem().getType();
				if (item.equals(Material.valueOf(confirm.getString("confirmyes.type").toUpperCase()))) {
					YamlConfiguration kitY = YamlConfiguration
							.loadConfiguration(new File(main.getDataFolder() + "/gui/kit.yml"));
					String kit = eventinv.getItem(confirm.getInt("kit.location")).getItemMeta().getDisplayName();
					kit = kit.substring(kit.indexOf("ID"), kit.length()).replace("ID", "").replace(")", "");
					String kitname = kitY.getString("item" + kit + ".kit");
					switch (KitUtils.buyKit(p, kitname)) {
					case 0:
						p.sendMessage(c("&6" + kitname + "を購入しました"));
						break;
					case 1:
						p.sendMessage(c("&cお金が足りません"));
						break;
					case 2:
						p.sendMessage(c("&6" + kit + "&cは既に持っています"));
						break;
					case 3:
						p.sendMessage(c("&cファイルのセーブ中にエラーが発生しました。"));
						break;
					case 4:
						p.sendMessage(c("&cお金の処理中にエラーが発生しました。"));
						break;
					case 5:
						p.sendMessage(c("&c引数が無効です"));
						break;
					default:
						break;
					}
					p.closeInventory();
				} else if (item.equals(Material.valueOf(confirm.getString("confirmno.type").toUpperCase()))) {
					p.sendMessage(c("&c購入をキャンセルしました。"));
					p.closeInventory();
				}
			}
		}
	}

	@EventHandler
	public void creatureSpawn(EntitySpawnEvent event) {
		if (event.getEntityType().equals(EntityType.DROPPED_ITEM)) {
			GameManager.delentities.add(event.getEntity());
		} else if (event.getEntityType().equals(EntityType.BOAT)) {
			GameManager.delentities.add(event.getEntity());
		} else {
			event.getEntity().remove();
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void enter_enemy_base(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (GameManager.ingame) {
			YamlConfiguration yml = GameManager.bases;
			Location l = player.getLocation();
			String pos = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
			if (GameManager.CTWRed.hasPlayer(player) || GameManager.TDMRed.hasPlayer(player)) {
				List<String> bluebase = yml.getStringList("Arena" + GameManager.gamenumber + ".base.blue");
				if (bluebase.contains(pos)) {
					player.teleport(l);
					if (!inbase.contains(player.getName())) {
						inbase.add(player.getName());
						player.sendMessage(c("&c敵チームの拠点には入れません"));
					}
				} else {
					inbase.remove(player.getName());
				}
			} else if (GameManager.CTWBlue.hasPlayer(player) || GameManager.TDMBlue.hasPlayer(player)) {
				List<String> redbase = yml.getStringList("Arena" + GameManager.gamenumber + ".base.red");
				if (redbase.contains(pos)) {
					player.teleport(l);
					if (!inbase.contains(player.getName())) {
						inbase.add(player.getName());
						player.sendMessage(c("&c敵チームの拠点には入れません"));
					}
				}
			}
		}
	}

	@EventHandler
	public void death(PlayerDeathEvent event) {
		Player p = event.getEntity();
		event.getDrops().clear();
		if (p.getKiller() instanceof Player) {
			Player kill = p.getKiller();
			List<String> reward = main.getConfig().getStringList("Arena" + GameManager.gamenumber + ".KillRewards");
			if (reward != null && !reward.isEmpty()) {
				for (String m : reward) {
					kill.getInventory().addItem(new ItemStack(Material.valueOf(m)));
				}
			}
			event.setDeathMessage(c("&7" + p.getName() + "は" + kill.getName() + "に殺害された"));
			if (kill != p) {
				EconomyResponse er = Main.getEconomy().depositPlayer(p.getKiller(), 30.0);
				if (er.transactionSuccess()) {
					PlayerUtils.sendActionBarMessage(p.getKiller(), c("&a30Msを手に入れた"));
				}
				PointUtils.setPoint(kill.getUniqueId(), PointUtils.getPoint(kill.getUniqueId()) + 1);
			}
		} else if (p.getLastDamageCause().getCause() == DamageCause.VOID) {
			event.setDeathMessage(c("&7" + p.getName() + "は奈落に落ちた"));
		} else if (p.getLastDamageCause().getCause() == DamageCause.FALL) {
			event.setDeathMessage(c("&7" + p.getName() + "は高所から落ちた"));
		}
		EconomyResponse er = Main.getEconomy().depositPlayer(p, -15.0);
		if (er.transactionSuccess()) {
			PlayerUtils.sendActionBarMessage(p, c("&c15Msを失った"));
		}
		if (GameManager.redFlagPlayer.contains(p)) {
			b(c("&7" + p.getName() + "が&c赤チーム&7の羊毛を落としました"));
			GameManager.redFlagPlayer.remove(p);
			if (GameManager.redFlagPlayer.size() == 0) {
				SidebarUtils.SidebarFlag("blue", false);
			}
		}
		if (GameManager.blueFlagPlayer.contains(p)) {
			b(c("&7" + p.getName() + "が&9青チーム&7の羊毛を落としました"));
			GameManager.blueFlagPlayer.remove(p);
			if (GameManager.blueFlagPlayer.size() == 0) {
				SidebarUtils.SidebarFlag("red", false);
			}
		}
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				if (GameManager.CTWRed.hasPlayer(p) || GameManager.TDMRed.hasPlayer(p)) {
					if (GameManager.playerkit.containsKey(p.getUniqueId())) {
						KitUtils.kit(p, "red", GameManager.playerkit.get(p.getUniqueId()));
					} else {
						KitUtils.kit(p, "red",
								main.getConfig().getString("Arena" + GameManager.gamenumber + ".defaultKit"));
					}
				} else if (GameManager.CTWBlue.hasPlayer(p) || GameManager.TDMBlue.hasPlayer(p)) {
					if (GameManager.playerkit.containsKey(p.getUniqueId())) {
						KitUtils.kit(p, "blue", GameManager.playerkit.get(p.getUniqueId()));
					} else {
						KitUtils.kit(p, "blue",
								main.getConfig().getString("Arena" + GameManager.gamenumber + ".defaultKit"));
					}
				}
				cancel();
			}
		}.runTaskLater(main, 20);
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	private void b(String str) {
		Bukkit.broadcastMessage(str);
	}

}
