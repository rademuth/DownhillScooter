package edu.virginia.lab1test;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.MovingSprite;
import edu.virginia.engine.display.ObstacleType;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.events.Event;
import edu.virginia.engine.util.Tween;
import edu.virginia.engine.util.TweenTransition;
import edu.virginia.engine.util.TweenTransitionType;
import edu.virginia.engine.util.TweenableParam;
import edu.virginia.engine.util.Vector;

/**
 * Example game that utilizes our engine. We can create a simple prototype game with just a couple lines of code
 * although, for now, it won't be a very fun game :)
 * */
public class LabOneGame extends Game {

	private final static String[] dogImages = {"Dog_move_1.png", "Dog_move_2.png"};
	private final static double INITIAL_VELOCITY = -125;
	private final static long JUMP_TIME = 500;
	private final static int GAME_WIDTH = 500;
	private final static int GAME_HEIGHT = 725;

	private Sprite scooter;
	private PhysicsSprite obstacleContainer;
	private DisplayObjectContainer lineContainer;
	private DisplayObjectContainer potholeContainer;
	private DisplayObjectContainer trafficConeContainer;
	private DisplayObjectContainer dogContainer;
	private DisplayObjectContainer uiContainer;

	private long lastJump;
	private double brakingFluid;
	
	static boolean firstPass = true;
	
	/**
	 * Constructor. See constructor in Game.java for details on the parameters given
	 * */
	public LabOneGame() {
		super("Lab One Test Game", GAME_WIDTH, GAME_HEIGHT);
		this.lastJump = System.nanoTime();
		this.brakingFluid = 100;
		
		this.scooter = new Sprite("Scooter", "Scooter.png");
		this.scooter.setXPivotPoint(this.scooter.getUnscaledWidth()/2);
		this.scooter.setYPivotPoint(this.scooter.getUnscaledHeight()/2);
		this.scooter.setXPosition(GAME_WIDTH/2);
		this.scooter.setYPosition(this.scooter.getUnscaledHeight()/2);
		this.obstacleContainer = new PhysicsSprite("ObstacleParent");
		this.obstacleContainer.setYVelocity(INITIAL_VELOCITY);
		this.lineContainer = new DisplayObjectContainer("Line Container");
		this.potholeContainer = new DisplayObjectContainer("Pothole Container");
		this.trafficConeContainer = new DisplayObjectContainer("Traffic Cone Container");
		this.dogContainer = new DisplayObjectContainer("Dog Container");
		this.uiContainer = new DisplayObjectContainer("UI Container");
		this.obstacleContainer.addChild(this.lineContainer);
		this.obstacleContainer.addChild(this.potholeContainer);
		this.obstacleContainer.addChild(this.trafficConeContainer);
		this.obstacleContainer.addChild(this.dogContainer);
		this.addChild(this.obstacleContainer);
		this.addChild(this.scooter);
		this.addChild(this.uiContainer);
	}
	
	public boolean canJump() {
		return (System.nanoTime() - this.lastJump) / 1000000 > JUMP_TIME;
	}
	
	public boolean isInAir() {
		return !this.canJump();
	}
	
	public void addObstacle(ObstacleType type, double xPos, double yPos) {
		Sprite s;
		switch(type) {
			case POTHOLE:
				s = new Sprite("Pothole", "Pothole.png", type);
				s.setPivotPoint(s.getUnscaledWidth()/2, s.getUnscaledHeight()/2);
				s.setPosition(xPos, yPos);
				this.potholeContainer.addChild(s);
				this.addSprite(s);
				break;
			case TRAFFIC_CONE:
				s = new Sprite("Traffic Cone", "Cone.png", type);
				s.setPivotPoint(s.getUnscaledWidth()/2, s.getUnscaledHeight()/2);
				s.setPosition(xPos, yPos);
				this.trafficConeContainer.addChild(s);
				this.addSprite(s);
				break;
			case DOG:
				s = new MovingSprite("Dog", dogImages, type, 0, GAME_WIDTH);
				s.setPivotPoint(s.getUnscaledWidth()/2, s.getUnscaledHeight()/2);
				s.setPosition(xPos, yPos);
				this.dogContainer.addChild(s);
				this.addSprite(s);
				break;
		}
	}
	
	public void addLine(double xPos, double yPos) {
		Sprite s = new Sprite("Line", "Line.png");
		s.setPivotPoint(s.getUnscaledWidth()/2, s.getUnscaledHeight()/2);
		s.setPosition(xPos, yPos);
		this.lineContainer.addChild(s);
	}
	
