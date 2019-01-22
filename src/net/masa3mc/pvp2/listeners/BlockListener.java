package net.masa3mc.pvp2.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.masa3mc.pvp2.Main;
import net.masa3mc.pvp2.GameManager.GameTeam;
import net.masa3mc.pvp2.GameManager.GameType;
import net.masa3mc.pvp2.utils.ChestUtils;
import net.masa3mc.pvp2.utils.PlayerUtils;
import net.masa3mc.pvp2.utils.PointUtils;
import net.masa3mc.pvp2.utils.SidebarUtils;

import static net.masa3mc.pvp2.GameManager.*;

import java.util.UUID;

public class BlockListener implements Listener {

	private final Main main = Main.getInstance();

	// TODO
	// -TeamBaseの書き方を、範囲を指定したStringListにする(どちらでも)
	// -全てのマップの最下層に岩盤の設置
	//
	// -羊毛の色で判断しているので、他のデータで判断するように(Metadata)
	// -Object場所から取得した羊毛を別の場所に設置した後にそれを取った処理(Metadata)
	// -羊毛の数を2個以上に対応するために
	// --- 羊毛のObjectとPutをW,X,Y,ZのStringListに
	// --- 獲得済みObjectの個数を保持・個数に達して勝利の処理(将来的に)

	@SuppressWarnings("deprecation")
	@EventHandler
	public void blockbreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Location l = event.getBlock().getLocation();
		String p = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
		if (ingame) {
			if (a1()) {
				if (isEnemyBase(GameTeam.RED, gamenumber, p) || isEnemyBase(GameTeam.BLUE, gamenumber, p)) {
					player.sendMessage(c("&c拠点のブロックを破壊することはできません。"));
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
			if (!game.equals(GameType.CTW))
				return;
			// CTWの羊毛破壊処理
			if (CTWRed.hasPlayer(player)) {
				if (main.getConfig().getStringList("Arena" + gamenumber + ".object.blue").contains(p)) {
					event.setCancelled(true);
					player.getInventory().addItem(new ItemStack(Material.WOOL, 1, (short) 11));
					player.updateInventory();
					if (!blueFlagPlayer.contains(player)) {
						blueFlagPlayer.add(player);
						player.sendMessage(c("&6羊毛を取得しました。自陣の羊毛置き場に設置することで勝利します。"));
						SidebarUtils.SidebarFlag("red", true);
						b(c("&6" + player.getName() + "が&9青チーム&6の羊毛を奪取しました"));
					} else {
						player.sendMessage(c("&c既に羊毛を持っています"));
					}
				}
			} else if (CTWBlue.hasPlayer(player)) {
				if (main.getConfig().getStringList("Arena" + gamenumber + ".object.red").contains(p)) {
					event.setCancelled(true);
					player.getInventory().addItem(new ItemStack(Material.WOOL, 1, (short) 14));
					player.updateInventory();
					if (!redFlagPlayer.contains(player)) {
						redFlagPlayer.add(player);
						player.sendMessage(c("&6羊毛を取得しました。自陣の羊毛置き場に設置することで勝利します。"));
						SidebarUtils.SidebarFlag("blue", true);
						b(c("&6" + player.getName() + "が&c赤チーム&6の羊毛を奪取しました"));
					} else {
						player.sendMessage(c("&c既に羊毛を持っています"));
					}
				}
			}
		} else {
			if (!player.isOp()) {
				event.setCancelled(true);
				return;
			}
			if (player.getGameMode().equals(GameMode.CREATIVE)) {
				if (player.getItemInHand() == null) {
					return;
				}
				if (player.getItemInHand().getType().equals(Material.ARROW)) {
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
						event.setCancelled(true);
					}
				}
			} else {
				PlayerUtils.sendActionBarMessage(player, c("&cCreativeモード以外での設置・破壊はできません"));
				event.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void blockplace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Location l = event.getBlock().getLocation();
		String p = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
		if (ingame) {
			if (a1()) {
				if (isEnemyBase(GameTeam.RED, gamenumber, p) || isEnemyBase(GameTeam.BLUE, gamenumber, p)) {
					player.sendMessage(c("&c拠点にブロックを設置することはできません。"));
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
			// CTWの羊毛設置処理
			if (game.equals(GameType.CTW) && event.getBlock().getType().equals(Material.WOOL)) {
				ItemStack item = event.getItemInHand();
				FileConfiguration conf = main.getConfig();
				if (CTWBlue.hasPlayer(player) && item.getDurability() == (short) 14) {
					if (conf.getStringList("Arena" + gamenumber + ".put.red").contains(p)) {
						GameEnd(GameTeam.BLUE);
						UUID uuid = player.getUniqueId();
						PointUtils.setPoint(uuid, PointUtils.getPoint(uuid) + 1);
						new BukkitRunnable() {
							public void run() {
								event.getBlock().getLocation().getBlock().setType(Material.AIR);
							}
						}.runTaskLater(main, 18 * 20);
					}
				} else if (CTWRed.hasPlayer(player) && item.getDurability() == (short) 11) {
					if (conf.getStringList("Arena" + gamenumber + ".put.blue").contains(p)) {
						GameEnd(GameTeam.RED);
						UUID uuid = player.getUniqueId();
						PointUtils.setPoint(uuid, PointUtils.getPoint(uuid) + 1);
						new BukkitRunnable() {
							public void run() {
								event.getBlock().getLocation().getBlock().setType(Material.AIR);
							}
						}.runTaskLater(main, 18 * 20);
					}
				} else {
					event.setCancelled(true);
				}
			}
		} else {
			if (!player.isOp()) {
				event.setCancelled(true);
				return;
			}
			if (!player.getGameMode().equals(GameMode.CREATIVE)) {
				PlayerUtils.sendActionBarMessage(player, c("&cCreativeモード以外での設置・破壊はできません"));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void bucketfill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		if (ingame) {
			if (a1())
				return;
			event.setCancelled(true);
			Block b = event.getBlockClicked();
			if (b != null) {
				if (b.getType().equals(Material.WATER)) {
					event.getPlayer().sendMessage(c("&c水を汲むことはできません"));
				} else if (b.getType().equals(Material.LAVA)) {
					event.getPlayer().sendMessage(c("&c溶岩を汲むことはできません"));
				}
			}
		} else {
			if (!player.isOp()) {
				event.setCancelled(true);
				return;
			}
			if (!player.getGameMode().equals(GameMode.CREATIVE)) {
				PlayerUtils.sendActionBarMessage(player, c("&cCreativeモード以外での設置・破壊はできません"));
				event.setCancelled(true);
			}
		}
	}

	// TODO
	// Arenaの設定に設置破壊が可能と書き込まれていたら無視する
	private boolean a1() {
		return true;
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	private void b(String str) {
		Bukkit.broadcastMessage(str);
	}
}
