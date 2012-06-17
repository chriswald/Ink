package com.destructorlabs.shape;

import com.destructorlabs.pkg.Corner;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LineSide implements DrawableSide{
	private final Corner[] corners = new Corner[2];

	public LineSide(Corner c0, Corner c1){
		this.corners[0] = c0;
		this.corners[1] = c1;
	}

	public void draw(Canvas canvas, Paint paint){
		canvas.drawLine(this.corners[0].getX(), this.corners[0].getY(), this.corners[1].getX(), this.corners[1].getY(), paint);
	}
}
