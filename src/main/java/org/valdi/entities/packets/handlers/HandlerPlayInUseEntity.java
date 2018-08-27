package org.valdi.entities.packets.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.api.PlayerInteractDisguisedPlayerEvent;
import org.valdi.entities.disguise.DisguiseType;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.util.EntityIdList;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

public class HandlerPlayInUseEntity extends ProtocolLibPacketListener {

	public HandlerPlayInUseEntity(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Client.USE_ENTITY);
	}
	
	@Override
	public void onPacketReceiving(PacketEvent e) {
		Player observer = e.getPlayer();
		int entityId = e.getPacket().getIntegers().read(0);
		
		final LivingEntity livingEntity = EntityIdList.getEntityByEntityId(entityId);
		boolean attack = e.getPacket().getEntityUseActions().read(0) == EntityUseAction.ATTACK;
		if(livingEntity != null && livingEntity != observer && DisguiseManager.isDisguisedTo(livingEntity, observer) && !attack) {
			DisguiseType type = DisguiseManager.getDisguise(livingEntity).getType();
			if(type == DisguiseType.SHEEP || type == DisguiseType.WOLF) {
				Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), () -> {
						DisguiseManager.resendPackets(livingEntity);
						observer.updateInventory();
				}, 2L);
			}
			if(livingEntity instanceof Player) Bukkit.getPluginManager().callEvent(new PlayerInteractDisguisedPlayerEvent(observer, (Player)livingEntity));
		}
	}

}
