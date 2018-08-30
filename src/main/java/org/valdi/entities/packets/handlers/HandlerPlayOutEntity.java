package org.valdi.entities.packets.handlers;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.DisguiseType;
import org.valdi.entities.disguise.FallingBlockDisguise;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.util.EntityIdList;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class HandlerPlayOutEntity extends ProtocolLibPacketListener {

	public HandlerPlayOutEntity(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		Player observer = e.getPlayer();
		int entityId = e.getPacket().getIntegers().read(0);
		
		final LivingEntity livingEntity = EntityIdList.getEntityByEntityId(entityId);
		if(livingEntity != null && livingEntity != observer && DisguiseManager.isDisguisedTo(livingEntity, observer)) {
			if(DisguiseManager.getDisguise(livingEntity).getType().equals(DisguiseType.FALLING_BLOCK)) {
				if(DisguiseManager.getDisguise(livingEntity) instanceof FallingBlockDisguise && ((FallingBlockDisguise)DisguiseManager.getDisguise(livingEntity)).onlyBlockCoordinates()) {
					PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
					packet.getIntegers().write(0, livingEntity.getEntityId());
					packet.getDoubles().write(0, Math.floor(livingEntity.getLocation().getX()) + 0.5);
					packet.getDoubles().write(1, Math.floor(livingEntity.getLocation().getY()));
					packet.getDoubles().write(2, Math.floor(livingEntity.getLocation().getZ()) + 0.5);
					packet.getBytes().write(0, (byte)(livingEntity.getLocation().getYaw() * 256 / 360));
					packet.getBytes().write(1, (byte)(livingEntity.getLocation().getPitch() * 256 / 360));
					packet.getBooleans().write(0, e.getPacket().getBooleans().read(0));
					
					e.setCancelled(true);
					try {
						this.sendPacket(observer, packet);
					} catch (InvocationTargetException ex) {
						ex.printStackTrace();
					}
				}
			} else if(DisguiseManager.getDisguise(livingEntity).getType().equals(DisguiseType.ENDER_DRAGON) ^ livingEntity instanceof EnderDragon) {
				byte yaw = e.getPacket().getBytes().read(0);
				if(yaw < 0) {
					yaw += 128;
				} else {
					yaw -= 128;
				}
				e.getPacket().getBytes().write(0, yaw);
			} 
		}
	}

}
