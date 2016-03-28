package edu.virginia.engine.display;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import edu.virginia.engine.events.EventDispatcher;
import edu.virginia.engine.util.Vector;

/**
 * A very basic display object for a java based gaming engine
 * 
 * */
public class DisplayObject extends EventDispatcher {

	/* All DisplayObject have a unique id */
	private String id;

	/* The image that is displayed by this object */
	private BufferedImage displayImage;

	/* Additional fields from Lab 2 */
	private boolean visible;
	private Vector position;
	private Vector pivot;
	private double scaleX;
	private double scaleY;
	private double rotation;
	private float alpha;
	
	/* Reference to parent DisplayObject from Lab 3 */
	private DisplayObject parent;

	/**
	 * Constructors: can pass in the id OR the id and image's file path and
	 * position OR the id and a buffered image and position
	 */
	public DisplayObject(String id) {
		super();
		this.setId(id);
		// Initialize new fields
		this.visible = true;
		this.position = new Vector(0,0);
		this.pivot = new Vector(0,0);
		this.scaleX = 1;
		this.scaleY = 1;
		this.rotation = 0;
		this.alpha = 1;
		this.parent = null;	
	}

	public DisplayObject(String id, String fileName) {
		super();
		this.setId(id);
		this.setImage(fileName);
		// Initialize new fields
		this.visible = true;
		this.position = new Vector(0,0);
		this.pivot = new Vector(0,0);
		this.scaleX = 1;
		this.scaleY = 1;
		this.rotation = 0;
		this.alpha = 1;
		this.parent = null;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	/* Additional getters and setters from Lab 2 */

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public double getXPosition() {
		return position.getX();
	}
	
	public void setXPosition(double xPosition) {
		this.position.setX(xPosition);
	}
	
	public double getYPosition() {
		return position.getY();
	}
	
	public void setYPosition(double yPosition) {
		this.position.setY(yPosition);
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public void setPosition(Vector position) {
		this.position = position;
	}
	
	public void setPosition(double xPosition, double yPosition) {
		this.position.setX(xPosition);
		this.position.setY(yPosition);
	}

	public double getXPivotPoint() {
		return pivot.getX();
	}
	
	public void setXPivotPoint(double xPosition) {
		this.pivot.setX(xPosition);
	}
	
	public double getYPivotPoint() {
		return pivot.getY();
	}
	
	public void setYPivotPoint(double yPosition) {
		this.pivot.setY(yPosition);
	}
	
	public Vector getPivotPoint() {
		return pivot;
	}
	
	public void setPivotPoint(double xPosition, double yPosition) {
		this.pivot.setX(xPosition);
		this.pivot.setY(yPosition);
	}
	
	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public DisplayObject getParent() {
		return parent;
	}
	
	public void setParent(DisplayObject parent) { 
		this.parent = parent;
	}

	/**
	 * Returns the unscaled width and height of this display object
	 * */
	public int getUnscaledWidth() {
		if (displayImage == null)
			return 0;
		return displayImage.getWidth();
	}

	public int getUnscaledHeight() {
		if (displayImage == null)
			return 0;
		return displayImage.getHeight();
	}

	public BufferedImage getDisplayImage() {
		return this.displayImage;
	}

	protected void setImage(String imageName) {
		if (imageName == null) {
			return;
		}
		displayImage = readImage(imageName);
		if (displayImage == null) {
			System.err.println("[DisplayObject.setImage] ERROR: " + imageName
					+ " does not exist!");
		}
	}

	/**
	 * Helper function that simply reads an image from the given image name
	 * (looks in resources\\) and returns the bufferedimage for that filename
	 * */
	public BufferedImage readImage(String imageName) {
		BufferedImage image = null;
		try {
			String file = ("resources" + File.separator + imageName);
			image = ImageIO.read(new File(file));
		} catch (IOException e) {
			System.out
					.println("[Error in DisplayObject.java:readImage] Could not read image "
							+ imageName);
			e.printStackTrace();
		}
		return image;
	}

	public void setImage(BufferedImage image) {
		if (image == null)
			return;
		displayImage = image;
	}

	/**
	 * Invoked on every frame before drawing. Used to update this display
	 * objects state before the draw occurs. Should be overridden if necessary
	 * to update objects appropriately.
	 * */
	protected void update(ArrayList<String> pressedKeys) {
		
	}

	/**
	 * Draws this image. This should be overloaded if a display object should
	 * draw to the screen differently. This method is automatically invoked on
	 * every frame.
	 * */
	public void draw(Graphics g) {

		if (displayImage != null && visible) {

			/*
			 * Get the graphics and apply this objects transformations
			 * (rotation, etc.)
			 */
			Graphics2D g2d = (Graphics2D) g;
						
			applyTransformations(g2d);

			/* Actually draw the image, perform the pivot point translation here */
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, alpha));
			g2d.drawImage(displayImage, 0, 0, (int) (getUnscaledWidth()),
					(int) (getUnscaledHeight()), null);

			/*
			 * Undo the transformations so this doesn't affect other display
			 * objects
			 */
			reverseTransformations(g2d);
			
			// g2d.draw(this.getHitbox());
		}
	}

