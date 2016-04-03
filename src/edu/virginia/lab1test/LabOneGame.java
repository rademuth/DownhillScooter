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
	private final static int GAME_WIDTH = 500;
	private final static int GAME_HEIGHT = 725;
	
	private final static double INITIAL_VELOCITY = -125;
	private final static double VELOCITY_INCREMENT = 10;
	private final static long JUMP_TIME = 500;
	private final static double MAX_FLUID = 100;
	private final static double FLUID_INCREMENT = 1;
	private final static double MAX_HEALTH = 0;
	private final static double HEALTH_INCREMENT = 0;

	private Sprite scooter;
	private PhysicsSprite physicsContainer;
	private DisplayObjectContainer lineContainer;
	private DisplayObjectContainer potholeContainer;
	private DisplayObjectContainer trafficConeContainer;
	private DisplayObjectContainer dogContainer;
	private DisplayObjectContainer fluidContainer;
	private DisplayObjectContainer heartContainer;
	private DisplayObjectContainer uiContainer;
	private Sprite fluidSprite;
	private Sprite fluidFrameSprite;

	private long lastJump;
	private double brakingFluid;
	
	static boolean firstPass = true;
	
	/**
	 * Constructor. See constructor in Game.java for details on the parameters given
	 * */
	public LabOneGame() {
		super("Lab One Test Game", GAME_WIDTH, GAME_HEIGHT);
		this.lastJump = System.nanoTime();
		this.brakingFluid = MAX_FLUID;
		
		this.fluidSprite = new Sprite("Fluid", "Fluid_bar.png");
		this.fluidFrameSprite = new Sprite("Fluid Frame", "Frame.png");
		this.fluidSprite.setAlpha(0.75f);
		this.fluidFrameSprite.setAlpha(0.90f);
		
		this.scooter = new Sprite("Scooter", "Scooter.png");
		this.scooter.setXPivotPoint(this.scooter.getUnscaledWidth()/2);
		this.scooter.setYPivotPoint(this.scooter.getUnscaledHeight()/2);
		this.scooter.setXPosition(GAME_WIDTH/2);
		this.scooter.setYPosition(this.scooter.getUnscaledHeight()/2);
		this.physicsContainer = new PhysicsSprite("ObstacleParent");
		this.physicsContainer.setYVelocity(INITIAL_VELOCITY);
		this.lineContainer = new DisplayObjectContainer("Line Container");
		this.potholeContainer = new DisplayObjectContainer("Pothole Container");
		this.trafficConeContainer = new DisplayObjectContainer("Traffic Cone Container");
		this.dogContainer = new DisplayObjectContainer("Dog Container");
		this.fluidContainer = new DisplayObjectContainer("Fluid Container");
		this.heartContainer = new DisplayObjectContainer("Heart Container");
		this.uiContainer = new DisplayObjectContainer("UI Container");
		this.uiContainer.setXPosition(25);
		this.uiContainer.setYPosition(GAME_HEIGHT-75);
		this.physicsContainer.addChild(this.lineContainer);
		this.physicsContainer.addChild(this.potholeContainer);
		this.physicsContainer.addChild(this.trafficConeContainer);
		this.physicsContainer.addChild(this.dogContainer);
		this.physicsContainer.addChild(this.fluidContainer);
		this.physicsContainer.addChild(this.heartContainer);
		this.uiContainer.addChild(fluidSprite);
		this.uiContainer.addChild(fluidFrameSprite);
		this.addChild(this.physicsContainer);
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
			case FLUID:
				s = new Sprite("Fluid", "Fluid.png", type);
				s.setPivotPoint(s.getUnscaledWidth()/2, s.getUnscaledHeight()/2);
				s.setPosition(xPos, yPos);
				this.fluidContainer.addChild(s);
				this.addSprite(s);
				break;
			case HEART:
				s = new Sprite("Heart", "Heart.png", type);
				s.setPivotPoint(s.getUnscaledWidth()/2, s.getUnscaledHeight()/2);
				s.setPosition(xPos, yPos);
				this.heartContainer.addChild(s);
				this.addSprite(s);
				break;
		}
	}
	
	public void addLine(double xPos, double yPos) {
		Sprite s = new Sprite("Line", "Line.png");
		s.setPivotPoint(s.getUnscaledWidth()/2, s.getUnscaledHeight()/2);
		s.setPosition(xPos, yPos);
		this.lineContainer.addChild(s);
		this.addSprite(s);
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
			for (Sprite s : returnObjects) {
				// Run collision detection algorithm between objects
				if (scooter.collidesWith(s)) {
					// TODO : Handle collision between the scooter and the given obstacle
					if (s.type != null) {
						switch (s.type) {
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
							case FLUID:
								System.out.println("Fluid");
								s.setVisible(false);
								this.brakingFluid = MAX_FLUID;
								this.fluidSprite.setScaleX(1);
							case HEART:
								System.out.println("Heart");
								s.setVisible(false);
								// this.health = MAX_HEALTH;
								// this.healthSprite.setScaleX(1);
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
			
			if (physicsContainer != null) {
				// Slow down the character
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_SPACE)) && this.brakingFluid > 0) {
					this.brakingFluid -= FLUID_INCREMENT;
					this.physicsContainer.addYVelocity(VELOCITY_INCREMENT);
					if (this.brakingFluid < 0)
						this.brakingFluid = 0;
					if (this.physicsContainer.getYVelocity() > 0)
						this.physicsContainer.setYVelocity(0);
					this.fluidSprite.setScaleX(this.brakingFluid / MAX_FLUID);
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
		
		game.addObstacle(ObstacleType.FLUID, GAME_WIDTH/2, 1500);
				
		for (int i = 0; i< 1000; i++) {
			game.addLine(GAME_WIDTH/2, 256*i);
		}
		
		/* Start the game */
		game.start();
		
	}
}
