package org.valdi.entities.packets;

import org.valdi.entities.iDisguise;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;

public abstract class ProtocolLibPacketListener extends PacketAdapter {
    private final iDisguise addon;
    private final ProtocolManager protocolManager;

    protected ProtocolLibPacketListener(iDisguise addon, ListenerPriority listenerPriority, PacketType ... arrpacketType) {
        super(addon, listenerPriority, arrpacketType);
        
        this.addon = addon;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }
    
    protected iDisguise getAddon() {
    	return this.addon;
    }
    
    protected ProtocolManager getProtocolManager() {
    	return this.protocolManager;
    }
}

