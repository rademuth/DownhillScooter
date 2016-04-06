package edu.virginia.lab1test;

import java.awt.Graphics;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;

import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.MovingSprite;
import edu.virginia.engine.display.ObstacleType;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.util.Tween;
import edu.virginia.engine.util.TweenTransition;
import edu.virginia.engine.util.TweenTransitionType;
import edu.virginia.engine.util.TweenableParam;

/**
 * Example game that utilizes our engine. We can create a simple prototype game with just a couple lines of code
 * although, for now, it won't be a very fun game :)
 * */
public class LabOneGame extends Game {

	private final static String[] dogImages = {"Dog_walk_1.png", "Dog_walk_2.png", "Dog_walk_3.png", "Dog_walk_4.png"};
	private final static int GAME_WIDTH = 500;
	private final static int GAME_HEIGHT = 725;
	
	private final static double INITIAL_VELOCITY = -125;
	private final static double VELOCITY_INCREMENT = 10;
	private final static double HORIZONTAL_INCREMENT = 5;
	private final static long JUMP_TIME = 750;
	private final static double MAX_FLUID = 100;
	private final static double FLUID_INCREMENT = 1;
	private final static double MAX_HEALTH = 100;
	private final static double HEALTH_INCREMENT = 10;

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
	private Sprite fluidIconSprite;
	private Sprite fluidFrameSprite;	
	private Sprite heartSprite;
	private Sprite heartIconSprite;
	private Sprite heartFrameSprite;

	private long lastJump;
	private double fluid;
	private double health;
	
	private boolean firstPass = true;
	
	private double score;
	
