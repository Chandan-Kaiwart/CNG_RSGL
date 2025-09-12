package com.apc.cng_hpcl.home.transaction.subtabs.mgslcv;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;
import static com.apc.cng_hpcl.util.Constant.Station_List;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.notification.DataModel;
import com.apc.cng_hpcl.home.transaction.LCVModel;
import com.apc.cng_hpcl.home.transaction.MgsDbsModel;
import com.apc.cng_hpcl.home.transaction.ReqAdapter;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_After_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransMotherGasStation extends AppCompatActivity {
    private ArrayList<DataModel> dataModels;
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    private static final String BASE_URL_URL2 = BASE_URL + "mgs_dbs_read_transaction.php?apicall=";
    public static final String URL_READ_NOTE = BASE_URL_URL2 + "readNotification";
    List<String> mgsIds=new ArrayList<>();
    RecyclerView reqRv;
    List<String> dbsIds=new ArrayList<>();
    int type=-1;

    public static final String URL_READ_TRANS = BASE_URL_URL2 + "readMGSDBSTransDetail";

    public static final String URL_NOTIFY1 = BASE_URL_URL + "notify1";
    public static final String URL_NOTIFY_STATUS = BASE_URL_URL + "readNotifyStatus";
    public static final String URL_LCV = BASE_URL_URL + "readLCV";
    public static final String URL_MgsDbs = BASE_URL_URL + "readMgsDbs";

    public static final String URL_MGS = BASE_URL_URL + "insertMGS";
    Timestamp prevRead, timestamp;
    long differenceInMilliSeconds, differenceInHours;
    Activity activity;
    long minutes;
    int seconds;
    //    String trans_flag,start_fill,end_fill,lcv_travel,start_empty,end_empty,trans_start,note_flag,note_status,note_start;
//    String flag,noteStatus,create_date,lcv_from_mgs_to_dbs,trans_create_date, start_fill_time,end_fill_time,mgs_to_dbs_reach_time,start_empty_time,end_empty_time;
    Spinner select_mgs, select_lcv, select_dbs;
    Button notify_mgs;
    private ArrayList<LCVModel> lcvModelArrayList;
    ArrayList<String> lcv = new ArrayList<>();
    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> mgs = new ArrayList<>();
    ArrayList<String> dbs = new ArrayList<>();
    private Spinner spinner;

    String username;
    TextView lcv_num2, lcv_num;
    SharedPreferences sharedpreferences;

    //    String[] mgs = {"MGS001", "MGS002", "MGS003"};
//    String[] dbs = {"DBS001", "DBS002", "DBS003"};
//    String[] lcv = {"DL-1MA-5353", "HR-38AB-9008", "HR-38AB-0291", "HR-38AB-35216", "DL-1MA-5216", "UP-17AT-7351", "HR-38AB-7669", "DL-1MA-6137", "DL-1MA-6103",
//            "DL-1MA-4669", "DL-1MA-6029", "HR-63E-6118", "DL-1MA-3661", "HR-63E-5616", "DL-1MA-4638", "HR-63E-1684"};
    String status = "Pending";
    String lcv_num_val, mgs_id, dbs_id,station_id;
    int lcvPosition, mgsPosition, dbsPosition;
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init((Application) getApplicationContext());
        setContentView(R.layout.activity_trans_mother_gas_station);
        reqRv=findViewById(R.id.reqRv);
        reqRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        reqRv.setHasFixedSize(true);
        dataModels=new ArrayList<>();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent thisIntent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            station_id=extras.getString("station_id");
            type=extras.getInt("type");
        }
        Log.e(TAG, "operator_id in MGS oncreate =" + username);
        notify_mgs = findViewById(R.id.notify_mgs);

        lcv_num2 = findViewById(R.id.lcv_num2);
        lcv_num = findViewById(R.id.lcv_num);


        select_mgs = findViewById(R.id.select_mgs);

        select_dbs = findViewById(R.id.select_dbs);

        select_lcv = findViewById(R.id.select_lcv);
     //   readMgsDbs();
     //   readLCV();

        getReqs();
        notify_mgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                notifymsg();
//                insertData();
                lcv_num_val = select_lcv.getSelectedItem().toString().trim();
                mgs_id = mgsIds.get(select_mgs.getSelectedItemPosition());
                dbs_id = dbsIds.get(select_dbs.getSelectedItemPosition());
//                getMgrResponse();
//                readTranasction();
                readNotification();
