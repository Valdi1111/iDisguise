package org.valdi.entities.packets.handlers;

import org.valdi.entities.iDisguise;
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
		
	}

}
