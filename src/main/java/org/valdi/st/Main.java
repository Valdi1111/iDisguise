package org.valdi.st;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.inventivetalent.apihelper.APIManager;
import org.inventivetalent.data.async.DataCallback;
import org.inventivetalent.mcwrapper.auth.GameProfileWrapper;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.valdi.idisguise.iDisguise;
import org.valdi.idisguise.management.PacketHandler;
import org.valdi.st.event.ChatInReplacementEvent;
import org.valdi.st.event.ChatOutReplacementEvent;
import org.valdi.st.event.ChatReplacementEvent;
import org.valdi.st.event.ChatTabCompleteReplacementEvent;
import org.valdi.st.event.NameReplacementEvent;
import org.valdi.st.event.NameReplacer;
import org.valdi.st.event.NickDisguiseEvent;
import org.valdi.st.event.PlayerJoinReplacementEvent;
import org.valdi.st.event.PlayerQuitReplacementEvent;
import org.valdi.st.event.PlayerRefreshEvent;
import org.valdi.st.event.ScoreboardReplacementEvent;
import org.valdi.st.event.ScoreboardScoreReplacementEvent;
import org.valdi.st.event.ScoreboardTeamReplacementEvent;
import org.valdi.st.event.SkinDisguiseEvent;
import org.valdi.st.event.SkinLoadedEvent;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class Main implements Listener, PluginMessageListener {
	private final iDisguise plugin;
	private static Main instance;
	
	public Main(final iDisguise plugin) {
		this.plugin = plugin;
		instance = this;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public Logger getLogger() {
		return plugin.getLogger();
	}
	
	boolean replaceChatPlayer = false;
	boolean replaceChatOut = false;
	boolean replaceChatInGeneral = false;
	boolean replaceChatInCommand = false;
	boolean replaceScoreboard = false;
	boolean replaceScoreboardScore = false;
	boolean replaceScoreboardTeam = false;
	boolean replaceTabCompleteChat = false;
	
	boolean updateSelf = true;

	public Map<String, Collection<String>> randomNicks = new HashMap<>();
	public Map<String, Collection<String>> randomSkins = new HashMap<>();

	public boolean randomJoinNick = false;
	public boolean randomJoinSkin = false;

	public boolean nameSpaces = false;
	public String namesFormat = "%s";
	public String namesChatFormat = "%s�r";

	public ISkinManager getAPI() {
		return SkinAPI.getSkinManager();
	}

	public void onLoad() {
		String javaVersion = System.getProperty("java.version");
		getLogger().info("Java Version: " + javaVersion);
		int majorVersion = Integer.parseInt(javaVersion.split("\\.")[1]);
		if (majorVersion < 8) {
			getLogger().severe("Please use Java 8 or higher (is " + majorVersion + ")");
			throw new RuntimeException("NickNamer requires Java 8+");
		}

		APIManager.require(PacketListenerAPI.class, plugin);
		APIManager.registerAPI(new SkinAPI(), plugin);
	}

	public void onEnable() {
		APIManager.initAPI(PacketListenerAPI.class);
		APIManager.initAPI(SkinAPI.class);

		Bukkit.getPluginManager().registerEvents(this, plugin);

		if (PacketHandler.bungeeCord) {
			if (Bukkit.getOnlineMode()) {
				getLogger().warning("Bungeecord is enabled, but server is in online mode!");
			}
			Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "NickNamer", this);
			Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "NickNamer");
		}

		//Replace the default NickManager
		//new PluginNickManager(this);
	}

	public void onDisable() {
		APIManager.disableAPI(SkinAPI.class);
	}

	/*<V> AsyncCacheMapper.CachedDataProvider<V> initCache(AsyncDataProvider<V> provider) {
		return AsyncCacheMapper.create(provider, CacheBuilder.newBuilder()
				.expireAfterAccess(5, TimeUnit.MINUTES)
				.expireAfterWrite(10, TimeUnit.MINUTES), storageExecutor);
	}*/

	// Internal event listeners

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(final NickDisguiseEvent event) {
		if (event.isCancelled()) { return; }
		if (getAPI().isNicked(event.getDisguised().getUniqueId())) {
			event.setNick(getAPI().getNick(event.getDisguised().getUniqueId()));

			((SkinManager) getAPI()).refreshCachedNick(event.getDisguised().getUniqueId());
		} else {
			((SkinManager) getAPI()).getNick(event.getDisguised().getUniqueId(), new DataCallback<String>() {
				@Override
				public void provide(@Nullable String nick) {
					if (nick != null && !nick.equals(event.getDisguised().getName())) {
						getAPI().refreshPlayer(event.getDisguised().getUniqueId());
					}
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(final SkinDisguiseEvent event) {
		if (event.isCancelled()) { return; }
		if (getAPI().hasSkin(event.getDisguised().getUniqueId())) {
			event.setSkin(getAPI().getSkin(event.getDisguised().getUniqueId()));

			((SkinManager) getAPI()).refreshCachedSkin(event.getDisguised().getUniqueId());
			if (event.getSkin() != null) { SkinLoader.refreshCachedData(event.getSkin()); }
		} else {
			((SkinManager) getAPI()).getSkin(event.getDisguised().getUniqueId(), new DataCallback<String>() {
				@Override
				public void provide(@Nullable final String skin) {
					if (skin != null && !skin.equals(event.getDisguised().getName())) {
						GameProfileWrapper skinProfile = SkinLoader.getSkinProfile(skin);
						if (skinProfile == null) {
							Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
								@Override
								public void run() {
									SkinLoader.loadSkin(skin);
									Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
										@Override
										public void run() {
											getAPI().refreshPlayer(event.getDisguised().getUniqueId());
										}
									}, 10);
								}
							});
						} else {
							getAPI().refreshPlayer(event.getDisguised().getUniqueId());
						}
					}
				}
			});

		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(SkinLoadedEvent event) {
		if (PacketHandler.bungeeCord) {
			if (Bukkit.getOnlinePlayers().isEmpty()) {
				getLogger().warning("Cannot send skin data to Bungeecord: no players online");
				return;
			}
			sendPluginMessage(Bukkit.getOnlinePlayers().iterator().next(), "data", event.getOwner(), event.getGameProfile().toJson().toString());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerRefreshEvent event) {
		event.setSelf(updateSelf);
	}

	// Name replacement listeners
	@EventHandler(priority = EventPriority.NORMAL)
	public void on(final AsyncPlayerChatEvent event) {
		if (ChatReplacementEvent.getHandlerList().getRegisteredListeners().length > 0) {
			final String message = event.getMessage();
			Set<String> nickedPlayerNames = SkinAPI.getNickedPlayerNames();
			String replacedMessage = SkinAPI.replaceNames(message, nickedPlayerNames, new NameReplacer() {
				@Override
				public String replace(String original) {
					Player player = Bukkit.getPlayer(original);
					if (player != null) {
						NameReplacementEvent replacementEvent = new ChatReplacementEvent(player, event.getRecipients(), message, original, original);
						Bukkit.getPluginManager().callEvent(replacementEvent);
						if (replacementEvent.isCancelled()) { return original; }
						return replacementEvent.getReplacement();
					}
					return original;
				}
			}, true);
			event.setMessage(replacedMessage);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void on(final PlayerJoinEvent event) {
		if (PlayerJoinReplacementEvent.getHandlerList().getRegisteredListeners().length > 0) {
			final String message = event.getJoinMessage();
			Set<String> nickedPlayerNames = SkinAPI.getNickedPlayerNames();
			String replacedMessage = SkinAPI.replaceNames(message, nickedPlayerNames, new NameReplacer() {
				@Override
				public String replace(String original) {
					Player player = Bukkit.getPlayer(original);
					if (player != null) {
						PlayerJoinReplacementEvent replacementEvent = new PlayerJoinReplacementEvent(player, Bukkit.getOnlinePlayers(), message, original, original);
						Bukkit.getPluginManager().callEvent(replacementEvent);
						if (replacementEvent.isCancelled()) { return original; }
						return replacementEvent.getReplacement();
					}
					return original;
				}
			}, true);
			event.setJoinMessage(replacedMessage);
		}

		if (randomJoinSkin && event.getPlayer().hasPermission("nicknamer.join.skin")) {
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					String skin = null;
					for (PermissionAttachmentInfo info : event.getPlayer().getEffectivePermissions()) {
						if (info.getValue() && info.getPermission().startsWith("nicknamer.join.skin.")) {
							if (skin != null) {
								getLogger().warning(event.getPlayer().getName() + " has multiple join-skin permissions");
							}
							skin = info.getPermission().substring("nicknamer.join.skin.".length());
						}
					}
					if (skin == null) {
						event.getPlayer().chat("/randomSkin");
					} else {
						event.getPlayer().chat("/changeskin " + skin);
					}
				}
			}, 10);
		}
		if (randomJoinNick && event.getPlayer().hasPermission("nicknamer.join.nick")) {
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					String name = null;
					for (PermissionAttachmentInfo info : event.getPlayer().getEffectivePermissions()) {
						if (info.getValue() && info.getPermission().startsWith("nicknamer.join.nick.")) {
							if (name != null) {
								getLogger().warning(event.getPlayer().getName() + " has multiple join-nick permissions");
							}
							name = info.getPermission().substring("nicknamer.join.nick.".length());
						}
					}
					if (name == null) {
						event.getPlayer().chat("/randomNick");
					} else {
						// Convert tp upper case
						String tempName = name;
						name = "";
						boolean toUpper = false;
						for (int i = 0; i < tempName.length(); i++) {
							char c = tempName.charAt(i);
							if (c == '^') {// found an identifier -> continue
								toUpper = true;
							} else if (toUpper) {// change following character to upper case
								name += Character.toUpperCase(c);
								toUpper = false;
							} else {// no changes
								name += c;
							}
						}
						if (toUpper) {
							getLogger().warning("Trailing upper-case identifier in " + event.getPlayer().getName() + "'s permission: " + tempName);
						}

						event.getPlayer().chat("/nickname " + name);
					}
				}
			}, 20);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void on(final PlayerQuitEvent event) {
		if (PlayerJoinReplacementEvent.getHandlerList().getRegisteredListeners().length > 0) {
			final String message = event.getQuitMessage();
			Set<String> nickedPlayerNames = SkinAPI.getNickedPlayerNames();
			String replacedMessage = SkinAPI.replaceNames(message, nickedPlayerNames, new NameReplacer() {
				@Override
				public String replace(String original) {
					Player player = Bukkit.getPlayer(original);
					if (player != null) {
						PlayerQuitReplacementEvent replacementEvent = new PlayerQuitReplacementEvent(player, Bukkit.getOnlinePlayers(), message, original, original);
						Bukkit.getPluginManager().callEvent(replacementEvent);
						if (replacementEvent.isCancelled()) { return original; }
						return replacementEvent.getReplacement();
					}
					return original;
				}
			}, true);
			event.setQuitMessage(replacedMessage);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void on(PlayerChatTabCompleteEvent event) {
		if (ChatTabCompleteReplacementEvent.getHandlerList().getRegisteredListeners().length > 0) {
			Set<String> nickedPlayerNames = SkinAPI.getNickedPlayerNames();
			for (ListIterator<String> iterator = ((List<String>) event.getTabCompletions()).listIterator(); iterator.hasNext(); ) {
				final String completion = iterator.next();
				String replacedCompletion = SkinAPI.replaceNames(completion, nickedPlayerNames, new NameReplacer() {
					@Override
					public String replace(String original) {
						Player player = Bukkit.getPlayer(original);
						if (player != null) {
							PlayerQuitReplacementEvent replacementEvent = new PlayerQuitReplacementEvent(player, Bukkit.getOnlinePlayers(), completion, original, original);
							Bukkit.getPluginManager().callEvent(replacementEvent);
							if (replacementEvent.isCancelled()) { return original; }
							return replacementEvent.getReplacement();
						}
						return original;
					}
				}, true);
				iterator.set(ChatColor.stripColor(replacedCompletion));
			}
		}
	}

	//// Replacement listeners

	@EventHandler(priority = EventPriority.LOW)
	public void on(ChatReplacementEvent event) {
		if (replaceChatPlayer) {
			if (SkinAPI.getSkinManager().isNicked(event.getDisguised().getUniqueId())) {
				event.setReplacement(String.format(namesChatFormat, SkinAPI.getSkinManager().getNick(event.getDisguised().getUniqueId())));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(ChatOutReplacementEvent event) {
		if (replaceChatOut) {
			if (SkinAPI.getSkinManager().isNicked(event.getDisguised().getUniqueId())) {
				event.setReplacement(String.format(namesChatFormat, SkinAPI.getSkinManager().getNick(event.getDisguised().getUniqueId())));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(ChatInReplacementEvent event) {
		if (replaceChatInGeneral || replaceChatInCommand) {
			if (replaceChatInCommand && event.getContext().startsWith("/")) { // Command
				if (SkinAPI.getSkinManager().isNicked(event.getDisguised().getUniqueId())) {
					event.setReplacement(SkinAPI.getSkinManager().getNick(event.getDisguised().getUniqueId()));
				}
			} else if (replaceChatInGeneral) {
				if (SkinAPI.getSkinManager().isNicked(event.getDisguised().getUniqueId())) {
					event.setReplacement(SkinAPI.getSkinManager().getNick(event.getDisguised().getUniqueId()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(ScoreboardReplacementEvent event) {
		if (replaceScoreboard) {
			if (SkinAPI.getSkinManager().isNicked(event.getDisguised().getUniqueId())) {
				event.setReplacement(SkinAPI.getSkinManager().getNick(event.getDisguised().getUniqueId()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(ScoreboardScoreReplacementEvent event) {
		if (replaceScoreboardScore) {
			if (SkinAPI.getSkinManager().isNicked(event.getDisguised().getUniqueId())) {
				event.setReplacement(SkinAPI.getSkinManager().getNick(event.getDisguised().getUniqueId()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(ScoreboardTeamReplacementEvent event) {
		if (replaceScoreboardTeam) {
			if (SkinAPI.getSkinManager().isNicked(event.getDisguised().getUniqueId())) {
				event.setReplacement(SkinAPI.getSkinManager().getNick(event.getDisguised().getUniqueId()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(ChatTabCompleteReplacementEvent event) {
		if (replaceTabCompleteChat) {
			if (SkinAPI.getSkinManager().isNicked(event.getDisguised().getUniqueId())) {
				event.setReplacement(SkinAPI.getSkinManager().getNick(event.getDisguised().getUniqueId()));
			}
		}
	}

	public void sendPluginMessage(Player player, String action, String... values) {
		if (!PacketHandler.bungeeCord) { return; }
		if (player == null || !player.isOnline()) { return; }

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(action);
		out.writeUTF(player.getUniqueId().toString());
		for (String s : values) {
			out.writeUTF(s);
		}
		player.sendPluginMessage(plugin, "NickNamer", out.toByteArray());
	}

	@Override
	public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
		if (!PacketHandler.bungeeCord) { return; }
		if ("NickNamer".equals(s)) {
			ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
			String sub = in.readUTF();
			UUID who = UUID.fromString(in.readUTF());
			if ("name".equals(sub)) {
				String name = in.readUTF();

				if (name == null || "reset".equals(name)) {
					getAPI().removeNick(who);
				} else {
					getAPI().setNick(who, name);
				}
			} else if ("skin".equals(sub)) {
				String skin = in.readUTF();

				if (skin == null || "reset".equals(skin)) {
					getAPI().removeSkin(who);
				} else {
					getAPI().setSkin(who, skin);
				}
			} else if ("data".equals(sub)) {
				try {
					String owner = in.readUTF();
					JsonObject data = new JsonParser().parse(in.readUTF()).getAsJsonObject();
					SkinLoaderBridge.getSkinProvider().put(owner, new GameProfileWrapper(data).toJson());
				} catch (JsonParseException e) {
					e.printStackTrace();
				}
			} else {
				getLogger().warning("Unknown incoming plugin message: " + sub);
			}
		}
	}

}
