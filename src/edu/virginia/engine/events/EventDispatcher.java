package edu.virginia.engine.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher implements IEventDispatcher {

	private Map<String, List<IEventListener>> listenersMap;
	
	public EventDispatcher() {
		this.listenersMap = new HashMap<String, List<IEventListener>>();
	}
	
	public void addEventListener(IEventListener listener, String eventType) {
		if (listenersMap.containsKey(eventType)) {
			// The map already contains the given eventType key
			listenersMap.get(eventType).add(listener);
		} else {
			// The given eventType key has not yet been entered into the map
			List<IEventListener> listeners = new ArrayList<IEventListener>();
			listeners.add(listener);
			listenersMap.put(eventType, listeners);
		}
	}

	public void removeEventListener(IEventListener listener, String eventType) {
		List<IEventListener> listeners = listenersMap.get(eventType);
		listeners.remove(listener);
	}

	public void dispatchEvent(Event event) {
		List<IEventListener> listeners = listenersMap.get(event.getEventType());
		if (listeners == null) {
			return;
		}
		for (IEventListener listener : listeners) {
			listener.handleEvent(event);
		}
	}

	public boolean hasEventListener(IEventListener listener, String eventType) {
		List<IEventListener> listeners = listenersMap.get(eventType);
		return listeners.contains(listener);
	}

}
