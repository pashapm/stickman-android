package org.hackday.stickman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ildar
 * Date: Sep 18, 2010
 * Time: 11:06:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity {
	private StickmanView mView;
	
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenProps.initialize(this);
//        startActivity(new Intent(this, LandscapeEditActivity.class));
        mView = new StickmanView(this);
        setContentView(mView);
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_MOVE) {  
    		mView.move((int)event.getX(), (int)event.getY());
    	} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
    		mView.setLastXY((int)event.getX(), (int)event.getY());
    	} else if (event.getAction() == MotionEvent.ACTION_UP) {
    		mView.setLastXY(0, 0);
    	}
    	return super.onTouchEvent(event); 
    }
}
