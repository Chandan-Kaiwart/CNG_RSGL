package com.apc.cng_hpcl.home.master.subtabs.mothergasstation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.master.MasterAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MotherGasStation extends AppCompatActivity {
    RecyclerView dataList;
    List<String> titles;
    List<Integer> images;
    MotherGasStationAdapter adapter;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mother_gas_station);
        ActionBar actionBar = getSupportActionBar();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");

        }
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        dataList = findViewById(R.id.dataList);

        titles = new ArrayList<>();
        images = new ArrayList<>();
//
//        titles.add("Register Mother Gas Station ");
//        titles.add("View/Edit Mother Gas Station ");

        titles.add("Register Station ");
        titles.add("View/Edit Station ");



        images.add(R.drawable.reg);
        images.add(R.drawable.edit);



        adapter = new MotherGasStationAdapter(this,titles,images);

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