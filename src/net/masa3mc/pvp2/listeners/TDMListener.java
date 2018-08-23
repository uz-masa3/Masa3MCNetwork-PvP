package net.masa3mc.pvp2.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import net.masa3mc.pvp2.GameManager;
import net.masa3mc.pvp2.utils.SidebarUtils;
import net.masa3mc.pvp2.GameManager.GameType;

public class TDMListener implements Listener {
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void kill(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			if (GameManager.ingame && GameManager.game.equals(GameType.TDM)) {
				Player p = (Player) event.getEntity();
				if (p.getKiller() instanceof Player) {
					if (GameManager.TDMRed.hasPlayer(p)) {
						GameManager.TDMBlue_Score++;
					} else if (GameManager.TDMBlue.hasPlayer(p)) {
						GameManager.TDMRed_Score++;
					}
				} else {
					if (GameManager.TDMRed.hasPlayer(p)) {
						GameManager.TDMRed_Score--;
					} else if (GameManager.TDMBlue.hasPlayer(p)) {
						GameManager.TDMBlue_Score--;
					}
				}
				SidebarUtils.SidebarTDMScore("blue", GameManager.TDMBlue_Score);
				SidebarUtils.SidebarTDMScore("red", GameManager.TDMRed_Score);
			}
		}
	}

}
