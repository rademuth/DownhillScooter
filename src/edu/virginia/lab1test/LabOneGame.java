package edu.virginia.lab1test;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.MovingSprite;
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

	static Sprite scooter = new Sprite("Scooter", "Scooter.png");
	static PhysicsSprite obstacleParent = new PhysicsSprite("ObstacleParent");
	static Sprite pothole1 = new Sprite("PotHole1", "Manhole.png");
	static Sprite pothole2 = new Sprite("PotHole2", "Manhole.png");
	static Sprite pothole3 = new Sprite("PotHole3", "Manhole.png");
	static Sprite pothole4 = new Sprite("PotHole4", "Manhole.png");
	static Sprite pothole5 = new Sprite("PotHole5", "Manhole.png");
	static Sprite pothole6 = new Sprite("PotHole6", "Manhole.png");
	static Sprite pothole7 = new Sprite("PotHole7", "Manhole.png");
	
	static String[] dogImages = {"Dog_move_1.png", "Dog_move_2.png"};
	static MovingSprite dog1 = new MovingSprite("Dog1", dogImages);
	
	static boolean firstPass = true;
		
	/**
	 * Constructor. See constructor in Game.java for details on the parameters given
	 * */
	public LabOneGame() {
		super("Lab One Test Game", 500, 725);
	}
	
	/**
	 * Engine will automatically call this update method once per frame and pass to us
	 * the set of keys (as strings) that are currently being pressed down
	 * */
	@Override
	public void update(ArrayList<String> pressedKeys){
		super.update(pressedKeys);
		
		/* Create lists of pressed and released keys */
		
		
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
					System.out.println("Collision");
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
				/*
				 * THIS SHOULD ONLY HAPPEN WHEN THE KEY IS INITIALLY PRESSED
				 * Or check if the character can jump given its current state
				 */
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_UP))) {
					//soundMgr.playSoundEffect("Jump");
					Tween tween = new Tween(scooter, new TweenTransition(TweenTransitionType.LINEAR));
					tween.animate(TweenableParam.SCALE_X, 1, 1.25, 500, 0);
					tween.animate(TweenableParam.SCALE_Y, 1, 1.25, 500, 0);
					tween.animate(TweenableParam.SCALE_X, 1.25, 1, 500);
					tween.animate(TweenableParam.SCALE_Y, 1.25, 1, 500);
					tweenJuggler.add(tween);
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
		
		// Scooter
		scooter.setXPivotPoint(scooter.getUnscaledWidth()/2);
		scooter.setYPivotPoint(scooter.getUnscaledHeight()/2);
		scooter.setXPosition(game.getWidth()/2);
		scooter.setYPosition(scooter.getUnscaledHeight()/2);
		
		// ObstacleParent
		obstacleParent.setYVelocity(-125);
		
		// Potholes
		pothole1.setXPosition(20);
		pothole1.setYPosition(500);
		pothole2.setXPosition(85);
		pothole2.setYPosition(500);
		pothole3.setXPosition(150);
		pothole3.setYPosition(500);
		pothole4.setXPosition(215);
		pothole4.setYPosition(500);
		pothole5.setXPosition(280);
		pothole5.setYPosition(500);
		pothole6.setXPosition(345);
		pothole6.setYPosition(500);
		pothole7.setXPosition(410);
		pothole7.setYPosition(500);
		
		dog1.setXPivotPoint(dog1.getUnscaledWidth()/2);
		dog1.setXPosition(game.getWidth()/2);
		dog1.setYPosition(1000);
		
		/* Set up the display tree */
		
		game.addChild(scooter);
		game.addChild(obstacleParent);
		
		obstacleParent.addChild(pothole1);
		obstacleParent.addChild(pothole2);
		obstacleParent.addChild(pothole3);
		obstacleParent.addChild(pothole4);
		obstacleParent.addChild(pothole5);
		obstacleParent.addChild(pothole6);
		obstacleParent.addChild(pothole7);
		obstacleParent.addChild(dog1);
		
		/* Set up the quad tree for collision detection */
		
		game.addSprite(pothole1);
		game.addSprite(pothole2);
		game.addSprite(pothole3);
		game.addSprite(pothole4);
		game.addSprite(pothole5);
		game.addSprite(pothole6);
		game.addSprite(pothole7);
		game.addSprite(dog1);

		/* Register event listeners */
		
		/* Start the game */
		
		game.start();
		
	}
}
