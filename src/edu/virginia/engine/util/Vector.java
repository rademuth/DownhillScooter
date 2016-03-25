package edu.virginia.engine.util;

public class Vector {

	private double x;
	private double y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Vector v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void zero() {
		this.x = 0;
		this.y = 0;
	}
	
	public void add(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	public void add(Vector v) {
		this.x += v.x;
		this.y += v.y;
	}
	
	public Vector sum(double x, double y) {
		return new Vector(this.x + x, this.y + y);
	}
	
	public Vector sum(Vector v) {
		return new Vector(this.x + v.x, this.y + v.y);
	}
	
	public void subtract(double x, double y) {
		this.x -= x;
		this.y -= y;
	}
	
	public void subtract(Vector v) {
		this.x -= v.x;
		this.y -= v.y;
	}
	
	public Vector difference(double x, double y) {
		return new Vector(this.x - x, this.y - y);
	}
	
	public Vector difference(Vector v) {
		return new Vector(this.x - v.x, this.y - v.y);
	}
	
	public void multiply(double k) {
		this.x *= k;
		this.y *= k;
	}
	
	public Vector product(double k) {
		return new Vector(this.x * k, this.y * k);
	}
	
	public void divide(double k) {
		this.x /= k;
		this.y /= k;
	}
	
	public Vector factor(double k) {
		return new Vector(this.x / k, this.y / k);
	}

	public double dot(Vector v) {
		return this.x*v.x + this.y*v.y;
	}

	@Override
	public String toString() {
		return "<" + this.x + "," + this.y + ">";
	}
	
}