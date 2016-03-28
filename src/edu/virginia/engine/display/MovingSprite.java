package edu.virginia.engine.display;

import java.util.ArrayList;
import java.util.Random;

import edu.virginia.engine.util.Vector;

public class MovingSprite extends AnimatedSprite {
	
	private final static double SPEED = 2;
	private final static double THRESHOLD = 0.975;
	
	private Random rand;
	private boolean movingRight;
	private int min;
	private int max;
		
	public MovingSprite(String id, String[] imageFileNames, ObstacleType type, int min, int max) {
		super(id, type);
		this.rand = new Random();
		this.movingRight = true;
		this.min = min;
		this.max = max;
		this.addAnimation("Move", imageFileNames);
		this.animate("Move", true, 500);
	}
	
	public MovingSprite(String id, String imageFileName, String[] imageFileNames, ObstacleType type, int min, int max) {
		super(id, imageFileName, type);
		this.rand = new Random();
		this.movingRight = true;
		this.min = min;
		this.max = max;
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
				if (this.getXPosition() < this.min) {
					this.setXPosition(this.min);
					movingRight = true;
					this.setScaleX(-this.getScaleX());
				}
			} else {
				this.setXPosition(this.getXPosition() + SPEED);
				movingRight = true;
				if (this.getXPosition() > this.max) {
					this.setXPosition(this.max);
					movingRight = false;
					this.setScaleX(-this.getScaleX());
				}
			}
			this.setScaleX(-this.getScaleX());
		} else {
			// Continue in the same direction
			if (movingRight) {
				this.setXPosition(this.getXPosition() + SPEED);
				if (this.getXPosition() > this.max) {
					this.setXPosition(this.max);
					movingRight = false;
				}
			} else {
				this.setXPosition(this.getXPosition() - SPEED);
				if (this.getXPosition() < this.min) {
					this.setXPosition(this.min);
					movingRight = true;
				}
			}
		}
			
	}

}
