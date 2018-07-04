package net.masa3mc.pvp2.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.masa3mc.pvp2.utils.KitUtils;

public class KitMenu implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("kitmenu")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					KitUtils.kitMenu((Player) sender);
				} else {
					sender.sendMessage(color("&5/kitmenu [player]"));
				}
			} else if (args.length == 1) {
				if (Bukkit.getPlayer(args[0]) != null) {
					KitUtils.kitMenu(Bukkit.getPlayer(args[0]));
				} else {
					sender.sendMessage(color("&5プレイヤーが存在しません"));
				}
			} else {
				sender.sendMessage(color("&5/kitmenu [player]"));
			}
		}
		return true;
	}

	private String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
