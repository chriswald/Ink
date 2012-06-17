package com.destructorlabs.shapefit;

import java.util.Vector;

import com.destructorlabs.companion.Point;

public class LineList {
	private final Vector<Point> points = new Vector<Point>();

	private double average_theta = 0;

	public int begin_index = 0;

	public int end_index = 0;

	public LineList(Point p0, Point p1, int begin_index) {
		this.points.add(p0);
		this.points.add(p1);
		this.average_theta = slope(p0, p1);
		this.begin_index = begin_index;
	}

	public void add(Point p) {
		this.points.add(p);
		this.average_theta = ((this.average_theta * this.points.size() - 2) + slope(
				this.points.get(this.points.size() - 2),
				this.points.get(this.points.size() - 1)))
				/ (this.points.size() - 1);
	}

	public static double slope(Point p0, Point p1) {
		int x = p0.x - p1.x;
		int y = p0.y - p1.y;

		return Math.atan2(y, x);
	}

	public double getTheta() {
		return this.average_theta;
	}

	public Point getFirst() {
		return this.points.firstElement();
	}

	public Point getLast() {
		return this.points.lastElement();
	}
}
