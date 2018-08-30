package org.valdi.entities.packets.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.PlayerDisguise;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.packets.PacketOptions;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

public class HandlerPlayOutScoreboardScore extends ProtocolLibPacketListener {

	public HandlerPlayOutScoreboardScore(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.SCOREBOARD_SCORE);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		if(PacketOptions.modifyScoreboardPackets) {
			//iDisguise.getInstance().getLogger().info("Skipping scoreboard score packet.");
			return;
		}

		Player observer = e.getPlayer();
		Player player = Bukkit.getPlayerExact(e.getPacket().getStrings().read(0));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
			e.getPacket().getStrings().write(0, ((PlayerDisguise)DisguiseManager.getDisguise(player)).getDisplayName());
		}
	}

}
