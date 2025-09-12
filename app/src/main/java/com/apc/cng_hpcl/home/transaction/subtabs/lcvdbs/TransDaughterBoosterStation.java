package com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.HomeAdmin;
import com.apc.cng_hpcl.home.transaction.LCVModel;
import com.apc.cng_hpcl.home.transaction.MgsDbsModel;
import com.apc.cng_hpcl.home.transaction.Transaction;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.home.transaction.subtabs.TransCityGateStation.RequestPermissionCode;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;
import static com.apc.cng_hpcl.util.Constant.Station_List;

public class TransDaughterBoosterStation extends AppCompatActivity {


    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    //    private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
    public static final String URL_NOTIFY_STATUS = BASE_URL_URL + "readDBSNotifyStatus";
    public static final String URL_NOTIFY1 = BASE_URL_URL + "notifydbs1";

    public static final String URL_DBS = BASE_URL_URL + "insertDBS";
    public static final String URL_LCV = BASE_URL_URL + "readLCV";
    public static final String URL_MgsDbs = BASE_URL_URL + "readMgsDbs";
    List<String> dbsIds=new ArrayList<>();

    private static final String BASE_URL_URL2 = BASE_URL + "mgs_dbs_read_transaction.php?apicall=";
    public static final String URL_READ_NOTE = BASE_URL_URL2 + "readNotification";
    char tmp = 0x00B0;
    String status = "Pending";
    Spinner select_mgs, select_lcv, select_dbs;
    Button notify_mgs;
    TextView lcv_num2, lcv_num;
    Timestamp prevRead, timestamp;
    long differenceInMilliSeconds, differenceInHours,minutes;
    int seconds;
    //    String[] mgs = {"MGS001", "MGS002", "MGS003"};
//    String[] dbs = {"DBS001", "DBS002", "DBS003"};
//    String[] lcv = {"DL-1MA-5353", "HR-38AB-9008", "HR-38AB-0291", "HR-38AB-35216", "DL-1MA-5216", "UP-17AT-7351", "HR-38AB-7669", "DL-1MA-6137", "DL-1MA-6103",
//            "DL-1MA-4669", "DL-1MA-6029", "HR-63E-6118", "DL-1MA-3661", "HR-63E-5616", "DL-1MA-4638", "HR-63E-1684"};
    String username;
    private ArrayList<LCVModel> lcvModelArrayList;
    ArrayList<String> lcv = new ArrayList<>();
    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> mgs = new ArrayList<>();
    ArrayList<String> dbs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_daughter_booster_station);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            Log.e(TAG, "Username from Transaction to DBS=" + username);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        notify_mgs = findViewById(R.id.notify_mgs);
        lcv_num = findViewById(R.id.lcv_num);


        select_mgs = findViewById(R.id.select_mgs);
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(TransDaughterBoosterStation.this, android.R.layout.simple_spinner_dropdown_item, mgs);
//        select_mgs.setAdapter(adapter1);

        select_dbs = findViewById(R.id.select_dbs);
//        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(TransDaughterBoosterStation.this, android.R.layout.simple_spinner_dropdown_item, dbs);
//        select_dbs.setAdapter(adapter3);


        select_lcv = findViewById(R.id.select_lcv);
        readMgsDbs();
        readLCV();
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(TransDaughterBoosterStation.this, android.R.layout.simple_spinner_dropdown_item, lcv);
//        select_lcv.setAdapter(adapter2);

