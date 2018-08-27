package org.valdi.entities.management.channel;

import static org.valdi.entities.management.Reflection.*;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.valdi.entities.iDisguise;
import org.valdi.entities.management.VersionHelper;

@Deprecated
public final class ChannelInjector {

	@Deprecated
	private ChannelInjector() {}

	@Deprecated
	private static final Map<Player, InjectedPlayerConnection> playerConnectionMap = new ConcurrentHashMap<Player, InjectedPlayerConnection>();
	@Deprecated
	private static Constructor<?> playerConnectionConstructor;

	@Deprecated
	public static void init() {
		try {
			playerConnectionConstructor = Class.forName("org.valdi.entities.management.channel.InjectedPlayerConnection" + VersionHelper.getVersionCode().replaceAll("[^0-9]*", "")).getConstructor(Player.class, Object.class);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot find the required player connection constructor.", e);
			}
		}
	}

	@Deprecated
	public static synchronized void inject(Player player) {
		try {
			InjectedPlayerConnection playerConnection = (InjectedPlayerConnection)playerConnectionConstructor.newInstance(player, EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(player)));
			playerConnectionMap.put(player, playerConnection);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot inject the given player connection: " + player.getName(), e);
			}
		}
	}

	@Deprecated
	public static synchronized void remove(Player player) {
		try {
			InjectedPlayerConnection playerConnection = playerConnectionMap.remove(player);
			if(playerConnection != null) {
				playerConnection.resetToDefaultConnection();
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot remove the given player connection: " + player.getName(), e);
			}
		}
	}

	@Deprecated
	public static void injectOnlinePlayers() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			inject(player);
		}
	}

	@Deprecated
	public static void removeOnlinePlayers() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			remove(player);
		}
	}
	
}