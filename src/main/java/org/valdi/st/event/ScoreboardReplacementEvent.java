package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

/**
 * Event called when a name in scoreboard objectives is replaced
 */
public class ScoreboardReplacementEvent extends NameReplacementEvent {

	public ScoreboardReplacementEvent(@Nonnull Player disguised, @Nonnull Player receiver, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receiver, ReplaceType.SCOREBOARD, context, original, replacement);
	}

	public ScoreboardReplacementEvent(@Nonnull Player disguised, @Nonnull Player receiver, @Nonnull ReplaceType replaceType, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receiver, replaceType, context, original, replacement);
	}

}
