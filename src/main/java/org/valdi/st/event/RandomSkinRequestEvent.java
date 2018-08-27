package org.valdi.st.event;

import java.util.List;

public class RandomSkinRequestEvent extends RandomRequestEvent {
	
	public RandomSkinRequestEvent(List<String> possibilities) {
		super(possibilities);
	}

}
