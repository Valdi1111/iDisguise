package org.valdi.st;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.inventivetalent.data.DataProvider;
import org.inventivetalent.data.async.DataCallable;
import org.inventivetalent.data.async.DataCallback;
import org.inventivetalent.data.mapper.AsyncCacheMapper;
import org.inventivetalent.data.mapper.MapMapper;
import org.inventivetalent.mcwrapper.auth.GameProfileWrapper;
import org.valdi.idisguise.iDisguise;
import org.valdi.idisguise.disguise.PlayerDisguise;
import org.valdi.idisguise.management.PacketHandler;
import org.valdi.st.event.NickNamerSelfUpdateEvent;
import org.valdi.st.event.PlayerRefreshEvent;

import com.google.gson.JsonObject;

public class SkinManager implements ISkinManager {
	private final iDisguise plugin;
	private static SkinManager instance;

	Class<?> EnumDifficulty = SkinLoader.nmsClassResolver.resolveSilent("EnumDifficulty");
	Class<?> WorldType      = SkinLoader.nmsClassResolver.resolveSilent("WorldType");
	Class<?> EnumGamemode   = SkinLoader.nmsClassResolver.resolveSilent("WorldSettings$EnumGamemode", "EnumGamemode");
	
	public SkinManager(final iDisguise plugin) {
		this.plugin = plugin;
	}
	
	public static SkinManager getInstance() {
		if(instance == null) {
			instance = new SkinManager(iDisguise.getInstance());
		}
		return instance;
	}

	DataProvider<String> nickDataProvider = MapMapper.sync(new HashMap<String, String>());
	DataProvider<String> skinDataProvider = MapMapper.sync(new HashMap<String, String>());

