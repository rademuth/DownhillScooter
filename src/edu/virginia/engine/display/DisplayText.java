package edu.virginia.engine.display;

import java.awt.Graphics;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

public class DisplayText extends DisplayObjectContainer {

	private String str;
	private int size;
	
	public DisplayText(String id, String str, int size) {
		super(id);
		this.str = str;
		this.size = size;
	}
	
	public void setText(String str) {
		this.str = str;
	}

	@Override
	public void draw(Graphics g){
		super.draw(g);
		if (this.isVisible()) {
			AttributedString attrStr = new AttributedString(this.str);
			attrStr.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
			attrStr.addAttribute(TextAttribute.WIDTH, TextAttribute.WIDTH_EXTENDED);
			attrStr.addAttribute(TextAttribute.SIZE, size);
			g.drawString(attrStr.getIterator(), (int)this.getXPosition(), (int)this.getYPosition());
		}
	}
}
