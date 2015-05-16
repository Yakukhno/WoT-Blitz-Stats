package com.example.ivan.wotblitzstats;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class StatsActivity extends ActionBarActivity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        int number = getIntent().getIntExtra("battles", -1);
        int wins = getIntent().getIntExtra("wins", -1);

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("battles: " + number);
        arrayList.add("wins: " + wins);

        list = (ListView) findViewById(R.id.list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.my_list, arrayList);

        list.setAdapter(adapter);

    }
}
