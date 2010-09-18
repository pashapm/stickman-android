package org.hackday.stickman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by IntelliJ IDEA.
 * User: ildar
 * Date: Sep 18, 2010
 * Time: 11:06:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenProps.initialize(this);
//        startActivity(new Intent(this, LandscapeEditActivity.class));
        setContentView(new StickmanView(this));
    }
}
