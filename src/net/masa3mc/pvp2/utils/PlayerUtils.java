package net.masa3mc.pvp2.utils;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class PlayerUtils {

	public static void respawn(Player player) {
		((CraftPlayer) player).getHandle().playerConnection
				.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
	}

	public static void sendActionBarMessage(Player player, String message) {
		IChatBaseComponent baseComponent = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat playOutChat = new PacketPlayOutChat(baseComponent, (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutChat);
	}

}
