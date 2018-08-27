package org.valdi.st.event;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

/**
 * Event called whan a name in a quit-message is replaced
 */
public class PlayerQuitReplacementEvent extends ChatReplacementEvent {

	public PlayerQuitReplacementEvent(@Nonnull Player disguised, @Nonnull Collection<? extends Player> receivers, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receivers, ReplaceType.PLAYER_QUIT, context, original, replacement);
	}

}
