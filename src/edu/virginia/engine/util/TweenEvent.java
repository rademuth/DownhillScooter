package edu.virginia.engine.util;

import edu.virginia.engine.events.Event;

public class TweenEvent extends Event {
	
	final static String TWEEN_COMPLETE_EVENT = "TWEEN_COMPLETE_EVENT";
	
	public TweenEvent(String eventType, Tween tween) {
		super(eventType, tween);
	}
	
	public Tween getTween() {
		return (Tween) this.getSource();
	}
	
}
