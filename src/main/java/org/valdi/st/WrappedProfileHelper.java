package org.valdi.st;

import static org.valdi.entities.management.Reflection.MinecraftServer_getServer;
import static org.valdi.entities.management.Reflection.MinecraftServer_getUserCache;
import static org.valdi.entities.management.Reflection.UserCache_getProfileById;
import static org.valdi.entities.management.Reflection.UserCache_putProfile;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.inventivetalent.mcwrapper.auth.GameProfileWrapper;
import org.inventivetalent.mcwrapper.auth.properties.PropertyWrapper;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.Disguise;
import org.valdi.entities.disguise.PlayerDisguise;
import org.valdi.entities.management.DisguiseManager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class WrappedProfileHelper {
	private static final WrappedProfileHelper instance;
	
	private final Map<String, GameProfile> profilesByName = new ConcurrentHashMap<String, GameProfile>();
	
	private final Map<UUID, Object> currentlyLoadingById = new ConcurrentHashMap<UUID, Object>();
	
	static {
		instance = new WrappedProfileHelper();
	}
	
	public static WrappedProfileHelper getInstance() {
		return instance;
	}

	public void loadGameProfileAsynchronously(String skinName, final SkinContainer profile) {
		final String name = skinName.toLowerCase(Locale.ENGLISH);
		Bukkit.getScheduler().runTaskAsynchronously(iDisguise.getInstance(), () -> loadGameProfile(name, profile));
	}
	
	private void loadGameProfile(String name, SkinContainer container) {
		//Bukkit.getLogger().info(container.getId() + " - " + container.getValue() + " - " + container.getSignature());
		UUID uniqueId = container.getId();
		try {
		if(currentlyLoadingById.containsKey(uniqueId)) {
			synchronized(currentlyLoadingById.get(uniqueId)) {
				try {
					currentlyLoadingById.get(uniqueId).wait(10000L);
				} catch (InterruptedException e) {
				}
				return;
			}
		}
		currentlyLoadingById.put(uniqueId, new Object());
		GameProfile profile = (GameProfile)UserCache_getProfileById.invoke(MinecraftServer_getUserCache.invoke(MinecraftServer_getServer.invoke(null)), uniqueId);
		if(profile == null) {
			profile = new GameProfile(uniqueId, name);
		}
		//PropertyWrapper prop = p.getProperties().values().iterator().next();
		if(!profile.getProperties().containsKey("textures")) {
			profile.getProperties().put("textures", new Property("textures", container.getValue(), container.getSignature()));
			profilesByName.put(profile.getName().toLowerCase(Locale.ENGLISH), profile);
			// put profile into user cache
			UserCache_putProfile.invoke(MinecraftServer_getUserCache.invoke(MinecraftServer_getServer.invoke(null)), profile);
			final String skinName = name;
			Bukkit.getScheduler().runTask(iDisguise.getInstance(), new Runnable() {

				@Override
				public void run() {
					for(Object disguisable : DisguiseManager.getDisguisedEntities()) {
						if(!(disguisable instanceof LivingEntity)) {
							return;
						}
						
						LivingEntity livingEntity = (LivingEntity)disguisable;
						Disguise disguise = DisguiseManager.getDisguise(livingEntity);
						if(disguise instanceof PlayerDisguise && ((PlayerDisguise)disguise).getSkinName().equalsIgnoreCase(skinName)) {
							DisguiseManager.resendPackets(livingEntity);
						}
					}
				}
				
			});
		}
		synchronized(currentlyLoadingById.get(uniqueId)) {
			currentlyLoadingById.remove(uniqueId).notifyAll();
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
