package org.valdi.entities.management.profile;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class GameProfileHelper {
	private static final GameProfileHelper instance;
	
	private final Map<String, WrappedGameProfile> profilesByName = new ConcurrentHashMap<>();
	private final Map<UUID, WrappedGameProfile> profilesById = new ConcurrentHashMap<>();
	
	static {
		instance = new GameProfileHelper();
	}
	
	public static GameProfileHelper getInstance() {
		return instance;
	}
	
	public WrappedGameProfile getGameProfile(UUID uniqueId, String skinName, String displayName) {
		WrappedGameProfile localProfile = new WrappedGameProfile(uniqueId, displayName.length() <= 16 ? displayName : skinName);
		if(profilesByName.containsKey(skinName.toLowerCase(Locale.ENGLISH))) {
			localProfile.getProperties().putAll(profilesByName.get(skinName.toLowerCase(Locale.ENGLISH)).getProperties());
		}
		return localProfile;
	}

}
