package edu.virginia.lab1test;

import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventDispatcher;

public class PickedUpEvent extends Event {
	
	public static String COIN_PICKED_UP = "COIN_PICKED_UP";

	public PickedUpEvent(String eventType, IEventDispatcher source) {
		super(eventType, source);
	}

}
