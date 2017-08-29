package com.uiyeestudio.scan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.uiyeestudio.scan.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    private ListView mListView;
    private SimpleAdapter mSimpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = (ListView) findViewById(R.id.layout_list);
        mSimpleAdapter = new SimpleAdapter(this, this.getItem(), R.layout.list_item, new String[]{"title"}, new int[]{R.id.list_item_title});

        mListView.setAdapter(mSimpleAdapter);

    }

    private ArrayList<HashMap<String, Object>> getItem() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        for (int i=0; i< 20; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("title", "hello world " + i);

            list.add(map);
        }

        return list;
    }
}
