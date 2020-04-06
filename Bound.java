package main;

import java.awt.Graphics;

public abstract class Bound {
	protected float x;
	protected float y;
	
	public Bound(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public abstract double distanceTo(float x1, float y1);
	
	public abstract void render(Graphics g);

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

}