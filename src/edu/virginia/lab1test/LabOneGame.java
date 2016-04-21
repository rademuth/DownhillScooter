package edu.virginia.lab1test;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.MovingSprite;
import edu.virginia.engine.display.ObstacleType;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.display.TemplateMarker;
import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventListener;
import edu.virginia.engine.util.Tween;
import edu.virginia.engine.util.TweenTransition;
import edu.virginia.engine.util.TweenTransitionType;
import edu.virginia.engine.util.TweenableParam;

/**
 * Example game that utilizes our engine. We can create a simple prototype game with just a couple lines of code
 * although, for now, it won't be a very fun game :)
 * */
public class LabOneGame extends Game implements IEventListener {

	private final static String[] dogImages = {"Dog_walk_1.png", "Dog_walk_2.png", "Dog_walk_3.png", "Dog_walk_4.png"};
	public final static int GAME_WIDTH = 500;
	public final static int GAME_HEIGHT = 725;
	
	private final static double INITIAL_VELOCITY = -100;
	private final static double VELOCITY_INCREMENT = 10;
	private final static double MIN_SPEED = -100;
	private final static double HORIZONTAL_INCREMENT = 5;
	private final static long JUMP_TIME = 750;
	private final static long INVINCIBILITY_TIME = 1000;
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
	private Sprite fluidBar;
	private Sprite fluidIcon;
	private Sprite fluidFrame;	
	private Sprite healthBar;
	private Sprite healthIcon;
	private Sprite healthFrame;

	private long lastJump;
	private long lastCollision;
	private long loseTime;
	private double fluid;
	private double health;
	private double score;
	
	private boolean firstPass = true;
	private boolean lost = false;
	
	private Random rand;
	private final static String[] templates = {"template1.txt", "template2.txt", "template3.txt", "template4.txt", "template5.txt", "template6.txt"};
	private final static double TEMPLATE_LENGTH = 6000;
	private final static double FIRST_TEMPLATE_OFFSET = 2000;
	private int numTemplatesAdded;
	
	private double fluidThreshold;
	private double heartThreshold;

	/**
	 * Constructor. See constructor in Game.java for details on the parameters given
	 * */
	public LabOneGame() {
		super("Lab One Test Game", GAME_WIDTH, GAME_HEIGHT);
		
		this.rand = new Random();
		this.lastJump = System.nanoTime();
		this.lastCollision = System.nanoTime();
		this.loseTime = -1;
		this.fluid = MAX_FLUID;
		this.health = MAX_HEALTH;
		this.score = 0;
		this.numTemplatesAdded = 0;
		this.fluidThreshold = 0.5;
		this.heartThreshold = 0.5;
		
		this.fluidBar = new Sprite("Fluid", "Fluid_bar.png");
		this.fluidIcon = new Sprite("Fluid Icon", "Fluid_icon.png");
		this.fluidFrame = new Sprite("Fluid Frame", "Frame.png");
		this.fluidBar.setAlpha(0.75f);
		this.fluidIcon.setAlpha(0.90f);
		this.fluidFrame.setAlpha(0.90f);
		this.fluidBar.setXPosition(25+fluidIcon.getUnscaledWidth());
		this.fluidIcon.setXPosition(20);
		this.fluidFrame.setXPosition(25+fluidIcon.getUnscaledWidth());
		
		this.healthBar = new Sprite("Health", "Health_bar.png");
		this.healthIcon = new Sprite("Heart Icon", "Heart_icon.png");
		this.healthFrame = new Sprite("Health Frame", "Frame.png");
		this.healthBar.setAlpha(0.75f);
		this.healthIcon.setAlpha(0.90f);
		this.healthFrame.setAlpha(0.90f);
		this.healthBar.setXPosition(GAME_WIDTH-(25+healthBar.getUnscaledWidth()));
		this.healthIcon.setXPosition(GAME_WIDTH-(30+healthBar.getUnscaledWidth()+healthIcon.getUnscaledWidth()));
		this.healthFrame.setXPosition(GAME_WIDTH-(25+healthFrame.getUnscaledWidth()));
		
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
		
		this.uiContainer.addChild(fluidBar);
		this.uiContainer.addChild(fluidIcon);
		this.uiContainer.addChild(fluidFrame);
		this.uiContainer.addChild(healthBar);
		this.uiContainer.addChild(healthIcon);
		this.uiContainer.addChild(healthFrame);
		
		this.addChild(this.physicsContainer);
		this.addChild(this.scooter);
		this.addChild(this.uiContainer);
		
	}
	
