package net.masa3mc.pvp2.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.masa3mc.pvp2.GameManager;
import net.masa3mc.pvp2.GameManager.GameTeam;

public class Game implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("game")) {
			return true;
		}
		if (args.length == 0) {
			help(sender);
		} else if (args[0].equalsIgnoreCase("start")) {
			if (GameManager.ingame) {
				sender.sendMessage(c("&5ゲーム中は実行できません。"));
			} else if (GameManager.nextcountdown) {
				sender.sendMessage(c("&5ゲーム開始のカウントダウンが始まっています。"));
			} else {
				sender.sendMessage(c("&5全員をチームに参加させました"));
				Bukkit.getOnlinePlayers().forEach(players -> {
					GameManager.CTWRed.removePlayer(players);
					GameManager.CTWBlue.removePlayer(players);
					GameManager.TDMRed.removePlayer(players);
					GameManager.TDMBlue.removePlayer(players);
					GameManager.addPlayer(players);
				});
			}
		} else if (args[0].equalsIgnoreCase("end")) {
			if (GameManager.ingame) {
				GameManager.GameEnd(GameTeam.NONE);
				sender.sendMessage(c("&5ゲームの停止・再実行が完了しました"));
			} else {
				sender.sendMessage(c("&5ゲームが始まっていない可能性があります"));
			}
		} else if (args[0].equalsIgnoreCase("next")) {
			if (args.length == 2) {
				try {
					if (GameManager.nextcountdown) {
						sender.sendMessage(c("&5次のゲームの準備中は実行できません"));
					} else {
						Integer integer = Integer.parseInt(args[1]);
						sender.sendMessage(c("&5次のゲームをNo" + integer + "に設定しました"));
						if (GameManager.ingame)
							integer -= 1;
						GameManager.gamenumber = integer;
						GameManager.isSelectNumber = true;
					}
				} catch (NumberFormatException e) {
					sender.sendMessage(c("&5数字を指定してください"));
				}
			} else {
				sender.sendMessage(c("&c- &7/game next [number]"));
			}
		} else {
			help(sender);
		}
		return true;
	}

	private void help(CommandSender sender) {
		sender.sendMessage(c("&c- &7/game start"));
		sender.sendMessage(c("&c- &7/game end"));
		sender.sendMessage(c("&c- &7/game next [number]"));
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
