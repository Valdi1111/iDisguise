package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

/**
 * Event called when a name in any outgoing message is replaced
 */
public class ChatOutReplacementEvent extends NameReplacementEvent {

	public ChatOutReplacementEvent(@Nonnull Player disguised, @Nonnull Player receiver, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receiver, ReplaceType.CHAT_OUT, context, original, replacement);
	}

}