	@Override
	public void setSkin(@Nonnull final UUID uuid, @Nonnull final String skinOwner, @Nullable final Callback callback) {
		if (skinDataProvider instanceof AsyncCacheMapper.CachedDataProvider) {
			((AsyncCacheMapper.CachedDataProvider<String>) skinDataProvider).put(uuid.toString(), new DataCallable<String>() {
				@Nonnull
				@Override
				public String provide() {
					SkinLoader.loadSkin(skinOwner);
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							refreshPlayer(uuid);
						}
					}, 20);
					if (callback != null) {
						callback.call();
					}
					return skinOwner;
				}
			});
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					skinDataProvider.put(uuid.toString(), skinOwner);
					SkinLoader.loadSkin(skinOwner);
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							refreshPlayer(uuid);
						}
					}, 20);
					if (callback != null) {
						callback.call();
					}
				}
			});
		}

		if (PacketHandler.bungeeCord) { Main.getInstance().sendPluginMessage(Bukkit.getPlayer(uuid), "skin", skinOwner); }
	}

	@Override
	public void setSkin(@Nonnull UUID uuid, @Nonnull String skinOwner) {
		setSkin(uuid, skinOwner, null);
	}

	public void refreshCachedSkin(UUID player) {
		if (skinDataProvider instanceof AsyncCacheMapper.CachedDataProvider) {
			((AsyncCacheMapper.CachedDataProvider) skinDataProvider).refresh(player.toString());
		}
	}

	@Override
	public void loadCustomSkin(@Nonnull String key, @Nonnull Object gameProfile) {
		loadCustomSkin(key, new GameProfileWrapper(gameProfile));
	}

	@Override
	public void loadCustomSkin(@Nonnull String key, @Nonnull GameProfileWrapper profileWrapper) {
		SkinLoader.skinDataProvider.put(key, profileWrapper.toJson());
	}

	@Override
	public void loadCustomSkin(@Nonnull String key, @Nonnull JsonObject data) {
		if (!data.has("properties")) { throw new IllegalArgumentException("JsonObject must contain 'properties' entry"); }
		loadCustomSkin(key, new GameProfileWrapper(data));
	}

	@Override
	public void setCustomSkin(@Nonnull UUID uuid, @Nonnull String skin) {
		if (!SkinLoader.skinDataProvider.contains(skin)) { throw new IllegalStateException("Specified skin has not been loaded yet"); }
		skinDataProvider.put(uuid.toString(), skin);

		//		updatePlayer(id, false, true, (boolean) getConfigOption("selfUpdate"));
		refreshPlayer(uuid);
	}

	@Override
	public void setCustomSkin(@Nonnull LivingEntity entity, @Nonnull String skin) {
		if (!SkinLoader.skinDataProvider.contains(skin)) { throw new IllegalStateException("Specified skin has not been loaded yet"); }
		skinDataProvider.put(entity.getUniqueId().toString(), skin);

		plugin.getAPI().disguise(entity, new PlayerDisguise("moonallien", "�aAllien", new SkinContainer(SkinLoader.skinDataProvider.get(skin))));
	}

	@Override
	public void removeSkin(@Nonnull final UUID uuid) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				skinDataProvider.remove(uuid.toString());

				//		nickNamer.sendPluginMessage(Bukkit.getPlayer(id), "skin", "reset");

				//		updatePlayer(id, false, true, (boolean) getConfigOption("selfUpdate"));
				Bukkit.getScheduler().runTaskLater(plugin, () -> refreshPlayer(uuid) , 10);
			}
		});

		if (PacketHandler.bungeeCord) { Main.getInstance().sendPluginMessage(Bukkit.getPlayer(uuid), "skin", "reset"); }
	}

	@Override
	public String getSkin(@Nonnull UUID uuid) {
		return skinDataProvider.get(uuid.toString());
	}

	public void getSkin(@Nonnull UUID uuid, DataCallback<String> callback) {
		if (skinDataProvider instanceof AsyncCacheMapper.CachedDataProvider) {
			((AsyncCacheMapper.CachedDataProvider<String>) skinDataProvider).get(uuid.toString(), callback);
		} else {
			callback.provide(null);
		}
	}

	@Override
	public boolean hasSkin(@Nonnull UUID uuid) {
		return skinDataProvider.contains(uuid.toString());
	}

	@Override
	public void refreshPlayer(@Nonnull UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) { return; }
		refreshPlayer(player);
	}

	@Override
	public void refreshPlayer(@Nonnull final Player player) {
		if (!player.isOnline()) { return; }

		PlayerRefreshEvent refreshEvent = new PlayerRefreshEvent(player, true);
		Bukkit.getPluginManager().callEvent(refreshEvent);
		if (refreshEvent.isCancelled()) { return; }

		if (refreshEvent.isSelf()) {
			updateSelf(player);
		}

		Bukkit.getScheduler().runTask(plugin, () -> {
			List<Player> canSee = new ArrayList<>();
			for (Player player1 : Bukkit.getOnlinePlayers()) {
				if (player1.canSee(player)) {
					canSee.add(player1);
					player1.hidePlayer(player);
				}
			}
			for (Player player1 : canSee) {
				player1.showPlayer(player);
			}
		});
	}

	protected void updateSelf(final Player player) {
		if (player == null || !player.isOnline()) { return; }
		Object profile = ClassBuilder.getGameProfile(player);

		NickNamerSelfUpdateEvent event = new NickNamerSelfUpdateEvent(player, /*isNicked(player.getUniqueId()) ? getNick(player.getUniqueId()) :*/ player.getPlayerListName(), profile, player.getWorld().getDifficulty(), player.getGameMode());
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) { return; }

		try {
			final Object removePlayer = ClassBuilder.buildPlayerInfoPacket(4, event.getGameProfile(), 0, event.getGameMode().ordinal(), event.getName());
			final Object addPlayer = ClassBuilder.buildPlayerInfoPacket(0, event.getGameProfile(), 0, event.getGameMode().ordinal(), event.getName());
			Object difficulty = EnumDifficulty.getDeclaredMethod("getById", int.class).invoke(null, event.getDifficulty().getValue());
			Object type = ((Object[]) WorldType.getDeclaredField("types").get(null))[0];
			Object gamemode = EnumGamemode.getDeclaredMethod("getById", int.class).invoke(null, event.getGameMode().getValue());
			final Object respawnPlayer = SkinLoader.nmsClassResolver.resolve("PacketPlayOutRespawn").getConstructor(int.class, EnumDifficulty, WorldType, EnumGamemode).newInstance(0, difficulty, type, gamemode);

			SkinAPI.packetListener.sendPacket(player, removePlayer);

			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					boolean flying = player.isFlying();
					Location location = player.getLocation();
					int level = player.getLevel();
					float xp = player.getExp();
					double maxHealth = player.getMaxHealth();
					double health = player.getHealth();

					SkinAPI.packetListener.sendPacket(player, respawnPlayer);

					player.setFlying(flying);
					player.teleport(location);
					player.updateInventory();
					player.setLevel(level);
					player.setExp(xp);
					player.setMaxHealth(maxHealth);
					player.setHealth(health);

					SkinAPI.packetListener.sendPacket(player, addPlayer);
				}
			}, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isNicked(@Nonnull UUID uuid) {
		return nickDataProvider.contains(uuid.toString());
	}

	public void isNicked(@Nonnull UUID uuid, @Nonnull DataCallback<Boolean> callback) {
		if (nickDataProvider instanceof AsyncCacheMapper.CachedDataProvider) {
			((AsyncCacheMapper.CachedDataProvider) nickDataProvider).contains(uuid.toString(), callback);
		} else {
			callback.provide(false);
		}
	}

	@Override
	public boolean isNickUsed(@Nonnull String nick) {
		for (String uuid : nickDataProvider.keys()) {
			if (nick.equals(nickDataProvider.get(uuid))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getNick(@Nonnull UUID id) {
		return nickDataProvider.get(id.toString());
	}

	public void getNick(@Nonnull UUID uuid, DataCallback<String> callback) {
		if (nickDataProvider instanceof AsyncCacheMapper.CachedDataProvider) {
			((AsyncCacheMapper.CachedDataProvider<String>) nickDataProvider).get(uuid.toString(), callback);
		} else {
			callback.provide(null);
		}
	}

	@Override
	public void setNick(@Nonnull final UUID uuid, @Nonnull final String nick) {
		if (nick.length() > 16) { throw new IllegalArgumentException("Name is too long (" + nick.length() + " > 16)"); }
		if (nickDataProvider instanceof AsyncCacheMapper.CachedDataProvider) {
			((AsyncCacheMapper.CachedDataProvider<String>) nickDataProvider).put(uuid.toString(), new DataCallable<String>() {
				@Nonnull
				@Override
				public String provide() {
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							refreshPlayer(uuid);
						}
					}, 20);
					return nick;
				}
			});
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					nickDataProvider.put(uuid.toString(), nick);
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							refreshPlayer(uuid);
						}
					}, 20);
				}
			});

		}
	}

	@Override
	public void removeNick(@Nonnull final UUID uuid) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				nickDataProvider.remove(uuid.toString());

				//		Player player = Bukkit.getPlayer(uuid);
				//		if (player != null) {
				//			if (getConfigOption("nick.chat")) {
				//				p.setDisplayName(storedNames.get(id));
				//			}
				//			if (getConfigOption("nick.tab")) {
				//				p.setPlayerListName(storedNames.get(id));
				//			}
				//			if (getConfigOption("nick.scoreboard")) {
				//				Scoreboard sb = p.getScoreboard();
				//				if (sb == null) {
				//					sb = Bukkit.getScoreboardManager().getMainScoreboard();
				//				}
				//				if (sb != null) {
				//					Team t = null;
				//					for (Team tm : sb.getTeams()) {
				//						for (String s : tm.getEntries()) {
				//							if (s.equals(nick)) {
				//								t = tm;
				//								break;
				//							}
				//						}
				//					}
				//					if (t != null) {
				//						t.removeEntry(nick);
				//						t.addPlayer(p);
				//					}
				//				}
				//			}
				//		}
				//		storedNames.remove(id);

				//		nickNamer.sendPluginMessage(player, "name", "reset");

				//		updatePlayer(id, true, false, (boolean) getConfigOption("selfUpdate"));
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						refreshPlayer(uuid);
					}
				}, 10);
			}
		});

		if (PacketHandler.bungeeCord) { Main.getInstance().sendPluginMessage(Bukkit.getPlayer(uuid), "name", "reset"); }
	}

	@Nonnull
	@Override
	public List<UUID> getPlayersWithNick(@Nonnull String nick) {
		List<UUID> list = new ArrayList<>();
		for (String uuid : nickDataProvider.keys()) {
			if (nick.equals(nickDataProvider.get(uuid))) { list.add(UUID.fromString(uuid)); }
		}
		//		for (Map.Entry<UUID, String> entry : nicks.entrySet()) {
		//			if (entry.getValue().equals(nick)) {
		//				list.add(entry.getKey());
		//			}
		//		}
		return list;
	}

	@Nonnull
	@Override
	public List<String> getUsedNicks() {
		List<String> nicks = new ArrayList<>();
		for (String uuid : nickDataProvider.keys()) {
			String nick = nickDataProvider.get(uuid);
			if (nick != null) { nicks.add(nick); }
		}
		return nicks;
	}

	public void refreshCachedNick(@Nonnull UUID player) {
		if (nickDataProvider instanceof AsyncCacheMapper.CachedDataProvider) {
			((AsyncCacheMapper.CachedDataProvider) nickDataProvider).refresh(player.toString());
		}
	}

}
