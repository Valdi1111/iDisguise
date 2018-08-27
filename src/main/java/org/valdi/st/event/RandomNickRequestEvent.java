package org.valdi.st.event;

import java.util.List;

public class RandomNickRequestEvent extends RandomRequestEvent {
	
	public RandomNickRequestEvent(List<String> possibilities) {
		super(possibilities);
	}

}
