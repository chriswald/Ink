/**
 *  _______    __    __   ________   __________    _______
 * /\   __ \  /\ \  /\ \ /\  ____ \ /\____  ___\  /\  ____\
 * \ \ \_/\_\ \ \ \_\_\ \\ \ \___\ \\/___/\ \__/  \ \ \___/
 *  \ \ \\/_/  \ \  ____ \\ \  ___ <     \ \ \     \ \____`\
 *   \ \ \   __ \ \ \__/\ \\ \ \ /\ \     \ \ \     \/___/\ \
 *    \ \ \__\ \ \ \ \ \ \ \\ \ \\ \ \    _\_\ \____   __\_\ \
 *     \ \______\ \ \_\ \ \_\\ \_\\ \_\  /\_________\ /\______\
 *      \/______/  \/_/  \/_/ \/_/ \/_/  \/_________/ \/______/
 *              __      __    ________    __        ______
 *             /\ \    /\ \  /\  ____ \  /\ \      /\  ___`,
 *             \ \ \   \ \ \ \ \ \__/\ \ \ \ \     \ \ \_/\ \
 *              \ \ \   \ \ \ \ \ \_\_\ \ \ \ \     \ \ \\ \ \
 *               \ \ \  _\ \ \ \ \  ____ \ \ \ \     \ \ \\ \ \
 *                \ \ \_\ \_\ \ \ \ \__/\ \ \ \ \_____\ \ \\_\ \
 *                 \ \_________\ \ \_\ \ \_\ \ \______\\ \_____/
 *                  \/_________/  \/_/  \/_/  \/______/ \/____/
 *
 *         ->ShapeRegression
 *         ->Developed By Christopher J. Wald
 *         ->Copyright 2011 (c) All Rights Reserved
 *
 *
 * @author  	Christopher J. Wald
 * @date    	Apr 22, 2011
 * @project 	ShapeRegression
 * @file    	ShapeRegression.java
 * @description Provides a framework for guessing the shape of a user-
 * 				drawn figure based on the closest match of a computer
 * 				generated shape. The Fast Fourier Transform is used to
 * 				determine the closest match based on the distance
 *				between all the points on the drawn figure and the mean
 *				center of the shape.
 * @license:
 *
 * 	Redistribution and use in source and binary forms, with or without
 * 	modification, are permitted provided that the following conditions
 * 	are met:
 *
 *	- Redistributions of source code must retain the above copyright
 *	  notice, this list of conditions and the following disclaimer.
 *
 *	- Redistributions in binary form must reproduce the above copyright
 *	  notice, this list of conditions and the following disclaimer in the
 *	  documentation and/or other materials provided with the distribution.
 *
 *	- The name of Christopher J. Wald may not be used to endorse or promote
 *	  products derived from this software without specific prior written
 *	  permission.
 *
 * 	THIS SOFTWARE IS PRIVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * 	EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * 	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * 	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * 	DIRECT, INDIRECT, INCIDENTAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING
 * 	BUT NOT LIMITED TO UNDESIRED ACTION, LOSS OF SECURITY, LOSS OF DATA, LOSS OF
 * 	SLEEP,  LOSS OF HAIR, OR EXPLOSIONS). USE AT YOUR OWN RISK.
 */

package com.destructorlabs.shape;

import java.util.ArrayList;
import java.util.Vector;

import com.destructorlabs.companion.Dimension;
import com.destructorlabs.companion.Point;

public class ShapeRegression {

	private Vector<Point>	pts					=new Vector<Point>(0);
	private final double	MIN_SPACE_DIFF		=12d;
	private final double	MIN_SLOPE_DIFF		=2d;
	private final double	MIN_CIRCLE_STDEV	=9d;

	private class LocInfo{
		public Point pt=null;
		public Point center=null;
		public Dimension dim=null;

		public LocInfo(final Point p, final Dimension d) {
			this.pt=p;
			this.dim=d;
			this.center=new Point(this.pt.x + (this.dim.width / 2), this.pt.y + (this.dim.height / 2));
		}
	}

	public ShapeRegression(final Vector<Point> points) {
		this.pts=points;
	}

	public ShapeRegression() {}

