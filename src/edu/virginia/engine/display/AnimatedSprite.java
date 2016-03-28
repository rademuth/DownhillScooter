package edu.virginia.engine.display;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import edu.virginia.engine.util.Pair;

public class AnimatedSprite extends Sprite {
	
	private List<BufferedImage> displayImages;
	private Map<String,Pair<Integer,Integer>> animations;
	private int currFrame;
	private long duration; // Animation length in milliseconds
	private long lastImageUpdate; // Last image update time in nanoseconds
	private boolean stopped;
	private boolean looping;
	private String animation;

	// Constructor for an animated sprite with no default image
	public AnimatedSprite(String id) {
		super(id);
		this.displayImages = new ArrayList<BufferedImage>();
		this.animations = new HashMap<String,Pair<Integer,Integer>>();
		this.currFrame = 0;
		this.duration = 1000;
		this.lastImageUpdate = System.nanoTime();
		this.stopped = true;
		this.looping = false;
	}
	
	// Constructor for an animated sprite with a default image
	public AnimatedSprite(String id, String imageFileName) {
		super(id, imageFileName);
		this.displayImages = new ArrayList<BufferedImage>();
		this.animations = new HashMap<String,Pair<Integer,Integer>>();
		this.currFrame = 0;
		this.duration = 1000;
		this.lastImageUpdate = System.nanoTime();
		this.stopped = true;
		this.looping = false;
		addImage(imageFileName);
	}
	
	public void addImage(String imageFileName) {		
		if (imageFileName == null) {
			return;
		}
		BufferedImage image = readImage(imageFileName);
		displayImages.add(image);
		if (image == null) {
			System.err.println("[DisplayObject.setImage] ERROR: " + imageFileName + " does not exist!");
		}
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public void start() {
		stopped = false;
	}
	
	public void stop() {
		stopped = true;
	}
	
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
		if (!stopped) {
			int numFrames = animations.get(animation).item2 - animations.get(animation).item1 + 1;
			int framesPassed = (int)((System.nanoTime() - lastImageUpdate) / (duration / numFrames * 1000000));
			if (framesPassed > 0) {
				lastImageUpdate = System.nanoTime();
				currFrame+= framesPassed; // Should help correct lag
				if (currFrame > animations.get(animation).item2) {
					if (looping) {
						currFrame = animations.get(animation).item1;
					} else {
						currFrame = 0;
						stop();
					}
				}
			}
			this.setImage(displayImages.get(currFrame));
		}
	}
	
	public void addAnimation(String name, String[] imageFileNames) {
		// Read the images and add them to the array
		int numImages = imageFileNames.length;
		for (int i = 0; i < numImages; i++) {
			addImage(imageFileNames[i]);
		}
		// Create an entry in the map
		animations.put(name,  new Pair<Integer,Integer>(displayImages.size()-numImages,displayImages.size()-1));
	}
	
	public void animate(String name, boolean loop, long duration) {
		if (isStopped()) {
			this.animation = name;
			this.looping = loop;
			this.duration = duration;
			this.currFrame = animations.get(animation).item1;
			this.setImage(displayImages.get(currFrame));
			start();
		}
	}
}
