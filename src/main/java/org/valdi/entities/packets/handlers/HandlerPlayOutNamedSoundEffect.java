package org.valdi.entities.packets.handlers;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.DisguiseType;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.Sounds;
import org.valdi.entities.management.util.EntityIdList;
import org.valdi.entities.packets.PacketOptions;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

public class HandlerPlayOutNamedSoundEffect extends ProtocolLibPacketListener {

	public HandlerPlayOutNamedSoundEffect(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.NAMED_SOUND_EFFECT);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		if(!PacketOptions.replaceSoundEffects) {
			//iDisguise.getInstance().getLogger().info("Skipping sound packet.");
			return;
		}
		
		Player observer = e.getPlayer();
		Sound sound = e.getPacket().getSoundEffects().read(0);
		int x = e.getPacket().getIntegers().read(0) / 8;
		int y = e.getPacket().getIntegers().read(1) / 8;
		int z = e.getPacket().getIntegers().read(2) / 8;
		
		LivingEntity livingEntity = EntityIdList.getClosestEntity(new Location(observer.getWorld(), x, y, z), 1.0);
		if(livingEntity != null && livingEntity != observer && DisguiseManager.isDisguisedTo(livingEntity, observer)) {
			//iDisguise.getInstance().getLogger().info("Analizing sound " + sound.name() + ".");
			Sound newSound = Sounds.replaceSoundEffect(DisguiseType.fromEntityType(livingEntity.getType()), sound, DisguiseManager.getDisguise(livingEntity));
			if(sound != newSound) {
				if(newSound == null) {
					e.setCancelled(true);
					return;
				}

				//iDisguise.getInstance().getLogger().info("New sound: " + newSound.name() + ".");
				e.getPacket().getSoundEffects().write(0, newSound);
			}
		}	
	}

}
