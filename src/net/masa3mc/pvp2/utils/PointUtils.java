package net.masa3mc.pvp2.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import net.masa3mc.pvp2.Main;

public class PointUtils {

	private static final Main ins = Main.getInstance();

	public static void setPoint(UUID uuid, int point) {
		YamlConfiguration py = YamlConfiguration.loadConfiguration(new File(ins.getDataFolder() + "/point.yml"));
		py.set("" + uuid, point);
		try {
			py.save(new File(ins.getDataFolder() + "/point.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getPoint(UUID uuid) {
		return YamlConfiguration.loadConfiguration(new File(ins.getDataFolder() + "/point.yml")).getInt("" + uuid);
	}

}
