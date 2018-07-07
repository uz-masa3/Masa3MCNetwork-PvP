package net.masa3mc.pvp2.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import net.masa3mc.pvp2.GameManager;
import net.masa3mc.pvp2.Main;
import net.masa3mc.pvp2.GameManager.GameType;
import net.masa3mc.pvp2.utils.SidebarUtils;

public class SWListener implements Listener {
	private Main main = null;

	public SWListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void Break(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (GameManager.ingame && GameManager.game.equals(GameType.SW)) {
			Location loc = event.getBlock().getLocation();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();

		} else {
			if (!player.getGameMode().equals(GameMode.CREATIVE) || !player.isOp()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void Place(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (GameManager.ingame && GameManager.game.equals(GameType.SW)) {
			int x = event.getBlock().getLocation().getBlockX();
			int y = event.getBlock().getLocation().getBlockY();
			int z = event.getBlock().getLocation().getBlockZ();

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