	public void addTemplate(String fileName, double yOffset) {
		String file = ("resources" + File.separator + "templates" + File.separator + fileName);
		File inputFile = new File(file);
		
		Scanner scan = null; 
		try {
			scan = new Scanner(inputFile);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file");
		}
		
		while (scan.hasNextLine()) {
			// ObstacleType,xPos,yPos
			String[] arr = scan.nextLine().split(",");
			ObstacleType type = ObstacleType.valueOf(arr[0]);
			this.addObstacle(type, Double.parseDouble(arr[1]), yOffset+Double.parseDouble(arr[2]));
		}
				
		TemplateMarker tm = new TemplateMarker("Template Marker", this);
		tm.setYPosition(yOffset);
		this.physicsContainer.addChild(tm);
		
		System.out.println("Loaded " + fileName);
	}
	
	public void addPowerup() {
		double val = this.rand.nextDouble();
		if (val < this.heartThreshold) {
			int tempX = (int) this.rand.nextInt(this.getWidth());
			int tempY = (int) (FIRST_TEMPLATE_OFFSET + this.numTemplatesAdded*TEMPLATE_LENGTH + this.rand.nextInt((int)TEMPLATE_LENGTH));
			Rectangle tempBox = new Rectangle(tempX, tempY, 48, 48);
			boolean collides = true;
			while (collides) {
				collides = false;
				for (DisplayObject d : this.getObstacles()) {
					if (d.collidesWith(tempBox)) {
						collides = true;
						tempX = (int) this.rand.nextInt(this.getWidth());
						tempY = (int) (FIRST_TEMPLATE_OFFSET + this.numTemplatesAdded*TEMPLATE_LENGTH + this.rand.nextInt((int)TEMPLATE_LENGTH));
						tempBox.setLocation(tempX, tempY);
						break;
					}
				}
				collides = false;
			}
			this.addObstacle(ObstacleType.HEART, tempX, tempY);
		} else if (val > this.fluidThreshold) {
			int tempX = (int) this.rand.nextInt(this.getWidth());
			int tempY = (int) (FIRST_TEMPLATE_OFFSET + this.numTemplatesAdded*TEMPLATE_LENGTH + this.rand.nextInt((int)TEMPLATE_LENGTH));
			Rectangle tempBox = new Rectangle(tempX, tempY, 48, 64);
			boolean collides = true;
			while (collides) {
				collides = false;
				for (DisplayObject d : this.getObstacles()) {
					if (d.collidesWith(tempBox)) {
						collides = true;
						tempX = (int) this.rand.nextInt(this.getWidth());
						tempY = (int) (FIRST_TEMPLATE_OFFSET + this.numTemplatesAdded*TEMPLATE_LENGTH + this.rand.nextInt((int)TEMPLATE_LENGTH));
						tempBox.setLocation(tempX, tempY);
						break;
					}
				}
				collides = false;
			}
			this.addObstacle(ObstacleType.FLUID, tempX, tempY);
		}
		if (this.heartThreshold > 0.25)
			this.heartThreshold -= 0.025;
		if (this.fluidThreshold < 0.75)
			this.fluidThreshold += 0.025;
	}
	
