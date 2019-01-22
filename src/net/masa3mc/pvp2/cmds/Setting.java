package net.masa3mc.pvp2.cmds;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

import net.masa3mc.pvp2.Main;

public class Setting implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Main main = Main.getInstance();

		if (cmd.getName().equalsIgnoreCase("setting")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(c("&cプレイヤー以外は実行できません"));
			}
			Player player = (Player) sender;
			if (args.length == 0) {
				allsettings(sender);
			} else {
				if (args[0].equalsIgnoreCase("teambase")) {
					WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager()
							.getPlugin("WorldEdit");
					if (worldEditPlugin == null) {
						sender.sendMessage(c("&cWorldEditが入っていません"));
						return true;
					}
					if (args.length == 1) {
						sender.sendMessage(c("&61. //pos1 and //pos2"));
						sender.sendMessage(c("&62. /setting teambase [arena] [team]"));
					} else if (args.length == 3) {
						if (main.getConfig().getString("Arena" + args[1]) != null) {
							if (!(args[2].equalsIgnoreCase("red") || args[2].equalsIgnoreCase("blue"))) {
								sender.sendMessage(c("&cチームはred/blueのみ指定可能です"));
								return true;
							}
							try {
								String team = args[2];
								YamlConfiguration yml = YamlConfiguration
										.loadConfiguration(new File(main.getDataFolder() + "/bases.yml"));
								Selection selection = worldEditPlugin.getSelection(player);
								if (selection instanceof CuboidSelection) {
									Vector min = selection.getNativeMinimumPoint();
									Vector max = selection.getNativeMaximumPoint();
									List<String> list = yml.getStringList("Arena" + args[1] + ".base." + team);
									for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
										for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
											for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
												String pos = player.getWorld().getName() + "," + x + "," + y + "," + z;
												list.add(pos);
											}
										}
									}
									yml.set("Arena" + args[1] + ".base." + team, list);
									yml.save(new File(main.getDataFolder() + "/bases.yml"));
									sender.sendMessage(c("&6登録が完了しました"));
								} else {
									sender.sendMessage(c("&cWorldEditで範囲を指定してください"));
								}
							} catch (IOException e) {
								sender.sendMessage(c("&c保存中にエラーが発生しました"));
							}
						} else {
							sender.sendMessage(c("&cアリーナが存在しません"));
						}
					} else {
						sender.sendMessage(c("&61. //pos1 and //pos2"));
						sender.sendMessage(c("&62. /setting teambase [arena] [team]"));
					}
				} else if (args[0].equalsIgnoreCase("object")) {
					if (args.length == 6) {
						String team = args[2];
						if (main.getConfig().getString("Arena" + args[1]) != null) {
							if (!(team.equalsIgnoreCase("red") || team.equalsIgnoreCase("blue"))) {
								sender.sendMessage(c("&cチームはred/blueのみ指定可能です"));
								return true;
							}
							try {
								FileConfiguration conf = main.getConfig();
								String str = "Arena" + args[1] + ".object." + team;
								conf.set(str + ".x", iparse(args[3]));
								conf.set(str + ".y", iparse(args[4]));
								conf.set(str + ".z", iparse(args[5]));
								conf.save(new File(main.getDataFolder() + "/config.yml"));
							} catch (NumberFormatException e) {
								sender.sendMessage(c("&cx/y/zには数字を入れてください"));
							} catch (IOException e) {
								sender.sendMessage(c("&c保存中にエラーが発生しました"));
							}
						} else {
							sender.sendMessage(c("&cアリーナが存在しません"));
						}
					} else {
						sender.sendMessage(c("&62. /setting object [arena] [team] [x] [y] [z]"));
					}
				} else if (args[0].equalsIgnoreCase("type")) {
					if (main.getConfig().getString("Arena" + args[1]) != null) {
						if (args.length == 3) {
							String type = args[2].toUpperCase();
							if (!(type.equals("CTW") || type.equals("TDM"))) {
								sender.sendMessage(c("&cタイプはCTW/TDMのみ指定可能です"));
								return true;
							}
							try {
								FileConfiguration conf = main.getConfig();
								conf.set("Arena" + args[1] + ".Type", args[2]);
								conf.save(new File(main.getDataFolder() + "/config.yml"));
							} catch (IOException e) {
								sender.sendMessage(c("&c保存中にエラーが発生しました"));
							}
						} else {
							sender.sendMessage(c("&62. /setting type [arena] [type]"));
						}
					} else {
						sender.sendMessage(c("&cアリーナが存在しません"));
					}
				} else {
					allsettings(sender);
				}
			}
		}
		return true;
	}

	private String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	private Integer iparse(String s) throws NumberFormatException {
		return Integer.parseInt(s);
	}

	private void allsettings(CommandSender sender) {
		sender.sendMessage(c("&7--------&cAll Settings&7--------"));
		sender.sendMessage(c("&6 /setting teambase [arena] [team]"));
		sender.sendMessage(c("&6 /setting object [arena] [team]"));
	}

}