//        select_mgs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        select_lcv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                lcv_num.setText(select_lcv.getSelectedItem().toString());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        notify_mgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readNotification();
//                getMgrResponse();
//                notifymsg();
//                insertData();


            }
        });


    }

    private void readNotification() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String lcv_num = select_lcv.getSelectedItem().toString().trim();
        String dbs_id = dbsIds.get(select_dbs.getSelectedItemPosition());
        String mgs_id = "NA";
        if (!( lcv_num.equals("NA") || dbs_id.equals("NA"))) {
            StringRequest request = new StringRequest(Request.Method.POST, URL_READ_NOTE, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(TAG, "getNotificationResponse = " + response);
                        JSONObject jsonObject = new JSONObject(response);

                        JSONObject myJsonObject = new JSONObject(response);


                        jsonObject.getString("status");
                        String flag = jsonObject.getString("flag");
                        String noteStatus = jsonObject.getString("status");
                        String create_date = jsonObject.getString("create_date");
                        Log.e(TAG, "Flag,NoteStatus,Create_date=" + flag + noteStatus + create_date);
                        if ( flag.equals("3")&& noteStatus.equals("Approved") ) {
                            Log.e(TAG, "In if Third transaction MGS");
                            notifymsg();
                             insertData();

                        }
                        else if (flag.equals("1") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval Before Filling Cascade", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("2") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval For Filling Cascade", Toast.LENGTH_SHORT).show();

                        }


                        else if (flag.equals("3") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS After Filling");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval After Filling Cascade", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("4") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending Before Emptying");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval For Before Emptying Cascade", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("5") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending DBS After Emptying");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval For Emptying Cascade", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("6") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval After Emptying Cascade", Toast.LENGTH_SHORT).show();

                        }

                        else if ((flag.equals("1")||flag.equals("2")) && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Approved First Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num);
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(TransDaughterBoosterStation.this, TransMotherGasStation.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }

                        else if ((flag.equals("4") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(TransDaughterBoosterStation.this, DBS_Before_Emptying.class);

//                            Intent intent = new Intent(TransDaughterBoosterStation.this, DBS_Before_Emptying_MFM.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        else if ((flag.equals("5") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(TransDaughterBoosterStation.this, DBS_After_Emptying.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }



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
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    Log.e(TAG, "Notification_MGS=" + mgs_id);
                    Log.e(TAG, "Notification_LCV=" + lcv_num);
                    Log.e(TAG, "Notification_DBS=" + dbs_id);
                    params.put("Notification_LCV", lcv_num);
                    params.put("Notification_MGS", mgs_id);
                    params.put("Notification_DBS", dbs_id);


                    return params;
                }
            };

            queue.add(request);
        } else {
            Toast.makeText(TransDaughterBoosterStation.this, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }

    }

    private void readLCV() {
//        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_LCV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.e("strrrrr", ">>" + response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            if (obj.optString("status").equals("true")) {

                                lcvModelArrayList = new ArrayList<>();
                                JSONArray dataArray = obj.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    LCVModel lcvModel = new LCVModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);

                                    lcvModel.setLcv_Num(dataobj.getString("Lcv_Num"));
                                    lcvModel.setLcv_Registered_To(dataobj.getString("Lcv_Registered_To"));
                                    lcvModel.setVechicle_Type(dataobj.getString("Vechicle_Type"));
                                    lcvModel.setChassis_Num(dataobj.getString("Chassis_Num"));

                                    lcvModelArrayList.add(lcvModel);

                                }
                                lcv.add(0, "NA");
                                for (int i = 0; i < lcvModelArrayList.size(); i++) {
                                    lcv.add(lcvModelArrayList.get(i).getLcv_Num());
                                }

                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(TransDaughterBoosterStation.this, simple_spinner_item, lcv);
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                select_lcv.setAdapter(spinnerArrayAdapter);
//                                removeSimpleProgressDialog();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        requestQueue.add(stringRequest);
    }
    private void readMgsDbs() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Station_List,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.e("strrrrr", ">>" + response);

                        try {



        /*                    JSONObject obj = new JSONObject(response);
                            if (obj.optString("status").equals("true")) {

                                mgsdbsModelArrayList = new ArrayList<>();
                                JSONArray dataArray = obj.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    MgsDbsModel mgsDbsModel = new MgsDbsModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);

                                    mgsDbsModel.setStation_Id(dataobj.getString("Station_Id"));
                                    mgsDbsModel.setMgsId(dataobj.getString("mgsId"));


                                    mgsdbsModelArrayList.add(mgsDbsModel);

                                }
                                dbs.add(0, "NA");
                                mgs.add(0, "NA");
                                for (int i = 0; i < mgsdbsModelArrayList.size(); i++) {
                                    String stationId = mgsdbsModelArrayList.get(i).getStation_Id().toLowerCase().substring(0, 3);
//                                    if (stationId.equals("dbs")) {
                                        dbs.add(mgsdbsModelArrayList.get(i).getStation_Id());
//                                    }
                                    if (!mgsdbsModelArrayList.get(i).getMgsId().equals("")) {
                                        String mgsId = mgsdbsModelArrayList.get(i).getMgsId().toLowerCase().substring(0, 3);
//                                        if (mgsId.equals("mgs")) {
                                            mgs.add(mgsdbsModelArrayList.get(i).getMgsId());
//                                        }
                                    }
                                }

                                ArrayAdapter<String> spinnerArrayAdapterMgs = new ArrayAdapter<String>(TransMotherGasStation.this, simple_spinner_item, mgs);
                                spinnerArrayAdapterMgs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                select_mgs.setAdapter(spinnerArrayAdapterMgs);

                                ArrayAdapter<String> spinnerArrayAdapterDbs = new ArrayAdapter<String>(TransMotherGasStation.this, simple_spinner_item, dbs);
                                spinnerArrayAdapterDbs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                select_dbs.setAdapter(spinnerArrayAdapterDbs);
//                                removeSimpleProgressDialog();

                            }*/
                            JSONArray arr=new JSONArray(response);
                            //   mgs.clear();
                            dbs.add(0, "NA");
                            dbsIds.add(0, "NA");

                            for(int i=0;i< arr.length();i++){
                                JSONObject obj=arr.getJSONObject(i);
                                if(obj.getString("Station_type").contains("Daughter")){
                                    dbs.add(obj.getString("Station_Name")+"("+obj.getString("Station_Id")+")");
                                    dbsIds.add(obj.getString("Station_Id"));
                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapterMgs = new ArrayAdapter<String>(TransDaughterBoosterStation.this, simple_spinner_item, dbs);
                            spinnerArrayAdapterMgs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            select_dbs.setAdapter(spinnerArrayAdapterMgs);

                        } catch (Exception e) {
                            Toast.makeText(TransDaughterBoosterStation.this,e.toString(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        requestQueue.add(stringRequest);
    }

/*
    private void readMgsDbs() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_MgsDbs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.e("strrrrr", ">>" + response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            if (obj.optString("status").equals("true")) {

                                mgsdbsModelArrayList = new ArrayList<>();
                                JSONArray dataArray = obj.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    MgsDbsModel mgsDbsModel = new MgsDbsModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);

                                    mgsDbsModel.setStation_Id(dataobj.getString("Station_Id"));
                                    mgsDbsModel.setMgsId(dataobj.getString("mgsId"));


                                    mgsdbsModelArrayList.add(mgsDbsModel);

                                }
                                dbs.add(0, "NA");
                                mgs.add(0, "NA");

                                for (int i = 0; i < mgsdbsModelArrayList.size(); i++) {
                                    String stationId = mgsdbsModelArrayList.get(i).getStation_Id().toLowerCase().substring(0, 3);
//                                    if (stationId.equals("dbs")) {
                                    dbs.add(mgsdbsModelArrayList.get(i).getStation_Name());
//                                    }
                                    if (!mgsdbsModelArrayList.get(i).getMgsId().equals("")) {
                                        String mgsId = mgsdbsModelArrayList.get(i).getMgsId().toLowerCase().substring(0, 3);
//                                        if (mgsId.equals("mgs")) {
                                        mgs.add(mgsdbsModelArrayList.get(i).getMgsId());
//                                        }
                                    }
                                }

                                ArrayAdapter<String> spinnerArrayAdapterMgs = new ArrayAdapter<String>(TransDaughterBoosterStation.this, simple_spinner_item, mgs);
                                spinnerArrayAdapterMgs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                select_mgs.setAdapter(spinnerArrayAdapterMgs);

                                ArrayAdapter<String> spinnerArrayAdapterDbs = new ArrayAdapter<String>(TransDaughterBoosterStation.this, simple_spinner_item, dbs);
                                spinnerArrayAdapterDbs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                select_dbs.setAdapter(spinnerArrayAdapterDbs);
//                                removeSimpleProgressDialog();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        requestQueue.add(stringRequest);
    }
*/

    private void getMgrResponse() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String lcv_num = select_lcv.getSelectedItem().toString().trim();
        String dbs_id = select_dbs.getSelectedItem().toString().trim();
        String mgs_id = "NA";
        if(!(lcv_num.equals("NA") || dbs_id.equals("NA") )) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_NOTIFY_STATUS, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject myJsonObject = new JSONObject(response);


                    jsonObject.getString("status");

                    String noteStatus = jsonObject.getString("status");
                    Log.e(TAG, "Status" + noteStatus);
                    String create_date = jsonObject.getString("create_date");
                    if (!create_date.equals("null")) {
                        prevRead = Timestamp.valueOf(create_date);
                        Log.e(TAG, "Status=" + noteStatus + "PrevRead=" + prevRead);
                        timestamp = new Timestamp(System.currentTimeMillis());
                        // Calculating the difference in milliseconds
                        differenceInMilliSeconds
                                = timestamp.getTime() - prevRead.getTime();
                        seconds = (int) differenceInMilliSeconds / 1000;
                        // Calculating the difference in Hours
                        differenceInHours = (seconds / 3600);
                        minutes = (differenceInMilliSeconds / 1000) / 60;
                        Log.e(TAG, "TimeDifference in min= " + minutes + "\nTime Greater Than 10min" + (minutes > 10));

                        Log.e(TAG, "TimeDifference= " + differenceInHours + "\nTime Greater Than 3hrs" + (differenceInHours > 3));
                    }
                    if (noteStatus.equals("null")) {
                        notifymsg();
                        insertData();
                    }
                    else if (minutes > 150) {
                        Log.e(TAG, "In if diff hours");
                        notifymsg();
                        insertData();
                        Toast.makeText(getApplicationContext(), "Waiting for Manager Approvel", Toast.LENGTH_SHORT).show();
                    }
                    else if (noteStatus.equals("Approved")) {

                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        bundle.putString("lcv_num", lcv_num);
                        bundle.putString("dbs_id", dbs_id);
                        Intent intent = new Intent(TransDaughterBoosterStation.this, DBS_Before_Emptying.class);

//                        Intent intent = new Intent(TransDaughterBoosterStation.this, DBS_Before_Emptying_MFM.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else if (noteStatus.equals("Pending")) {
                        Toast.makeText(getApplicationContext(), "Waiting for Manager Approvel", Toast.LENGTH_SHORT).show();
                    }
//                    else if (differenceInHours < 3) {
//                        Log.e(TAG, "In if diff hours");
//
//                        Toast.makeText(getApplicationContext(), "Your transit time is less than expected", Toast.LENGTH_SHORT).show();
//                    }

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
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();


                params.put("Notification_LCV", lcv_num);
                params.put("Notification_MGS", mgs_id);
                params.put("Notification_DBS", dbs_id);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(TransDaughterBoosterStation.this);
        queue.add(request);}
        else {
            Toast.makeText(TransDaughterBoosterStation.this, "Please select valid LCV and DBS station ", Toast.LENGTH_SHORT).show();
        }

    }

    public void insertData() {


        final String lcv_id = select_lcv.getSelectedItem().toString();
        final String station_id = dbsIds.get(select_dbs.getSelectedItemPosition());
        final String lcv_from_mgs_to_dbs = "4";

        if (!(station_id.equals("NA") || lcv_id.equals("NA"))) {

            StringRequest request = new StringRequest(Request.Method.POST, URL_DBS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("message") == null) {
                                    Toast.makeText(TransDaughterBoosterStation.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(TransDaughterBoosterStation.this, message, Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(TransDaughterBoosterStation.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
                }
            }

            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();

                    Log.e(TAG, "LCVID=" + lcv_id + "DBSStation=" + station_id + "LCV_from_MGS_To_DBS=" + lcv_from_mgs_to_dbs);
                    String lcv_status=lcv_id+" waiting at"+station_id;
                    params.put("lcv_status",lcv_status );
                    params.put("lcv_id", lcv_id);
                    params.put("dbs_station_id", station_id);
                    params.put("operator_id_at_dbs", username);

                    params.put("lcv_from_mgs_to_dbs", lcv_from_mgs_to_dbs);


                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(TransDaughterBoosterStation.this);
            requestQueue.add(request);
        } else {
            Toast.makeText(TransDaughterBoosterStation.this, "Please select valid LCV and DBS station ", Toast.LENGTH_SHORT).show();
        }

    }


    public void notifymsg() {


        final String MGS = "NA";
        final String LCV = select_lcv.getSelectedItem().toString();
        final String DBS =dbsIds.get(select_dbs.getSelectedItemPosition());
        final String operator_id = username;
        Log.e("DBS>>", "operator_id in insert Notify =" + DBS);

        final String Message = LCV + " " + "Reached at Daughter Booster Station" + ":" + DBS;
        StringRequest request = new StringRequest(Request.Method.POST, URL_NOTIFY1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(TransDaughterBoosterStation.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(TransDaughterBoosterStation.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TransDaughterBoosterStation.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                params.put("Notification_LCV", LCV);
                params.put("Notification_MGS", MGS);
                params.put("Notification_DBS", DBS);
                params.put("Notification_Message", Message);
                params.put("status", status);
                params.put("flag", "4");
                params.put("operator_id", operator_id);
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(TransDaughterBoosterStation.this);
        requestQueue.add(request);


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