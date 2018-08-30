package org.valdi.entities.management;

import static org.valdi.entities.management.Reflection.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;
import org.valdi.entities.disguise.Disguise;
import org.valdi.entities.disguise.PlayerDisguise;
import org.valdi.entities.management.hooks.ScoreboardHooks;
import org.valdi.entities.management.profile.GameProfileHelper;
import org.valdi.entities.management.util.DisguiseMap;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;

public final class DisguiseManager {
	
	private DisguiseManager() {}
	
	private static DisguiseMap disguiseMap = DisguiseMap.emptyMap();
	private static Set<UUID> seeThroughSet = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
	
	public static synchronized void disguise(OfflinePlayer offlinePlayer, Disguise disguise) {
		if(offlinePlayer.isOnline()) {
			disguise(offlinePlayer.getPlayer(), disguise);
		} else {
			disguiseMap.updateDisguise(offlinePlayer.getUniqueId(), disguise);
		}
	}
	
	public static synchronized void disguise(Player player, Disguise disguise) {
		disguise((LivingEntity) player, disguise);
	}
	
	public static synchronized void disguise(LivingEntity livingEntity, Disguise disguise) {
		// do nothing if entity is invalid (dead or despawned)
		if(!livingEntity.isValid()) return;
		
		hideEntityFromAll(livingEntity);
		disguiseMap.updateDisguise(livingEntity.getUniqueId(), disguise);
		showEntityToAll(livingEntity);
	}
	
	public static synchronized Disguise undisguise(OfflinePlayer offlinePlayer) {
		if(offlinePlayer.isOnline()) {
			return undisguise(offlinePlayer.getPlayer());
		} else {
			return disguiseMap.removeDisguise(offlinePlayer.getUniqueId());
		}
	}
	
	public static synchronized Disguise undisguise(Player player) {
		return undisguise((LivingEntity) player);
	}
	
	public static synchronized Disguise undisguise(LivingEntity livingEntity) {
		Disguise disguise = disguiseMap.getDisguise(livingEntity.getUniqueId());
		if(disguise == null) {
			return null;
		}
		hideEntityFromAll(livingEntity);
		disguiseMap.removeDisguise(livingEntity.getUniqueId());
		showEntityToAll(livingEntity);
		return disguise;
	}
	
	public static synchronized void undisguiseAll() {
		for(Object disguisable : getDisguisedEntities()) {
			if(disguisable instanceof LivingEntity) {
				undisguise((LivingEntity)disguisable);
			} else if(disguisable instanceof OfflinePlayer) {
				undisguise((OfflinePlayer)disguisable);
			}
		}
	}
	
	public static boolean isDisguised(OfflinePlayer offlinePlayer) {
		return disguiseMap.isDisguised(offlinePlayer.getUniqueId());
	}
	
	public static boolean isDisguised(Player player) {
		return disguiseMap.isDisguised(player.getUniqueId());
	}
	
	public static boolean isDisguised(LivingEntity livingEntity) {
		return disguiseMap.isDisguised(livingEntity.getUniqueId());
	}
	
	public static boolean isDisguisedTo(OfflinePlayer offlinePlayer, Player observer) {
		return disguiseMap.isDisguised(offlinePlayer.getUniqueId()) && disguiseMap.getDisguise(offlinePlayer.getUniqueId()).isVisibleTo(observer);
	}
	
	public static boolean isDisguisedTo(Player player, Player observer) {
		return disguiseMap.isDisguised(player.getUniqueId()) && disguiseMap.getDisguise(player.getUniqueId()).isVisibleTo(observer);
	}
	
	public static boolean isDisguisedTo(LivingEntity livingEntity, Player observer) {
		return disguiseMap.isDisguised(livingEntity.getUniqueId()) && disguiseMap.getDisguise(livingEntity.getUniqueId()).isVisibleTo(observer);
	}
	
	public static Disguise getDisguise(OfflinePlayer offlinePlayer) {
		return disguiseMap.getDisguise(offlinePlayer.getUniqueId());
	}
	
	public static Disguise getDisguise(Player player) {
		return disguiseMap.getDisguise(player.getUniqueId());
	}
	
	public static Disguise getDisguise(LivingEntity livingEntity) {
		return disguiseMap.getDisguise(livingEntity.getUniqueId());
	}
	
