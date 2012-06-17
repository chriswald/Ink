package com.destructorlabs.ink;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.destructorlabs.companion.Point;
import com.destructorlabs.pkg.Corner;
import com.destructorlabs.shape.Shape;
import com.destructorlabs.shapefit.ShapeFit;

public class DrawView extends View {

	private Paint paint;
	private Paint pathPaint;
	private Paint cornerPaint;

	private Vector<Point> new_shape_points = new Vector<Point>();
	private Vector<Shape> shapes = new Vector<Shape>();
	private Corner current_edit_corner = null;

	public enum TouchType {EDIT, DRAW, REGISTER_DRAW, NONE};

	private TouchType draw_mode = TouchType.NONE;

	public DrawView(final Context context) {
		super(context);
		this.init();
	}

	public DrawView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	public DrawView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}

	/*private void addTouchEvent(final MotionEvent event) {
		synchronized (DrawView.pointer) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_DOWN) {
				Corner c = this.overAnyCorners(event);
				if (c == null)
					DrawView.pointer.press_type = PointerState.Type.DRAW;
				else {
					DrawView.pointer.press_type = PointerState.Type.MOVE;
					DrawView.pointer.setCorner(c);
				}

				DrawView.pointer.dots.clear();
				DrawView.pointer.velocity = VelocityTracker.obtain();
				DrawView.pointer.curDown = true;
			}

			if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
				DrawView.pointer.velocity = VelocityTracker.obtain();
				DrawView.pointer.curDown = true;
			}

			DrawView.pointer.velocity.addMovement(event);
			DrawView.pointer.velocity.computeCurrentVelocity(1);
			DrawView.pointer.dots.add(new Point(event.getX(), event.getY()));

			if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP)
				DrawView.pointer.curDown = false;

			if (action == MotionEvent.ACTION_UP)
				DrawView.pointer.curDown = false;
		}

		this.postInvalidate();
	}*/

	private void init(){
		this.setFocusable(true);
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setARGB(255, 0, 0, 0);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setStrokeWidth(2);

		this.pathPaint = new Paint();
		this.pathPaint.setAntiAlias(false);

		this.cornerPaint = new Paint();
		this.cornerPaint.setAntiAlias(true);
		this.cornerPaint.setARGB(255, 255, 64, 128);
		this.cornerPaint.setStyle(Style.STROKE);
		this.cornerPaint.setStrokeWidth(2);

		this.setBackgroundColor(Color.WHITE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		this.getTouchType(evt);

		switch (this.draw_mode) {
			case DRAW:
				this.new_shape_points.add(new Point(evt.getX(), evt.getY()));
				this.current_edit_corner = null;
				break;
			case REGISTER_DRAW:
				this.do_regression();
				this.new_shape_points = new Vector<Point>();
				this.current_edit_corner = null;
				break;
			case EDIT:
				this.new_shape_points = new Vector<Point>();
				this.doEdit(evt);
				break;
			default:
				return true;
		}

		this.postInvalidate();

		return true;
	}

	private void getTouchType(MotionEvent evt) {
		int action = evt.getAction();

		if (action == MotionEvent.ACTION_DOWN  ||  (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
			this.current_edit_corner = this.overAnyCorners(evt);
			if (this.current_edit_corner != null)
				this.draw_mode = TouchType.EDIT;
			else
				this.draw_mode = TouchType.DRAW;
		}

		if (action == MotionEvent.ACTION_UP) {
			this.draw_mode = TouchType.REGISTER_DRAW;
		}
	}

	private void doEdit(MotionEvent evt) {
		this.moveCorner(evt, this.current_edit_corner);
		this.setColors(evt, this.current_edit_corner);
	}

	private void moveCorner(MotionEvent evt, Corner c) {
		c.move(evt.getX(), evt.getY());
	}

	private void setColors(MotionEvent evt, Corner c) {
		float hue = (float) (Math.random() * 360);
		float hsv[] = {hue, 1, 1};
		c.color = Color.HSVToColor(hsv);
	}

	private Corner overAnyCorners(MotionEvent evt) {
		Corner c = null;
		Corner tmp = null;

		for (Shape s : this.shapes) {
			tmp = s.overCorner(evt);
			if (tmp != null)
				c = tmp;
		}

		return c;
	}

	private void do_regression() {
		if (this.new_shape_points.size() < 5)
			return;

		ShapeFit SF = new ShapeFit();
		SF.addPointsList(this.new_shape_points);
		Vector<Point> points = SF.runRegression();

		if (points == null)
			return;

		Shape new_shape = new Shape();
		for (Point p : points) {
			new_shape.addCorner(new Corner(p));
		}

		this.shapes.add(new_shape);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (Shape s : this.shapes) {
			s.drawShape(canvas, this.paint);
		}

		if (this.new_shape_points.size() < 5)
			return;

		for (int i = 0; i < this.new_shape_points.size(); i ++) {
			int next_point = (i + 1) % this.new_shape_points.size();

			canvas.drawLine(this.new_shape_points.get(i).x, this.new_shape_points.get(i).y,
					this.new_shape_points.get(next_point).x, this.new_shape_points.get(next_point).y,
					this.paint);

			canvas.drawLine(this.new_shape_points.get(i).x, this.new_shape_points.get(i).y,
					this.new_shape_points.get(i).x, this.new_shape_points.get(i).y,
					this.cornerPaint);
		}

		if (this.draw_mode == TouchType.EDIT) {
			Log.d("EDIT MODE", "In Edit Mode");
			canvas.drawCircle(0, 0, 300, this.pathPaint);
		}
	}

}
