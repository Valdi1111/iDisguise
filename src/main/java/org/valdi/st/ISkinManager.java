package org.valdi.st;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.inventivetalent.mcwrapper.auth.GameProfileWrapper;

import com.google.gson.JsonObject;

public interface ISkinManager {

	void setSkin(@Nonnull final UUID uuid, @Nonnull final String skinOwner, @Nullable final Callback callback);

	void setSkin(@Nonnull UUID uuid, @Nonnull String skinOwner);

	void loadCustomSkin(@Nonnull String key, @Nonnull Object gameProfile);

	void loadCustomSkin(@Nonnull String key, @Nonnull GameProfileWrapper profileWrapper);

	void loadCustomSkin(@Nonnull String key, @Nonnull JsonObject data);

	void setCustomSkin(@Nonnull UUID uuid, @Nonnull String skin);

	void setCustomSkin(@Nonnull LivingEntity entity, @Nonnull String skin);

	void removeSkin(@Nonnull final UUID uuid);

	String getSkin(@Nonnull UUID uuid);

	boolean hasSkin(@Nonnull UUID uuid);

	void refreshPlayer(@Nonnull UUID uuid);

	void refreshPlayer(@Nonnull final Player player);

	boolean isNicked(@Nonnull UUID uuid);

	boolean isNickUsed(@Nonnull String nick);

	String getNick(@Nonnull UUID id);

	void setNick(@Nonnull final UUID uuid, @Nonnull final String nick);

	void removeNick(@Nonnull final UUID uuid);

	List<UUID> getPlayersWithNick(@Nonnull String nick);

	List<String> getUsedNicks();
	
	default boolean isSimple() {
		return false;
	}

}
