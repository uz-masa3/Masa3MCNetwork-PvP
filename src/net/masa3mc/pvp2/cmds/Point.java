package net.masa3mc.pvp2.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.masa3mc.pvp2.utils.PointUtils;

public class Point implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("point")) {
			if (args.length >= 2) {
				if (args[0].equalsIgnoreCase("get")) {
					if (args.length == 2) {
						if (sender instanceof Player) {
							sender.sendMessage(color("&5" + args[1] + "'s Point: "
									+ PointUtils.getPoint(Bukkit.getOfflinePlayer(args[1]).getUniqueId())));
						} else {
							sender.sendMessage(color("&5/point get [player]"));
						}
					} else {
						sender.sendMessage(color("&5/point get [player]"));
					}
				} else if (args[0].equalsIgnoreCase("set")) {
					if (!sender.isOp()) {
						sender.sendMessage(color("&5管理者以外実行できません"));
					} else {
						if (args.length == 3) {
							try {
								int point = Integer.parseInt(args[2]);
								PointUtils.setPoint(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), point);
								sender.sendMessage(color("&5" + args[1] + "のポイントを" + args[2] + "に変更しました"));
							} catch (NumberFormatException ex) {
								sender.sendMessage(color("&5数字以外は受け付けれません"));
							}
						} else {
							sender.sendMessage(color("&5/point set [player] [point]"));
						}
					}
				} else {
					sender.sendMessage(color("&5/point get [player]"));
					sender.sendMessage(color("&5/point set [player] [point]"));
				}
			} else {
				sender.sendMessage(color("&5/point get [player]"));
				sender.sendMessage(color("&5/point set [player] [point]"));
			}
		}
		return true;
	}

	private String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

}
