package edu.virginia.engine.display;

import java.util.ArrayList;
import java.util.Random;

import edu.virginia.engine.util.Vector;

public class MovingSprite extends AnimatedSprite {
	
	private final static double SPEED = 2;
	private final static double THRESHOLD = 0.975;
	
	private Random rand;
	private boolean movingRight;
	
	//public void addAnimation(String name, String[] imageFileNames) {
	//public void animate(String name, boolean loop, long duration) {
	
	public MovingSprite(String id, String[] imageFileNames) {
		super(id);
		this.rand = new Random();
		this.movingRight = true;
		this.addAnimation("Move", imageFileNames);
		this.animate("Move", true, 500);
	}
	
	public MovingSprite(String id, String imageFileName, String[] imageFileNames) {
		super(id, imageFileName);
		this.rand = new Random();
		this.movingRight = true;
		this.addAnimation("Move", imageFileNames);
		this.animate("Move", true, 500);
	}
	
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
		double val = this.rand.nextDouble();
		if (val > THRESHOLD) {
			// Switch directions
			if (movingRight) {
				this.setXPosition(this.getXPosition() - SPEED);
				movingRight = false;
			} else {
				this.setXPosition(this.getXPosition() + SPEED);
				movingRight = true;
			}
			this.setScaleX(-this.getScaleX());
		} else {
			// Continue in the same direction
			if (movingRight) {
				this.setXPosition(this.getXPosition() + SPEED);
			} else {
				this.setXPosition(this.getXPosition() - SPEED);
			}
		}
			
	}

}
