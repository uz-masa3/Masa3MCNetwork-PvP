package net.masa3mc.pvp2.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.masa3mc.pvp2.utils.KitUtils;

public class SetKit implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setkit")) {
			if (sender instanceof Player) {
				if (args.length == 2) {
					KitUtils.saveKit((Player) sender, args[1], args[0]);
					sender.sendMessage(color("&5Saved"));
				} else {
					sender.sendMessage(color("&5/setkit [kit] [team]"));
				}
			} else {
				sender.sendMessage(color("&5コンソールからは実行できません"));
			}
		}
		return true;
	}

	private String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
