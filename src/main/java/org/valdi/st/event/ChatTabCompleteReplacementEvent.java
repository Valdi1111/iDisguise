package org.valdi.st.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

public class ChatTabCompleteReplacementEvent extends NameReplacementEvent {

	public ChatTabCompleteReplacementEvent(@Nonnull Player disguised, @Nonnull Player receiver,  @Nonnull String context, @Nonnull String original, @Nullable String replacement) {
		super(disguised, receiver, ReplaceType.CHAT_TAB_COMPLETE, context, original, replacement);
	}

}
