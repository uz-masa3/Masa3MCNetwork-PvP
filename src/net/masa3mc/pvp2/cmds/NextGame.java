package net.masa3mc.pvp2.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.masa3mc.pvp2.GameManager;

public class NextGame implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("nextgame")) {
			if (args.length == 1) {
				try {
					if (GameManager.nextcountdown) {
						sender.sendMessage(color("&5次のゲームの準備中は実行できません"));
					} else {
						Integer integer = Integer.parseInt(args[0]);
						sender.sendMessage(color("&5次のゲームをNo" + integer + "に設定しました"));
						if (GameManager.ingame)
							integer -= 1;
						GameManager.gamenumber = integer;
						GameManager.isSelectNumber = true;
					}
				} catch (NumberFormatException e) {
					sender.sendMessage(color("&5数字を指定してください"));
				}
			} else {
				sender.sendMessage(color("&5/nextgame [number]"));
			}
		}
		return true;
	}

	private String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
