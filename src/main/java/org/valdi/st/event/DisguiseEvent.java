package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class DisguiseEvent extends Event implements Cancellable {

	private OfflinePlayer disguised;
	private Player        receiver;

	private boolean cancelled;

	public DisguiseEvent(@Nonnull OfflinePlayer disguised, @Nonnull Player receiver) {
		this.disguised = disguised;
		this.receiver = receiver;
	}

	@Nullable
	public Player getPlayer() {
		return getDisguised().getPlayer();
	}

	/**
	 * @return The player to disguise
	 */
	@Nonnull
	public OfflinePlayer getDisguised() {
		return disguised;
	}

	/**
	 * @return The player who will see the changed name/skin
	 */
	@Nonnull
	public Player getReceiver() {
		return receiver;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}

}
