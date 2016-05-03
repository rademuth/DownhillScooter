package edu.virginia.lab1test;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.DisplayText;
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

	// Predefined images and templates
	private final static String[] dogImages = {"Dog_walk_1.png", "Dog_walk_2.png", "Dog_walk_3.png", "Dog_walk_4.png"};
	private final static String[] templates = {"template1.txt", "template2.txt", "template3.txt", "template4.txt", "template5.txt", "template6.txt", "template7.txt", "template8.txt"};
	
	// Game dimensions
	public final static int GAME_WIDTH = 500;
	public final static int GAME_HEIGHT = 725;
	
	// Game information
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
	private final static double TEMPLATE_LENGTH = 6000;
	private final static double FIRST_TEMPLATE_OFFSET = 2000;

	/* 
	 * Sprites and containers
	 * 
	 * - Game
	 *   - gameContainer
	 *     - scooter
	 *     - physicsContainer
	 *       - lineContainer
	 *       - potholeContainer
	 *       - trafficConeContainer
	 *       - dogContainer
	 *       - fluidContainer
	 *       - heartContainer
	 *     - uiContainer
	 *       - fluidBar
	 *       - fluidIcon
	 *       - fluidFrame
	 *       - healthBar
	 *       - healthIcon
	 *       - healthFrame
	 *     - scoreText
	 *     - lossText
	 *   - menuContainer
	 *     - lines
	 *     - dog
	 *     - menuImage
	 */
	private Sprite scooter;
	private PhysicsSprite physicsContainer;
	private DisplayObjectContainer lineContainer;
	private DisplayObjectContainer potholeContainer;
	private DisplayObjectContainer trafficConeContainer;
	private DisplayObjectContainer dogContainer;
	private DisplayObjectContainer fluidContainer;
	private DisplayObjectContainer heartContainer;
	private DisplayObjectContainer uiContainer;
	private DisplayObjectContainer menuContainer;
	private DisplayObjectContainer highScoresContainer;
	private DisplayObjectContainer gameContainer;
	private Sprite fluidBar;
	private Sprite fluidIcon;
	private Sprite fluidFrame;	
	private Sprite healthBar;
	private Sprite healthIcon;
	private Sprite healthFrame;
	private DisplayText scoreText;
	private DisplayText lostText;
	
	// Game variables
	private boolean objectsReady = false;
	private boolean displayMenu;
	private boolean lost;
	private long lastLoss;
	private long lastJump;
	private long lastCollision;
	private long startTime;
	private double fluid;
	private double health;
	private double score;
	private double fluidThreshold;
	private double heartThreshold;
	private int numTemplatesAdded;	
	private Random rand;
	
	private DisplayObject menuImage = new DisplayObject("Menu Image", "Menu2.png");
	
	/**
	 * Constructor. See constructor in Game.java for details on the parameters given
	 * */
	public LabOneGame() {
		super("Lab One Test Game", GAME_WIDTH, GAME_HEIGHT);
		
		// Initialize sprites and containers
		this.scooter = new Sprite("Scooter", "Test 2.png");
		this.physicsContainer = new PhysicsSprite("ObstacleParent");
		this.lineContainer = new DisplayObjectContainer("Line Container");
		this.potholeContainer = new DisplayObjectContainer("Pothole Container");
		this.trafficConeContainer = new DisplayObjectContainer("Traffic Cone Container");
		this.dogContainer = new DisplayObjectContainer("Dog Container");
		this.fluidContainer = new DisplayObjectContainer("Fluid Container");
		this.heartContainer = new DisplayObjectContainer("Heart Container");
		this.uiContainer = new DisplayObjectContainer("UI Container");
		this.menuContainer = new DisplayObjectContainer("Menu Container");
		this.highScoresContainer = new DisplayObjectContainer("High Scores Container");
		this.gameContainer = new DisplayObjectContainer("Game Container");
		this.fluidBar = new Sprite("Fluid", "Fluid_bar.png");
		this.fluidIcon = new Sprite("Fluid Icon", "Fluid_icon.png");
		this.fluidFrame = new Sprite("Fluid Frame", "Frame.png");
		this.healthBar = new Sprite("Health", "Health_bar.png");
		this.healthIcon = new Sprite("Heart Icon", "Heart_icon.png");
		this.healthFrame = new Sprite("Health Frame", "Frame.png");
		this.scoreText = new DisplayText("Score Text", "0", 18);
		this.lostText = new DisplayText("Lost Text", "YOU LOST", 60);
		
		// Edit initial values for sprites and containers
		this.scooter.setXPivotPoint(this.scooter.getUnscaledWidth()/2);
		this.scooter.setXPosition(GAME_WIDTH/2);
		this.scooter.hitboxYBase = this.scooter.getUnscaledHeight()/2;
		this.scooter.hitboxHeight = this.scooter.getUnscaledHeight()/2;
		this.uiContainer.setYPosition(GAME_HEIGHT-75);
		this.fluidBar.setAlpha(0.75f);
		this.fluidIcon.setAlpha(0.90f);
		this.fluidFrame.setAlpha(0.90f);
		this.fluidBar.setXPosition(25+fluidIcon.getUnscaledWidth());
		this.fluidIcon.setXPosition(20);
		this.fluidFrame.setXPosition(25+fluidIcon.getUnscaledWidth());		
		this.healthBar.setAlpha(0.75f);
		this.healthIcon.setAlpha(0.90f);
		this.healthFrame.setAlpha(0.90f);
		this.healthBar.setXPosition(GAME_WIDTH-(25+healthBar.getUnscaledWidth()));
		this.healthIcon.setXPosition(GAME_WIDTH-(30+healthBar.getUnscaledWidth()+healthIcon.getUnscaledWidth()));
		this.healthFrame.setXPosition(GAME_WIDTH-(25+healthFrame.getUnscaledWidth()));
		this.scoreText.setXPosition(15);
		this.scoreText.setYPosition(25);
		this.highScoresContainer.setXPosition(205);
		this.highScoresContainer.setYPosition(70);
		//this.highScoresText.setXPosition(205);
		//this.highScoresText.setYPosition(70);
		this.lostText.setXPosition(15);
		this.lostText.setYPosition(GAME_HEIGHT/2);
		this.lostText.setVisible(false);
		
		// Initialize game variables
		this.displayMenu = true;
		this.lost = false;
		this.lastLoss = -1;
		this.lastJump = -1;
		this.lastCollision = -1;
		this.startTime = -1;
		this.fluid = MAX_FLUID;
		this.health = MAX_HEALTH;
		this.score = 0;
		this.fluidThreshold = 0.5;
		this.heartThreshold = 0.5;
		this.numTemplatesAdded = 0;
		this.rand = new Random();
		
		// Organize the display tree
		this.addChild(menuContainer);
		this.addChild(gameContainer);
		for (int i = 0; i < 3; i++) {
			Sprite lineSprite = new Sprite("Line", "Line.png");
			lineSprite.setPivotPoint(lineSprite.getUnscaledWidth()/2, 0);
			lineSprite.setPosition(GAME_WIDTH/2, 50+256*i);
			this.menuContainer.addChild(lineSprite);
		}
		this.menuContainer.addChild(menuImage);
		MovingSprite dogSprite = new MovingSprite("Dog", dogImages, ObstacleType.DOG, 0, GAME_WIDTH);
		dogSprite.setPivotPoint(dogSprite.getUnscaledWidth()/2, dogSprite.getUnscaledHeight()/2);
		dogSprite.setPosition(125, 580);
		this.menuContainer.addChild(dogSprite);
		
		loadHighScores();
		this.menuContainer.addChild(highScoresContainer);

		this.gameContainer.addChild(physicsContainer);
		this.gameContainer.addChild(uiContainer);
		this.gameContainer.addChild(scoreText);
		this.gameContainer.addChild(scooter);
		this.gameContainer.addChild(lostText);
		this.physicsContainer.addChild(lineContainer);
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
		
		this.removeChild(gameContainer);
		
	}
	
	public void startGame() {
		
		// Edit initial values for sprites and containers
		this.scooter.setXPosition(GAME_WIDTH/2);
		this.physicsContainer.setYPosition(0);
		this.physicsContainer.setYVelocity(INITIAL_VELOCITY);
		this.physicsContainer.setYAcceleration(-10);
		this.fluidBar.setScaleX(1);
		this.healthBar.setScaleX(1);
		this.lostText.setVisible(false);
		
		// Initialize game variables
		this.displayMenu = false;
		this.lost = false;
		this.lastLoss = -1;
		this.lastJump = System.nanoTime();
		this.lastCollision = System.nanoTime();
		this.startTime = System.nanoTime();
		this.fluid = MAX_FLUID;
		this.health = MAX_HEALTH;
		this.score = 0;
		this.fluidThreshold = 0.5;
		this.heartThreshold = 0.5;
		this.numTemplatesAdded = 0;
						
		/* Add obstacles to the level */
				
		// Add lines
		for (int i = 0; i< 1000; i++) {
			this.addLine(GAME_WIDTH/2, 256*i);
		}
		
		// Tutorial obstacles
		this.addObstacle(ObstacleType.ROW, 0, 1000);
		this.addObstacle(ObstacleType.FUNNEL, 0, 1250);
		this.addObstacle(ObstacleType.DOG, GAME_WIDTH/2, 1750);
		
		// Load the first template
		this.handleEvent(null);

		// Swap the gameContainer with the menuContainer
		this.removeChild(menuContainer);
		this.addChild(gameContainer);
		Tween menuTween = new Tween(menuContainer, new TweenTransition(TweenTransitionType.LINEAR));
		menuTween.animate(TweenableParam.Y, 0, -750, 1000);
		tweenJuggler.add(menuTween);
		Tween gameTween = new Tween(gameContainer, new TweenTransition(TweenTransitionType.LINEAR));
		gameTween.animate(TweenableParam.Y, 750, 0, 1000);
		tweenJuggler.add(gameTween);
	}
	
	public void endGame() {
		
		// Clean up obstacles from the level
		this.lineContainer.removeAll();
		this.potholeContainer.removeAll();
		this.trafficConeContainer.removeAll();
		this.dogContainer.removeAll();
		this.fluidContainer.removeAll();
		this.heartContainer.removeAll();
		this.removeSprites();

		// Edit initial values for sprites and containers
		this.physicsContainer.setYPosition(0);
		this.physicsContainer.setYVelocity(0);
		this.physicsContainer.setYAcceleration(0);
		this.physicsContainer.lastKinematicsUpdate = -1;
		
		// Initialize game variables
		this.displayMenu = true;
		this.loadHighScores();
		
		// Add the menu container as a child
		this.removeChild(gameContainer);
		this.addChild(menuContainer);
		Tween menuTween = new Tween(menuContainer, new TweenTransition(TweenTransitionType.LINEAR));
		menuTween.animate(TweenableParam.Y, 750, 0, 1000);
		tweenJuggler.add(menuTween);
		System.out.println("Where is the menu");
	}
	
	public void saveScore() {
		String fileName = "highscores.txt";
		Scanner infile = null;
		ArrayList<String> scores = new ArrayList<String>();
		try {
			infile = new Scanner(new File(fileName));
			while(infile.hasNextLine()){
				String line = infile.nextLine();
				scores.add(line);
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("Could not find file " + fileName);
		}
		
		String name = (String)JOptionPane.showInputDialog("Enter your name (Max of three characters)");
		
		if(scores.size() == 0)
			scores.add(name.substring(0, 3) + " " + (int)this.score);
		else
			for(int x = 0; x < scores.size(); x++){
				//String[] arr = line.split(", ");
				if((int)this.score > Integer.parseInt(scores.get(x).split(" ")[1])){
					scores.add(x, name.substring(0, 3) + " " +(int)this.score);
					break;
				}
				else if(x == scores.size()-1 && scores.size() < 5){
					scores.add(name.substring(0, 3) + " " +(int)this.score);
					break;
				}
			}
		
		if(scores.size() > 5)
			scores.remove(5);
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			for(String s : scores)
				writer.println(s);
			writer.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Could not find file " + fileName);
		}
		catch (UnsupportedEncodingException e) {
			System.out.println("Doesn't support UTF-8");
		}
		
		this.endGame();
	}
	
	public void loadHighScores(){
		this.highScoresContainer.removeAll();
		
		String fileName = "highscores.txt";
		Scanner infile = null;
		
		DisplayText highScore = new DisplayText("High Score", "0", 18);
		highScore.setText("High Scores: ");
		highScore.setXPosition(0);
		highScore.setYPosition(0);
		this.highScoresContainer.addChild(highScore);

		int scoreX = 0;
		int scoreY = 20;
		
		try {
			infile = new Scanner(new File(fileName));
			while(infile.hasNextLine()){
				String line = infile.nextLine();
				//String[] arr = line.split(", ");
				//scoreText += line + "\n";
				DisplayText score = new DisplayText("Score", "0", 18);
				score.setText(line);
				score.setXPosition(scoreX);
				score.setYPosition(scoreY);
				this.highScoresContainer.addChild(score);
				scoreY += 20;
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("Could not find file " + fileName);
		}
	}
	
	public void addTemplate(String fileName, double yOffset) {
		String file = ("resources" + File.separator + "templates" + File.separator + fileName);
		File inputFile = new File(file);
		
		Scanner scan = null; 
		try {
			scan = new Scanner(inputFile);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file " + file);
		}
		
		while (scan.hasNextLine()) {
			// ObstacleType,xPos,yPos
			String line = scan.nextLine();
			if (!line.substring(0,2).equals("//")) {
				String[] arr = line.split(",");
				ObstacleType type = ObstacleType.valueOf(arr[0]);
				this.addObstacle(type, Double.parseDouble(arr[1]), yOffset+Double.parseDouble(arr[2]));
			}
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
		if (this.health < 0){
			this.health = 0;
			this.lost = true;
			this.lostText.setVisible(true);
			this.lastLoss = System.nanoTime();
			this.physicsContainer.setYVelocity(0);
			this.physicsContainer.setYAcceleration(0);
		}
		this.lastCollision = System.nanoTime();
		// Scale down the health bar
		Tween healthTween = new Tween(healthBar, new TweenTransition(TweenTransitionType.LINEAR));
		healthTween.animate(TweenableParam.SCALE_X, healthBar.getScaleX(), this.health / MAX_HEALTH + 0.01, 250, 0);
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
					this.addObstacle(ObstacleType.POTHOLE, GAME_WIDTH - (50*i + 25), yPos + 50*9);	
				}
				break;
			case WEDGE:
				conesNeeded = (int)Math.ceil((double)GAME_WIDTH / 35.0) - 6;
				this.addObstacle(ObstacleType.TRAFFIC_CONE, GAME_WIDTH / 2, yPos);
				for (int i = 1; i <= conesNeeded / 2; i++) {
					this.addObstacle(ObstacleType.TRAFFIC_CONE, GAME_WIDTH/2 - 25*i, yPos + 50*i);	
					this.addObstacle(ObstacleType.TRAFFIC_CONE, GAME_WIDTH/2 + 25*i, yPos + 50*i);	
				}
				break;
			case CONSTRUCTION_ZONE:
				this.addObstacle(ObstacleType.TRAFFIC_CONE, xPos, yPos);
				this.addObstacle(ObstacleType.POTHOLE, xPos + 50, yPos);
				this.addObstacle(ObstacleType.POTHOLE, xPos + 100, yPos);
				this.addObstacle(ObstacleType.POTHOLE, xPos + 150, yPos);
				this.addObstacle(ObstacleType.TRAFFIC_CONE, xPos + 200, yPos);				
				break;
			case RANDOM:
				for (int i = 0; i < 10; i++) {
					int x = this.rand.nextInt(455); // Need to add 20
					int y = this.rand.nextInt(1000); // Need to add offset
					this.addObstacle(ObstacleType.TRAFFIC_CONE, x + 20, y + yPos);
				}
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
		
		// Check if all objects have been initialized
		if (objectsReady) {
			if (displayMenu) {
				if (pressedKeys.size() > 0) {
					this.startGame();
				}
			} else {
		
				if (this.lost) {
					if ((System.nanoTime() - this.lastLoss) / 1000000 > 5000) {
						displayMenu = true;
						// Calculate average speed multiplier
						double averageVelocity = this.physicsContainer.getYPosition() / ((System.nanoTime() - this.startTime) / 1000000000);
						double scoreMultiplier = averageVelocity / INITIAL_VELOCITY;
						double finalScore = this.score*scoreMultiplier;
						System.out.println("Initial Score: " + this.score + "   Average Velocity: " + averageVelocity + "   Multiplier: " + scoreMultiplier + "   Final Score: " + finalScore);
						this.saveScore();
					} else {
						return;
					}
				}
				
				//this.score -= this.physicsContainer.getYVelocity();
				this.score = -this.physicsContainer.getYPosition();
				this.scoreText.setText((int)this.score+"");
					
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
			}
		} else {
			objectsReady = true;
		}
	}
	
	/**
	 * Engine automatically invokes draw() every frame as well. If we want to make sure mario gets drawn to
	 * the screen, we need to make sure to override this method and call mario's draw method.
	 * */
	@Override
	public void draw(Graphics g) {				
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
		soundMgr.loadSoundEffect("Fluid", "fluid.wav");
		soundMgr.loadSoundEffect("Heart", "heart.wav");
		soundMgr.loadSoundEffect("Dog", "Dog Barking.wav");
		soundMgr.loadSoundEffect("Cone", "Batman Punch.wav");
		soundMgr.loadSoundEffect("Pothole", "cartoon037.wav");
		soundMgr.loadMusic("Background Music", "Racing Menu.wav");
		soundMgr.playMusic("Background Music");

		/* Start the game */
		game.start();
		
	}

}
