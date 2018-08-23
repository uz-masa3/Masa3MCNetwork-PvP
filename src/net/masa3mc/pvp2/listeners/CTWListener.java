package net.masa3mc.pvp2.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.masa3mc.pvp2.GameManager;
import net.masa3mc.pvp2.GameManager.GameType;
import net.masa3mc.pvp2.Main;
import net.masa3mc.pvp2.utils.PointUtils;
import net.masa3mc.pvp2.utils.SidebarUtils;

public class CTWListener implements Listener {

	private Main main = null;

	public CTWListener(Main main) {
		this.main = main;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Break(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (GameManager.ingame && GameManager.game.equals(GameType.CTW)) {
			Location loc = event.getBlock().getLocation();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();

			if (event.getBlock().getType().equals(Material.WOOL)) {
				event.setCancelled(true);
				if (GameManager.CTWRed.hasPlayer(player)) {
					int number = GameManager.gamenumber;
					int x2 = main.getConfig().getInt("Arena" + number + ".object.blue.x");
					int y2 = main.getConfig().getInt("Arena" + number + ".object.blue.y");
					int z2 = main.getConfig().getInt("Arena" + number + ".object.blue.z");
					if (x - x2 == 0 && y - y2 == 0 && z - z2 == 0) {
						if (!GameManager.blueFlagPlayer.contains(player)) {
							GameManager.blueFlagPlayer.add(player);
							player.sendMessage(c("&6羊毛を取得しました。自陣の羊毛置き場に設置することで勝利します。"));
							player.getInventory().addItem(new ItemStack(Material.WOOL, 1, (short) 11));
							SidebarUtils.SidebarFlag("red", true);
							b(c("&6" + player.getName() + "が&9青チーム&6の羊毛を奪取しました"));
							player.updateInventory();
						} else {
							player.sendMessage(c("&c既に羊毛を持っています"));
							player.getInventory().addItem(new ItemStack(Material.WOOL, 1, (short) 11));
							player.updateInventory();
						}
					}
				} else if (GameManager.CTWBlue.hasPlayer(player)) {
					int number = GameManager.gamenumber;
					int x2 = main.getConfig().getInt("Arena" + number + ".object.red.x");
					int y2 = main.getConfig().getInt("Arena" + number + ".object.red.y");
					int z2 = main.getConfig().getInt("Arena" + number + ".object.red.z");
					if (x - x2 == 0 && y - y2 == 0 && z - z2 == 0) {
						if (!GameManager.redFlagPlayer.contains(player)) {
							GameManager.redFlagPlayer.add(player);
							player.sendMessage(c("&6羊毛を取得しました。自陣の羊毛置き場に設置することで勝利します。"));
							player.getInventory().addItem(new ItemStack(Material.WOOL, 1, (short) 14));
							SidebarUtils.SidebarFlag("blue", true);
							b(c("&6" + player.getName() + "が&c赤チーム&6の羊毛を奪取しました"));
							player.updateInventory();
						} else {
							player.sendMessage(c("&c既に羊毛を持っています"));
							player.getInventory().addItem(new ItemStack(Material.WOOL, 1, (short) 14));
							player.updateInventory();
						}
					}
				}
			}
		} else {
			if (!player.getGameMode().equals(GameMode.CREATIVE) || !player.isOp()) {
				event.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Place(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (GameManager.ingame && GameManager.game.equals(GameType.CTW)) {
			int x = (int) event.getBlock().getLocation().getX();
			int y = (int) event.getBlock().getLocation().getY();
			int z = (int) event.getBlock().getLocation().getZ();
			if (event.getBlock().getType().equals(Material.WOOL)) {
				ItemStack item = event.getItemInHand();
				if (GameManager.CTWBlue.hasPlayer(player) && item.getDurability() == (short) 14) {
					FileConfiguration conf = main.getConfig();
					String str = "Arena" + GameManager.gamenumber + ".put.red.";
					int x2 = conf.getInt(str + "x");
					int y2 = conf.getInt(str + "y");
					int z2 = conf.getInt(str + "z");
					if (x - x2 == 0 && y - y2 == 0 && z - z2 == 0) {
						UUID uuid = player.getUniqueId();
						PointUtils.setPoint(uuid, PointUtils.getPoint(uuid) + 1);
						GameManager.GameEnd(GameManager.GameTeam.BLUE);
						GameManager.redFlagPlayer.clear();
						GameManager.blueFlagPlayer.clear();
						new BukkitRunnable() {
							public void run() {
								event.getBlock().getLocation().getBlock().setType(Material.AIR);
							}
						}.runTaskLater(main, 18 * 20);
					} else {
						player.sendMessage(c("&cそこには置けません"));
						event.setCancelled(true);
					}
				} else if (GameManager.CTWRed.hasPlayer(player) && item.getDurability() == (short) 11) {
					FileConfiguration conf = main.getConfig();
					String str = "Arena" + GameManager.gamenumber + ".put.blue.";
					int x2 = conf.getInt(str + "x");
					int y2 = conf.getInt(str + "y");
					int z2 = conf.getInt(str + "z");
					if (x - x2 == 0 && y - y2 == 0 && z - z2 == 0) {
						UUID uuid = player.getUniqueId();
						PointUtils.setPoint(uuid, PointUtils.getPoint(uuid) + 1);
						GameManager.GameEnd(GameManager.GameTeam.RED);
						GameManager.redFlagPlayer.clear();
						GameManager.blueFlagPlayer.clear();
						new BukkitRunnable() {
							public void run() {
								event.getBlock().getLocation().getBlock().setType(Material.AIR);
							}
						}.runTaskLater(main, 18 * 20);
					} else {
						player.sendMessage(c("&cそこには置けません"));
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		} else {
			if (!player.getGameMode().equals(GameMode.CREATIVE) || !player.isOp()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		ItemStack stack = event.getItemDrop().getItemStack();
		Player player = event.getPlayer();
		if (stack.getType().equals(Material.WOOL)) {
			if (GameManager.redFlagPlayer.contains(player) && stack.getDurability() == (short) 14) {
				if (!player.getInventory().contains(stack)) {
					GameManager.redFlagPlayer.remove(player);
					if (GameManager.redFlagPlayer.size() == 0) {
						SidebarUtils.SidebarFlag("blue", false);
					}
					b(c("&7" + player.getName() + "が&c赤チーム&7の羊毛を落としました"));
				}
			} else if (GameManager.blueFlagPlayer.contains(player) && stack.getDurability() == (short) 11) {
				if (!player.getInventory().contains(stack)) {
					GameManager.blueFlagPlayer.remove(player);
					if (GameManager.blueFlagPlayer.size() == 0) {
						SidebarUtils.SidebarFlag("red", false);
					}
					b(c("&7" + player.getName() + "が&9青チーム&7の羊毛を落としました"));
				}
			}
		}
	}

	@EventHandler
	public void Quit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (GameManager.redFlagPlayer.contains(player)) {
			b(c("&7" + player.getName() + "が&c赤チーム&7の羊毛を落としました"));
			GameManager.redFlagPlayer.remove(player);
		}
		if (GameManager.blueFlagPlayer.contains(player)) {
			b(c("&7" + player.getName() + "が&9青チーム&7の羊毛を落としました"));
			GameManager.blueFlagPlayer.remove(player);
		}
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	private void b(String str) {
		Bukkit.broadcastMessage(str);
	}
}
