/**
 *  _______    __    __   ________   __________    _______
 * /\   __ \  /\ \  /\ \ /\  ____ \ /\____  ___\  /\  ____\
 * \ \ \_/\_\ \ \ \_\_\ \\ \ \___\ \\/___/\ \__/  \ \ \___/
 *  \ \ \\/_/  \ \  ____ \\ \  ___ <     \ \ \     \ \____`\
 *   \ \ \   __ \ \ \__/\ \\ \ \ /\ \     \ \ \     \/___/\ \
 *    \ \ \__\ \ \ \ \ \ \ \\ \ \\ \ \    _\_\ \____   __\_\ \
 *     \ \______\ \ \_\ \ \_\\ \_\\ \_\  /\_________\ /\______\
 *      \/______/  \/_/  \/_/ \/_/ \/_/  \/_________/ \/______/
 *             __      __    ________    __        ______
 *            /\ \    /\ \  /\  ____ \  /\ \      /\  ___`,
 *            \ \ \   \ \ \ \ \ \__/\ \ \ \ \     \ \ \_/\ \
 *             \ \ \   \ \ \ \ \ \_\_\ \ \ \ \     \ \ \\ \ \
 *              \ \ \  _\ \ \ \ \  ____ \ \ \ \     \ \ \\ \ \
 *               \ \ \_\ \_\ \ \ \ \__/\ \ \ \ \_____\ \ \\_\ \
 *                \ \_________\ \ \_\ \ \_\ \ \______\\ \_____/
 *                 \/_________/  \/_/  \/_/  \/______/ \/____/
 *
 *         ->Ink
 *         ->Developed By Christopher J. Wald
 *         ->Copyright 2011 (c) All Rights Reserved
 *
 *
 * @author  	Christopher J. Wald
 * @date    	Sep 24, 2011
 * @project 	Ink
 * @file    	Corner.java
 * @description Class for handling shape locations (dragging, adding,
 * 				removing, etc.).
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
package com.destructorlabs.pkg;

import java.io.Serializable;

import android.graphics.Color;
import android.view.MotionEvent;

import com.destructorlabs.companion.Point;

public class Corner implements Serializable{

	private static final long serialVersionUID = 2776959186746125338L;
	private Point	location;
	private static final int RADIUS = 30;
	private boolean	selected = false;
	public int color = Color.BLACK;

	@Override
	public String toString(){
		return this.location.toString();
	}

	public Corner(final Point p) {
		this.location = p;
	}

	public Corner(final int x, final int y) {
		this.location = new Point(x, y);
	}

	public Corner(final float x, final float y) {
		this.location = new Point((int) x, (int) y);
	}

	public boolean isOver(final MotionEvent e) {
		int x = (int) e.getX();
		int y = (int) e.getY();
		return this.isOver(x, y);
	}

	public boolean isOver(final float x, final float y) {
		return this.isOver((int) x, (int) y);
	}

	public boolean isOver(final int x, final int y) {
		int xx = this.getLocation().x;
		int yy = this.getLocation().y;

		double distance = Math.sqrt(Math.pow(xx - x, 2) + Math.pow(yy - y, 2));

		return (distance < RADIUS);
	}

	public boolean toggleSelected() {
		return this.selected = !this.selected;
	}

	public void move(final Point p) {
		this.location = p;
	}

	public void move(final int x, final int y) {
		this.location = new Point(x, y);
	}

	public void move(final float x, final float y) {
		this.location = new Point((int) x, (int) y);
	}

	public void moveBy(final float x, final float y) {
		this.location = new Point((int) x + this.location.x, (int) y + this.location.y);
	}

	public Point getLocation() {
		return this.location;
	}

	public int getX() {
		return this.location.x;
	}

	public int getY() {
		return this.location.y;
	}
}
