/**
 *     _______    __    __   ________   __________    _______
 *    /\   __ \  /\ \  /\ \ /\  ____ \ /\____  ___\  /\  ____\
 *    \ \ \_/\_\ \ \ \_\_\ \\ \ \___\ \\/___/\ \__/  \ \ \___/
 *     \ \ \\/_/  \ \  ____ \\ \  ___ <     \ \ \     \ \____`\
 *      \ \ \   __ \ \ \__/\ \\ \ \ /\ \     \ \ \     \/___/\ \
 *       \ \ \__\ \ \ \ \ \ \ \\ \ \\ \ \    _\_\ \____   __\_\ \
 *        \ \______\ \ \_\ \ \_\\ \_\\ \_\  /\_________\ /\______\
 *         \/______/  \/_/  \/_/ \/_/ \/_/  \/_________/ \/______/
 *                __      __    ________    __        ______
 *               /\ \    /\ \  /\  ____ \  /\ \      /\  ___`,
 *               \ \ \   \ \ \ \ \ \__/\ \ \ \ \     \ \ \_/\ \
 *                \ \ \   \ \ \ \ \ \_\_\ \ \ \ \     \ \ \\ \ \
 *                 \ \ \  _\ \ \ \ \  ____ \ \ \ \     \ \ \\ \ \
 *                  \ \ \_\ \_\ \ \ \ \__/\ \ \ \ \_____\ \ \\_\ \
 *                   \ \_________\ \ \_\ \ \_\ \ \______\\ \_____/
 *                    \/_________/  \/_/  \/_/  \/______/ \/____/
 * 
 * 			->ShapeFit
 * 			->Developed By Christopher J. Wald
 * 			->Copyright 2011 (c) All Rights Reserved
 * 
 * 
 * @author		Christopher J. Wald
 * @date		Apr 22, 2011
 * @project		ShapeFit
 * @file		ShapeFit.java
 * @description Provides a framework for guessing the shape of a user-drawn
 *              figure based on the closest match of a computer generated shape.
 *              Slope analysis is used to determine whether or not two points are
 *              of similar enough slope to be considered of the same line. The
 *              slopes of two points is compared to the slope of the previous two
 *              points, as well as the slope of what is considered the current
 *              line. If it is outside of a certain range it is considered to be
 *              the first point of a new line in the shape.
 * @license:
 * 
 *           Redistribution and use in source and binary forms, with or without
 *           modification, are permitted provided that the following conditions
 *           are met:
 * 
 *           - Redistributions of source code must retain the above copyright
 *           notice, this list of conditions and the following disclaimer.
 * 
 *           - Redistributions in binary form must reproduce the above copyright
 *           notice, this list of conditions and the following disclaimer in the
 *           documentation and/or other materials provided with the
 *           distribution.
 * 
 *           - The name of Christopher J. Wald may not be used to endorse or
 *           promote products derived from this software without specific prior
 *           written permission.
 * 
 *           THIS SOFTWARE IS PRIVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 *           EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *           IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *           PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE
 *           LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, EXEMPLARY, OR
 *           CONSEQUENTIAL DAMAGES (INCLUDING BUT NOT LIMITED TO UNDESIRED
 *           ACTION, LOSS OF SECURITY, LOSS OF DATA, LOSS OF SLEEP, LOSS OF
 *           HAIR, OR EXPLOSIONS). USE AT YOUR OWN RISK.
 */

package com.destructorlabs.shape;

import java.util.ArrayList;
import java.util.Vector;

import android.util.Log;

import com.destructorlabs.companion.Dimension;
import com.destructorlabs.companion.Point;

public class ShapeFit {

	/*
	 * Globals
	 */
	private Vector<Point>		pts					= new Vector<Point>();
	private static final double	MIN_SPACE_DIFF		= 15D;		// 11/02/2011 Value was 12
	private static final double MIN_CIRCLE_STDEV	= 8.5D;

	/*
	 * The higher these values are, the fewer lines will be
	 * expected from the analysis because it is becoming more
	 * inclusive at higher values.
	 * 
	 * NOTE:	These values are in Radians!
	 * 			The minimum value is 0, the max is 2 * pi ~= 6.28
	 */
	private static final double MIN_THETA_S_DIFF	= .60D;		// 11/02/2011 Value was .80D
	private static final double MIN_THETA_L_DIFF	= .60D;		// 11/02/2011 Value was .80D

	private class LocInfo {
		public Point pt = null;
		public Point center = null;
		public Dimension dim = null;

		public LocInfo(final Point p, final Dimension d) {
			this.pt = p;
			this.dim = d;
			this.center = new Point(this.pt.x + (this.dim.width / 2), this.pt.y
					+ (this.dim.height / 2));
		}
	}

	public ShapeFit(final Vector<Point> points) {
		this.pts = points;
	}

	public ShapeFit() {}