	public Vector<Point> runRegression() {
		//Get information on location of points
		LocInfo li=this.getLocInfo();
		if (li == null)
			return null;

		//Find stuff for a circle
		double stdev = this.circleRegression(li);
		if (stdev < this.MIN_CIRCLE_STDEV) {
			System.out.println("Standard Dev: " + stdev);
			Vector<Point> circle = new Vector<Point>();
			circle.add(new Point(li.center.x, li.center.y));
			circle.add(new Point(-1, li.dim.width));
			return circle;
		}

		//Set up vars
		ArrayList<Double> dist=new ArrayList<Double>(0);
		double ave=0.0;
		int index=1;

		//Find average distance from center of figure
		for (Point p : this.pts) {
			index++;
			dist.add(this.distForm(li.center.x, li.center.y, p.x, p.y));
			ave = ((ave * (index - 1) + dist.get(dist.size()-1)) / index);
		}

		//Copy to new array
		double[] distances=this.copyArray(dist);
		double[] dist_temp=this.copyArray(dist);

		int N=this.findHighestN(distances.length);

		//Set up the new Fast Fourier Transforms
		FFT f	=new FFT(N);
		FFT ff	=new FFT(N);

		//Run initial FFT on distances from center of figure
		double[] f_results=f.fft(distances);

		//Run FFT on the original FFT
		double[] ff_results=ff.fft(f_results);

		//Find highest non-first spike of each FFT
		int fsp=this.findSpike(f_results, N);
		int ffsp=this.findSpike(ff_results, N);

		//Store as the number of sides
		int sides0=0;
		int sides1=0;

		try {
			sides0=fsp;
			sides1=ff_results.length / ffsp;
		} catch (ArithmeticException e) {
			System.out.println("Arithmetic Exception: / by 0");
			return null;
		}

		int sides=this.findNumSides(sides0, sides1);
		ArrayList<Integer> dist_index=this.findCorners(dist_temp, sides);

		//At this point the preliminary list of corners has been made.
		//Now we run editing routines to smooth and make into a more common
		//shape.

		//Convert from an array to a vector
		Vector<Point> p=new Vector<Point>(0);
		for (int i : dist_index) {
			p.add(this.pts.get(i));
		}

		p=this.merge(p);
		p=this.smooth(p);

		//Print Stats to console
		System.out.println("N: " + N);
		System.out.println("Number of Sides: " + (p.size()));

		//Return point vector
		return p;
	}

	private double circleRegression(final LocInfo li) {
		//Set up vars
		ArrayList<Double> dist=new ArrayList<Double>(0);
		double ave=0.0;
		int index=1;

		//Find average distance from center of figure
		for (Point p : this.pts) {
			index++;
			dist.add(this.distForm(li.center.x, li.center.y, p.x, p.y));
			ave = ((ave * (index - 1) + dist.get(dist.size()-1)) / index);
		}

		double diff=0;

		//Find the standard deviation
		for (int i=0; i<dist.size(); i++) {
			diff+=Math.pow((ave - this.distForm(li.center.x, li.center.y, this.pts.get(i).x, this.pts.get(i).y)), 2);
		}

		diff = (diff / (dist.size() - 1));
		double stdev = Math.sqrt(diff);

		return stdev;
	}

