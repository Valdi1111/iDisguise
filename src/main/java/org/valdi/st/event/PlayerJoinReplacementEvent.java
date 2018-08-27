package org.valdi.st.event;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

/**
 * Event called when a name in a join-message is replaced
 */
public class PlayerJoinReplacementEvent extends ChatReplacementEvent {

	public PlayerJoinReplacementEvent(@Nonnull Player disguised, @Nonnull Collection<? extends Player> receivers, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receivers, ReplaceType.PLAYER_JOIN, context, original, replacement);
	}
}
