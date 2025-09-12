package com.apc.cng_hpcl.home.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.HomeManager;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class Notification extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private ArrayList<DataModel> dataModels;
    private FloatingActionButton fab;
    DataModel dataModel;
    boolean isDbs;
    String username,mgsId,dbsId,stationId;
    String userStation;
    SwipeRefreshLayout swipeRefreshLayout;
    private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
    private static final String URL_NOTE = BASE_URL + "notce.php";
    public static final String URL_NOTIFY_STATUS = BASE_URL_URL + "readNotificationApprover";

    @Override
    public void onBackPressed() {
        //   super.onBackPressed();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification2);
        dataModels = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyle_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setRecyclerViewData(); //adding data to array list

//        fab.setOnClickListener(onAddingListener());
        ActionBar actionBar = getSupportActionBar();
        Intent thisIntent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            userStation = extras.getString("station");
            isDbs=extras.getBoolean("isDbs");

        }
      //  recyclerView.invalidate();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(Notification.this, Notification.class);
////                startActivity(intent);
////                finish();
//                startActivity(thisIntent);
//                finish();
//            }
//        });
     //   setRecyclerViewData();
        //readNotificationApprover();
        setRecyclerViewData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecyclerViewData();
             //   adapter.notifyDataSetChanged();
            }
        });

    }



    private void setRecyclerViewData() {
        swipeRefreshLayout.setRefreshing(true);


        //Create json array for filter
        JSONArray array = new JSONArray();

        //Create json objects for two filter Ids
        JSONObject jsonParam = new JSONObject();
        try {
            //Add string params
            jsonParam.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        array.put(jsonParam);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, URL_NOTE, null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {
                        swipeRefreshLayout.setRefreshing(false);

                        Log.d("NOTI>>", response.toString());

                        dataModels.clear();
                        try {/*
                            if(response.length()<1){
                                Toast.makeText(Notification.this,"No new notification !",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Notification.this, HomeManager.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            }*/

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = response.getJSONObject(i);

                                String id = object.getString("Notification_Id");
                                String date = object.getString("Notification_Date");
                                String time = object.getString("Notification_Time");
                                String lcvnum = object.getString("Notification_LCV");
                                String mgs = object.getString("Notification_MGS");
                                String dbs = object.getString("Notification_DBS");
                                String msg = object.getString("Notification_Message");
                                String operator = object.getString("operator_id");
                                String status = object.getString("status");
                                String flag = object.getString("flag");
                                String create_date = object.getString("create_date");
                                String emp_type = username.toLowerCase().substring(0,3);

                                Log.d("DBS>>flag", "onResponse: "+flag);
                                if (emp_type.equals("ope") && operator.equals(username)){
                                    if (status.equals("Approved")) {
                                        dataModel = new DataModel(id, msg, date, time, lcvnum, mgs, dbs, status, create_date,flag);
                                        dataModels.add(dataModel);
//                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                else  if (emp_type.equals("adm")) {
                                    if (status.equals("Pending")) {
                                        dataModel = new DataModel(id, msg, date, time, lcvnum, mgs, dbs, status, create_date,flag);
                                        dataModels.add(dataModel);
//                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                else {
                                    Log.d("DBS>>>1", "onResponse: "+userStation);
                                    Log.d("DBS>>>2", "onResponse: "+dbs);
                                    Log.d("DBS>>>3", "onResponse: "+mgs);

                                    if(Integer.parseInt(flag)==1||Integer.parseInt(flag)==2||Integer.parseInt(flag)==3){
                                        if(status.equals("Pending") && (mgs.equals(userStation))){
                                            dataModel = new DataModel(id, msg, date, time, lcvnum, mgs, dbs, status, create_date,flag);
                                            dataModels.add(dataModel);
                                        }
                                    }
                                    else{
                                        if(status.equals("Pending") && (dbs.equals(userStation))){
                                            dataModel = new DataModel(id, msg, date, time, lcvnum, mgs, dbs, status, create_date,flag);
                                            dataModels.add(dataModel);
                                        }
                                    }



                                }


                            }
                            adapter = new MyAdapter(Notification.this, dataModels,username);
                            recyclerView.setAdapter(adapter);



//                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Notification.this, "Data Inserted Successfully ", Toast.LENGTH_SHORT).show();
            }
        })
//        {
//
//            //Important part to convert response to JSON Array Again
//            @Override
//            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
//                String responseString;
//                JSONArray array = new JSONArray();
//                if (response != null) {
//
//                    try {
//                        responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                        JSONObject obj = new JSONObject(responseString);
//                        (array).put(obj);
//                    } catch (Exception ex) {
//                    }
//                }
//                //return array;
//                return Response.success(array, HttpHeaderParser.parseCacheHeaders(response));
//            }
//        }
                ;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


//        dataModels.add(new DataModel("16/07/21", "11:54 AM:",  "LCV DL AB 0987","HPCL","Kargil","LCV reached at HPCL MGS"));
//        dataModels.add(new DataModel("16/07/21","10:55 AM:",  "LCV DL AB 0987","HPCL","Kargil","LCV leaves from Kargil DBS "));
//        dataModels.add(new DataModel("16/07/21"," 09:54 PM:",  "LCV DL AB 0987 ","HPCL","Kargil","Reading captured after emptying LCV at Kargil DBS"));
//        dataModels.add(new DataModel("15/07/21 ","11:54 AM:",  "LCV DL AB 0987 ","HPCL","Kargil","Readings captured before emptying LCV at DBS"));
//        dataModels.add(new DataModel("14/07/21 ","08:54 AM:",  "DL AB 0987 ","HPCL","Kargil","LCV reached at kargil DBS"));
//        dataModels.add(new DataModel("13/07/21 ","10:54 PM:",  "LCV DL AB 0987 ","HPCL","Kargil","started its journey from HPCL MGS to Kargil "));
//

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
