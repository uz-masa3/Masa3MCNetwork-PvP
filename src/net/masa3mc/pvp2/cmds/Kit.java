package net.masa3mc.pvp2.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.masa3mc.pvp2.utils.KitUtils;

public class Kit implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("kit")) {
			if (args.length == 0) {// /kit
				if (sender instanceof Player) {
					KitUtils.kitMenu((Player) sender);
				} else {
					help(sender);
				}
			} else {
				if (!sender.isOp()) {
					help(sender);
					return true;
				}
				if (args[0].equalsIgnoreCase("menu")) {
					if (args.length == 2) {// /kit menu [player]
						if (Bukkit.getPlayer(args[1]) == null) {
							sender.sendMessage(c("&cプレイヤーが存在しません"));
						} else {
							sender.sendMessage(c("&7" + args[1] + "にKitメニューを開かせました"));
							KitUtils.kitMenu(Bukkit.getPlayer(args[1]));
						}
					} else {
						if (!(sender instanceof Player)) {
							sender.sendMessage(c("&cコンソールからは実行できません"));
							return true;
						}
						KitUtils.kitMenu((Player) sender);
					}
				} else if (args[0].equalsIgnoreCase("set")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(c("&cコンソールからは実行できません"));
						return true;
					}
					if (args.length == 3) {
						KitUtils.saveKit((Player) sender, args[2], args[1]);
						sender.sendMessage(c("&cKit[" + args[1] + "]&7の&cチーム[" + args[2] + "]&7を変更しました"));
					} else {
						sender.sendMessage(c("&7/kit set [kit] [team]"));
					}
				} else if (args[0].equalsIgnoreCase("load")) {
					if (args.length == 1) {
						sender.sendMessage(c("&7/kit load [kit] [team] [player]"));
					} else if (args.length == 3) {
						if (sender instanceof Player) {
							KitUtils.kit((Player) sender, args[2], args[1]);
						} else {
							sender.sendMessage(c("&7/kit load [kit] [team] [player]"));
						}
					} else if (args.length == 4) {
						if (Bukkit.getPlayer(args[3]) != null) {
							KitUtils.kit(Bukkit.getPlayer(args[3]), args[2], args[1]);
						} else {
							sender.sendMessage(c("&cプレイヤーが存在しません"));
						}
					} else {
						sender.sendMessage(c("&7/kit load [kit] [team] [player]"));
					}
				} else {
					help(sender);
				}
			}
		}
		return true;
	}

	private void help(CommandSender sender) {
		sender.sendMessage(c("&c- &7/kit"));
		if (sender.isOp()) {
			sender.sendMessage(c("&c- &7/kit menu [player]"));
			sender.sendMessage(c("&c- &7/kit set [kit] [team]"));
			sender.sendMessage(c("&c- &7/kit load [kit] [team] [player]"));
		}
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
