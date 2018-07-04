package net.masa3mc.pvp2.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.masa3mc.pvp2.GameManager;

public class GameStart implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gamestart")) {
			if (GameManager.ingame|| GameManager.nextcountdown) {
				sender.sendMessage(color("&5現在、何らかのプロセスが走っているため実行できません"));
			} else {
				Bukkit.getOnlinePlayers().forEach(players -> {
					GameManager.CTWRed.removePlayer(players);
					GameManager.CTWBlue.removePlayer(players);
					GameManager.TDMRed.removePlayer(players);
					GameManager.TDMBlue.removePlayer(players);
					GameManager.addPlayer(players);
				});
			}
		}
		return true;
	}

	private String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
