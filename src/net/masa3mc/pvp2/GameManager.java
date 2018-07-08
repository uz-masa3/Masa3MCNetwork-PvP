package net.masa3mc.pvp2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import net.masa3mc.pvp2.utils.ChestUtils;
import net.masa3mc.pvp2.utils.KitUtils;
import net.masa3mc.pvp2.utils.SidebarUtils;

public class GameManager {

	private static final Main main = Main.getInstance();

	public static YamlConfiguration canbreaks = null;
	public static YamlConfiguration bases = null;
	public static YamlConfiguration chests = null;

	public static boolean ingame = false;
	public static List<Entity> delentities = new ArrayList<Entity>();
	public static Set<Player> entried = new HashSet<Player>();
	public static Set<Player> gamenow = new HashSet<Player>();
	public static int gamenumber = 0;
	public static boolean isSelectNumber = false;

	public static HashMap<Location, Material> breaking = new HashMap<>();

	// 羊毛取得した人を入れる
	public static ArrayList<Player> blueFlagPlayer = new ArrayList<Player>();
	public static ArrayList<Player> redFlagPlayer = new ArrayList<Player>();

	// ゲーム開始から何秒たったか
	public static int seconds = 0;

	// TDMスコア
	public static int TDMRed_Score = 1;
	public static int TDMBlue_Score = 1;

	public static GameType game;
	public static GameType nextgame;

	public static HashMap<UUID, String> playerkit = new HashMap<UUID, String>();

	// Teams
	public static final Team CTWRed = Main.team("CTW_red");
	public static final Team CTWBlue = Main.team("CTW_blue");
	public static final Team TDMRed = Main.team("TDM_red");
	public static final Team TDMBlue = Main.team("TDM_blue");

	// addPlayerが2回以上呼ばれた時にtrue
	public static boolean nextcountdown = false;

	private static final int min_players = 2;

	private static String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	private static void b(String str) {
		Bukkit.broadcastMessage(str);
	}

