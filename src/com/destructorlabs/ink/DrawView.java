package com.destructorlabs.ink;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.destructorlabs.companion.Point;
import com.destructorlabs.pkg.Corner;
import com.destructorlabs.pkg.Shape;
import com.destructorlabs.pkg.Shape.ShapeType;
import com.destructorlabs.shape.ShapeFit;

public class DrawView extends View {
	public static class PointerState {
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

	private final Paint paint;
	private final Paint pathPaint;
	private final Paint cornerPaint;
	private static final PointerState pointer = new PointerState();
	private static Vector<Shape> shapes_list = new Vector<Shape>();

	public DrawView(final Context context) {
		super(context);
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

		DrawView.pointer.velocity = VelocityTracker.obtain();

		this.setBackgroundColor(Color.WHITE);
	}

	public DrawView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
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

		DrawView.pointer.velocity = VelocityTracker.obtain();

		this.setBackgroundColor(Color.WHITE);
	}

	public DrawView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
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

		DrawView.pointer.velocity = VelocityTracker.obtain();

		this.setBackgroundColor(Color.WHITE);
	}

	public static Vector<Shape> getShapesForSave() {
		return new Vector<Shape>(DrawView.shapes_list);
	}

	public static void addShapes(Vector<Shape> shapes){
		DrawView.shapes_list = new Vector<Shape>(shapes);
		DrawView.pointer.dots.removeAllElements();
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		if (DrawView.pointer.press_type == PointerState.Type.DRAW)
			for (int i = 0; i < DrawView.pointer.size() - 1; i++) {
				canvas.drawLine(DrawView.pointer.get(i).x, DrawView.pointer.get(i).y,
						DrawView.pointer.get(i + 1).x, DrawView.pointer.get(i + 1).y,
						this.pathPaint);
				canvas.drawPoint(DrawView.pointer.get(i).x, DrawView.pointer.get(i).y,
						this.paint);
			}

		// Draw all stored shapes
		for (Shape s : shapes_list){
			s.drawShape(canvas, this.paint);
		}

		try {
			Shape last = shapes_list.lastElement();
			if (last.getType() != ShapeType.CIRCLE)
				this.drawHandles(last, canvas, this.cornerPaint);
		} catch (Exception e) {}
	}

	private void drawHandles(final Shape shape, final Canvas canvas,
			final Paint paint) {
		if (shape.getType() == Shape.ShapeType.POLYGON)
			for (Corner corner : shape.getCorners()) {
				this.drawSingleHandle(corner, canvas, paint);
			}
		else {
			Point point = new Point(shape.getCorners().get(1).getY()
					+ shape.getCenter().getX(), shape.getCenter().getY());
			Corner handle = new Corner(point, 20);
			this.drawSingleHandle(handle, canvas, paint);
		}
	}

	private void drawSingleHandle(final Corner c, final Canvas canvas, final Paint paint) {
		try {
			Bitmap handle = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.handle);
			int w = handle.getWidth();
			int h = handle.getHeight();
			canvas.drawBitmap(handle, c.getX() - (w / 2), c.getY() - (h / 2),
					null);
		} catch (Exception e) {
			canvas.drawCircle(c.getX(), c.getY(), c.getRadius(), paint);
		}
	}

	private void addTouchEvent(final MotionEvent event) {
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
	}

	private Corner overAnyCorners(final MotionEvent event) {
		for (Shape s : DrawView.shapes_list) {
			for (Corner c : s.getCorners()) {
				if (this.overCorner(event, c))
					return c;
			}
		}
		return null;
	}

	private boolean overCorner(final MotionEvent event, final Corner c) {
		float x = event.getX();
		float y = event.getY();

		int cx = c.getX();
		int cy = c.getY();

		if (Common.distance(x, y, cx, cy) <= c.getRadius())
			return true;

		return false;
	}

	private void doRegression() {
		ShapeFit SR = new ShapeFit();
		SR.addPointsList(DrawView.pointer.getDots());
		Vector<Point> points = SR.runRegression();

		if (points == null)
			return;

		Shape shape = new Shape();
		for (Point p : points) {
			shape.addCorner(new Corner(p, 10));
		}

		// If the user is done drawing add the most
		// recent shape to the list
		if (!DrawView.pointer.curDown)
			DrawView.shapes_list.add(shape);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		this.addTouchEvent(event);

		if (DrawView.pointer.press_type == PointerState.Type.DRAW)
			this.doRegression();
		else
			DrawView.pointer.corner.move(DrawView.pointer.getLast());

		return true;
	}

	@Override
	public boolean onTrackballEvent(final MotionEvent event) {
		return super.onTrackballEvent(event);
	}

}
