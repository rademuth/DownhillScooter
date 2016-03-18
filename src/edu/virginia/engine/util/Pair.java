package edu.virginia.engine.util;

public class Pair<S,T> {
	
	public S item1;
	public T item2;
	
	public Pair(S item1, T item2) {
		this.item1 = item1;
		this.item2 = item2;
	}

	@Override
	public String toString() {
		return "Pair [item1=" + item1 + ", item2=" + item2 + "]";
	}
	
	

}
