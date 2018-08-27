package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

public class ScoreboardTeamReplacementEvent extends ScoreboardReplacementEvent {
	
	public ScoreboardTeamReplacementEvent(@Nonnull Player disguised, @Nonnull Player receiver, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receiver, ReplaceType.SCOREBOARD_TEAM, context, original, replacement);
	}

}
