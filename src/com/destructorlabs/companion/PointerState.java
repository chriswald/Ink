package com.destructorlabs.companion;

import java.util.Vector;

import android.view.VelocityTracker;

import com.destructorlabs.pkg.Corner;

public class PointerState {
	private final Vector<Point> dots = new Vector<Point>();
	private boolean curDown;
	private VelocityTracker velocity;
	private Corner corner = null;
	public Type press_type = null;

	public enum Type {MOVE, DRAW};

	public Point			getLast()					{return this.dots.lastElement();}
	public Point			get(final int index)		{return this.dots.get(index);}
	public boolean			getCurDown()				{return this.curDown;}
	public VelocityTracker	getVelocity()				{return this.velocity;}
	public int				size()						{return this.dots.size();}
	public Vector<Point>	getDots()					{return this.dots;}
	public void				setCorner(final Corner c)	{this.corner = c;}
	public Corner			getCorner()					{return this.corner;}
}
