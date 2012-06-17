package com.destructorlabs.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.destructorlabs.companion.Point;

public class Handle {
	private Point		location	= new Point(0, 0);
	private static int	radius		= 30;
	private int			color		= Color.BLACK;
	private int			alpha		= 255;
	private int			width		= 2;

	public Handle(int x, int y){
		this.location = new Point(x, y);
	}

	public Handle(Point loc){
		this.location = loc;
	}

	public void move_abs(int x, int y){
		this.location.x = x;
		this.location.y = y;
	}

	public void move_abs(Point loc){
		this.location = loc;
	}

	public void move_rel(int x, int y){
		this.location.x += x;
		this.location.y += y;
	}

	public void move_rel(Point loc){
		this.location.x += loc.x;
		this.location.y += loc.y;
	}

	public void drawHandle(Canvas c, Paint p){
		/*int x = this.location.x - (DrawView.bmp.getWidth() / 2);
		int y = this.location.y - (DrawView.bmp.getHeight() / 2);

		try {
			c.drawBitmap(DrawView.bmp, x, y, null);
		} catch (Exception e) {
			c.drawCircle(this.location.x, this.location.y, radius, p);
		}*/
	}

	public void drawChrome(Canvas c, Paint p){
		double distance = 40 + (this.width * 2);
		double angle = (this.alpha * (90 / 255)) - 45;

		int x = (int) (distance * Math.cos(angle));
		int y = (int) (distance * Math.sin(angle));

		c.drawLine(this.location.x, this.location.y, x, y, p);
		c.drawCircle(x, y, 20, p);
	}
}
