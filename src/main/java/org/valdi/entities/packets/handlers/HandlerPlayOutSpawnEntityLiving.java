package org.valdi.entities.packets.handlers;

import org.valdi.entities.iDisguise;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

public class HandlerPlayOutSpawnEntityLiving extends AbstractPlayOutEntitySpawn {

	public HandlerPlayOutSpawnEntityLiving(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.SPAWN_ENTITY_LIVING);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
	}

}
