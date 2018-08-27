package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Event called whenever the API finds a name to disguise
 */
public class NameReplacementEvent extends DisguiseEvent implements Cancellable {

	private ReplaceType replaceType;
	private String      context;
	private String      original;
	private String      replacement;

	public NameReplacementEvent(@Nonnull Player disguised, @Nonnull Player receiver, @Nonnull ReplaceType replaceType, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receiver);
		this.replaceType = replaceType;
		this.context = context;
		this.original = original;
		this.replacement = replacement;
	}

	/**
	 * @return The player whose name is being replaced
	 */
	@Nonnull
	@Override
	public OfflinePlayer getDisguised() {
		return super.getDisguised();
	}

	/**
	 * @return The player whose name is being replaced
	 */
	@Nullable
	@Override
	public Player getPlayer() {
		return super.getPlayer();
	}

	/**
	 * @return The {@link ReplaceType} of this event
	 */
	@Nonnull
	public ReplaceType getReplaceType() {
		return replaceType;
	}

	/**
	 * @return The full message the name was replaced in
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @return The original name
	 */
	public String getOriginal() {
		return original;
	}

	/**
	 * @return The replacement name (or the original name, if nothing has been replaced yet)
	 */
	public String getReplacement() {
		return replacement;
	}

	/**
	 * @param replacement The replacement name
	 */
	public void setReplacement(String replacement) {
		this.replacement = replacement;
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
