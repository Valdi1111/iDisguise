package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.inventivetalent.mcwrapper.auth.GameProfileWrapper;

public class SkinDisguiseEvent extends ProfileDisguiseEvent implements Cancellable {

	private final String originalSkin;
	private       String skin;

	public SkinDisguiseEvent(@Nonnull OfflinePlayer disguised, @Nonnull Player receiver, @Nonnull GameProfileWrapper gameProfile, @Nullable String skin) {
		super(disguised, receiver, gameProfile);
		this.originalSkin = this.skin = skin;
	}

	/**
	 * @return The player's skin, or the player's name
	 */
	@Nullable
	public String getSkin() {
		return skin;
	}

	/**
	 * @param skin The new skin
	 */
	public void setSkin(@Nullable String skin) {
		this.skin = skin;
	}

	/**
	 * @return <code>true</code> if the skin is disguised
	 */
	public boolean isDisguised() {
		return !isCancelled() && skin != null && !originalSkin.equals(skin);
	}

	private static HandlerList handlerList = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}

}