	// サーバー参加時に呼び出される
	public static void addPlayer(Player player) {
		entried.add(player);
		kitInventory(player, true);
		player.setMaxHealth(20);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(0);
		player.setGameMode(GameMode.SURVIVAL);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		if (ingame) {
			addTeam(player, game, true);
		} else {
			GameType a = GameType.valueOf(main.getConfig().getString("Arena1.Type"));
			String next = main.getConfig().getString("Arena" + (gamenumber + 1) + ".Type");
			if (next != null)
				a = GameType.valueOf(next);
			if (a.equals(GameType.CTW) || a.equals(GameType.TDM)) {
				addTeam(player, a, false);
			} else if (a.equals(GameType.SW)) {

			}
			if (entried.size() >= min_players && !nextcountdown) {
				nextcountdown = true;
				new BukkitRunnable() {
					int count = 0;

					public void run() {
						int sec = 16;
						if (entried.size() >= min_players) {
							count++;
							if (count == sec) {
								entried.forEach(players -> {
									players.closeInventory();
									SidebarUtils.SidebarCreate(players);
								});
								if (!isSelectNumber) {
									gamenumber++;
								}
								if (main.getConfig().getString("Arena" + gamenumber) == null) {
									gamenumber = 1;
								}
								GameStart(gamenumber);
								nextcountdown = false;
								cancel();
							} else {
								b(c("&a開始まで残り&6" + (sec - count) + "&a秒"));
								if (sec - count <= 5) {
									entried.forEach(players -> players.playSound(players.getLocation(),
											Sound.NOTE_PLING, 1, 1));
								}
							}
						} else {
							b(c("&cゲーム開始には" + min_players + "人以上必要です"));
							nextcountdown = false;
							cancel();
						}
					}
				}.runTaskTimer(main, 20, 20);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void addGamePlayer(Player player) {
		FileConfiguration f = main.getConfig();
		gamenow.add(player);
		SidebarUtils.SidebarCreate(player);
		game = GameType.valueOf(f.getString("Arena" + gamenumber + ".Type"));
		Location redLoc = getSpawnLocation(gamenumber, "Red");
		Location blueLoc = getSpawnLocation(gamenumber, "Blue");
		player.setGameMode(GameMode.SURVIVAL);
		player.setFallDistance(0.0f);
		player.getVelocity().zero();
		if (game.equals(GameType.CTW)) {
			if (CTWRed.hasPlayer(player)) {
				player.teleport(redLoc);
				player.setBedSpawnLocation(redLoc, true);
				if (playerkit.containsKey(player.getUniqueId())) {
					KitUtils.kit(player, "red", playerkit.get(player.getUniqueId()));
				} else {
					KitUtils.kit(player, "red", f.getString("Arena" + gamenumber + ".defaultKit"));
				}
			} else if (CTWBlue.hasPlayer(player)) {
				player.teleport(blueLoc);
				player.setBedSpawnLocation(blueLoc, true);
				if (playerkit.containsKey(player.getUniqueId())) {
					KitUtils.kit(player, "blue", playerkit.get(player.getUniqueId()));
				} else {
					KitUtils.kit(player, "blue", f.getString("Arena" + gamenumber + ".defaultKit"));
				}
			}
		} else if (game.equals(GameType.TDM)) {
			if (TDMRed.hasPlayer(player)) {
				player.teleport(redLoc);
				player.setBedSpawnLocation(redLoc, true);
				if (playerkit.containsKey(player.getUniqueId())) {
					KitUtils.kit(player, "red", playerkit.get(player.getUniqueId()));
				} else {
					KitUtils.kit(player, "red", f.getString("Arena" + gamenumber + ".defaultKit"));
				}
			} else if (TDMBlue.hasPlayer(player)) {
				player.teleport(blueLoc);
				player.setBedSpawnLocation(blueLoc, true);
				if (playerkit.containsKey(player.getUniqueId())) {
					KitUtils.kit(player, "blue", playerkit.get(player.getUniqueId()));
				} else {
					KitUtils.kit(player, "blue", f.getString("Arena" + gamenumber + ".defaultKit"));
				}
			}
		} else if (game.equals(GameType.SW)) {

		}
	}

	public static void GameStart(int number) {
		gamenumber = number;
		ingame = true;
		game = GameType.valueOf(main.getConfig().getString("Arena" + gamenumber + ".Type"));
		FileConfiguration f = main.getConfig();
		ChestUtils.restoreAllChests();
		canbreaks = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/canbreaks.yml"));
		bases = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/bases.yml"));
		chests = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/chests.yml"));
		b(c("&a=============================="));
		b(c("&6ゲームが開始されました"));
		b(c("&6制限時間: " + f.getInt("Arena" + gamenumber + ".seconds") + "秒"));
		b(c("&6ゲーム: " + game.getName()));
		if (game.equals(GameType.CTW)) {
			b(c("&6目標: &f敵チームの拠点にある白羊毛を自チームの羊毛置き場に設置する"));
		} else if (game.equals(GameType.TDM)) {
			b(c("&6目標: &f制限時間内に敵チームよりkill数を稼ぐ"));
		} else if (game.equals(GameType.SW)) {
			b(c("&6目標: &f最後の一人になるまで生き残る"));
		}
		b(c("&a=============================="));
		entried.forEach(players -> {
			players.setHealth(20);
			players.setFoodLevel(20);
			players.playSound(players.getLocation(), Sound.NOTE_PLING, 1, 2);
			addGamePlayer(players);
		});
		seconds = 0;
		new BukkitRunnable() {
			public void run() {
				seconds++;
				if (ingame) {
					SidebarUtils.SidebarSeconds();
					if (entried.size() < min_players) {
						GameEnd(GameTeam.NONE);
						TDMRed_Score = 0;
						TDMBlue_Score = 0;
						cancel();
					} else {
						int sec = f.getInt("Arena" + gamenumber + ".seconds");
						if (sec - seconds <= 5) {
							if (sec - seconds != 0) {
								b(c("&7" + game.getName() + "終了まで残り&6" + (sec - seconds) + "&7秒"));
							}
						}
						if (seconds >= sec) {
							seconds = 0;
							if (game.equals(GameType.CTW)) {
								GameEnd(GameTeam.NONE);
							} else if (game.equals(GameType.TDM)) {
								if (TDMRed_Score > TDMBlue_Score) {
									GameEnd(GameTeam.RED);
								} else if (TDMBlue_Score > TDMRed_Score) {
									GameEnd(GameTeam.BLUE);
								} else if (TDMBlue_Score == TDMRed_Score) {
									GameEnd(GameTeam.NONE);
								}
							} else if (game.equals(GameType.SW)) {

							}
							TDMRed_Score = 0;
							TDMBlue_Score = 0;

							cancel();
						}
					}
				} else {
					reset(false);
					cancel();
				}
			}
		}.runTaskTimer(main, 20, 20);
	}

	// 終わりのカウント || 羊毛設置で呼び出す
	public static void GameEnd(GameTeam team) {
		if (ingame) {
			ingame = false;
			blueFlagPlayer.clear();
			redFlagPlayer.clear();
			ChestUtils.restoreAllChests();
			breaking.forEach((l, b) -> {
				l.getBlock().setType(b);
			});
			b(c("&a=============================="));
			b(c("&6" + game.getName() + "が終了しました!"));
			if (game.equals(GameType.CTW)) {
				b(c("&6結果は" + (team.equals(GameTeam.NONE) ? "引き分けです"
						: "&b" + (team.name().equals("RED") ? "&c" : "&9") + team.name() + "&6チームの勝利です")));
			} else if (game.equals(GameType.TDM)) {
				b(c("&6結果は" + (team.equals(GameTeam.NONE) ? "引き分けです"
						: TDMRed_Score + "/" + TDMBlue_Score + "で&b" + (team.name().equals("RED") ? "&c" : "&9")
								+ team.name() + "&6チームの勝利です")));
			}
			b(c("&a=============================="));
			reset(true);
			nextGamePreparation(); // GameEnd()の6秒後
		}
	}

	public static void GameEnd(Player... players) {
		if (ingame) {
			ChestUtils.restoreAllChests();
			breaking.forEach((l, b) -> {
				l.getBlock().setType(b);
			});
			b(c("&a=============================="));
			b(c("&6Skywarsが終了しました!"));
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < players.length; i++) {
				if (i <= 3) {
					sb.append(i + ": " + players[i].getName());
				}
			}
			b(c("&6結果は ") + sb.toString());
			b(c("&a=============================="));
			reset(true);
			nextGamePreparation();// GameEnd()の6秒後
		}
	}

	public static void nextGamePreparation() {
		new BukkitRunnable() {
			int count = 0;

			public void run() {
				count++;
				if (count != 16 && (16 - count <= 10)) {
					b(c("&7次のゲーム準備まで残り&6" + (16 - count) + "&7秒"));
				} else if (count == 16) {
					SidebarUtils.SidebarUnregist();
					entried.forEach(players -> {
						players.teleport(players.getWorld().getSpawnLocation());
						KitUtils.kitMenu(players);
						kitInventory(players, true);
					});
					entried.clear();
					cancel();
				}
			}

		}.runTaskTimer(main, 120, 20);
	}

	@SuppressWarnings("deprecation")
	private static void reset(boolean fireworks) {
		ingame = false;
		nextcountdown = false;
		isSelectNumber = false;
		delentities.forEach(e -> {
			if (!e.isDead())
				e.remove();
		});
		gamenow.clear();
		SidebarUtils.SidebarUnregist();
		CTWRed.getPlayers().forEach(players -> CTWRed.removePlayer(players));
		CTWBlue.getPlayers().forEach(players -> CTWBlue.removePlayer(players));
		TDMRed.getPlayers().forEach(players -> TDMRed.removePlayer(players));
		TDMBlue.getPlayers().forEach(players -> TDMBlue.removePlayer(players));
		entried.forEach(players -> {
			kitInventory(players, false);
			players.setHealth(20);
			players.setFoodLevel(20);
			players.setLevel(0);
			players.setExp(0);
			players.setGameMode(GameMode.SURVIVAL);
			if (fireworks) {
				players.playSound(players.getLocation(), Sound.FIREWORK_LAUNCH, 1, 0);
			}
			players.getActivePotionEffects().forEach(potions -> players.removePotionEffect(potions.getType()));
		});
	}

	public static void kitInventory(Player player, boolean bool) {
		ItemStack air = new ItemStack(Material.AIR);
		player.getInventory().setHelmet(air);
		player.getInventory().setChestplate(air);
		player.getInventory().setLeggings(air);
		player.getInventory().setBoots(air);
		player.getInventory().clear();
		if (bool) {
			ItemStack kit = new ItemStack(Material.DIAMOND_SWORD);
			ItemMeta meta = kit.getItemMeta();
			meta.setDisplayName(c("&eKitSelect"));
			kit.setItemMeta(meta);
			player.getInventory().setItem(0, kit);

			ItemStack bkit = new ItemStack(Material.CHEST);
			ItemMeta bmeta = bkit.getItemMeta();
			bmeta.setDisplayName(c("&dBuy Kit"));
			bkit.setItemMeta(bmeta);
			player.getInventory().setItem(2, bkit);
		}
		player.updateInventory();
	}

	@SuppressWarnings("deprecation")
	public static void addTeam(Player player, GameType type, boolean addgame) {
		if (type.equals(GameType.CTW)) {
			if (CTWRed.hasPlayer(player) || CTWBlue.hasPlayer(player)) { // 既に参加してたら無効
				player.sendMessage(c("&c既に参加しています"));
			} else {
				if ((CTWBlue.getPlayers().size() + CTWRed.getPlayers().size()) % 2 == 0) {
					CTWRed.addPlayer(player);
					b(c("&c" + player.getName() + "&6が&c赤チーム&6に参加しました"));
				} else {
					CTWBlue.addPlayer(player);
					b(c("&c" + player.getName() + "&6が&9青チーム&6に参加しました"));
				}
				if (addgame)
					addGamePlayer(player);
			}
		} else if (type.equals(GameType.TDM)) {
			if (TDMRed.hasPlayer(player) || TDMBlue.hasPlayer(player)) {
				player.sendMessage(c("&c既に参加しています"));
			} else {
				if ((TDMBlue.getPlayers().size() + TDMRed.getPlayers().size()) % 2 == 0) {
					TDMRed.addPlayer(player);
					b(c("&c" + player.getName() + "&6が&c赤チーム&6に参加しました"));
				} else {
					TDMBlue.addPlayer(player);
					b(c("&c" + player.getName() + "&6が&9青チーム&6に参加しました"));
				}
				if (addgame)
					addGamePlayer(player);
			}
		}
	}

	public static Location getSpawnLocation(int arena, String name) {
		FileConfiguration f = main.getConfig();
		Location location = new Location(Bukkit.getWorld(f.getString("Arena" + gamenumber + "." + name + ".W")),
				f.getInt("Arena" + gamenumber + "." + name + ".X"), f.getInt("Arena" + gamenumber + "." + name + ".Y"),
				f.getInt("Arena" + gamenumber + "." + name + ".Z"));
		return location;
	}

	public static enum GameType {
		CTW, TDM, SW;

		public String getName() {
			return (this.name().equals("SW") ? "Skywars" : this.name());
		}

	}

	public static enum GameTeam {
		NONE, RED, BLUE;
	}
}
