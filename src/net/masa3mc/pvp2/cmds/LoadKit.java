package net.masa3mc.pvp2.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.masa3mc.pvp2.utils.KitUtils;

public class LoadKit implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("loadkit")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					KitUtils.kitMenu((Player) sender);
				} else {
					sender.sendMessage(color("&5/loadkit [kit] [team] [player]"));
				}
			} else if (args.length == 2) {
				if (sender instanceof Player) {
					KitUtils.kit((Player) sender, args[1], args[0]);
				} else {
					sender.sendMessage(color("&5/loadkit [kit] [team] [player]"));
				}
			} else if (args.length == 3) {
				if (Bukkit.getPlayer(args[2]) != null) {
					KitUtils.kit(Bukkit.getPlayer(args[2]), args[1], args[0]);
				} else {
					sender.sendMessage(color("&5プレイヤーが存在しません"));
				}
			} else {
				sender.sendMessage(color("&5/loadkit [kit] [team] [player]"));
			}
		}
		return true;
	}

	private String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

}
