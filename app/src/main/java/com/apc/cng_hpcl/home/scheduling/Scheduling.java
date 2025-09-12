package com.apc.cng_hpcl.home.scheduling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.apc.cng_hpcl.R;

import java.util.ArrayList;
import java.util.List;

public class Scheduling extends AppCompatActivity {
    RecyclerView dataList;
    List<String> titles;
    List<Integer> images;
    SchedularAdapter adapter;
    String username,station;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling);
        // showing the back button in action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            station=extras.getString("station");
        }
//        this.setTitle("Welcome " + username);

        dataList = findViewById(R.id.dataList);

        titles = new ArrayList<>();
        images = new ArrayList<>();

        titles.add("Stationary Cascade");
        titles.add("Stationary Cascade Gas Info");



        images.add(R.drawable.city_gas_station);
        images.add(R.drawable.city_gas_station);


        adapter = new SchedularAdapter(this,titles,images,username,station);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        dataList.setLayoutManager(gridLayoutManager);
        dataList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}