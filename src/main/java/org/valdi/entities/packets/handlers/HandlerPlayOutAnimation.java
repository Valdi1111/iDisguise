package org.valdi.entities.packets.handlers;

import org.valdi.entities.iDisguise;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

public class HandlerPlayOutAnimation extends ProtocolLibPacketListener {

	public HandlerPlayOutAnimation(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.ANIMATION);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
	}

}
