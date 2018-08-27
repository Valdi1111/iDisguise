package org.valdi.st;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.apihelper.API;
import org.inventivetalent.apihelper.APIManager;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.valdi.st.event.NameReplacer;
import org.valdi.st.event.RandomNickRequestEvent;
import org.valdi.st.event.RandomSkinRequestEvent;

public class SkinAPI implements API, Listener {

	static final Random random = new Random();

	protected static ISkinManager skinManager;
	//	private static UUIDResolver uuidResolver;

	protected static PacketListener packetListener;

	public static ISkinManager getSkinManager() {
		return skinManager;
	}

	//	public static UUIDResolver getUuidResolver() {
	//		return uuidResolver;
	//	}

	/**
	 * Replaces all specified names in the string and calls the {@link NameReplacer} for every name
	 *
	 * @param original       original message
	 * @param namesToReplace names to replace
	 * @param replacer       {@link NameReplacer}
	 * @param ignoreCase     whether to ignore case
	 * @return the replaced message
	 */
	public static String replaceNames(@Nonnull final String original, @Nonnull final Iterable<String> namesToReplace, @Nonnull final NameReplacer replacer, boolean ignoreCase) {
		if (original == null) { return null; }
		if (namesToReplace == null) { return original; }
		if (replacer == null) { return original; }
		String replaced = original;
		for (String name : namesToReplace) {
			Pattern pattern = Pattern.compile((ignoreCase ? "(?i)" : "") + name);
			Matcher matcher = pattern.matcher(replaced);

			StringBuffer replacementBuffer = new StringBuffer();
			while (matcher.find()) {
				String replace = replacer.replace(name);
				matcher.appendReplacement(replacementBuffer, replace);
			}
			matcher.appendTail(replacementBuffer);

			replaced = replacementBuffer.toString();
		}
		return replaced;
	}

	/**
	 * @return The names of all nicked players (only works if the plugin is installed)
	 */
	public static Set<String> getNickedPlayerNames() {
		Set<String> nickedPlayerNames = new HashSet<>();
		if (getSkinManager().isSimple()) { return nickedPlayerNames; }
		for (String nick : getSkinManager().getUsedNicks()) {
			for (UUID uuid : getSkinManager().getPlayersWithNick(nick)) {
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
				if (offlinePlayer != null) {
					nickedPlayerNames.add(offlinePlayer.getName());
				}
			}
		}
		return nickedPlayerNames;
	}

	public static String getRandomNick(Collection<String> nicks) {
		RandomNickRequestEvent event = new RandomNickRequestEvent(new ArrayList<>(nicks));
		Bukkit.getPluginManager().callEvent(event);
		if (event.getPossibilities().isEmpty()) { return ""; }
		return ((List<String>) event.getPossibilities()).get(random.nextInt(event.getPossibilities().size()));
	}

	public static String getRandomSkin(Collection<String> skins) {
		RandomSkinRequestEvent event = new RandomSkinRequestEvent(new ArrayList<>(skins));
		Bukkit.getPluginManager().callEvent(event);
		if (event.getPossibilities().isEmpty()) { return ""; }
		return ((List<String>) event.getPossibilities()).get(random.nextInt(event.getPossibilities().size()));
	}

	@Override
	public void load() {
		APIManager.require(PacketListenerAPI.class, null);
	}

	@Override
	public void init(Plugin plugin) {
		APIManager.initAPI(PacketListenerAPI.class);

		APIManager.registerEvents(this, this);

		skinManager = SkinManager.getInstance();
		//		uuidResolver = new UUIDResolver(plugin, 3600000/* 1 hour */);

		PacketHandler.addHandler(packetListener = new PacketListener(plugin));
	}

	@Override
	public void disable(Plugin plugin) {
		PacketHandler.removeHandler(packetListener);
		APIManager.disableAPI(PacketListenerAPI.class);
	}

}
