package org.hackday.stickman;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

/**
 * Created by IntelliJ IDEA.
 * User: ildar
 * Date: Sep 18, 2010
 * Time: 11:23:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class LandscapeEditActivity extends Activity implements View.OnTouchListener {
    private View moveObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landscape_edit);
        findViewById(R.id.landscape_main_view).setOnTouchListener(this);
        moveObject = findViewById(R.id.landscape_moving_object);
    }

    private int oldX = -1;
    private int oldY = -1;

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = (int) motionEvent.getX();
                oldY = (int) motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) moveObject.getLayoutParams();
                lp.x -= oldX - (int) motionEvent.getX();
                lp.y -= oldY - (int) motionEvent.getY();
                moveObject.setLayoutParams(lp);
                oldX = (int) motionEvent.getX();
                oldY = (int) motionEvent.getY();
                break;
        }
//        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//            oldX = (int) motionEvent.getX();
//            oldY = (int) motionEvent.getY();
//        }
//        {
//            AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) moveObject.getLayoutParams();
//            lp.x += oldX - (int) motionEvent.getX();
//            lp.y += oldY - (int) motionEvent.getY();
//            moveObject.setLayoutParams(lp);
//            oldX = (int) motionEvent.getX();
//            oldY = (int) motionEvent.getY();
//        }

        return true;
    }
}
