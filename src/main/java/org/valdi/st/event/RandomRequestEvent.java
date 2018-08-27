package org.valdi.st.event;

import java.util.Collection;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class RandomRequestEvent extends Event {

	private List<String> possibilities;

	public RandomRequestEvent(List<String> possibilities) {
		this.possibilities = possibilities;
	}

	public Collection<String> getPossibilities() {
		return possibilities;
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
