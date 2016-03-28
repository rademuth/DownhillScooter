package edu.virginia.engine.display;

import java.util.ArrayList;

/**
 * Nothing in this class (yet) because there is nothing specific to a Sprite yet that a DisplayObject
 * doesn't already do. Leaving it here for convenience later. you will see!
 * */
public class Sprite extends DisplayObjectContainer {
	
	public ObstacleType type;
	
	public Sprite(String id) {
		super(id);
		this.type = null;
	}

	public Sprite(String id, String imageFileName) {
		super(id, imageFileName);
		this.type = null;
	}
	
	public Sprite(String id, ObstacleType type) {
		super(id);
		this.type = type;
	}

	public Sprite(String id, String imageFileName, ObstacleType type) {
		super(id, imageFileName);
		this.type = type;
	}
	
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
	}
	
}