	public Vector<Point> runRegression() {
		// Get information on location of points
		LocInfo li = this.getLocInfo();
		if (li == null)
			return null;

		// Find stuff for a circle
		double stdev = this.circleRegression(li);
		if (stdev < MIN_CIRCLE_STDEV) {
			Vector<Point> circle = new Vector<Point>();
			circle.add(new Point(li.center.x, li.center.y));
			circle.add(new Point(-1, li.dim.width));
			return circle;
		}

		Vector<LineList> lines = null;
		try{
			lines = this.analyzeSlopesForSides();
		} catch (Exception e){
			return null;
		}

		Vector<Point> p = new Vector<Point>();
		for (LineList line : lines){
			p.add(line.getFirst());
		}

		this.merge(p);	// was p = this.merge(p)

		return p;
	}

	/**
	 * Determines if the points closely represent a circle
	 * @param LocInfo for general locations on the screen
	 * @return The standard deviation of the distances from the center
	 */
	private double circleRegression(final LocInfo li) {
		// Set up vars
		ArrayList<Double> dist = new ArrayList<Double>(0);
		double ave = 0.0;
		int index = 1;

		// Find average distance from center of figure
		for (Point p : this.pts) {
			index++;
			dist.add(this.distForm(li.center.x, li.center.y, p.x, p.y));
			ave = ((ave * (index - 1) + dist.get(dist.size() - 1)) / index);
		}

		double diff = 0;

		// Find the standard deviation
		for (int i = 0; i < dist.size(); i++)
			diff += Math.pow((ave - this.distForm(li.center.x, li.center.y, this.pts.get(i).x, this.pts.get(i).y)), 2);

		diff = (diff / (dist.size() - 1));
		double stdev = Math.sqrt(diff);

		return stdev;
	}

	/**
	 * Analyzes all the points looking for *significant* changes in slope,
	 * either between three points, or between two points and a generated line
	 * @return The list of points, broken into "slope similar" lines
	 */
	private Vector<LineList> analyzeSlopesForSides() throws Exception{
		Vector<LineList> lines = new Vector<LineList>();
		lines.add(new LineList(this.pts.get(0), this.pts.get(1), 0));

		for (int i = 2; i < this.pts.size(); i++) {
			double slope_old = LineList.slope(this.pts.get(i - 2),
					this.pts.get(i - 1));
			double slope = LineList.slope(this.pts.get(i - 1), this.pts.get(i));

			if (Math.abs(slope_old - slope) > MIN_THETA_S_DIFF)
				if (Math.abs(lines.lastElement().getTheta() - slope) < MIN_THETA_L_DIFF)
					lines.lastElement().add(this.pts.get(i));
				else {
					lines.lastElement().end_index = i - 1;
					lines.add(new LineList(this.pts.get(i - 1),
							this.pts.get(i), i - 1));
				}
			else
				lines.lastElement().add(this.pts.get(i));
		}

		return lines;
	}

	/**
	 * Merges corners based on proximity
	 * @param p
	 */
	private void merge(Vector<Point> pts){
		try{
			int index = 0;
			while (index < pts.size()){
				Point p0;
				if (index == pts.size()){
					p0 = pts.firstElement();
				} else {
					p0 = pts.get(index + 1);
				}

				if (this.distForm(p0, pts.get(index)) < MIN_SPACE_DIFF){
					pts.add(index+1, this.midpoint(pts.get(index), p0));
					pts.remove(index);
					pts.remove(index+1);
				}
				else
					index++;
			}
		} catch (Exception e){
			Log.d("Merge Exception", e.getMessage());
		}
	}

	private Point midpoint(Point p0, Point p1){
		int mx = (p0.x + p1.x) / 2;
		int my = (p0.y + p1.y) / 2;

		return new Point(mx, my);
	}

	/**
	 * Finds the center, width, and height of the drawing
	 * @return LocInfo object containing on screen location information...
	 */
	private LocInfo getLocInfo() {

		int lx = this.pts.get(0).x;
		int hx = this.pts.get(0).x;
		int ly = this.pts.get(0).y;
		int hy = this.pts.get(0).y;

		for (Point p : this.pts) {
			if (p.x < lx)
				lx = p.x;
			if (p.x > hx)
				hx = p.x;
			if (p.y < ly)
				ly = p.y;
			if (p.y > hy)
				hy = p.y;
		}

		try {
			return new LocInfo(new Point(lx, ly), new Dimension(hx - lx, hy - ly));
		} catch (NullPointerException e) {
			return null;
		}
	}

	private double distForm(final Point p0, final Point p1) {
		return this.distForm(p0.x, p0.y, p1.x, p1.y);
	}

	private double distForm(final int a, final int b, final int c, final int d) {
		return Math.sqrt((Math.pow(a - c, 2) + Math.pow(b - d, 2)));
	}

	public void addPointsList(final Vector<Point> v) {
		this.pts = v;
	}
}
