package org.valdi.entities.packets.handlers;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.MobDisguise;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.util.EntityIdList;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

public class HandlerPlayOutEntityMetadata extends ProtocolLibPacketListener {

	public HandlerPlayOutEntityMetadata(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_METADATA);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		Player observer = e.getPlayer();
		int entityId = e.getPacket().getIntegers().read(0);
		
		final LivingEntity livingEntity = EntityIdList.getEntityByEntityId(entityId);
		if(livingEntity != null && livingEntity != observer && DisguiseManager.isDisguisedTo(livingEntity, observer)/* && !(DisguiseManager.getDisguise(livingEntity) instanceof PlayerDisguise)*/) {
			boolean living = DisguiseManager.getDisguise(livingEntity) instanceof MobDisguise;
			List<WrappedWatchableObject> metadataList = e.getPacket().getWatchableCollectionModifier().read(0);
			Iterator<WrappedWatchableObject> it = metadataList.iterator();
			while(it.hasNext()) {
				WrappedWatchableObject metadataItem = it.next();
				int metadataId = metadataItem.getWatcherObject().getIndex();
				if(living) {
					if(metadataId != 0 && metadataId != 6 && metadataId != 7 && metadataId != 8 && metadataId != 9 && metadataId != 10) {
						it.remove();
					}
				} else {
					if(metadataId != 0) {
						it.remove();
					}
				}
			}
			e.getPacket().getWatchableCollectionModifier().write(0, metadataList);
		}
	}

}
