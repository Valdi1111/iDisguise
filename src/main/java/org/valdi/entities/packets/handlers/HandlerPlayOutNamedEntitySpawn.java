package org.valdi.entities.packets.handlers;

import org.valdi.entities.iDisguise;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

public class HandlerPlayOutNamedEntitySpawn extends AbstractPlayOutEntitySpawn {

	public HandlerPlayOutNamedEntitySpawn(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.NAMED_ENTITY_SPAWN);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
	}

}