	public void handleEvent(Event event) {
		int index = rand.nextInt(templates.length);
		this.addTemplate(templates[index], FIRST_TEMPLATE_OFFSET + this.numTemplatesAdded*TEMPLATE_LENGTH);
		this.addPowerup();
		this.numTemplatesAdded++;
	}
	
	public boolean canJump() {
		return (System.nanoTime() - this.lastJump) / 1000000 > JUMP_TIME;
	}
	
	public boolean isInvincible() {
		return (System.nanoTime() - this.lastCollision) / 1000000 < INVINCIBILITY_TIME;
	}
	
	public void subtractFluid() {
		this.fluid -= FLUID_INCREMENT;
		this.physicsContainer.addYVelocity(VELOCITY_INCREMENT);
		if (this.fluid < 0)
			this.fluid = 0;
		if (this.physicsContainer.getYVelocity() > MIN_SPEED)
			this.physicsContainer.setYVelocity(MIN_SPEED);
		this.fluidBar.setScaleX((this.fluid+0.01) / MAX_FLUID);
	}
	
	public void pickupFluid() {
		soundMgr.playSoundEffect("Fluid");
		this.fluid = MAX_FLUID;
		this.fluidBar.setScaleX(1);
	}
	
	public void subtractHealth() {
		this.health -= HEALTH_INCREMENT;
		if (this.health <= 0){
			//System.out.println("You're dead!");
			System.out.println(this.score);
			//System.exit(0);
			//exitGame();
		}
		this.lastCollision = System.nanoTime();
		// Scale down the health bar
		Tween healthTween = new Tween(healthBar, new TweenTransition(TweenTransitionType.LINEAR));
		healthTween.animate(TweenableParam.SCALE_X, healthBar.getScaleX(), this.health / MAX_HEALTH, 250, 0);
		tweenJuggler.add(healthTween);
		// Make the scooter 'blink' to indicate temporary invincibility
		Tween invincibilityTween = new Tween(scooter, new TweenTransition(TweenTransitionType.LINEAR));
		invincibilityTween.animate(TweenableParam.ALPHA, 1, 0.5, INVINCIBILITY_TIME/8, 0*INVINCIBILITY_TIME/8);
		invincibilityTween.animate(TweenableParam.ALPHA, 0.5, 1, INVINCIBILITY_TIME/8, 1*INVINCIBILITY_TIME/8);
		invincibilityTween.animate(TweenableParam.ALPHA, 1, 0.5, INVINCIBILITY_TIME/8, 2*INVINCIBILITY_TIME/8);
		invincibilityTween.animate(TweenableParam.ALPHA, 0.5, 1, INVINCIBILITY_TIME/8, 3*INVINCIBILITY_TIME/8);
		invincibilityTween.animate(TweenableParam.ALPHA, 1, 0.5, INVINCIBILITY_TIME/8, 4*INVINCIBILITY_TIME/8);
		invincibilityTween.animate(TweenableParam.ALPHA, 0.5, 1, INVINCIBILITY_TIME/8, 5*INVINCIBILITY_TIME/8);
		invincibilityTween.animate(TweenableParam.ALPHA, 1, 0.5, INVINCIBILITY_TIME/8, 6*INVINCIBILITY_TIME/8);
		invincibilityTween.animate(TweenableParam.ALPHA, 0.5, 1, INVINCIBILITY_TIME/8, 7*INVINCIBILITY_TIME/8);
		tweenJuggler.add(invincibilityTween);
	}
	
	public void exitGame() {
		//System.out.println("Exiting Game");
		lost = true;
		if(loseTime == -1){
			//System.out.println("Setting loseTime");
			loseTime = System.nanoTime();
		}
		
		if((System.nanoTime() - loseTime)/1000000 >= 3000){
			//System.out.println("Actually Exiting Game");
			System.exit(0);
		}
	}
	
	public void pickupHealth() {
		soundMgr.playSoundEffect("Heart");
		this.health = MAX_HEALTH;
		this.healthBar.setScaleX(1);
	}
	
