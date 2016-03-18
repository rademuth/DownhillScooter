package edu.virginia.engine.util;

public class TweenParam {
	
	private TweenableParam paramToTween;
	private double startVal;
	private double endVal;
	private double time;
	private double delay;
	
	public TweenParam(TweenableParam paramToTween, double startVal, double endVal, double time, double delay) {
		this.paramToTween = paramToTween;
		this.startVal = startVal;
		this.endVal = endVal;
		this.time = time;
		this.delay = delay;
	}
		
	public double getStartVal() {
		return this.startVal;
	}
	
	public double getEndVal() {
		return this.endVal;
	}
	
	public double getTime() {
		return this.time;
	}
	
	public double getDelay() {
		return this.delay;
	}
	
	public boolean isComplete(double elapsedTime) {
		return elapsedTime > time + delay;
	}
	
	public TweenableParam getParamToTween() {
		return this.paramToTween;
	}
}