	/**
	 * Merges corners based on proximity
	 * @param p
	 * @return p
	 */
	private Vector<Point> merge(final Vector<Point> p){
		boolean target_hit;

		do {
			target_hit=false;
			try {
				for (int i=0; i<p.size()-1; i++) {
					target_hit=((this.distForm(p.get(i), p.get(i+1)) < this.MIN_SPACE_DIFF) ? true : target_hit );

					if (target_hit && p.size() > 3) {
						Point temp_point=this.midpoint(p.get(i), p.get(i+1));
						p.remove(i+1);
						p.set(i, temp_point);
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("WARNING: Array Index Exception");
			}
		} while (target_hit==true);

		return p;
	}

	/**
	 * Combines lines with similar slopes
	 * @param p
	 * @return p
	 */
	private Vector<Point> smooth(final Vector<Point> p){
		for (int i=0; i<p.size(); i++) {
			try {
				int get0=0;
				int get1=0;

				if (i==p.size()-1) {
					get0=0;
					get1=1;
				} else if (i==p.size()-2) {
					get0=i+1;
					get1=0;
				} else {
					get0=i+1;
					get1=i+2;
				}

				double slope0=this.findTheta(p.get(i), p.get(get0));
				double slope1=this.findTheta(p.get(get0), p.get(get1));
				if (slope0 == Double.NaN  ||  slope1 == Double.NaN)
					break;

				if (Math.abs(slope1 - slope0) < this.MIN_SLOPE_DIFF  &&  p.size() > 3)
					p.remove(get0);

			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("WARNING: Array Index Exception");
			}
		}

		return p;
	}

	/**
	 * Copies an ArrayList to an array
	 * @param dist
	 * @return
	 */
	private double[] copyArray(final ArrayList<Double> dist) {
		double[] distances=new double[dist.size()];
		for (int i=0; i<dist.size(); i++) {
			distances[i]=dist.get(i);
		}

		return distances;
	}

	/**
	 * Finds the closest power of 2 to the size of the point
	 * list (less than the size of the list)
	 * @param size
	 * @return N
	 */
	private int findHighestN(final int size) {
		int N=0;
		for (int i=0; ; i++) {
			if (Math.pow(2, i) > size) {
				N=(int) Math.pow(2, i-1);
				break;
			}
		}
		return N;
	}

	/**
	 * Find the point farthest from the center for each of <sides> chunks
	 * of the points list
	 * 
	 * @param dist_temp
	 * @param sides
	 * @return dist_index
	 */
	private ArrayList<Integer> findCorners(final double[] dist_temp, final int sides){
		//Find point farthest from the center for each of <sides> chunks
		//of the points list
		double[] temp=dist_temp;
		double max=0.0;
		ArrayList<Integer> dist_index=new ArrayList<Integer>(0);
		dist_index.add(0);

		for (int i=0; i<sides; i++) {
			for (int j=(temp.length / sides) * i; j<(temp.length / sides) * (i+1); j++) {
				if (temp[j] > max) {
					max=temp[j];
					dist_index.set(dist_index.size() - 1, j);
				}
			}
			max=0;
			temp[dist_index.get(dist_index.size() - 1)]=0;
			dist_index.add(0);

			for (int k=dist_index.get(dist_index.size() - 1) - 5; k<dist_index.get(dist_index.size() - 1) + 5; k++) {
				while (k<0) {
					k++;
				}
				temp[k]=0;
			}
		}

		return dist_index;
	}

	/**
	 * Algorithm to find the best number of sides from the two calculations
	 * @param sides0
	 * @param sides1
	 * @return sides
	 */
	private int findNumSides(final int sides0, int sides1) {
		int sides = 0;

		if (sides1 == 2)
			sides1++;
		if (sides0 > 7)
			sides=sides1;
		else if (sides1 > 7)
			sides=sides0;
		else if (sides0 < sides1)
			sides=sides0;
		else
			sides=sides1;

		return sides;
	}

	/**
	 * Find the slope between the two points
	 * @param p0
	 * @param p1
	 * @return slope (NaN if Vertical)
	 */
	private double findTheta(final Point p0, final Point p1) {
		try {
			return Math.atan2((p1.y - p0.y) / (p1.x - p0.x), 1);
		} catch (ArithmeticException e) {
			return Double.NaN;
		}
	}

	/**
	 * Finds the highest spike in the provided list
	 * @param d
	 * @param N
	 * @return index of the spike
	 */
	private int findSpike(final double[] d, final int N) {
		//Find the "x value" with the highest "y"
		double	max_value=0;
		int		max_index=0;
		for (int i=3; i<N/2; i++) {
			if (Math.abs(d[i]) > max_value) {
				max_value=d[i];
				max_index=i;
			}
		}

		return max_index;
	}

	/**
	 * Finds the center, width, and height of the drawing
	 * @return LocInfo object
	 */
	private LocInfo getLocInfo() {

		System.out.println("made it");
		Integer lx=null;
		Integer hx=null;
		Integer ly=null;
		Integer hy=null;

		for(Point p : this.pts) {
			if (lx==null  ||  p.x < lx) {
				lx=p.x;
			}
			if (hx==null  ||  p.x > hx) {
				hx=p.x;
			}
			if (ly==null  ||  p.y < ly) {
				ly=p.y;
			}
			if (hy==null  ||  p.y > hy) {
				hy=p.y;
			}
		}

		try {
			return new LocInfo(new Point(lx, ly), new Dimension(hx-lx, hy-ly));
		} catch (NullPointerException e) {
			return null;
		}
	}

	private Point midpoint(final Point p0, final Point p1) {
		return new Point((p0.x + p1.x) / 2, (p0.y + p1.y) / 2);
	}

	private double distForm(final Point p0, final Point p1) {
		return this.distForm(p0.x, p0.y, p1.x, p1.y);
	}

	private double distForm(final int a, final int b, final int c, final int d) {
		return Math.sqrt((Math.pow(a - c, 2) + Math.pow(b - d, 2)));
	}

	public void addPointsList(final Vector<Point> v) {
		this.pts=v;
	}
}
