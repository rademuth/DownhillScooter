package edu.virginia.engine.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import edu.virginia.engine.display.Sprite;

public class Quadtree {

	private int MAX_OBJECTS = 10; // How many objects a node can hold before it
									// splits
	private int MAX_LEVELS = 5; // The deepest level subnode

	private int level; // The current node level
	private List<Sprite> objects;
	private Rectangle bounds; // The 2D space that the node occupies
	private Quadtree[] nodes; // The four subnodes

	public Quadtree(int level, Rectangle bounds) {
		this.level = level;
		this.objects = new ArrayList<Sprite>();
		this.bounds = bounds;
		this.nodes = new Quadtree[4];
	}

	public void clear() {
		objects.clear();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null) {
				nodes[i].clear();
				nodes[i] = null;
			}
		}
	}

	private void split() {
		int subWidth = (int) (bounds.getWidth() / 2);
		int subHeight = (int) (bounds.getHeight() / 2);
		int x = (int) bounds.getX();
		int y = (int) bounds.getY();

		nodes[0] = new Quadtree(level + 1, new Rectangle(x + subWidth, y,
				subWidth, subHeight));
		nodes[1] = new Quadtree(level + 1, new Rectangle(x, y, subWidth,
				subHeight));
		nodes[2] = new Quadtree(level + 1, new Rectangle(x, y + subHeight,
				subWidth, subHeight));
		nodes[3] = new Quadtree(level + 1, new Rectangle(x + subWidth, y
				+ subHeight, subWidth, subHeight));
	}

	private int getIndex(Sprite d) {
		int index = -1;
		double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
		double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

		Rectangle rect = d.getHitbox();

		// Object can completely fit within the top quadrants
		boolean topQuadrant = (rect.getY() < horizontalMidpoint && rect.getY()
				+ rect.getHeight() < horizontalMidpoint);
		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = (rect.getY() > horizontalMidpoint);

		// Object can completely fit within the left quadrants
		if (rect.getX() < verticalMidpoint
				&& rect.getX() + rect.getWidth() < verticalMidpoint) {
			if (topQuadrant) {
				index = 1;
			} else if (bottomQuadrant) {
				index = 2;
			}
		}

		// Object can completely fit within the right quadrants
		else if (rect.getX() > verticalMidpoint) {
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}

		return index;
	}

	public void insert(Sprite d) {
		if (nodes[0] != null) {
			int index = getIndex(d);
			if (index != -1) {
				nodes[index].insert(d);
				return;
			}
		}

		objects.add(d);

		if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
			if (nodes[0] == null) {
				split();
			}
			int i = 0;
			while (i < objects.size()) {
				int index = getIndex(objects.get(i));
				if (index != -1) {
					nodes[index].insert(objects.remove(i));
				} else {
					i++;
				}
			}
		}
	}

	public List<Sprite> retrieve(List<Sprite> returnObjects, Sprite d) {
		int index = getIndex(d);
		if (index != -1 && nodes[0] != null) {
			nodes[index].retrieve(returnObjects, d);
		}
		returnObjects.addAll(objects);
		return returnObjects;
	}

}
