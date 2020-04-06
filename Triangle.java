package main;

import java.awt.Color;
import java.awt.Graphics;

public class Triangle extends Bound {
	private float x1;
	private float y1; 
	private float x2; 
	private float y2;

	public Triangle(float x, float y, float x1, float y1, float x2, float y2) {
		super(x, y);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public double distanceTo(float x3, float y3) {
		double d1 = this.shortestDistanceToLineSegment(this.x, this.y, this.x1, this.y1, x3, y3);
		double d2 = this.shortestDistanceToLineSegment(this.x1, this.y1, this.x2, this.y2, x3, y3);
		double d3 = this.shortestDistanceToLineSegment(this.x2, this.y2, this.x, this.y, x3, y3);
		return Math.min(d1, Math.min(d2, d3));
	}
	
	private double shortestDistanceToLineSegment(float x1, float y1, float x2, float y2, float x3, float y3) {
		float abx = (x2-x1);
		float aby = (y2-y1);
		float bax = (x1-x2);
		float bay = (y1-y2);
		float apx = (x3-x1);
		float apy = (y3-y1);
		float bpx = (x3-x2);
		float bpy = (y3-y2);
		
		//a closest
		if((abx*apx+aby*apy) <= 0)
			return Math.hypot(apx, apy);
		
		//b closest
		if((bax*bpx+bay*bpy) <= 0)
			return Math.hypot(bpx, bpy);
		
		//between a and b
		return Math.abs((aby)*x3 - (abx)*y3 + x2*y1 - y2*x1) / Math.hypot(abx, aby);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.drawPolygon(new int[] {(int)this.x, (int)this.x1, (int)this.x2}, new int[] {(int)this.y, (int)this.y1, (int)this.y2}, 3);
	}

}