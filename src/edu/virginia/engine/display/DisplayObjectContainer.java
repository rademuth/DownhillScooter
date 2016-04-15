package edu.virginia.engine.display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DisplayObjectContainer extends DisplayObject {

	private CopyOnWriteArrayList<DisplayObject> children;
	
	public DisplayObjectContainer(String id) {
		super(id);
		this.children = new CopyOnWriteArrayList<DisplayObject>();
	}

	public DisplayObjectContainer(String id, String imageFileName) {
		super(id, imageFileName);
		this.children = new CopyOnWriteArrayList<DisplayObject>();
	}
	
	/* Override the update() and draw() methods */
	
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
		
		// Call the update() method on each child
		Iterator<DisplayObject> iter = this.children.iterator();
		while (iter.hasNext()) {
			DisplayObject child = iter.next();
			child.update(pressedKeys);
		}
		/*
		for (DisplayObject child : children) {
			child.update(pressedKeys);
		}
		*/
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		
		// Only draw children if the container is visible
		if (isVisible()) {
			Graphics2D g2d = (Graphics2D) g;
			this.applyTransformations(g2d);
			Iterator<DisplayObject> iter = this.children.iterator();
			while (iter.hasNext()) {
				DisplayObject child = iter.next();
				if (child != null) {
					child.draw(g);
				}
			}
			this.reverseTransformations(g2d);
		}
	}
	
	/* Add and remove methods */
	
	public List<DisplayObject> getChildren() {
		return children;
	}
	
	public void addChild(DisplayObject child) {
		child.setParent(this);
		children.add(child);
	}
	
	public void addChildAtIndex(DisplayObject child, int index) {
		child.setParent(this);
		children.add(index, child);
	}
	
	public boolean removeChild(DisplayObject child) {
		child.setParent(null);
		return children.remove(child);
	}
	
	public DisplayObject removeChildAtIndex(int index) {
		DisplayObject child = children.remove(index);
		if (child != null) {
			child.setParent(null);
		}
		return child;
	}
	
	public void removeAll() {
		for (DisplayObject child : children) {
			child.setParent(null);
		}
		children.clear();
	}
	
	public boolean contains(DisplayObject d) {
		return children.contains(d);
	}
	
	public DisplayObject getChild(String id) {
		Iterator<DisplayObject> iter = this.children.iterator();
		while (iter.hasNext()) {
			DisplayObject child = iter.next();
			if (child.getId().equals(id)) {
				return child;
			}
		}
		return null;
	}
	
	public DisplayObject getChild(int index) {
		return children.get(index);
	}
	
}
