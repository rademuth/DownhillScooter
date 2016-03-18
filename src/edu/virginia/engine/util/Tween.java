package edu.virginia.engine.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.events.EventDispatcher;

public class Tween extends EventDispatcher {

	private DisplayObject object;
	private TweenTransition transition;
	
	private List<TweenParam> tweenParams;
	
	private long startTime;
	private long elapsedTime;
	
	public Tween(DisplayObject object) {
		super();
		this.object = object;
		this.transition = null;
		this.tweenParams = new ArrayList<TweenParam>();
		this.startTime = -1;
		this.elapsedTime = -1;
	}
	
	public Tween(DisplayObject object, TweenTransition transition) {
		super();
		this.object = object;
		this.transition = transition;
		this.tweenParams = new ArrayList<TweenParam>();
		this.startTime = -1;
		this.elapsedTime = -1;
	}
	
	public void animate(TweenableParam fieldToAnimate, double startVal, double endVal, double duration) {
		TweenParam param = new TweenParam(fieldToAnimate, startVal, endVal, duration, 0);
		tweenParams.add(param);
	} 

	public void animate(TweenableParam fieldToAnimate, double startVal, double endVal, double duration, double delay) {
		TweenParam param = new TweenParam(fieldToAnimate, startVal, endVal, duration, delay);
		tweenParams.add(param);
	}
	
	public void update() {
		// Find the start time if it has not yet been calculated
		if (startTime == -1) {
			startTime = System.nanoTime();
		}
		// Find the time elapsed since the start time
		elapsedTime = System.nanoTime() - startTime; // This time is in nanoseconds
		Iterator<TweenParam> iter = tweenParams.iterator();
		while (iter.hasNext()) {
			TweenParam param = iter.next();
			// Find the time percentage
			double paramElapsedTime = elapsedTime - (param.getDelay()*1000000);
			double percent = (double)(paramElapsedTime) / (double)(param.getTime()*1000000);
			if (percent < 0) {
				percent = 0;
			} else if (percent > 1) {
				percent = 1;
			}
			// Find the value percentage
			if (transition != null) {
				percent = transition.getPercentDone(percent);
			}
			if (percent != 0) {
				// Find the value
				double value = param.getStartVal() + (percent * (param.getEndVal()-param.getStartVal()));
				// Set the value
				setValue(param.getParamToTween(), value);
				// Remove the TweenParam if necessary
				if (param.isComplete(elapsedTime / 1000000)) {
					iter.remove();
				}
			}
		}
		
	}
	
	public boolean isComplete() {
		/*
		for (TweenParam param : tweenParams) {
			if (elapsedTime < (param.getTime()+param.getDelay())*1000000) {
				return false;
			}
		}
		return true;
		*/
		return tweenParams.size() == 0;
	}
	
	public void setValue(TweenableParam param, double value) {
		switch (param) {
			case X:
				object.setXPosition(value);
				break;
			case Y:
				object.setYPosition(value);
				break;
			case X_PIVOT_POINT:
				object.setXPivotPoint(value);
				break;
			case Y_PIVOT_POINT:
				object.setYPivotPoint(value);
				break;
			case SCALE_X:
				object.setScaleX(value);
				break;
			case SCALE_Y:
				object.setScaleY(value);
				break;
			case ROTATION:
				object.setRotation(value);
				break;
			case ALPHA:
				object.setAlpha((float)value);
				break;
		}
	}
	
}
