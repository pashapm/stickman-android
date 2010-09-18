package org.hackday.stickman;


import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class ScreenProps {

	public static int screenHeight;
	public static int screenWidth;

	public static void initialize(Context ctx) {
		Display disp = ((WindowManager) ctx.getSystemService(
				android.content.Context.WINDOW_SERVICE)).getDefaultDisplay();
		ScreenProps.screenHeight = disp.getHeight();
		ScreenProps.screenWidth = disp.getWidth();
	}
	
}