	public static int getNumberOfDisguisedPlayers() {
		int i = 0;
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(isDisguised(player)) {
				i++;
			}
		}
		return i;
	}
	
	public static Set<Object> getDisguisedEntities() {
		Set<UUID> origin = disguiseMap.getDisguisedEntities();
		Set<Object> destination = new HashSet<Object>();
		for(UUID disguisable : origin) {
			Entity entity = Bukkit.getEntity(disguisable);
			if(entity != null) {
				destination.add(entity);
			} else {
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(disguisable);
				if(offlinePlayer != null) {
					destination.add(offlinePlayer);
				}
			}
		}
		return destination;
	}
	
	public static Map<?, Disguise> getDisguises() {
		return disguiseMap.getMap();
	}
	
	public static void updateDisguises(Map<?, Disguise> map) {
		disguiseMap = DisguiseMap.fromMap(map);
	}
	
	public static boolean canSeeThrough(OfflinePlayer offlinePlayer) {
		return seeThroughSet.contains(offlinePlayer.getUniqueId());
	}
	
	public static void setSeeThrough(OfflinePlayer offlinePlayer, boolean seeThrough) {
		if(seeThroughSet.contains(offlinePlayer.getUniqueId()) == seeThrough) {
			return;
		}
		
		if(offlinePlayer.isOnline()) {
			Player observer = offlinePlayer.getPlayer();
			for(Object disguisable : getDisguisedEntities()) {
				if(disguisable instanceof LivingEntity) {
					hideEntityFromOne(observer, (LivingEntity)disguisable);
				} else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
					hidePlayerFromOne(observer, ((OfflinePlayer)disguisable).getPlayer());
				}
			}
			if(seeThrough) {
				seeThroughSet.add(offlinePlayer.getUniqueId());
			} else {
				seeThroughSet.remove(offlinePlayer.getUniqueId());
			}
			for(Object disguisable : getDisguisedEntities()) {
				if(disguisable instanceof LivingEntity) {
					showEntityToOne(observer, (LivingEntity)disguisable);
				} else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
					showPlayerToOne(observer, ((OfflinePlayer)disguisable).getPlayer());
				}
			}
		} else {
			if(seeThrough) {
				seeThroughSet.add(offlinePlayer.getUniqueId());
			} else {
				seeThroughSet.remove(offlinePlayer.getUniqueId());
			}
		}
	}
	
	private static void hideEntityFromAll(LivingEntity livingEntity) {
		// do nothing if entity is invalid (dead or despawned)
		if(!livingEntity.isValid()) {
			return;
		}
		
		// use other function if the entity is a player
		if(livingEntity instanceof Player) {
			hidePlayerFromAll((Player) livingEntity);
			return;
		}
		
		for(Player observer : Bukkit.getOnlinePlayers()) {
			hideEntityFromOne(observer, livingEntity);
		}
		
		// we don't care about scoreboard packets for entities
	}
	
	private static void hideEntityFromOne(Player observer, LivingEntity livingEntity) {
		// do nothing if entity is invalid (dead or despawned)
		if(!livingEntity.isValid()) {
			return;
		}
		
		// use other function if the entity is a player
		if(livingEntity instanceof Player) {
			hidePlayerFromOne(observer, (Player) livingEntity);
			return;
		}
		
		try {
			// clear the entity tracker entry
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
			if(entityTrackerEntry != null) {
				EntityTrackerEntry_clear.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
			}
			
			// remove player info if necessary
			if(isDisguisedTo(livingEntity, observer) && getDisguise(livingEntity) instanceof PlayerDisguise) {
				PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
				packet.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
				List<PlayerInfoData> infos = new ArrayList<>();
				infos.add(new PlayerInfoData(GameProfileHelper.getInstance().getGameProfile(livingEntity.getUniqueId(), "", ""), 35, NativeGameMode.NOT_SET, null));
				packet.getPlayerInfoDataLists().write(0, infos);
				
				ProtocolLibrary.getProtocolManager().sendServerPacket(observer, packet);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// we don't care about scoreboard packets for entities
	}
	
	private static void hidePlayerFromAll(Player player) {
		for(Player observer : Bukkit.getOnlinePlayers()) {
			if(observer == player) {
				continue;
			}
			
			hidePlayerFromOne(observer, player);
		}
	}
	
	private static void hidePlayerFromOne(Player observer, Player player) {
		// hide the player
		observer.hidePlayer(player);
		
		// do we care about scoreboard packets?
		if(!PacketHandler.modifyScoreboardPackets) {
			return;
		}
		
		// construct the scoreboard packets
		List<PacketContainer> packets = new ArrayList<>();
		Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
		if(team != null) {
			try {
				PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
				packet.getStrings().write(0, team.getName());
				packet.getIntegers().write(0, 4);
				packet.getStringArrays().write(0, new String[] { player.getName() });
				
				packets.add(packet);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player.getName());
		for(Score score : scores) {
			try {
				PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
				packet.getStrings().write(0, player.getName());
				packet.getStrings().write(1, score.getObjective().getName());
				packet.getScoreboardActions().write(0, ScoreboardAction.REMOVE);
				
				packets.add(packet);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		// send the scoreboard packets
		for(PacketContainer packet : packets) {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(observer, packet);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void showEntityToAll(final LivingEntity livingEntity) {
		// do nothing if entity is invalid (dead or despawned)
		if(!livingEntity.isValid()) {
			return;
		}
		
		// use other function if the entity is a player
		if(livingEntity instanceof Player) {
			showPlayerToAll((Player) livingEntity);
			return;
		}
		
		// do the actual sending and stuff
		for(Player observer : Bukkit.getOnlinePlayers()) {
			showEntityToOne(observer, livingEntity);
		}
		
		// we don't care about scoreboard packets for entities
	}
	
	private static void showEntityToOne(final Player observer, final LivingEntity livingEntity) {
		// do nothing if entity is invalid (dead or despawned)
		if(!livingEntity.isValid()) {
			return;
		}
		
		// use other function if the entity is a player
		if(livingEntity instanceof Player) {
			showPlayerToOne(observer, (Player) livingEntity);
			return;
		}
		
		// update the entity tracker entry
		try {
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
			if(entityTrackerEntry != null) {
				EntityTrackerEntry_updatePlayer.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// we don't care about scoreboard packets for entities
	}
	
	private static void showPlayerToAll(final Player player) {
		for(Player observer : Bukkit.getOnlinePlayers()) {
			if(observer == player) continue;
			
			showPlayerToOne(observer, player, false);
		}
		
		if(PacketHandler.modifyScoreboardPackets) {
			// update scoreboard hooks
			ScoreboardHooks.updatePlayer(player);
		}
	}
	
	private static void showPlayerToOne(final Player observer, final Player player) {
		showPlayerToOne(observer, player, true);
	}
	
	private static void showPlayerToOne(final Player observer, final Player player, boolean scoreHook) {
		// show the player
		observer.showPlayer(player);
		
		// do we care about scoreboard packets?
		if(!PacketHandler.modifyScoreboardPackets) {
			return;
		}
		
		// construct the scoreboard packets
		List<PacketContainer> packets = new ArrayList<>();
		Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
		if(team != null) {
			try {
				PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
				packet.getStrings().write(0, team.getName());
				packet.getIntegers().write(0, 3);
				packet.getStringArrays().write(0, new String[] { player.getName() });
				
				packets.add(packet);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player.getName());
		for(Score score : scores) {
			try {
				PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
				packet.getStrings().write(0, player.getName());
				packet.getStrings().write(1, score.getObjective().getName());
				packet.getIntegers().write(0, score.getScore());
				packet.getScoreboardActions().write(0, ScoreboardAction.CHANGE);
				
				packets.add(packet);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		// send the scoreboard packets
		for(PacketContainer packet : packets) {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(observer, packet);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(scoreHook) {
			// update scoreboard hooks
			ScoreboardHooks.updatePlayer(player);
		}
	}
	
	public static void resendPackets(Player player) {
		hidePlayerFromAll(player);
		showPlayerToAll(player);
	}
	
	public static void resendPackets(LivingEntity livingEntity) {
		hideEntityFromAll(livingEntity);
		showEntityToAll(livingEntity);
	}
	
	public static void resendPackets() {
		for(Object disguisable : getDisguisedEntities()) {
			if(disguisable instanceof LivingEntity) {
				hideEntityFromAll((LivingEntity)disguisable);
				showEntityToAll((LivingEntity)disguisable);
			} else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
				hidePlayerFromAll(((OfflinePlayer)disguisable).getPlayer());
				showPlayerToAll(((OfflinePlayer)disguisable).getPlayer());
			}
		}
	}
	
}