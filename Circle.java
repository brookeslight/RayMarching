package main;

import java.awt.Color;
import java.awt.Graphics;

public class Circle extends Bound {

	private float r;
	
	public Circle(float x, float y, float r) {
		super(x, y);
		this.r = r;
	}

	@Override
	public double distanceTo(float x1, float y1) {
		return Math.abs(Math.hypot(this.x - x1, this.y - y1) - this.r);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.drawOval((int)(this.x - this.r), (int)(this.y - this.r), (int)(2*this.r), (int)(2*this.r));
	}

	public float getR() {
		return r;
	}

}