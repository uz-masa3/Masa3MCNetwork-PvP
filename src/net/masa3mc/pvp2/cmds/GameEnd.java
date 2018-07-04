package net.masa3mc.pvp2.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.masa3mc.pvp2.GameManager;
import net.masa3mc.pvp2.GameManager.GameTeam;

public class GameEnd implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gameend")) {
			if (GameManager.ingame) {
				GameManager.GameEnd(GameTeam.NONE);
				sender.sendMessage(color("&5ゲームの停止・再実行が完了しました"));
			} else {
				sender.sendMessage(color("&5ゲームが始まっていない可能性があります"));
			}
		}
		return true;
	}

	private String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
