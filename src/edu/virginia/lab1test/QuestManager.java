package edu.virginia.lab1test;

import edu.virginia.engine.display.Game;
import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventListener;

public class QuestManager implements IEventListener {

	private Game game;
	
	public QuestManager(Game game) {
		this.game = game;
	}
	
	public void handleEvent(Event event) {
		if (event.getEventType().equals(PickedUpEvent.COIN_PICKED_UP)) {
			System.out.println("Quest is complete");
			//event.getSource().removeEventListener(this, PickedUpEvent.COIN_PICKED_UP);
		}
	}

}
