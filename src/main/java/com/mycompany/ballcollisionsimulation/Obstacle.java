package com.mycompany.ballcollisionsimulation;

import java.awt.*;

/**
 * Axis-aligned rectangular obstacle used in the simulation.
 */
public class Obstacle {
	public static final double DEFAULT_WIDTH = 120.0;
	public static final double DEFAULT_HEIGHT = 40.0;

	private double x;
	private double y;
	private double width;
	private double height;
	private boolean selected;

	public Obstacle(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void offsetPosition(double dx, double dy) {
		this.x += dx;
		this.y += dy;
	}

	public boolean contains(double pointX, double pointY) {
		return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void paint(Graphics2D g2d) {
		Color fill = selected ? new Color(80, 80, 160, 160) : new Color(100, 100, 100, 140);
		g2d.setColor(fill);
		g2d.fillRect((int) Math.round(x), (int) Math.round(y), (int) Math.round(width), (int) Math.round(height));

		g2d.setColor(selected ? Color.BLUE : Color.DARK_GRAY);
		Stroke stroke = selected ? new BasicStroke(2f) : new BasicStroke(1f);
		Stroke previous = g2d.getStroke();
		g2d.setStroke(stroke);
		g2d.drawRect((int) Math.round(x), (int) Math.round(y), (int) Math.round(width), (int) Math.round(height));
		g2d.setStroke(previous);
	}
}
