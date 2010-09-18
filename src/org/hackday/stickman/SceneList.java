package org.hackday.stickman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Toast;

import java.util.LinkedList;

public class SceneList extends Activity implements View.OnClickListener {
    private SceneAdapter adapter;
    private Gallery gallery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenProps.initialize(this);
        setContentView(R.layout.scene_list);

        gallery = (Gallery) findViewById(R.id.gallery);
        adapter = new SceneAdapter();
        adapter.add(new Stickman());
        gallery.setAdapter(adapter);

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
            System.out.println("SceneList$SceneAdapter.getView");
            StickmanView stickmanView = new StickmanView(SceneList.this);
//            stickmanView.setmStickman(scenes.get(i));
            return stickmanView;
        }

    }

}
