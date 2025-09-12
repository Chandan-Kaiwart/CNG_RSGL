package com.apc.cng_hpcl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.apc.cng_hpcl.home.HomeAdmin;
import com.apc.cng_hpcl.home.HomeManager;
import com.apc.cng_hpcl.home.HomeOperator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen


        setContentView(R.layout.activity_main);
        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);


        boolean a = sh.getBoolean("isLoggedIn", false);
        boolean isDBS = sh.getBoolean("isDbs", false);
        String usernameJO = sh.getString("username", "");
        String station = sh.getString("station", "");

        if(a){
            String emp_type = usernameJO.toLowerCase().substring(0, 3);
            switch (emp_type) {
                case "ope":
                    Intent intent = new Intent(this, HomeOperator.class);
                    intent.putExtra("username", usernameJO);
                    intent.putExtra("station", station);
                    intent.putExtra("isDbs",isDBS);
                    startActivity(intent);
                   finish();

                    break;
                case "man":
                    Intent intent1 = new Intent(this, HomeManager.class);
                    intent1.putExtra("username", usernameJO);
                    intent1.putExtra("station", station);
                    intent1.putExtra("isDbs",isDBS);
                    startActivity(intent1);
                    finish();
                    break;
                case "adm":

                    Intent intent2 = new Intent(this, HomeAdmin.class);
                    intent2.putExtra("username", usernameJO);
                    startActivity(intent2);
                   finish();

                    break;

            }
        }
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
               R.id.navigation_login,R.id.navigation_driver,R.id.speech_frag)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

}