//                Log.e(TAG,"lcv_from_mgs_to_dbs"+lcv_from_mgs_to_dbs);


            }
        });
    }
    private void getReqs(){
        dataModels.clear();

        String s="msg_dbs_transaction.php?apicall=readFulfilledRequests";
        Log.d("reqs>>>", s);

        StringRequest jsonObjectRequest=new StringRequest(Request.Method.POST, s, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                Log.d("Res>>>", resp);

                try {
                    JSONObject response=new JSONObject(resp);
                    Log.d("Req>>>", response.toString());
                    JSONArray arr=response.getJSONArray("fulfilled_requests");
                    for (int i=0;i<arr.length();i++){
                       /* {
                            "date": "2023-02-20 17:20:18",
                                "dbs": "DBS1",
                                "MGS": "MGS123",
                                "LCV_Num": "DL1TEMP2021",
                                "Route_Duration": "3",
                                "Route_distance": "200",
                                "Route_description": "MGS132-DBS123",
                                "stage": "1"
                        }*/
                        JSONObject obj=arr.getJSONObject(i);
                        DataModel dm=new DataModel();
                        if(station_id.equals(obj.getString("dbs"))||station_id.equals(obj.getString("MGS"))){
                            dm.setLcvnum(obj.getString("LCV_Num"));
                            dm.setStatus(obj.getString("status"));
                            dm.setDbs(obj.getString("dbs"));
                            dm.setMgs(obj.getString("stage"));
                            dm.setMsg("");
                            dm.setDate(obj.getString("date"));
                            List<String> mgsStages=new ArrayList<>();
                            mgsStages.add("1");
                            mgsStages.add("2");
                            mgsStages.add("3");
                            List<String> dbsStages=new ArrayList<>();
                            dbsStages.add("4");
                            dbsStages.add("5");
                            dbsStages.add("6");
                            if(station_id.equals(obj.getString("MGS"))&&mgsStages.contains(dm.getMgs())){
                                dataModels.add(dm);

                            }
                           else if(station_id.equals(obj.getString("dbs"))&&dbsStages.contains(dm.getMgs())){
                                setTitle("Daughter Booster Station And LCV");

                                dataModels.add(dm);
                            }
                        }



                    }
                   ReqAdapter adapter = new ReqAdapter(TransMotherGasStation.this, dataModels,username,null);
                    reqRv.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){@Override
        protected Map<String, String> getParams() throws AuthFailureError {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("mgs_id", station_id);
            Log.d("MGS>>", station_id);
            return params;
        }};
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*48,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setShouldCache (false);
        Volley.newRequestQueue(TransMotherGasStation.this).add (jsonObjectRequest);
    }


    private void readNotification() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        if (!(station_id.equals("NA") || lcv_num_val.equals("NA"))) {
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
                       if (flag.equals("1") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("2") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval Before Filling", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("3") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval After Filling", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("4") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval Before Emptying", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("5") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval After Emptying", Toast.LENGTH_SHORT).show();

                        }


                        else if (flag.equals("1") && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Approved First Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", station_id);
                            bundle.putString("dbs_id", station_id);
                            Intent intent = new Intent(TransMotherGasStation.this, MGS_Before_Filling.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else if (flag.equals("2") && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Approved Second Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", station_id);
                            bundle.putString("dbs_id", station_id);
                           Intent intent = new Intent(TransMotherGasStation.this, MGS_After_Filling.class);

//                           Intent intent = new Intent(TransMotherGasStation.this, MGS_After_Filling_MFM.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else if ((flag.equals("3") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Third Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                           bundle.putString("mgs_id", station_id);
                           bundle.putString("dbs_id", station_id);
                            Intent intent = new Intent(TransMotherGasStation.this, TransDaughterBoosterStation.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }

                        else if ((flag.equals("4") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                           bundle.putString("mgs_id", station_id);
                           bundle.putString("dbs_id", station_id);
                           Intent intent = new Intent(TransMotherGasStation.this, DBS_Before_Emptying.class);

//                           Intent intent = new Intent(TransMotherGasStation.this, DBS_Before_Emptying_MFM.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        else if ((flag.equals("5") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                           bundle.putString("mgs_id", station_id);
                           bundle.putString("dbs_id", station_id);
                            Intent intent = new Intent(TransMotherGasStation.this, DBS_After_Emptying.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }  else  {
                            Log.e(TAG, "In if First transaction MGS");
                            notifymsg();
                            insertData();
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
                    Log.e(TAG, "Notification_LCV=" + lcv_num_val);
                    Log.e(TAG, "Notification_DBS=" + dbs_id);
                    params.put("Notification_LCV", lcv_num_val);
                    params.put("Notification_MGS", station_id);
                    params.put("Notification_DBS", station_id);


                    return params;
                }
            };

            queue.add(request);
        } else {
            Toast.makeText(TransMotherGasStation.this, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }

    }

    private void updatestatus() {
        String updateFlag="0";
        String updateStatus="Nullified";
        String message="Failed to record transaction";
        final String operator_id = username;

        Log.e(TAG, "operator_id in insert Notify =" + operator_id);


        StringRequest request = new StringRequest(Request.Method.POST, URL_NOTIFY1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e(TAG, "notifymsg Response = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(TransMotherGasStation.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(TransMotherGasStation.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TransMotherGasStation.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                params.put("Notification_LCV", lcv_num_val);
                params.put("Notification_MGS", station_id);
                params.put("Notification_DBS", station_id);
                params.put("Notification_Message", message);
                params.put("status", updateStatus);
                params.put("flag", updateFlag);
                params.put("operator_id", operator_id);


                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(TransMotherGasStation.this);
        requestQueue.add(request);

    }

    private void readTranasction() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        if (!(mgs_id.equals("NA") || lcv_num_val.equals("NA") || dbs_id.equals("NA"))) {
            StringRequest request = new StringRequest(Request.Method.POST, URL_READ_TRANS, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(TAG, "getTransactionResponse = " + response);
                        JSONObject jsonObject = new JSONObject(response);
//                        lcv_from_mgs_to_dbs = jsonObject.getString("lcv_from_mgs_to_dbs");
//                        start_fill_time = jsonObject.getString("start_fill_time");
//                        end_fill_time = jsonObject.getString("end_fill_time");
//                        mgs_to_dbs_reach_time = jsonObject.getString("mgs_to_dbs_reach_time");
//                        start_empty_time = jsonObject.getString("start_empty_time");
//                        end_empty_time = jsonObject.getString("end_empty_time");
//                        trans_create_date = jsonObject.getString("create_date");
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
                    Log.e(TAG, "station_id=" + station_id);
                    Log.e(TAG, "lcv_id=" + lcv_num_val);
                    Log.e(TAG, "dbs_station_id=" + station_id);
                    params.put("lcv_id", lcv_num_val);
                    params.put("station_id", station_id);
                    params.put("dbs_station_id", station_id);


                    return params;
                }
            };

            queue.add(request);
        } else {
            Toast.makeText(TransMotherGasStation.this, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
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

                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(TransMotherGasStation.this, simple_spinner_item, lcv);
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
                            mgs.add(0, "NA");
                            mgsIds.add(0, "NA");

                            for(int i=0;i< arr.length();i++){
                                JSONObject obj=arr.getJSONObject(i);
                                if(obj.getString("Station_type").contains("Mother")){
                                    mgs.add(obj.getString("Station_Name")+"("+obj.getString("Station_Id")+")");
                                    mgsIds.add(obj.getString("Station_Id"));
                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapterMgs = new ArrayAdapter<String>(TransMotherGasStation.this, simple_spinner_item, mgs);
                            spinnerArrayAdapterMgs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            select_mgs.setAdapter(spinnerArrayAdapterMgs);
                            select_mgs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i1, long l) {
                                    dbs.clear();
                                    dbsIds.clear();
                                    dbs.add(0, "NA");
                                    dbsIds.add(0, "NA");

                                    for(int i=1;i< arr.length();i++){
                                        try {

                                            JSONObject obj=arr.getJSONObject(i);
                                            if(obj.getString("Station_type").contains("Daughter") && obj.getString("mgsId").contains(mgsIds.get(i1))){
                                                dbs.add(obj.getString("Station_Name")+"("+obj.getString("Station_Id")+")");
                                                dbsIds.add(obj.getString("Station_Id"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    ArrayAdapter<String> spinnerArrayAdapterDbs = new ArrayAdapter<String>(TransMotherGasStation.this, simple_spinner_item, dbs);
                                    spinnerArrayAdapterDbs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                    select_dbs.setAdapter(spinnerArrayAdapterDbs);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                        } catch (Exception e) {
                            Toast.makeText(TransMotherGasStation.this,e.toString(),Toast.LENGTH_LONG).show();
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



    public void insertData() {
        final String lcv_id = lcv_num_val;
        final String dbs_station_id = dbs_id;


        if (!(station_id.equals("NA") && lcv_id.equals("NA") && dbs_station_id.equals("NA"))) {
            StringRequest request = new StringRequest(Request.Method.POST, URL_MGS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e(TAG, "insertData Response = " + response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("message") == null) {
                                    Toast.makeText(TransMotherGasStation.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(TransMotherGasStation.this, message, Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(TransMotherGasStation.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
                }
            }

            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();

                    String lcv_from_mgs_to_dbs = "1";
                    String lcv_status=lcv_id+ " waiting for Filling at "+ station_id;
                    params.put("lcv_status",lcv_status );
                    params.put("lcv_id", lcv_id);
                    params.put("station_id", station_id);
                    params.put("dbs_station_id", dbs_station_id);
                    params.put("lcv_from_mgs_to_dbs", lcv_from_mgs_to_dbs);
                    params.put("operator_id", username);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(TransMotherGasStation.this);
            requestQueue.add(request);
        } else {
            Toast.makeText(TransMotherGasStation.this, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }


    }


    public void notifymsg() {


        final String MGS = station_id;
        final String LCV = lcv_num_val;
        final String DBS = station_id;
        final String operator_id = username;

        Log.e(TAG, "operator_id in insert Notify =" + operator_id);


        final String Message = LCV + " " + "Reached at Mother Gas Station READY FOR RE-FILLING" + ":" + MGS;
        StringRequest request = new StringRequest(Request.Method.POST, URL_NOTIFY1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e(TAG, "notifymsg Response = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(TransMotherGasStation.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(TransMotherGasStation.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TransMotherGasStation.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
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
                params.put("flag", "1");
                params.put("operator_id", operator_id);


                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(TransMotherGasStation.this);
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