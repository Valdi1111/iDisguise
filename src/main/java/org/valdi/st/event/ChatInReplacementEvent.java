package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

/**
 * Event called when a name in an incoming chat message is replaced
 */
public class ChatInReplacementEvent extends NameReplacementEvent {

	public ChatInReplacementEvent(@Nonnull Player disguised, @Nonnull Player receiver, @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receiver, ReplaceType.CHAT_IN, context, original, replacement);
	}

}
