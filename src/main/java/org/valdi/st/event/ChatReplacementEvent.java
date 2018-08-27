package org.valdi.st.event;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

/**
 * Event called when a name in a message sent by a player is being replaced
 */
public class ChatReplacementEvent extends NameReplacementEvent {

	private Collection<Player> receivers = new HashSet<>();

	public ChatReplacementEvent(@Nonnull Player disguised, @Nonnull Collection<? extends Player> receivers, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		this(disguised, receivers, ReplaceType.PLAYER_CHAT, context, original, replacement);
	}

	public ChatReplacementEvent(@Nonnull Player disguised, @Nonnull Collection<? extends Player> receivers, @Nonnull ReplaceType replaceType, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receivers.iterator().next(), replaceType, context, original, replacement);
		this.receivers.addAll(receivers);
	}

	public Collection<Player> getReceivers() {
		return receivers;
	}

}
