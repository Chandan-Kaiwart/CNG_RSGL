package com.apc.cng_hpcl.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.MainActivity;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.newTrans.NewTransActivity;
import com.apc.cng_hpcl.home.notification.Notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class HomeOperator extends AppCompatActivity {
    RecyclerView dataList;
    List<String> titles;
    List<Integer> images;
    AdapterOperator adapter;
    String username,user_name,station;
    boolean isDbs=false;
    private static final String BASE_URL_URL = BASE_URL + "luag_login.php?apicall=";
    public static final String URL_LOGIN = BASE_URL_URL + "update_status";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_employee);
        dataList = findViewById(R.id.dataList);

        titles = new ArrayList<>();
        images = new ArrayList<>();

        titles.add("Company/User Reg and Admin");
        titles.add("स्टेशन पर गाडी क लिए रीडिंग\n(Gas Transaction)");
        titles.add("Dispenser Reading");
        titles.add("MGS/DBS/LCV Master");
        titles.add("Molar Mass/Gas Density");
        titles.add("Tracking of LCV");
        titles.add("Analytical Reports");
        titles.add("Gas Reconciliation");
        titles.add("Stationary Cascade");
        titles.add("Notifications");
//        titles.add("Camera");
//        titles.add("ImageUpload");

        images.add(R.drawable.admin);
        images.add(R.drawable.transaction);
        images.add(R.drawable.dbs);
        images.add(R.drawable.master);
        images.add(R.drawable.dbs);
        images.add(R.drawable.tracking);
        images.add(R.drawable.analytics);
        images.add(R.drawable.reconciliation);
        images.add(R.drawable.city_gas_station);
        images.add(R.drawable.ic_notifications_black_24dp);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user_name = extras.getString("username");
            station = extras.getString("station");
            isDbs=extras.getBoolean("isDbs");

            username=user_name.substring(3);
            username = String.valueOf(username.charAt(0)).toUpperCase() + username.substring(1, username.length());

        }

        this.setTitle("Welcome " + username);
        Log.d("station", "onCreate: "+station);
        adapter = new AdapterOperator(this, titles, images,user_name,station,isDbs);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
/*        Intent intent = new Intent(this, Notification.class);
        intent.putExtra("username", username);
        intent.putExtra("station", station);
        intent.putExtra("isDbs", isDbs);


            startActivity(intent);*/
        Intent intent = new Intent(this, NewTransActivity.class);
        intent.putExtra("username", username);
        finish();
        startActivity(intent);
        dataList.setLayoutManager(gridLayoutManager);
        dataList.setAdapter(adapter);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        // first parameter is the file for icon and second one is menu
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // We are using switch case because multiple icons can be kept
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("username", "");
            myEdit.putString("station","");
            myEdit.putString("lcv","");

            myEdit.putBoolean("isDbs",false);
            myEdit.putBoolean("isLoggedIn",false);
            myEdit.apply();
            updateStatus();
            Intent intent = new Intent(HomeOperator.this,
                    MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
//            moveTaskToBack(true);
//            Process.killProcess(Process.myPid());
//            System.exit(1);
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                updateStatus();
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateStatus() {
        Log.e(TAG, "In Logout Update status ");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String userName = user_name;
        Log.e(TAG, "username="+userName);

        StringRequest request = new StringRequest(Request.Method.POST, URL_LOGIN, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);

//                    Log.e(TAG, "username = " + jsonObject.getString("username"));

//                    boolean success = jsonObject.getBoolean("success");
                    String success = jsonObject.getString("success");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to get data" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", user_name);
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

}


