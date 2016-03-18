package edu.virginia.engine.util;

public class TweenTransition {

	private TweenTransitionType transition;
	
	public TweenTransition(TweenTransitionType transition) {
		this.transition = transition;
	}
	
	public double getPercentDone(double percentDone) {
		switch(transition) {
			case LINEAR:
				return linear(percentDone);
			case QUADRATIC:
				return quadratic(percentDone);
			default:
				return percentDone;
		}
	}
		
	private double linear(double percentDone) {
		return percentDone;
	}
	
	private double quadratic(double percentDone) {
		return percentDone*percentDone;
	}
	
}