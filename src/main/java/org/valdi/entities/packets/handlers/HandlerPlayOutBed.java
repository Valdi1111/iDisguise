package org.valdi.entities.packets.handlers;

import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.PlayerDisguise;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.util.EntityIdList;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

public class HandlerPlayOutBed extends ProtocolLibPacketListener {

	public HandlerPlayOutBed(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.BED);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		Player observer = e.getPlayer();
		int entityId = e.getPacket().getIntegers().read(0);
		
		final Player player = EntityIdList.getPlayerByEntityId(entityId);
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && !(DisguiseManager.getDisguise(player) instanceof PlayerDisguise)) {
			e.setCancelled(true);
		}
	}

}
