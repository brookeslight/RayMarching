package main;

import java.awt.Color;
import java.awt.Graphics;

public class Rectangle extends Bound {
	private float w;
	private float h;

	public Rectangle(float x, float y, float w, float h) {
		super(x, y);
		this.w = w;
		this.h = h;
	}

	@Override
	public double distanceTo(float x1, float y1) {
		return Math.hypot(x1 - Math.max(this.x, Math.min(this.x + this.w, x1)), y1 - Math.max(this.y, Math.min(this.y + this.h, y1)));
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.drawRect((int)(this.x), (int)(this.y), (int)(this.w), (int)(this.h));
	}

	public float getW() {
		return w;
	}

	public float getH() {
		return h;
	}
	
}