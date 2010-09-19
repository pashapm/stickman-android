package org.hackday.stickman.processing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

import org.hackday.stickman.SceneList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class ProcessingService extends Service {

	private static final String TAG = "ProcessingService";
	
	/**
	 * Flag to be set to true if processing completed successfully
	 */
	private boolean processingOk = false;
	
	static {                           
	    System.loadLibrary("testf");
	}
	
	private native void callFfmpeg(int argc, String[] argv);
	
	private void processMedia(String params) {
		Log.d(TAG, "Processing: ffmpeg "+params);
		String[] argv = params.split(" ");
		callFfmpeg(argv.length, argv);
	}
	
	private void processShell(String params) {
		Log.d(TAG, "Shell: "+params);
		try {
			Runtime.getRuntime().exec(params);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void multicat(String[] files, boolean fromAssets) throws IOException {
		FileOutputStream fos = new FileOutputStream(files[files.length-1]);
		byte[] buff = new byte[1000];
		
		InputStream is = null;
		for (int i=0; i<files.length-1; ++i) {
			if (TextUtils.isEmpty(files[i])) {
				continue;
			}
			
			if (fromAssets) {
				is = getAssets().open(files[i]);
			} else {
				is = new FileInputStream(files[i]);
			}
			
			int c = 0;
			while ((c=is.read(buff, 0, 1000)) != -1) {
				fos.write(buff,0,c);
			}
		}
		fos.close();
	}
	
	private void cat(String s1, String s2, String dest) throws IOException {
		FileInputStream fi0;
		fi0 = new FileInputStream(s1);
		FileInputStream fi1;
		fi1 = new FileInputStream(s2);
		SequenceInputStream seq = new SequenceInputStream(fi0, fi1);
		FileOutputStream fos = new FileOutputStream(dest);
		byte[] buff = new byte[1000];
		int c = 0;
		while ((c=seq.read(buff, 0, 1000)) != -1) {
			fos.write(buff,0,c);
		}
		fos.close();
	}
	
	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				start(intent);
			}
		}).start();
		
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				if (!processingOk) {
//					Intent in = new Intent(ProcessingService.this, ConvertingActivity.class);
//					in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//					in.setAction(ConvertingActivity.PROCESSING_FAILED);
//					startActivity(in);
//				}
//			}
//		}));
	}
	
	
	public void start(Intent intent) {
		int i = intent.getIntExtra("num", 0);
		String[] commands = intent.getStringArrayExtra("commands");
		if (commands == null || i > commands.length-1) {
			return;
		}
		
		String command = commands[i];
		
		
		if (command.startsWith("s")) {
			processShell(command.replaceFirst("s", ""));
		} else if (command.startsWith("cat")){
			String args[] = command.replaceFirst("cat", "").split(" ");
			try {
				multicat(args, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (command.startsWith("acat")){  //files from assets
			String args[] = command.replaceFirst("acat", "").split(" ");
			try {
				multicat(args, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			processMedia(command);
		}
		processingOk = true;
		
		++i;
		if (i == commands.length) { //it was the last command, calling Activity
			Intent in = new Intent(ProcessingService.this, SceneList.class);
			in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			in.setAction(SceneList.PROCESSING_FINISHED);
			startActivity(in);
		}
		
		//Choose one of the services to launch a new process. 
		//A Service class can't hold more than 1 process so we have 3 different classes
		
		Class<?> cl = null;
		switch (i%3) {
		case 0:
			cl = ProcessingService.class;
			break;
		case 1:
			cl = ProcessingService1.class;
			break;
		case 2:
			cl = ProcessingService2.class;
			break;
		}
		intent.setClass(this, cl);
		intent.putExtra("num", i);
		startService(intent);
		System.exit(0);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}

