package org.valdi.entities.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import java.util.HashSet;
import java.util.Set;

import org.valdi.entities.iDisguise;
import org.valdi.entities.packets.handlers.HandlerPlayInUseEntity;
import org.valdi.entities.packets.handlers.HandlerPlayOutAnimation;
import org.valdi.entities.packets.handlers.HandlerPlayOutBed;
import org.valdi.entities.packets.handlers.HandlerPlayOutCollect;
import org.valdi.entities.packets.handlers.HandlerPlayOutEntity;
import org.valdi.entities.packets.handlers.HandlerPlayOutEntityDestroy;
import org.valdi.entities.packets.handlers.HandlerPlayOutEntityMetadata;
import org.valdi.entities.packets.handlers.HandlerPlayOutEntityTeleport;
import org.valdi.entities.packets.handlers.HandlerPlayOutNamedEntitySpawn;
import org.valdi.entities.packets.handlers.HandlerPlayOutNamedSoundEffect;
import org.valdi.entities.packets.handlers.HandlerPlayOutPlayerInfo;
import org.valdi.entities.packets.handlers.HandlerPlayOutScoreboardScore;
import org.valdi.entities.packets.handlers.HandlerPlayOutScoreboardTeam;
import org.valdi.entities.packets.handlers.HandlerPlayOutSpawnEntityLiving;
import org.valdi.entities.packets.handlers.HandlerPlayOutUpdateAttributes;

public class ProtocolLibPacketsManager {
    protected final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private final Set<ProtocolLibPacketListener> activePacketHandlerModules = new HashSet<ProtocolLibPacketListener>();

    public ProtocolLibPacketsManager(final iDisguise addon) {
    	this.addPacketHandlerModule(new HandlerPlayInUseEntity(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutAnimation(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutBed(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutCollect(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutEntity(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutEntityDestroy(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutEntityMetadata(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutEntityTeleport(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutNamedEntitySpawn(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutNamedSoundEffect(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutPlayerInfo(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutScoreboardScore(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutScoreboardTeam(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutSpawnEntityLiving(addon));
    	this.addPacketHandlerModule(new HandlerPlayOutUpdateAttributes(addon));
    }
    
    public ProtocolManager getProtocolManager() {
    	return this.protocolManager;
    }

    public void addPacketHandlerModule(ProtocolLibPacketListener packetHandlerModule) {
        this.activePacketHandlerModules.add(packetHandlerModule);
        this.getProtocolManager().addPacketListener(packetHandlerModule);
    }

    public void removePacketHandlerModule(ProtocolLibPacketListener packetHandlerModule) {
        this.getProtocolManager().removePacketListener(packetHandlerModule);
        this.activePacketHandlerModules.remove(packetHandlerModule);
    }

    public Set<ProtocolLibPacketListener> getActivePacketHandlerModules() {
        return this.activePacketHandlerModules;
    }

}

