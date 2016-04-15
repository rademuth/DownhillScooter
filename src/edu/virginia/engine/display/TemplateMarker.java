package edu.virginia.engine.display;

import java.util.ArrayList;

import edu.virginia.engine.events.Event;
import edu.virginia.lab1test.LabOneGame;

public class TemplateMarker extends DisplayObject {

	//private LabOneGame game;
	private boolean templateHandled;
	
	public TemplateMarker(String id, LabOneGame game) {
		super(id);
		//this.gameRef = gameRef;
		this.templateHandled = false;
		this.addEventListener(game, "ADD_TEMPLATE_EVENT");
	}

	@Override
	public void update(ArrayList<String> pressedKeys) {
		if (!templateHandled && this.getLocalToGlobalCoors(0, 0).getY() < LabOneGame.GAME_HEIGHT) {
			//this.game.handleTemplateMarker();
			System.out.println("A template marker is sending an event...");
			Event e = new Event("ADD_TEMPLATE_EVENT", this);
			this.dispatchEvent(e);
			this.templateHandled = true;
		}
	}
	
}
