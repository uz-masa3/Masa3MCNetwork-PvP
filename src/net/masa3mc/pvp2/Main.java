package net.masa3mc.pvp2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.masa3mc.pvp2.cmds.GameEnd;
import net.masa3mc.pvp2.cmds.GameStart;
import net.masa3mc.pvp2.cmds.KitMenu;
import net.masa3mc.pvp2.cmds.LoadKit;
import net.masa3mc.pvp2.cmds.NextGame;
import net.masa3mc.pvp2.cmds.Point;
import net.masa3mc.pvp2.cmds.SetKit;
import net.masa3mc.pvp2.cmds.Setting;
import net.masa3mc.pvp2.listeners.CTWListener;
import net.masa3mc.pvp2.listeners.MainListener;
import net.masa3mc.pvp2.listeners.SWListener;
import net.masa3mc.pvp2.listeners.TDMListener;
import net.masa3mc.pvp2.utils.ChestUtils;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	private static Main main;
	private static Economy economy;

	public void onEnable() {
		main = this;
		saveDefaultConfig();
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new MainListener(this), this);
		pm.registerEvents(new CTWListener(this), this);
		pm.registerEvents(new TDMListener(), this);
		pm.registerEvents(new SWListener(this), this);

		getCommand("gamestart").setExecutor(new GameStart());
		getCommand("gameend").setExecutor(new GameEnd());
		getCommand("kitmenu").setExecutor(new KitMenu());
		getCommand("loadkit").setExecutor(new LoadKit());
		getCommand("setkit").setExecutor(new SetKit());
		getCommand("point").setExecutor(new Point());
		getCommand("setting").setExecutor(new Setting());
		getCommand("nextgame").setExecutor(new NextGame());

		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		if (board.getObjective("Kills") == null)
			board.registerNewObjective("Kills", "playerKillCount");
		if (board.getObjective("Deaths") == null)
			board.registerNewObjective("Deaths", "deathCount");
		if (team("CTW_red") == null)
			board.registerNewTeam("CTW_red").setAllowFriendlyFire(false);
		if (team("CTW_blue") == null)
			board.registerNewTeam("CTW_blue").setAllowFriendlyFire(false);
		if (team("TDM_red") == null)
			board.registerNewTeam("TDM_red").setAllowFriendlyFire(false);
		if (team("TDM_blue") == null)
			board.registerNewTeam("TDM_blue").setAllowFriendlyFire(false);
		ChestUtils.restoreAllChests();
		Bukkit.getOnlinePlayers().forEach(players -> {
			players.teleport(players.getWorld().getSpawnLocation());
			players.setBedSpawnLocation(players.getWorld().getSpawnLocation());
			players.getInventory().clear();
			players.updateInventory();
			players.setHealth(20);
			players.setFoodLevel(20);
			GameManager.kitInventory(players, true);
		});
		setupEconomy();
	}

	@SuppressWarnings("deprecation")
	public void onDisable() {
		ChestUtils.restoreAllChests();
		GameManager.breaking.forEach((l, b) -> {
			l.getBlock().setType(b);
		});
		Bukkit.getOnlinePlayers().forEach(players -> {
			players.teleport(players.getWorld().getSpawnLocation());
			players.setBedSpawnLocation(players.getWorld().getSpawnLocation());
			players.setHealth(20);
			players.setFoodLevel(20);
			if (players.getScoreboard().getObjective("SidebarStatus") != null) {
				players.getScoreboard().getObjective("SidebarStatus").unregister();
			}
			team("CTW_red").removePlayer(players);
			team("CTW_blue").removePlayer(players);
			team("TDM_red").removePlayer(players);
			team("TDM_blue").removePlayer(players);
		});
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = (Economy) rsp.getProvider();
		return economy != null;
	}

	public static Economy getEconomy() {
		return economy;
	}

	public static Main getInstance() {
		return main;
	}

	public static Team team(String team) {
		return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team);
	}

	// TODO
	// リログでチームの数が1対3とかになるのを改善
	// Skywarsの追加

}
