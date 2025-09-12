package com.apc.cng_hpcl.home.analytics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.apc.cng_hpcl.R;

import java.util.ArrayList;
import java.util.List;

import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class Analytics extends AppCompatActivity {
    RecyclerView dataList;
    List<String> titles;
    List<Integer> images;
    AnalyticsAdapter adapter;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
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

        titles.add("Notification Lag");
        titles.add("Vehicle Turnaround Time");
        titles.add("Total Gas Loss between Stations");
//        titles.add("Transportation level");
//        titles.add("Test");


        images.add(R.drawable.ic_notifications_black_24dp);
        images.add(R.drawable.lcv_vector);
        images.add(R.drawable.analytics);
//        images.add(R.drawable.lcv_vector);
//        images.add(R.drawable.camera);


        adapter = new AnalyticsAdapter(this,titles,images);

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