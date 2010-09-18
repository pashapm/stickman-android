package org.hackday.stickman;

import org.hackday.stickman.Stickman.Edge;
import org.hackday.stickman.Stickman.Point;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class StickmanView extends View {

	private Stickman mStickman = new Stickman();
	private Paint mPaint = new Paint();

	private Point mCaptured = null;

	public StickmanView(Context context, AttributeSet attrs) {
		super(context, attrs);
        init();
	}

	public StickmanView(Context context) {
		super(context);
        init();
	}

	public void setStickman(Stickman s) {
		Stickman ss = new Stickman();
		ss.set(mStickman);
		mStickman = ss;
	}


    private void init() {
        setMinimumHeight(100);
        setMinimumWidth(75);
    }

	@Override
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
//        System.out.println("width = " + width);
//        System.out.println("height = " + height);
        if (height > 0) {
            float scaleFactor = 1.0f * height / ScreenProps.screenHeight;
//            System.out.println("scaleFactor = " + scaleFactor);
            canvas.scale(scaleFactor, scaleFactor);
        }


        canvas.drawColor(Color.WHITE);
		mPaint.setAntiAlias(true);

		for (Edge edge : mStickman.getEdges()) {
			canvas.drawLine(edge.mStart.x, edge.mStart.y, edge.mEnd.x, edge.mEnd.y, mPaint);
		}

		for (Point p : mStickman.getPoints().values()) {
			mPaint.setColor(p.mSelected ? Color.RED : Color.BLACK);
			canvas.drawCircle(p.x, p.y, p.mBig ? 10 : 5, mPaint);
		}
		mPaint.setColor(Color.BLACK);
	}

	private Point findNearestPoint(int x, int y) {
		Point near = null;
		for (Point p : mStickman.getPoints().values()) {
			if (near == null) {
				near = p;
			} else {
				if (Math.hypot(p.x - x, p.y - y) < Math.hypot(near.x - x, near.y - y)) {
					near = p;
				}
			}
		}
		return near;
 	}

	private int lastX;
	private int lastY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Point sel = findNearestPoint((int)event.getX(), (int)event.getY());
            System.out.println("sel = " + sel);
			mStickman.selectPoint(sel);
			mCaptured = sel;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			mCaptured = null;
		}

		invalidate();
		return super.onTouchEvent(event);
	}

	public void move(int x, int y) {
		int dx = x - lastX;
		int dy = y - lastY;
		mStickman.move(mCaptured, x, y, dx, dy);
		invalidate();
	}

	public void setLastXY(int x, int y) {
		lastX = x;
		lastY = y;
	}

    public void setmStickman(Stickman mStickman) {
        this.mStickman = mStickman;
    }
}
