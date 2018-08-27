package org.valdi.st.event;

import javax.annotation.Nonnull;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.inventivetalent.mcwrapper.auth.GameProfileWrapper;

public class SkinLoadedEvent extends Event {

	private String owner;
	private GameProfileWrapper gameProfileWrapper;

	public SkinLoadedEvent(@Nonnull String owner, @Nonnull GameProfileWrapper gameProfileWrapper) {
		this.owner = owner;
		this.gameProfileWrapper = gameProfileWrapper;
	}

	public String getOwner() {
		return owner;
	}

	public GameProfileWrapper getGameProfile() {
		return gameProfileWrapper;
	}

	public void setGameProfile(@Nonnull GameProfileWrapper gameProfileWrapper) {
		this.gameProfileWrapper = gameProfileWrapper;
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
