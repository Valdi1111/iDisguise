package org.valdi.st.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRefreshEvent extends Event implements Cancellable {

	private Player  player;
	private boolean self;

	private boolean cancelled;

	public PlayerRefreshEvent(Player player, boolean self) {
		this.player = player;
		this.self = self;
	}

	/**
	 * @return the refreshed player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return Whether to send the update to the player themselves
	 */
	public boolean isSelf() {
		return self;
	}

	/**
	 * @param self Whether to send the update to the player themselves
	 */
	public void setSelf(boolean self) {
		this.self = self;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
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
