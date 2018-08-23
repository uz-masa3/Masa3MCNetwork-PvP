package net.masa3mc.pvp2.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import net.masa3mc.pvp2.GameManager;
import net.masa3mc.pvp2.Main;
import net.masa3mc.pvp2.GameManager.GameType;

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

		}
	}

	@EventHandler
	public void Place(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (GameManager.ingame && GameManager.game.equals(GameType.SW)) {
			int x = event.getBlock().getLocation().getBlockX();
			int y = event.getBlock().getLocation().getBlockY();
			int z = event.getBlock().getLocation().getBlockZ();

		}
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	private void b(String str) {
		Bukkit.broadcastMessage(str);
	}
}
