package com.apc.cng_hpcl.splashscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.apc.cng_hpcl.MainActivity;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.ui.GailActivity;

public class LandingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen


        setContentView(R.layout.activity_landing_page);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.landpage);
        // Implement it's on click listener.
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                startActivity(intent);
            }
        });
        Intent intent = new Intent(this, GailActivity.class);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(intent);
                finish();
            },1500);

    }
}