	/**
	 * Applies transformations for this display object to the given graphics
	 * object
	 * */
	protected void applyTransformations(Graphics2D g2d) {
		// Apply position, pivotPoint, scale, rotation, and alpha
		g2d.translate(-this.getXPivotPoint()*scaleX, -this.getYPivotPoint()*scaleY);
		g2d.translate(this.getXPosition(), this.getYPosition());
		g2d.rotate(rotation * (Math.PI / 180), this.getXPivotPoint()*scaleX, this.getYPivotPoint()*scaleY);
		g2d.scale(scaleX, scaleY);
	}

	/**
	 * Reverses transformations for this display object to the given graphics
	 * object
	 * */
	protected void reverseTransformations(Graphics2D g2d) {
		// Reverse position, pivotPoint, scale, rotation, and alpha
		g2d.scale(1/scaleX, 1/scaleY);
		g2d.rotate(-rotation * (Math.PI / 180), this.getXPivotPoint()*scaleX, this.getYPivotPoint()*scaleY);
		g2d.translate(-this.getXPosition(), -this.getYPosition());
		g2d.translate(this.getXPivotPoint()*scaleX, this.getYPivotPoint()*scaleY);
	}
	
	// Returns the global coordinates of a given point in the current object's coordinate system
	public Vector getLocalToGlobalCoors(double x, double y) {
		Vector retVector = new Vector(x,y);
		retVector.add(position);
		
		DisplayObject d = this.getParent();
		while (d != null) {
			retVector.add(d.position);
			d = d.getParent();
		}
		return retVector;
	}
	
	// Returns the local coordinates with respect to the current object of a given point in the global coordinate system
	public Vector getGlobalToLocalCoors(double x, double y) {
		Vector retVector = new Vector(x,y);
		return retVector.difference(this.getLocalToGlobalCoors(0,0));
	}
	
	// Returns the local coordinates with respect to the given object of a given point in the current object's coordinate system
	public Vector getLocalToLocalCoors(double x, double y, DisplayObject d) {
		Vector retVector = this.getGlobalToLocalCoors(x,y);
		return retVector.difference(d.getLocalToGlobalCoors(0,0));
	}
	
	public Rectangle getHitbox() {
		Vector global = this.getLocalToGlobalCoors(-this.getXPivotPoint(), -this.getYPivotPoint());
		return new Rectangle((int)global.getX(), (int)global.getY(), this.getUnscaledWidth(), this.getUnscaledHeight());
		/*
		Vector global;
		if (scaleX >= 0) {
			if (scaleY >= 0 ) {
				global = this.getLocalToGlobalCoors(-this.getXPivotPoint()*scaleX, -this.getYPivotPoint()*scaleY);
			} else {
				global = this.getLocalToGlobalCoors(-this.getXPivotPoint()*scaleX, (this.getUnscaledHeight()-this.getYPivotPoint())*scaleY);
			}
		} else {
			if (scaleY >= 0 ) {
				global = this.getLocalToGlobalCoors((this.getUnscaledWidth()-this.getXPivotPoint())*scaleX, -this.getYPivotPoint()*scaleY);
			} else {
				global = this.getLocalToGlobalCoors((this.getUnscaledWidth()-this.getXPivotPoint())*scaleX, (this.getUnscaledHeight()-this.getYPivotPoint())*scaleY);
			}
		}
		return new Rectangle((int)global.getX(), (int)global.getY(), (int)Math.abs((this.getUnscaledWidth()*scaleX)), (int)Math.abs((this.getUnscaledHeight()*scaleY)));
		*/
	}

	public boolean collidesWith(DisplayObject d) {
		return this.getHitbox().intersects(d.getHitbox());
	}
	
}