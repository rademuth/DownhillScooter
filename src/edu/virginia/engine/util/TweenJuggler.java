package edu.virginia.engine.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TweenJuggler {
	
	private static TweenJuggler instance = null;

	public List<Tween> tweens;
	
	public TweenJuggler() {
		this.tweens = new ArrayList<Tween>();
	}
	
	public static TweenJuggler getInstance() {
		if (instance == null) {
			instance = new TweenJuggler();
		}
		return instance;
	}
	
	public void add(Tween tween) {
		tweens.add(tween);
	}
	
	public void nextFrame() {
		Iterator<Tween> iter = tweens.iterator();
		while (iter.hasNext()) {
			Tween tween = iter.next();
			tween.update();
			if (tween.isComplete()) {
				// Remove the tween from the juggler
				iter.remove();
				// Dispatch an event that the tween has finished
				TweenEvent event = new TweenEvent(TweenEvent.TWEEN_COMPLETE_EVENT, tween);
				tween.dispatchEvent(event);
			}
		}
	}
	
}
