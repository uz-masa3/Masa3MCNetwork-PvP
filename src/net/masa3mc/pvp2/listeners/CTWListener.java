package net.masa3mc.pvp2.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import net.masa3mc.pvp2.utils.SidebarUtils;
import static net.masa3mc.pvp2.GameManager.*;

public class CTWListener implements Listener {

	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		ItemStack stack = event.getItemDrop().getItemStack();
		Player player = event.getPlayer();
		if (stack.getType().equals(Material.WOOL)) {
			if (redFlagPlayer.contains(player) && stack.getDurability() == (short) 14) {
				if (!player.getInventory().contains(stack)) {
					redFlagPlayer.remove(player);
					if (redFlagPlayer.size() == 0) {
						SidebarUtils.SidebarFlag("blue", false);
					}
					b(c("&7" + player.getName() + "が&c赤チーム&7の羊毛を落としました"));
				}
			} else if (blueFlagPlayer.contains(player) && stack.getDurability() == (short) 11) {
				if (!player.getInventory().contains(stack)) {
					blueFlagPlayer.remove(player);
					if (blueFlagPlayer.size() == 0) {
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
		if (redFlagPlayer.contains(player)) {
			b(c("&7" + player.getName() + "が&c赤チーム&7の羊毛を落としました"));
			redFlagPlayer.remove(player);
		}
		if (blueFlagPlayer.contains(player)) {
			b(c("&7" + player.getName() + "が&9青チーム&7の羊毛を落としました"));
			blueFlagPlayer.remove(player);
		}
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	private void b(String str) {
		Bukkit.broadcastMessage(str);
	}
}
