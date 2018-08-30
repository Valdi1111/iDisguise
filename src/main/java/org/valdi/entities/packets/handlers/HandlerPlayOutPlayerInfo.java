package org.valdi.entities.packets.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.Disguise;
import org.valdi.entities.disguise.PlayerDisguise;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.profile.GameProfileHelper;
import org.valdi.entities.packets.PacketOptions;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import net.minecraft.server.v1_12_R1.ChatModifier;
import net.minecraft.server.v1_12_R1.EnumChatFormat;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;

public class HandlerPlayOutPlayerInfo extends ProtocolLibPacketListener {

	public HandlerPlayOutPlayerInfo(iDisguise addon) {
		super(addon, ListenerPriority.HIGH, PacketType.Play.Server.PLAYER_INFO);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		Player observer = e.getPlayer();
		
		List<PlayerInfoData> infos = e.getPacket().getPlayerInfoDataLists().read(0);
		if(infos == null || infos.isEmpty()) {
			return;
		}
		
		List<PlayerInfoData> infosToAdd = new ArrayList<>();
		List<PlayerInfoData> infosToRemove = new ArrayList<>();
		for(PlayerInfoData info : infos) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(info.getProfile().getUUID());
			if(offlinePlayer != null && offlinePlayer != observer && DisguiseManager.isDisguisedTo(offlinePlayer, observer)) {
				PlayerInfoData newInfo = getPlayerInfo(offlinePlayer, info.getLatency(), info.getGameMode(), info.getDisplayName());
				infosToRemove.add(info);
				if(newInfo != null) {
					infosToAdd.add(newInfo);
				}
			}
		}
		
		infos.removeAll(infosToRemove);
		infos.addAll(infosToAdd);
		
		e.getPacket().getPlayerInfoDataLists().write(0, infos);
	}

	private PlayerInfoData getPlayerInfo(OfflinePlayer offlinePlayer, int latency, NativeGameMode gameMode, WrappedChatComponent displayName) {
		Disguise disguise = DisguiseManager.getDisguise(offlinePlayer);
		try {
			if(disguise == null) {
				WrappedGameProfile profile = offlinePlayer.isOnline() ? WrappedGameProfile.fromPlayer(offlinePlayer.getPlayer()) : WrappedGameProfile.fromOfflinePlayer(offlinePlayer);
				return new PlayerInfoData(profile, latency, gameMode, displayName);
			} else if(disguise instanceof PlayerDisguise) {
				if(PacketOptions.modifyPlayerListEntry) {
					WrappedGameProfile profile = GameProfileHelper.getInstance().getGameProfile(formatUniqueId(offlinePlayer.getUniqueId()), ((PlayerDisguise)disguise).getSkinName(), ((PlayerDisguise)disguise).getDisplayName());
					WrappedChatComponent tabName = displayName != null ? WrappedChatComponent.fromJson(this.fromComponent(displayName, EnumChatFormat.WHITE).replace(offlinePlayer.getName(), ((PlayerDisguise)disguise).getDisplayName())) : null;
					return new PlayerInfoData(profile, latency, gameMode, tabName);
				} else {
					WrappedGameProfile profile = GameProfileHelper.getInstance().getGameProfile(formatUniqueId(offlinePlayer.getUniqueId()), ((PlayerDisguise)disguise).getSkinName(), ((PlayerDisguise)disguise).getDisplayName());
					WrappedChatComponent tabName = displayName != null ? displayName : WrappedChatComponent.fromChatMessage(offlinePlayer.isOnline() ? offlinePlayer.getPlayer().getPlayerListName() : offlinePlayer.getName())[0];
					return new PlayerInfoData(profile, latency, gameMode, tabName);
				}
			} else if(!PacketOptions.modifyPlayerListEntry) {
				WrappedGameProfile profile = offlinePlayer.isOnline() ? WrappedGameProfile.fromPlayer(offlinePlayer.getPlayer()) : WrappedGameProfile.fromOfflinePlayer(offlinePlayer);
				return new PlayerInfoData(profile, latency, gameMode, displayName);
			}
		} catch(Exception e) {
			iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot construct the required player info.", e);
		}
		return null;
	}
	
	private UUID formatUniqueId(UUID origin) {
		return PacketOptions.bungeeCord ? new UUID(origin.getMostSignificantBits() & 0xFFFFFFFFFFFF0FFFL | 0x0000000000005000, origin.getLeastSignificantBits()) : origin;
	}
	
	private String fromComponent(WrappedChatComponent component, EnumChatFormat defaultColor) {
	if (component == null) return "";
	StringBuilder out = new StringBuilder();

	for (IChatBaseComponent c : (IChatBaseComponent) component.getHandle()) {
		ChatModifier modi = c.getChatModifier();
		out.append(modi.getColor() == null ? defaultColor : modi.getColor());
		if (modi.isBold()) {
			out.append(EnumChatFormat.BOLD);
		}
		if (modi.isItalic()) {
			out.append(EnumChatFormat.ITALIC);
		}
		if (modi.isUnderlined()) {
			out.append(EnumChatFormat.UNDERLINE);
		}
		if (modi.isStrikethrough()) {
			out.append(EnumChatFormat.STRIKETHROUGH);
		}
		if (modi.isRandom()) {
			out.append(EnumChatFormat.OBFUSCATED);
		}
			out.append(c.getText());
		}
		return out.toString().replaceFirst("^(" + defaultColor + ")*", "");
	}

}
