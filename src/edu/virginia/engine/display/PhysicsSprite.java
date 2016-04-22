package edu.virginia.engine.display;

import java.util.ArrayList;
import java.util.List;

import edu.virginia.engine.util.Vector;

public class PhysicsSprite extends AnimatedSprite {

	private final static Vector GRAVITY = new Vector(0,-10); // applied as acceleration
	
	private long lastKinematicsUpdate;
	private Vector velocity;
	private Vector acceleration;
	
	private Vector oldPosition;
	
	public PhysicsSprite(String id) {
		super(id);
		this.lastKinematicsUpdate = -1;
		this.velocity = new Vector(0,0);
		this.acceleration = new Vector(0,0);
		this.oldPosition = new Vector(0,0);
	}
	
	public PhysicsSprite(String id, String imageFileName) {
		super(id, imageFileName);
		this.lastKinematicsUpdate = -1;
		this.velocity = new Vector(0,0);
		this.acceleration = new Vector(0,0);
		this.oldPosition = new Vector(0,0);
	}
	
	public Vector getVelocity() {
		return velocity;
	}

	public void setXVelocity(double xVelocity) {
		this.velocity.setX(xVelocity);
	}

	public void setYVelocity(double yVelocity) {
		this.velocity.setY(yVelocity);
	}
	
	public double getXVelocity() {
		return this.velocity.getX();
	}

	public double getYVelocity() {
		return this.velocity.getY();
	}
	
	public void addXVelocity(double xVelocity) {
		this.velocity.setX(this.velocity.getX() + xVelocity);
	}
	
	public void addYVelocity(double yVelocity) {
		this.velocity.setY(this.velocity.getY() + yVelocity);
	}

	public Vector getAcceleration() {
		return acceleration;
	}

	public void setXAcceleration(double xAcceleration) {
		this.acceleration.setX(xAcceleration);
	}

	public void setYAcceleration(double yAcceleration) {
		this.acceleration.setY(yAcceleration);
	}
	
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);

		if (lastKinematicsUpdate < 0) {
			lastKinematicsUpdate = System.nanoTime();
		} else {
			oldPosition = this.getPosition();
			
			long elapsedTime_ns = System.nanoTime() - lastKinematicsUpdate;
			double elapsedTime = (double) elapsedTime_ns / 1000000000;
			lastKinematicsUpdate = System.nanoTime();
				
			// Calculate the velocity
			velocity.add(GRAVITY.product(elapsedTime));
				
			// Calculate the position
			this.setPosition(oldPosition.sum(velocity.product(elapsedTime)));
		}
	}
	
}
