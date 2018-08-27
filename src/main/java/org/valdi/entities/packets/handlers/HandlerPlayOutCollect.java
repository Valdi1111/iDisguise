package org.valdi.entities.packets.handlers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.ObjectDisguise;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.util.EntityIdList;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

public class HandlerPlayOutCollect extends ProtocolLibPacketListener {

	public HandlerPlayOutCollect(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.COLLECT);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		Player observer = e.getPlayer();
		int entityId = e.getPacket().getIntegers().read(0);
		
		final LivingEntity livingEntity = EntityIdList.getEntityByEntityId(entityId);
		if(livingEntity != null && livingEntity != observer && DisguiseManager.isDisguisedTo(livingEntity, observer) && DisguiseManager.getDisguise(livingEntity) instanceof ObjectDisguise) {
			e.setCancelled(true);
		}
	}

}
