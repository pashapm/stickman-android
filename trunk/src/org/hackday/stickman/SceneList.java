package org.hackday.stickman;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Toast;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

public class SceneList extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private SceneAdapter adapter;
    private Gallery gallery;
    private StickmanView stickmanView;
    private static final int DIALOG_WAIT = 0;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_WAIT:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("work in progress...");
                return dialog;
            default:
                return super.onCreateDialog(id);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenProps.initialize(this);
        setContentView(R.layout.scene_list);

        gallery = (Gallery) findViewById(R.id.gallery);
        adapter = new SceneAdapter();
        adapter.add(new Stickman());
        gallery.setAdapter(adapter);
        gallery.setOnItemSelectedListener(this);

        stickmanView = (StickmanView) findViewById(R.id.stickman_view);

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);

        findViewById(R.id.play).setOnClickListener(this);
        findViewById(R.id.export).setOnClickListener(this);

    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add:
                adapter.add(adapter.scenes.get(gallery.getSelectedItemPosition()));
                adapter.notifyDataSetChanged();
                break;
            case R.id.remove:
                if (adapter.scenes.size() > 1) {
                    adapter.scenes.remove(gallery.getSelectedItemPosition());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "cannot remove last", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.play:
            	ArrayList<Stickman> frames = Stickman.getIntermediateFrames(adapter.scenes.get(0), adapter.scenes.get(1), 10);
            	Stickman rm = adapter.scenes.removeLast();
            	for (Stickman s : frames) {
                	adapter.add(s);
                }
            	adapter.add(rm);
                adapter.notifyDataSetChanged();
            	break;
            case R.id.export:
                new AsyncTask<Object, Object, Object>(){
                    @Override
                    protected Object doInBackground(Object... objects) {
                        for (int i = 0, scenesSize = adapter.scenes.size(); i < scenesSize-1; i++) {
                            Stickman scene1 = adapter.scenes.get(i);
                            Stickman scene2 = adapter.scenes.get(i+1);
                            int framenum = 10;
                            ArrayList<Stickman> frames = Stickman.getIntermediateFrames(scene1, scene2, framenum);
                            File parent = new File("/sdcard/stickman/");
                            parent.mkdir();
                            DecimalFormat df = new DecimalFormat("img000.png");
                            for (int i1 = 0, framesSize = frames.size(); i1 < framesSize; i1++) {
                                Stickman frame = frames.get(i1);
                                StickmanView stickmanView = new StickmanView(SceneList.this);
                                stickmanView.setmStickman(frame);
                                Bitmap b = stickmanView.makeBitmap(100, 100);
                                File currentFrameFile = new File(parent, df.format(i * framenum + i1));
                                System.out.println("working on:" + currentFrameFile.getPath());
                                BufferedOutputStream os = null;
                                try {
                                    os = new BufferedOutputStream(new FileOutputStream(currentFrameFile));
                                    b.compress(Bitmap.CompressFormat.PNG, 100, os);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (os != null) try {
                                        os.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        showDialog(DIALOG_WAIT);
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        dismissDialog(DIALOG_WAIT);
                    }
                }.execute();
                break;
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Stickman s = adapter.scenes.get(i);
        stickmanView.setmStickman(s);
        stickmanView.invalidate();
        adapter.notifyDataSetChanged();
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        stickmanView.onTouchEvent(motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                stickmanView.move((int) motionEvent.getX(), (int) motionEvent.getY());
                break;
            case MotionEvent.ACTION_DOWN:
                stickmanView.setLastXY((int) motionEvent.getX(), (int) motionEvent.getY());
                break;
            case MotionEvent.ACTION_UP:
                stickmanView.setLastXY(0, 0);
                break;

        }
        return true;
    }

    private class SceneAdapter extends BaseAdapter {
        private LinkedList<Stickman> scenes = new LinkedList<Stickman>();

        public void add(Stickman stickman) {
        	Stickman newstick = new Stickman();
        	newstick.set(stickman);
            scenes.add(newstick);
            gallery.setSelection(scenes.size());
        }

        public void add(int index, Stickman stickman) {
        	Stickman newstick = new Stickman();
        	newstick.set(stickman);
            scenes.add(index, stickman);
        }

        public int getCount() {
            return scenes.size();
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Gallery.LayoutParams lp = new Gallery.LayoutParams(100, 100);
            StickmanView stickmanView = new StickmanView(SceneList.this);
            stickmanView.setClickable(false);
            stickmanView.setLayoutParams(lp);
            stickmanView.setmStickman(scenes.get(i));
            return stickmanView;
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.share:
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
