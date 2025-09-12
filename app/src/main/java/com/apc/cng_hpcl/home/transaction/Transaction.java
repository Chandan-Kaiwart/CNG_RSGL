package com.apc.cng_hpcl.home.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.HomeAdmin;
import com.apc.cng_hpcl.home.HomeManager;
import com.apc.cng_hpcl.home.HomeOperator;
import com.apc.cng_hpcl.home.master.MasterAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

import static com.apc.cng_hpcl.util.Constant.Get_Mapping;

import org.json.JSONException;
import org.json.JSONObject;

public class Transaction extends AppCompatActivity {
    RecyclerView dataList;
    List<String> titles;
    List<Integer> images;
    TransactionAdapter adapter;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            Log.e(TAG, "username=" + username);

        }
//        this.setTitle("Welcome " + username);
        dataList = findViewById(R.id.dataList);
        titles = new ArrayList<>();
        images = new ArrayList<>();
        getMapping();
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

    private void getMapping() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, Get_Mapping+username, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("MAP>>", response);
                    JSONObject obj=new JSONObject(response);
                    titles.add("City Gate Sation");
                    String stat="NA";
                    images.add(R.drawable.mgs);
                    if(!username.contains("adm")){

                    if(!obj.getString("note_approver_dbs").equals("NA")){
                        titles.add("LCV and DBS");
                        images.add(R.drawable.lcv_vector);
                        stat=obj.getString("note_approver_dbs");

                    }
                    else if(!obj.getString("note_approver_mgs").equals("NA")){
                        titles.add("MGS and LCV");
                        images.add(R.drawable.city_gas_station);
                        stat=obj.getString("note_approver_mgs");
                    }}
                    else{
                        titles.add("MGS and LCV");
                        images.add(R.drawable.city_gas_station);
                        titles.add("LCV and DBS");
                        images.add(R.drawable.lcv_vector);
                        stat=obj.getString("note_approver_mgs");

                    }
                    titles.add("Dispenser and Cascade at DBS");
                    images.add(R.drawable.dbs);
                    adapter = new TransactionAdapter(stat,Transaction.this,titles,images,username);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(Transaction.this,2,GridLayoutManager.VERTICAL,false);
                    dataList.setLayoutManager(gridLayoutManager);
                    dataList.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Transaction.this, "Failed to get data" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }



}