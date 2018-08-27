package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.inventivetalent.mcwrapper.auth.GameProfileWrapper;

/**
 * Event called when a player's name is disguised
 * <p>
 * The name won't be changed if
 * - the nick is equal to the player's name
 * - the nick is null
 * - the event is cancelled
 *
 * @see ProfileDisguiseEvent
 * @see SkinDisguiseEvent
 */
public class NickDisguiseEvent extends ProfileDisguiseEvent implements Cancellable {

	private final String originalNick;
	private       String nick;

	public NickDisguiseEvent(@Nonnull OfflinePlayer disguised, @Nonnull Player receiver, @Nonnull GameProfileWrapper gameProfile, @Nullable String nick) {
		super(disguised, receiver, gameProfile);
		this.originalNick = this.nick = nick;
	}

	/**
	 * @return The player's nick, or the player's actual name
	 */
	@Nullable
	public String getNick() {
		return nick;
	}

	/**
	 * @param nick The new nick
	 */
	public void setNick(@Nullable String nick) {
		this.nick = nick;
	}

	/**
	 * @return <code>true</code> if the name is disguised
	 */
	public boolean isDisguised() {
		return !isCancelled() && nick != null && !originalNick.equals(nick);
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