	public boolean isInAir() {
		return !this.canJump();
	}
	
	public void addObstacle(ObstacleType type, double xPos, double yPos) {
		Sprite s;
		int potholesNeeded;
		int conesNeeded;
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
			case CAR:
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
			case ZIG:
				conesNeeded = (int)Math.ceil((double)GAME_WIDTH / 35.0) - 5;
				for (int i = 0; i < conesNeeded; i++) {
					this.addObstacle(ObstacleType.TRAFFIC_CONE, 35*i + 20, yPos + 50*i);
				}
				break;
			case ZAG:
				conesNeeded = (int)Math.ceil((double)GAME_WIDTH / 35.0) - 5;
				for (int i = 0; i < conesNeeded; i++) {
					this.addObstacle(ObstacleType.TRAFFIC_CONE, GAME_WIDTH - (35*i + 25), yPos + 50*i);
				}
				break;
			case ZIG_ZAG:
				this.addObstacle(ObstacleType.ZIG, 0, yPos);
				this.addObstacle(ObstacleType.ZAG, 0, yPos + 750);
				break;
			case ZAG_ZIG:
				this.addObstacle(ObstacleType.ZAG, 0, yPos);
				this.addObstacle(ObstacleType.ZIG, 0, yPos + 750);
				break;
			case FUNNEL:
				conesNeeded = (int)Math.ceil((double)GAME_WIDTH / 35.0) - 5;
				for (int i = 0; i < conesNeeded / 2; i++) {
					this.addObstacle(ObstacleType.TRAFFIC_CONE, 35*i + 20, yPos + 50*i);
					this.addObstacle(ObstacleType.TRAFFIC_CONE, GAME_WIDTH - (35*i + 25), yPos + 50*i);
				}
				break;
			case ROW:
				potholesNeeded = (int)Math.ceil((double)GAME_WIDTH / 50.0) + 1;
				for (int i = 0; i < potholesNeeded; i++) {
					this.addObstacle(ObstacleType.POTHOLE, 50*i, yPos);
				}
				break;
			case SPLIT:
				for (int i = 0; i < 10; i++) {
					this.addObstacle(ObstacleType.TRAFFIC_CONE, GAME_WIDTH / 2, yPos + 50*i);
				}
				break;
			case SPLIT_LEFT:
				this.addObstacle(ObstacleType.SPLIT, 0, yPos);
				potholesNeeded = (int)Math.ceil((double)GAME_WIDTH / 50.0);
				for (int i = 0; i < potholesNeeded / 2; i++) {
					this.addObstacle(ObstacleType.POTHOLE, 50*i + 20, yPos + 50*9);
				}
				break;
			case SPLIT_RIGHT:
				this.addObstacle(ObstacleType.SPLIT, 0, yPos);
				potholesNeeded = (int)Math.ceil((double)GAME_WIDTH / 50.0);
				for (int i = 0; i < potholesNeeded / 2; i++) {
					this.addObstacle(ObstacleType.POTHOLE,  GAME_WIDTH - (50*i + 25), yPos + 50*9);
					
				}
				break;
			case CONSTRUCTION_ZONE:
				this.addObstacle(ObstacleType.TRAFFIC_CONE, xPos, yPos);
				this.addObstacle(ObstacleType.POTHOLE, xPos + 50, yPos);
				this.addObstacle(ObstacleType.POTHOLE, xPos + 100, yPos);
				this.addObstacle(ObstacleType.POTHOLE, xPos + 150, yPos);
				this.addObstacle(ObstacleType.TRAFFIC_CONE, xPos + 200, yPos);				
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
				//this.stop();
				exitGame();
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
								if (!this.isInAir() && !this.isInvincible()) {
									//System.out.println("Collision");
									soundMgr.playSoundEffect("Pothole");
									this.subtractHealth();
									iter.remove();
								}
								break;
							case TRAFFIC_CONE:
								if (!this.isInvincible()) {
									//System.out.println("Collision");
									this.subtractHealth();
									soundMgr.playSoundEffect("Cone");
									iter.remove();
								}
								break;
							case DOG:
								if (!this.isInvincible()) {
									//System.out.println("Collision");
									this.subtractHealth();
									soundMgr.playSoundEffect("Dog");
									iter.remove();
								}
								break;
							case FLUID:
								//System.out.println("Fluid");
								s.setVisible(false);
								this.pickupFluid();
								iter.remove();
								break;
							case HEART:
								//System.out.println("Heart");
								s.setVisible(false);
								pickupHealth();
								iter.remove();
								break;
						default:
							break;
						}
					}
				} else {
					if (s.getLocalToGlobalCoors(0, s.getUnscaledHeight()).getY() < 0) {
						iter.remove();
					}
				}
			}
			
			if (scooter != null) {
				
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT)) && !pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT))) {
					// Move the character to the left
					// scooter.animate(...)
					scooter.setXPosition(scooter.getXPosition() - HORIZONTAL_INCREMENT);
					scooter.setRotation(2.5);
					if (scooter.getXPosition() < 0) {
						scooter.setXPosition(0);
						scooter.setRotation(0);
					}
				} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT)) && !pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT))) {
					// Move the character to the right
					// scooter.animate(...)
					scooter.setXPosition(scooter.getXPosition() + HORIZONTAL_INCREMENT);
					scooter.setRotation(-2.5);
					if (scooter.getXPosition() > this.getWidth()) {
						scooter.setXPosition(this.getWidth());
						scooter.setRotation(0);
					}
				} else {
					// No horizontal movement
					scooter.setRotation(0);
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
					this.subtractFluid();
				}
				
				// Speed up the character
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_DOWN))) {
					this.physicsContainer.addYVelocity(-VELOCITY_INCREMENT/4);
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
		AttributedString scoreString = new AttributedString((int)this.score + "");
		scoreString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		scoreString.addAttribute(TextAttribute.WIDTH, TextAttribute.WIDTH_EXTENDED);
		scoreString.addAttribute(TextAttribute.SIZE, 18);
		g.drawString(scoreString.getIterator(), 15, 25);
				
		super.draw(g);
		
		if (lost){
			//System.out.println("Printing YOU LOST");
			AttributedString loseString = new AttributedString("YOU LOST");
			loseString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
			loseString.addAttribute(TextAttribute.WIDTH, TextAttribute.WIDTH_EXTENDED);
			loseString.addAttribute(TextAttribute.SIZE, 60);
			//g.drawString(loseString.getIterator(), this.getWidth()/2, this.getHeight()/2);
			g.drawString(loseString.getIterator(), 15, this.getHeight()/2);
		}
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
		soundMgr.loadSoundEffect("Dog", "Dog Barking.wav");
		soundMgr.loadSoundEffect("Cone", "Batman Punch.wav");
		soundMgr.loadSoundEffect("Pothole", "cartoon037.wav");
		soundMgr.loadMusic("Background Music", "Racing Menu.wav");
		soundMgr.playMusic("Background Music");
		//soundMgr.loadMusic("Background Music", "01-super-mario-bros.wav");
		//soundMgr.playMusic("Background Music");

		/* Add obstacles to the level */

		for (int i = 0; i< 1000; i++) {
			game.addLine(GAME_WIDTH/2, 256*i);
		}
		
		// Tutorial Obstacles
		game.addObstacle(ObstacleType.ROW, 0, 1000);
		game.addObstacle(ObstacleType.FUNNEL, 0, 1250);
		game.addObstacle(ObstacleType.DOG, GAME_WIDTH/2, 1750);
		
		// Load the first template
		game.handleEvent(null);

		/* Start the game */
		game.start();
		
	}

}
