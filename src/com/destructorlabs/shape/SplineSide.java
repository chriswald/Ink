package com.destructorlabs.shape;

import com.destructorlabs.pkg.Corner;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class SplineSide implements DrawableSide{
	private final Corner[] corners = new Corner[3];

	public SplineSide(Corner c0, Corner c1, Corner c2){
		this.corners[0] = c0;
		this.corners[1] = c1;
		this.corners[2] = c2;
	}

	public void draw(Canvas canvas, Paint paint){
		Path path = new Path();
		path.moveTo(this.corners[0].getX(), this.corners[0].getY());
		path.quadTo(this.corners[1].getX(), this.corners[1].getY(), this.corners[2].getX(), this.corners[2].getY());
		canvas.drawPath(path, paint);
	}
}
