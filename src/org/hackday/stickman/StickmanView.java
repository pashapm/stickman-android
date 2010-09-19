package org.hackday.stickman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

import org.hackday.stickman.Stickman.Edge;
import org.hackday.stickman.Stickman.Point;

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
        setMinimumWidth(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scaleFactor = getScaleFactor(canvas);
        canvas.scale(scaleFactor, scaleFactor);

        canvas.drawColor(Color.WHITE);
        mPaint.setAntiAlias(true);

        for (Edge edge : mStickman.getEdges()) {
            canvas.drawLine(edge.mStart.x, edge.mStart.y, edge.mEnd.x, edge.mEnd.y, mPaint);
        }

        for (Point p : mStickman.getPoints().values()) {
            mPaint.setColor(p.mSelected ? Color.RED : Color.BLACK);
            canvas.drawCircle(p.x, p.y, p.mBig ? Stickman.RESCALE_MULT / 30 : Stickman.RESCALE_MULT / 60, mPaint);
        }
        mPaint.setColor(Color.BLACK);
    }

    private float getScaleFactor() {
        return 1.0f * getMeasuredHeight() / Stickman.RELATIVE_HEIGHT;
    }

    private float getScaleFactor(Canvas canvas) {
        return 1.0f * canvas.getHeight() / Stickman.RELATIVE_HEIGHT;
    }

    private Point findNearestPoint(int x, int y) {
        x = (int) (x / getScaleFactor());
        y = (int) (y / getScaleFactor());
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
    	if (getParent() instanceof Gallery) {
    		return false;
    	}
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Point sel = findNearestPoint((int) event.getX(), (int) event.getY());
                mStickman.selectPoint(sel);
                mCaptured = sel;
                break;
            case MotionEvent.ACTION_MOVE:
                move((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                mCaptured = null;
                break;

        }

        invalidate();
        return true;
    }

    public void move(int x, int y) {
        x = (int) (x / getScaleFactor());
        y = (int) (y / getScaleFactor());
        int dx = x - lastX;
        int dy = y - lastY;
        mStickman.move(mCaptured, x, y, dx, dy);
        invalidate();
    }

    public void setLastXY(int x, int y) {
        x = (int) (x / getScaleFactor());
        y = (int) (y / getScaleFactor());
        lastX = x;
        lastY = y;
    }

    public void setmStickman(Stickman mStickman) {
        this.mStickman = mStickman;
    }

    public Bitmap makeBitmap(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

}
