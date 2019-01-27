package net.masa3mc.pvp2.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import net.masa3mc.pvp2.GameManager;
import net.masa3mc.pvp2.Main;

public class SidebarUtils {

	private static final Main ins = Main.getInstance();

	public static void SidebarCreate(Player p) {
		if (p.getScoreboard().getObjective("SidebarStatus") != null) {
			p.getScoreboard().getObjective("SidebarStatus").unregister();
		}
		Objective obj = p.getScoreboard().getObjective("SidebarStatus");
		obj = p.getScoreboard().registerNewObjective("SidebarStatus", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		if (GameManager.ingame) {
			if (GameManager.game.equals(GameManager.GameType.CTW)) {
				obj.setDisplayName(c("&6CTW"));
				obj.getScore(c("&9青チーム&6□")).setScore(1);
				obj.getScore(c("&c赤チーム&6□")).setScore(1);
				// set 0
				obj.getScore(c("&9青チーム&6□")).setScore(0);
				obj.getScore(c("&c赤チーム&6□")).setScore(0);
			} else if (GameManager.game.equals(GameManager.GameType.TDM)) {
				obj.setDisplayName(c("&6TDM"));
				obj.getScore(c("&9青チーム")).setScore(1);
				obj.getScore(c("&c赤チーム")).setScore(1);
				// set 0
				obj.getScore(c("&9青チーム")).setScore(0);
				obj.getScore(c("&c赤チーム")).setScore(0);
			}
			obj.getScore(c("&e残り: " + ins.getConfig().getInt("Arena" + GameManager.gamenumber + ".seconds") + "秒"))
					.setScore(255);
		}
	}

	public static void SidebarUnregist() {
		Bukkit.getOnlinePlayers().forEach(players -> {
			if (players.getScoreboard().getObjective("SidebarStatus") != null) {
				players.getScoreboard().getObjective("SidebarStatus").unregister();
			}
		});
	}

	public static void SidebarSeconds() {
		int sec = ins.getConfig().getInt("Arena" + GameManager.gamenumber + ".seconds");
		Bukkit.getOnlinePlayers().forEach(players -> {
			Objective obj = players.getScoreboard().getObjective("SidebarStatus");
			obj.getScoreboard().resetScores(c("&e残り: " + sec + "秒"));
			obj.getScoreboard().resetScores(c("&e残り: " + (sec - GameManager.seconds + 1) + "秒"));
			obj.getScore(c("&e残り: " + (sec - GameManager.seconds) + "秒")).setScore(255);
		});
	}

	public static void SidebarFlag(String team, int a123) {
		// 1-初期化
		// 2-所持
		// 3-設置済
		if (GameManager.ingame && team.equalsIgnoreCase("red")) {
			Bukkit.getOnlinePlayers().forEach(players -> {
				Objective obj = players.getScoreboard().getObjective("SidebarStatus");
				if (a123 == 1) {
					obj.getScoreboard().resetScores(c("&c赤チーム&6▧"));
					obj.getScoreboard().resetScores(c("&c赤チーム&6■"));
					obj.getScore(c("&c赤チーム&6□")).setScore(1);
					obj.getScore(c("&c赤チーム&6□")).setScore(0);
				} else if (a123 == 2) {
					obj.getScoreboard().resetScores(c("&c赤チーム&6□"));
					obj.getScoreboard().resetScores(c("&c赤チーム&6■"));
					obj.getScore(c("&c赤チーム&6▧")).setScore(1);
					obj.getScore(c("&c赤チーム&6▧")).setScore(0);
				} else if (a123 == 3) {
					obj.getScoreboard().resetScores(c("&c赤チーム&6□"));
					obj.getScoreboard().resetScores(c("&c赤チーム&6▧"));
					obj.getScore(c("&c赤チーム&6■")).setScore(1);
					obj.getScore(c("&c赤チーム&6■")).setScore(0);
				}
			});
		} else if (GameManager.ingame && team.equalsIgnoreCase("blue")) {
			Bukkit.getOnlinePlayers().forEach(players -> {
				Objective obj = players.getScoreboard().getObjective("SidebarStatus");
				if (a123 == 1) {
					obj.getScoreboard().resetScores(c("&9青チーム&6▧"));
					obj.getScoreboard().resetScores(c("&9青チーム&6■"));
					obj.getScore(c("&9青チーム&6□")).setScore(1);
					obj.getScore(c("&9青チーム&6□")).setScore(0);
				} else if (a123 == 2) {
					obj.getScoreboard().resetScores(c("&9青チーム&6□"));
					obj.getScoreboard().resetScores(c("&9青チーム&6■"));
					obj.getScore(c("&9青チーム&6▧")).setScore(1);
					obj.getScore(c("&9青チーム&6▧")).setScore(0);
				} else if (a123 == 3) {
					obj.getScoreboard().resetScores(c("&9青チーム&6□"));
					obj.getScoreboard().resetScores(c("&9青チーム&6▧"));
					obj.getScore(c("&9青チーム&6■")).setScore(1);
					obj.getScore(c("&9青チーム&6■")).setScore(0);
				}
			});
		}
	}

	public static void SidebarTDMScore(String team, int score) {
		if (GameManager.ingame && team.equalsIgnoreCase("red")) {
			Bukkit.getOnlinePlayers().forEach(players -> players.getScoreboard().getObjective("SidebarStatus")
					.getScore(c("&c赤チーム")).setScore(score));
		} else if (GameManager.ingame && team.equalsIgnoreCase("blue")) {
			Bukkit.getOnlinePlayers().forEach(players -> players.getScoreboard().getObjective("SidebarStatus")
					.getScore(c("&9青チーム")).setScore(score));
		}
	}

	private static String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

}