	/**
	 * Constructor. See constructor in Game.java for details on the parameters given
	 * */
	public LabOneGame() {
		super("Lab One Test Game", GAME_WIDTH, GAME_HEIGHT);
		this.lastJump = System.nanoTime();
		this.fluid = MAX_FLUID;
		this.health = MAX_HEALTH;
		this.score = 0;
		
		this.fluidSprite = new Sprite("Fluid", "Fluid_bar.png");
		this.fluidIconSprite = new Sprite("Fluid Icon", "Fluid_icon.png");
		this.fluidFrameSprite = new Sprite("Fluid Frame", "Frame.png");
		this.fluidSprite.setAlpha(0.75f);
		this.fluidIconSprite.setAlpha(0.90f);
		this.fluidFrameSprite.setAlpha(0.90f);
		this.fluidSprite.setXPosition(25+fluidIconSprite.getUnscaledWidth());
		this.fluidIconSprite.setXPosition(20);
		this.fluidFrameSprite.setXPosition(25+fluidIconSprite.getUnscaledWidth());
		
		this.heartSprite = new Sprite("Health", "Health_bar.png");
		this.heartIconSprite = new Sprite("Heart Icon", "Heart_icon.png");
		this.heartFrameSprite = new Sprite("Health Frame", "Frame.png");
		this.heartSprite.setAlpha(0.75f);
		this.heartIconSprite.setAlpha(0.90f);
		this.heartFrameSprite.setAlpha(0.90f);
		this.heartSprite.setXPosition(GAME_WIDTH-(25+heartSprite.getUnscaledWidth()));
		this.heartIconSprite.setXPosition(GAME_WIDTH-(30+heartSprite.getUnscaledWidth()+heartIconSprite.getUnscaledWidth()));
		this.heartFrameSprite.setXPosition(GAME_WIDTH-(25+heartFrameSprite.getUnscaledWidth()));
		
		this.scooter = new Sprite("Scooter", "Test 2.png");
		this.scooter.setXPivotPoint(this.scooter.getUnscaledWidth()/2);
		this.scooter.setXPosition(GAME_WIDTH/2);
		this.scooter.hitboxYBase = this.scooter.getUnscaledHeight()/2;
		this.scooter.hitboxHeight = this.scooter.getUnscaledHeight()/2;
		
		this.physicsContainer = new PhysicsSprite("ObstacleParent");
		this.physicsContainer.setYVelocity(INITIAL_VELOCITY);
		
		this.lineContainer = new DisplayObjectContainer("Line Container");
		this.potholeContainer = new DisplayObjectContainer("Pothole Container");
		this.trafficConeContainer = new DisplayObjectContainer("Traffic Cone Container");
		this.dogContainer = new DisplayObjectContainer("Dog Container");
		this.fluidContainer = new DisplayObjectContainer("Fluid Container");
		this.heartContainer = new DisplayObjectContainer("Heart Container");
		
		this.uiContainer = new DisplayObjectContainer("UI Container");
		this.uiContainer.setYPosition(GAME_HEIGHT-75);
		
		this.physicsContainer.addChild(this.lineContainer);
		this.physicsContainer.addChild(this.potholeContainer);
		this.physicsContainer.addChild(this.trafficConeContainer);
		this.physicsContainer.addChild(this.dogContainer);
		this.physicsContainer.addChild(this.fluidContainer);
		this.physicsContainer.addChild(this.heartContainer);
		
		this.uiContainer.addChild(fluidSprite);
		this.uiContainer.addChild(fluidIconSprite);		
		this.uiContainer.addChild(fluidFrameSprite);
		this.uiContainer.addChild(heartSprite);
		this.uiContainer.addChild(heartIconSprite);
		this.uiContainer.addChild(heartFrameSprite);
		
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

			if (this.health <= 0) {
				this.stop();
			}
			
			this.score -= this.physicsContainer.getYVelocity();
			
			/** 
			 * Might have issues running during the first frame of the game since sprites
			 * might not have been set to their correct positions. This will result in
			 * collisions even though sprites may not physically touch each other.
			 */
			
			Iterator<Sprite> iter = this.getObstacles().iterator();
			while (iter.hasNext()) {
				Sprite s = iter.next();
				// Run collision detection algorithm between objects
				if (scooter.collidesWith(s)) {
					if (s.type != null) {
						switch (s.type) {
							case POTHOLE:
								if (!this.isInAir()) {
									System.out.println("Collision");
									this.health -= HEALTH_INCREMENT;
									if (this.health < 0)
										this.health = 0;
									this.heartSprite.setScaleX(this.health / MAX_HEALTH);
									iter.remove();
								}
								break;
							case TRAFFIC_CONE:
								System.out.println("Collision");
								this.health -= HEALTH_INCREMENT;
								if (this.health < 0)
									this.health = 0;
								this.heartSprite.setScaleX(this.health / MAX_HEALTH);
								iter.remove();
								break;
							case DOG:
								System.out.println("Collision");
								this.health -= HEALTH_INCREMENT;
								if (this.health < 0)
									this.health = 0;
								this.heartSprite.setScaleX(this.health / MAX_HEALTH);
								iter.remove();
								break;
							case FLUID:
								System.out.println("Fluid");
								soundMgr.playSoundEffect("Fluid");
								s.setVisible(false);
								this.fluid = MAX_FLUID;
								this.fluidSprite.setScaleX(1);
								iter.remove();
								break;
							case HEART:
								System.out.println("Heart");
								soundMgr.playSoundEffect("Heart");
								s.setVisible(false);
								this.health = MAX_HEALTH;
								this.heartSprite.setScaleX(1);
								iter.remove();
								break;
						}
					}
				}
			}
			
			if (scooter != null) {
				
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT)) && !pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT))) {
					// Move the character to the left
					// scooter.animate(...)
					scooter.setXPosition(scooter.getXPosition() - HORIZONTAL_INCREMENT);
					if (scooter.getXPosition() < 0)
						scooter.setXPosition(0);
				} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT)) && !pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT))) {
					// Move the character to the right
					// scooter.animate(...)
					scooter.setXPosition(scooter.getXPosition() + HORIZONTAL_INCREMENT);
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
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_SPACE)) && this.fluid > 0) {
					this.fluid -= FLUID_INCREMENT;
					this.physicsContainer.addYVelocity(VELOCITY_INCREMENT);
					if (this.fluid < 0)
						this.fluid = 0;
					if (this.physicsContainer.getYVelocity() > 0)
						this.physicsContainer.setYVelocity(0);
					this.fluidSprite.setScaleX(this.fluid / MAX_FLUID);
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
		AttributedString scoreString = new AttributedString((int)this.score + "");
		scoreString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		scoreString.addAttribute(TextAttribute.WIDTH, TextAttribute.WIDTH_EXTENDED);
		scoreString.addAttribute(TextAttribute.SIZE, 18);
		g.drawString(scoreString.getIterator(), 15, 25);
	}

	/**
	 * Quick main class that simply creates an instance of our game and starts the timer
	 * that calls update() and draw() every frame
	 * */
	public static void main(String[] args) {
		
		LabOneGame game = new LabOneGame();
		
		/* Set up utility managers */
		
		// Sound manager
		soundMgr.loadSoundEffect("Fluid", "fluid.wav");
		soundMgr.loadSoundEffect("Heart", "heart.wav");
		//soundMgr.loadMusic("Background Music", "01-super-mario-bros.wav");
		//soundMgr.playMusic("Background Music");

		/* Add obstacles to the level */
		
		for (int i = 0; i < 10; i++) {
			game.addObstacle(ObstacleType.POTHOLE, 50*i, 500);
		}
		for (int i = 0; i < 6; i++) {
			game.addObstacle(ObstacleType.TRAFFIC_CONE, 35*i, 800 + 50*i);
			game.addObstacle(ObstacleType.TRAFFIC_CONE, GAME_WIDTH - 35*i, 800 + 50*i);
		}
		game.addObstacle(ObstacleType.DOG, GAME_WIDTH/2, 1250);
		
		game.addObstacle(ObstacleType.FLUID, 200, 1500);
		game.addObstacle(ObstacleType.HEART, 400, 1500);
				
		for (int i = 0; i< 1000; i++) {
			game.addLine(GAME_WIDTH/2, 256*i);
		}
		
		/* Start the game */
		game.start();
		
	}
}
