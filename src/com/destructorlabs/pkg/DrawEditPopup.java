package com.destructorlabs.pkg;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawEditPopup {
	private Corner corner;

	public DrawEditPopup(Corner c) {
		this.corner = c;
	}

	public void draw(Canvas c, Paint paint){
		c.drawCircle(this.corner.getX(), this.corner.getY(), 30, paint);
	}
}
