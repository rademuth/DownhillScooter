package edu.virginia.engine.display;

import java.util.ArrayList;
import java.util.List;

import edu.virginia.engine.util.Vector;

public class PhysicsSprite extends AnimatedSprite {

	private final static Vector GRAVITY = new Vector(0,750); // applied as acceleration
	public final static double INFINITE_MASS = Double.MAX_VALUE;
	
	private long lastKinematicsUpdate;
	private double mass;
	private double e; // restitution between 0 (hard) and 1 (bouncy)
	private Vector velocity;
	private Vector acceleration;
	private boolean fixed;

	private List<Vector> forces;
	
	private Vector oldPosition;
	
	public PhysicsSprite(String id, String imageFileName) {
		super(id, imageFileName);
		this.lastKinematicsUpdate = System.nanoTime();
		this.mass = 1;
		this.e = 0;
		this.velocity = new Vector(0,0);
		this.acceleration = new Vector(0,0);
		this.fixed = false;
		this.forces = new ArrayList<Vector>();
		this.oldPosition = new Vector(0,0);
	}
	
	public void addForce(Vector v) {
		forces.add(v);
	}
	
	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public double getRestitution() {
		return e;
	}
	
	public void setRestitution(double e) {
		this.e = e;
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

	public Vector getAcceleration() {
		return acceleration;
	}

	public void setXAcceleration(double xAcceleration) {
		this.acceleration.setX(xAcceleration);
	}

	public void setYAcceleration(double yAcceleration) {
		this.acceleration.setY(yAcceleration);
	}
	
	public boolean isFixed() {
		return fixed;
	}
	
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);

		oldPosition = this.getPosition();
		
		if (mass != INFINITE_MASS) {
			long elapsedTime_ns = System.nanoTime() - lastKinematicsUpdate;
			double elapsedTime = (double) elapsedTime_ns / 1000000000;
			lastKinematicsUpdate = System.nanoTime();

			// Compute the sum of current forces
			forces.clear();
			Vector force = new Vector(0,0);
			for (Vector f : forces) {
				force.add(f);
			}
			
			// Calculate the acceleration
			acceleration = force.factor(mass);
			acceleration.add(GRAVITY);
		
			// Calculate the velocity
			velocity.add(acceleration.product(elapsedTime));
			
			// Calculate the position
			this.setPosition(oldPosition.sum(velocity.product(elapsedTime)));
			
		}
	}

	public void move(Vector direction, Game g) {
		this.getPosition().add(direction);
		List<PhysicsSprite> returnObjects = new ArrayList<PhysicsSprite>();
		g.getQuadtree().retrieve(returnObjects, this);
		for (PhysicsSprite collider : returnObjects) {
			// Run collision detection algorithm between objects
			if (this != collider && this.collidesWith(collider)) {
				handleCollision(collider);
				return;
			}
		}
		//oldPosition = this.getPosition().difference(direction);
	}
	
	public void handleCollision(PhysicsSprite sprite) {
		this.setPosition(oldPosition);
		sprite.setPosition(sprite.oldPosition);
		
		/*
		Vector velocity_this = sprite.velocity.product((e+1)*sprite.mass).sum(velocity.product(mass-(e*sprite.mass))).factor(mass+sprite.mass);
		Vector velocity_sprite = velocity.product((sprite.e+1)*mass).sum(sprite.velocity.product(mass-(sprite.e*sprite.mass))).factor(mass+sprite.mass);
		
		this.velocity = velocity_this;
		sprite.velocity = velocity_sprite;
		*/
		
		velocity = velocity.product(-e);
		sprite.velocity = sprite.velocity.product(-sprite.e);
	}
	
}
