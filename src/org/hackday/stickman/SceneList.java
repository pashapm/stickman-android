package org.hackday.stickman;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Toast;

import java.util.LinkedList;

public class SceneList extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener, View.OnTouchListener {
    private SceneAdapter adapter;
    private Gallery gallery;
    private StickmanView stickmanView;

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
        stickmanView.setOnTouchListener(this);

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);

    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add:
                adapter.scenes.add(new Stickman());
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
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        Stickman s = adapter.scenes.get(i);
//        stickmanView.setmStickman(s);
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

//    @Override
//	public boolean onTouchEvent(MotionEvent motionEvent) {
//        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                stickmanView.move((int) motionEvent.getX(), (int) motionEvent.getY());
//                break;
//            case MotionEvent.ACTION_DOWN:
//                stickmanView.setLastXY((int) motionEvent.getX(), (int) motionEvent.getY());
//                break;
//            case MotionEvent.ACTION_UP:
//                stickmanView.setLastXY(0, 0);
//                break;
//
//        }
//        return true;
//    }

    private class SceneAdapter extends BaseAdapter {
        private LinkedList<Stickman> scenes = new LinkedList<Stickman>();

        public void add(Stickman stickman) {
            scenes.add(stickman);
        }

        public void add(int index, Stickman stickman) {
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
            Gallery.LayoutParams lp = new Gallery.LayoutParams(75, 100);
            StickmanView stickmanView = new StickmanView(SceneList.this);
            stickmanView.setLayoutParams(lp);
            stickmanView.setmStickman(scenes.get(i));
            return stickmanView;
        }
    }
}
