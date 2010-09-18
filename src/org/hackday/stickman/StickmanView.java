package org.hackday.stickman;

import org.hackday.stickman.Stickman.Edge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StickmanView extends View {

	private Stickman mStickman = new Stickman();
	private Paint mPaint = new Paint();
	
	public StickmanView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public StickmanView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		
		for (Edge edge : mStickman.getEdges()) {
			canvas.drawLine(edge.mStart.x, edge.mStart.y, edge.mEnd.x, edge.mEnd.y, mPaint);
		}
		
		canvas.drawCircle(mStickman.getHead().x, mStickman.getHead().y, 10, mPaint);
	}

}
