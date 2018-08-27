package org.valdi.entities.packets.handlers;

import org.valdi.entities.iDisguise;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

public class HandlerPlayInUseEntity extends ProtocolLibPacketListener {

	public HandlerPlayInUseEntity(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Client.USE_ENTITY);
	}
	
	@Override
	public void onPacketReceiving(PacketEvent e) {
		
	}

}
