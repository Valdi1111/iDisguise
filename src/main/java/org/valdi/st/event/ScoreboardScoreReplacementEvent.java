package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

/**
 * Called when the target-playername for a scoreboard score is replaced
 */
public class ScoreboardScoreReplacementEvent extends ScoreboardReplacementEvent {

	public ScoreboardScoreReplacementEvent(@Nonnull Player disguised, @Nonnull Player receiver, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receiver, ReplaceType.SCOREBOARD_SCORE, context, original, replacement);
	}

}
