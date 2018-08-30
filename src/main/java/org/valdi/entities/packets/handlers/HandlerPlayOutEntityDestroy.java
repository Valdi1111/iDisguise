package org.valdi.entities.packets.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.PlayerDisguise;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.profile.GameProfileHelper;
import org.valdi.entities.management.util.EntityIdList;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;

public class HandlerPlayOutEntityDestroy extends ProtocolLibPacketListener {

	public HandlerPlayOutEntityDestroy(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_DESTROY);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		Player observer = e.getPlayer();
		int[] entityIds = e.getPacket().getIntegerArrays().read(0);
		
		// construct the player info packet
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
		packet.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
		List<PlayerInfoData> infos = new ArrayList<>();
		
		for(int entityId : entityIds) {
			LivingEntity livingEntity = EntityIdList.getEntityByEntityId(entityId);
			if(livingEntity != null && !(livingEntity instanceof Player) && DisguiseManager.isDisguisedTo(livingEntity, observer) && DisguiseManager.getDisguise(livingEntity) instanceof PlayerDisguise) {
				infos.add(new PlayerInfoData(GameProfileHelper.getInstance().getGameProfile(livingEntity.getUniqueId(), "", ""), 35, NativeGameMode.NOT_SET, null));
			}
		}

		packet.getPlayerInfoDataLists().write(0, infos);
		if(!infos.isEmpty()) {
			try {
				this.sendPacket(observer, packet);
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			}
		}
	}

}
