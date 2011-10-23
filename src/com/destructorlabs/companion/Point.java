package com.destructorlabs.companion;

import java.io.Serializable;

public class Point implements Serializable{
	private static final long serialVersionUID = -1338550134922245737L;
	public int x=0 ,y=0;

	public Point(final int x, final int y) {
		this.x=x;
		this.y=y;
	}

	public Point() {}

	public Point(final Float x, final Float y) {
		float fx=(float) x;
		float fy=(float) y;

		this.x=(int) fx;
		this.y=(int) fy;
	}
}