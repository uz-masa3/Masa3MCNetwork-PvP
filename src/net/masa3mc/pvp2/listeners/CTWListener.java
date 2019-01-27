package net.masa3mc.pvp2.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import net.masa3mc.pvp2.utils.SidebarUtils;
import static net.masa3mc.pvp2.GameManager.*;

public class CTWListener implements Listener {

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack stack = event.getItemDrop().getItemStack();
		Player player = event.getPlayer();
		if (ingame && game.equals(GameType.CTW)) {
			if (stack.getType().equals(Material.WOOL)) {
				// TODO
				if (redFlagPlayer.contains(player) && stack.getDurability() == (short) 14) {
					if (!player.getInventory().contains(stack)) {
						redFlagPlayer.remove(player);
						if (redFlagPlayer.size() == 0) {
							SidebarUtils.SidebarFlag("blue", 1);
						}
						b(c("&7" + player.getName() + "が&c赤チーム&7の羊毛を落としました"));
					}
					// TODO
				} else if (blueFlagPlayer.contains(player) && stack.getDurability() == (short) 11) {
					if (!player.getInventory().contains(stack)) {
						blueFlagPlayer.remove(player);
						if (blueFlagPlayer.size() == 0) {
							SidebarUtils.SidebarFlag("red", 1);
						}
						b(c("&7" + player.getName() + "が&9青チーム&7の羊毛を落としました"));
					}
				}
			}
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem().getItemStack();
		if (ingame && game.equals(GameType.CTW)) {
			if (item.getType().equals(Material.WOOL)) {
				// TODO
				if (!redFlagPlayer.contains(player) && item.getDurability() == (short) 14) {
					redFlagPlayer.add(player);
					SidebarUtils.SidebarFlag("blue", 2);
					b(c("&7" + player.getName() + "が&c赤チーム&7の羊毛を奪取しました"));
					// TODO
				} else if (!blueFlagPlayer.contains(player) && item.getDurability() == (short) 11) {
					blueFlagPlayer.add(player);
					SidebarUtils.SidebarFlag("red", 2);
					b(c("&7" + player.getName() + "が&9青チーム&7の羊毛を奪取しました"));
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (redFlagPlayer.contains(player)) {
			b(c("&7" + player.getName() + "が&c赤チーム&7の羊毛を落としました"));
			redFlagPlayer.remove(player);
			if (redFlagPlayer.size() == 0) {
				SidebarUtils.SidebarFlag("blue", 1);
			}
		}
		if (blueFlagPlayer.contains(player)) {
			b(c("&7" + player.getName() + "が&9青チーム&7の羊毛を落としました"));
			blueFlagPlayer.remove(player);
			if (blueFlagPlayer.size() == 0) {
				SidebarUtils.SidebarFlag("red", 1);
			}
		}
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	private void b(String str) {
		Bukkit.broadcastMessage(str);
	}
}
