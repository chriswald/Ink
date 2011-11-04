package com.destructorlabs.pkg;

import java.io.Serializable;
import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.destructorlabs.companion.Point;
import com.destructorlabs.ink.DrawView.PointerState;

public class Shape implements Serializable{
	private static final long serialVersionUID = 1L;
	private Vector<Corner>	corners			= new Vector<Corner>();
	private ShapeType		type			= null;
	private Corner			center			= null;
	public static final int	DEFAULT_RADIUS	= 10;

	public enum ShapeType{
		CIRCLE,
		POLYGON;

		@Override
		public String toString(){
			return (this.ordinal() == 0 ? "CIRCLE" : "POLYGON");
		}

	};

	public Shape() {}

	private Shape(ShapeType st, Corner c, Vector<Corner> cs){
		this.type = st;
		this.center = c;
		this.corners = cs;
	}

	public static Shape makeShapeFromSave(ShapeType st, Corner c, Vector<Corner> cs){
		return new Shape(st, c, cs);
	}

	@Override
	public String toString(){
		String string = "@SHAPE\n";
		string += "#SHAPETYPE\n";
		string += this.type.toString();
		string += "\n";
		string += "#CENTER\n";
		string += this.center.toString();
		string += "\n";
		string += "#CORNERS\n";
		for (Corner c : this.corners){
			string += c.toString() + "\n";
		}

		return string;
	}

	public void addCorner(final Corner c) {
		this.corners.add(c);
		this.determineType();
		this.findCenter();
	}

	public void drawShape(final Canvas c, final Paint p) {
		if (this.type == ShapeType.CIRCLE) {
			c.drawCircle(this.corners.get(0).getX(), this.corners.get(0).getY(), this.corners.get(1).getY() / 2, p);
		} else {
			for (int i=0; i<this.corners.size(); i++) {
				if (i == this.corners.size() - 1) {
					c.drawLine(this.corners.get(i).getX(), this.corners.get(i).getY(), this.corners.get(0).getX(), this.corners.get(0).getY(), p);
				} else {
					c.drawLine(this.corners.get(i).getX(), this.corners.get(i).getY(), this.corners.get(i+1).getX(), this.corners.get(i+1).getY(), p);
				}
			}
		}
	}

	public void drawHandles(final Canvas c, final Paint p) {
		if (this.type == ShapeType.POLYGON) {
			for (Corner corner : this.corners)
				corner.draw(c, p);
		} else {
			Point point = new Point(this.center.getX(), this.center.getY());
			Corner handle = new Corner(point, 20);
			handle.draw(c, p);
		}

		this.center.draw(c, p);
	}

	public void moveHandle(final Corner c, final int x, final int y) {
		if (c == this.center)
			this.moveShape(x, y);
		else {
			c.move(x, y);
		}
	}

	public void moveHandle(final Corner c, final Point p) {
		this.moveHandle(c, p.x, p.y);
	}

	private void moveShape(final int x_offset, final int y_offset) {
		for (Corner c : this.corners) {
			c.move(c.getX() + x_offset, c.getY() + y_offset);
		}

		this.center.move(this.center.getX() + x_offset, this.center.getY() + y_offset);
	}

	public int overHandle(final PointerState ps) {
		float x = ps.getLast().x;
		float y = ps.getLast().y;

		for (int i=0; i<this.corners.size(); i++)
			if (this.corners.get(i).isOver(x, y))
				return i;

		if (this.center.isOver(x, y))
			return -2;

		return -1;
	}

	public Corner getCenter() {
		return this.center;
	}

	public Corner getHandle(final int index) {
		return this.corners.get(index);
	}

	public Vector<Corner> getCorners() {
		return this.corners;
	}

	private void determineType() {
		if (this.corners.size() == 2  &&  this.corners.get(1).getX() == -1)
			this.type=ShapeType.CIRCLE;
		else
			this.type=ShapeType.POLYGON;
	}

	private void findCenter() {
		if (this.type == ShapeType.CIRCLE)
			this.center = new Corner(this.corners.get(0).getX(), this.corners.get(0).getY(), Shape.DEFAULT_RADIUS);
		else
			this.center = new Corner(new Point(this.findMidX(), this.findMidY()), Shape.DEFAULT_RADIUS);
	}

	private int findMidX() {
		return (this.findMinX() + this.findMaxX()) / 2;
	}

	private int findMinX() {
		int min=this.corners.get(0).getX();

		for (Corner c : this.corners) {
			if (min > c.getX())
				min = c.getX();
		}

		return min;
	}

	private int findMaxX() {
		int max = this.corners.get(0).getX();

		for (Corner c : this.corners) {
			if (max < c.getX())
				max = c.getX();
		}

		return max;
	}

	private int findMidY() {
		return (this.findMinY() + this.findMaxY()) / 2;
	}

	private int findMinY() {
		int min=this.corners.get(0).getY();

		for (Corner c : this.corners) {
			if (min > c.getY())
				min = c.getY();
		}

		return min;
	}

	private int findMaxY() {
		int max = this.corners.get(0).getY();

		for (Corner c : this.corners) {
			if (max < c.getY())
				max = c.getY();
		}

		return max;
	}

	public ShapeType getType() {
		return this.type;
	}

}
