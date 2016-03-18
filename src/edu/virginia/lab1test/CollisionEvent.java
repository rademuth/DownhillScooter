package edu.virginia.lab1test;

import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventDispatcher;

public class CollisionEvent extends Event {

	public static String COLLISION_EVENT = "COLLISION_EVENT";
	public static String CHARACTER_LANDED = "CHARACTER_LANDED";
	
	public CollisionEvent(String eventType, IEventDispatcher source) {
		super(eventType, source);
	}
	
}