	/**
	 * Engine will automatically call this update method once per frame and pass to us
	 * the set of keys (as strings) that are currently being pressed down
	 * */
	@Override
	public void update(ArrayList<String> pressedKeys){
		super.update(pressedKeys);		
		
		if (!firstPass) {
			
			/** 
			 * Might have issues running during the first frame of the game since sprites
			 * might not have been set to their correct positions. This will result in
			 * collisions even though sprites may not physically touch each other.
			 */
			
			// Check for collisions
			List<Sprite> returnObjects = new ArrayList<Sprite>();
			returnObjects.clear();
			this.getQuadtree().retrieve(returnObjects, scooter);
			for (Sprite obstacle : returnObjects) {
				// Run collision detection algorithm between objects
				if (scooter.collidesWith(obstacle)) {
					// TODO : Handle collision between the scooter and the given obstacle
					if (obstacle.type != null) {
						switch (obstacle.type) {
							case POTHOLE:
								if (!this.isInAir()) {
									System.out.println("Collision");
								}
								break;
							case TRAFFIC_CONE:
								System.out.println("Collision");
								break;
							case DOG:
								System.out.println("Collision");
								break;
						}
					}
				}
			}
			
			if (scooter != null) {
				
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT)) && !pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT))) {
					// Move the character to the left
					// scooter.animate(...)
					scooter.setXPosition(scooter.getXPosition() - 3);
					if (scooter.getXPosition() < 0)
						scooter.setXPosition(0);
				} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT)) && !pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT))) {
					// Move the character to the right
					// scooter.animate(...)
					scooter.setXPosition(scooter.getXPosition() + 3);
					if (scooter.getXPosition() > this.getWidth())
						scooter.setXPosition(this.getWidth());
				} else {
					// No horizontal movement
					
				}
				
				// Make the character jump
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_UP)) && this.canJump()) {
					//soundMgr.playSoundEffect("Jump");
					this.lastJump = System.nanoTime();
					Tween tween = new Tween(scooter, new TweenTransition(TweenTransitionType.QUADRATIC));
					tween.animate(TweenableParam.SCALE_X, 1, 1.25, JUMP_TIME/2);
					tween.animate(TweenableParam.SCALE_Y, 1, 1.25, JUMP_TIME/2);
					tween.animate(TweenableParam.SCALE_X, 1.25, 1, JUMP_TIME/2, JUMP_TIME/2);
					tween.animate(TweenableParam.SCALE_Y, 1.25, 1, JUMP_TIME/2, JUMP_TIME/2);
					tweenJuggler.add(tween);
				}
			}	
			
			if (obstacleContainer != null) {
				// Slow down the character
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_SPACE)) && this.brakingFluid > 0) {
					this.brakingFluid -= 1;
					this.obstacleContainer.addYVelocity(10);
					if (this.brakingFluid < 0)
						this.brakingFluid = 0;
					if (this.obstacleContainer.getYVelocity() > 0)
						this.obstacleContainer.setYVelocity(0);
				}
			}
			
		} else {
			firstPass = false;
		}
		
	}
	
	/**
	 * Engine automatically invokes draw() every frame as well. If we want to make sure mario gets drawn to
	 * the screen, we need to make sure to override this method and call mario's draw method.
	 * */
	@Override
	public void draw(Graphics g){
		super.draw(g);
	}

	/**
	 * Quick main class that simply creates an instance of our game and starts the timer
	 * that calls update() and draw() every frame
	 * */
	public static void main(String[] args) {
		
		LabOneGame game = new LabOneGame();
		
		/* Set up utility managers */
		
		// Sound manager
		/*
		soundMgr.loadSoundEffect("Jump", "smb_jump-small.wav");
		soundMgr.loadSoundEffect("Coin", "Mario-coin-sound.wav");
		soundMgr.loadMusic("Background Music", "01-super-mario-bros.wav");
		soundMgr.playMusic("Background Music");
		*/
		
		// Quest manager
		QuestManager questManager = new QuestManager(game);

		/* Set up Sprite animations and information */
		for (int i = 0; i < 10; i++) {
			game.addObstacle(ObstacleType.POTHOLE, 64*i, 500);
		}
		for (int i = 0; i < 6; i++) {
			game.addObstacle(ObstacleType.TRAFFIC_CONE, 35*i, 800 + 50*i);
			game.addObstacle(ObstacleType.TRAFFIC_CONE, GAME_WIDTH - 35*i, 800 + 50*i);
		}
		game.addObstacle(ObstacleType.DOG, GAME_WIDTH/2, 1250);
				
		for (int i = 0; i< 1000; i++) {
			game.addLine(GAME_WIDTH/2, 256*i);
		}
		
		/* Start the game */
		game.start();
		
	}
}
