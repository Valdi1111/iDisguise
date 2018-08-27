package org.valdi.entities.packets.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.PlayerDisguise;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.PacketHandler;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

import de.robingrether.util.ObjectUtil;

public class HandlerPlayOutScoreboardTeam extends ProtocolLibPacketListener {

	public HandlerPlayOutScoreboardTeam(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.SCOREBOARD_TEAM);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		if(PacketHandler.modifyScoreboardPackets) {
			//iDisguise.getInstance().getLogger().info("Skipping scoreboard team packet.");
			return;
		}
		
		Player observer = e.getPlayer();
		int action = e.getPacket().getIntegers().read(0);
		if(action != 0 && action != 3 && action != 4) {
			return;
		}
		
		List<String> entries = Arrays.asList(e.getPacket().getStringArrays().read(0));
		List<String> itemsToRemove = new ArrayList<>();
		List<String> itemsToAdd = new ArrayList<>();
		for(String entry : entries) {
			Player player = Bukkit.getPlayerExact(entry);
			if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
				itemsToRemove.add(entry);
				itemsToAdd.add(((PlayerDisguise)DisguiseManager.getDisguise(player)).getDisplayName());
			}
		}
		entries.removeAll(itemsToRemove);
		entries.addAll(itemsToAdd);
		e.getPacket().getStringArrays().write(0, entries.toArray(new String[entries.size()]));
	}

}
