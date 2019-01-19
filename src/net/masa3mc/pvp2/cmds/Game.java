package net.masa3mc.pvp2.cmds;

import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

import net.masa3mc.pvp2.GameManager;
import net.masa3mc.pvp2.RollBack;
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
				sender.sendMessage(c("&cゲーム中は実行できません。"));
			} else if (GameManager.nextcountdown) {
				sender.sendMessage(c("&cゲーム開始のカウントダウンが始まっています。"));
			} else {
				sender.sendMessage(c("&6全員をチームに参加させました"));
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
				sender.sendMessage(c("&6ゲームの停止・再実行が完了しました"));
			} else {
				sender.sendMessage(c("&cゲームが始まっていない可能性があります"));
			}
		} else if (args[0].equalsIgnoreCase("next")) {
			if (args.length == 2) {
				try {
					if (GameManager.nextcountdown) {
						sender.sendMessage(c("&c次のゲームの準備中は実行できません"));
					} else {
						Integer integer = Integer.parseInt(args[1]);
						sender.sendMessage(c("&6次のゲームをNo" + integer + "に設定しました"));
						if (GameManager.ingame)
							integer -= 1;
						GameManager.gamenumber = integer;
						GameManager.isSelectNumber = true;
					}
				} catch (NumberFormatException e) {
					sender.sendMessage(c("&c数字を指定してください"));
				}
			} else {
				sender.sendMessage(c("&c- &7/game next [number]"));
			}
		} else if (args[0].equalsIgnoreCase("rollback")) {
			if (args.length == 1) {
				sender.sendMessage(c("&c- &7/game rollback list"));
				sender.sendMessage(c("&c- &7/game rollback load [data]"));
				sender.sendMessage(c("&c- &7/game rollback save [name]"));
				sender.sendMessage(c("&c- &7/game rollback reload"));
			} else {
				if (args[1].equalsIgnoreCase("reload")) {
					Bukkit.getLogger().info("ReloadData: " + RollBack.loadData());
					sender.sendMessage(c("&6ロールバックデータを再読込しました。"));
				} else if (args[1].equalsIgnoreCase("load")) {
					if (args.length != 3) {
						sender.sendMessage(c("&cデータを指定してください"));
						sender.sendMessage(c("&c- &7/game rollback load [data]"));
					} else {
						if (RollBack.getList().contains(args[2])) {
							RollBack.load(args[2]);
							sender.sendMessage(c("&6ロールバックが完了しました。"));
							sender.sendMessage(c("&cデータ: " + args[2]));
						} else {
							sender.sendMessage(c("&c存在しないデータです。"));
							sender.sendMessage(c("&cデータ: " + args[2]));
						}
					}
				} else if (args[1].equalsIgnoreCase("list")) {
					LinkedList<String> list = RollBack.getList();
					if (list == null || list.isEmpty()) {
						sender.sendMessage(c("&cデータが見つかりませんでした"));
					}
					sender.sendMessage(c("&7------------RollbackData------------"));
					list.forEach(name -> {
						sender.sendMessage(c("&7- " + name));
					});
				} else if (args[1].equalsIgnoreCase("save")) {
					WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager()
							.getPlugin("WorldEdit");
					if (worldEditPlugin == null) {
						sender.sendMessage(c("&cWorldEditが入っていません"));
						return true;
					}
					if (!(sender instanceof Player)) {
						sender.sendMessage(c("&cプレイヤー以外は実行できません"));
						return true;
					}
					if (args.length != 3) {
						sender.sendMessage(c("&c名前を指定してください"));
						sender.sendMessage(c("&c- &7/game rollback save [name]"));
					} else {
						Selection sel = worldEditPlugin.getSelection((Player) sender);
						String name = (args[2].endsWith(".yml") ? args[2] : args[2] + ".yml");
						if (sel instanceof CuboidSelection) {
							RollBack.save(sel.getMaximumPoint(), sel.getMinimumPoint(), name, name);
							sender.sendMessage(c("&6登録が完了しました"));
							sender.sendMessage(c("&cデータ: " + name));
						} else {
							sender.sendMessage(c("&cWorldEditで範囲を指定してください"));
						}
					}
				} else {
					sender.sendMessage(c("&c- &7/game rollback list"));
					sender.sendMessage(c("&c- &7/game rollback load [data]"));
					sender.sendMessage(c("&c- &7/game rollback save [name]"));
					sender.sendMessage(c("&c- &7/game rollback reload"));
				}
			}
		} else {
			help(sender);
		}
		return true;
	}

	private void help(CommandSender sender) {
		sender.sendMessage(c("&c- &7/game start"));
		sender.sendMessage(c("&c- &7/game end"));
		sender.sendMessage(c("&c- &7/game rollback [list/load/reload/save]"));
		sender.sendMessage(c("&c- &7/game next [number]"));
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
