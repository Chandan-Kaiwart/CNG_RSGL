package com.apc.cng_hpcl.home.master.subtabs.daughterboosterstation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MotherGasStationAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DaughterBoosterStation extends AppCompatActivity {
    RecyclerView dataList;
    List<String> titles;
    List<Integer> images;
    DaughterBoosterStationAdapter adapter;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daughter_booster_sation);
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

        titles.add("Register Daughter Booster Station ");
        titles.add("View/Edit Daughter Booster Station ");



        images.add(R.drawable.reg);
        images.add(R.drawable.edit);



        adapter = new DaughterBoosterStationAdapter(this,titles,images);

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