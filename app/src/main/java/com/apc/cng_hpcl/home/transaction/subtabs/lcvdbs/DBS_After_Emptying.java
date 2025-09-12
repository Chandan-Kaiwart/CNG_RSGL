package com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.ImageUpload.VolleyMultipartRequest;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.transaction.Transaction;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.home.transaction.subtabs.TransCityGateStation.RequestPermissionCode;
import static com.apc.cng_hpcl.util.Constant.AI_URL_Pressure;
import static com.apc.cng_hpcl.util.Constant.AI_URL_Temp;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;
import static com.apc.cng_hpcl.util.Constant.molarmass;

public class DBS_After_Emptying extends AppCompatActivity {
    private static final String ROOT_URL = "https://cng-suvidha.in/";
    public static final String UPLOAD_URL_PRESSURE_GENERAL = ROOT_URL + "/instru/Pressure/pressure_general_api.php";
    public static final String UPLOAD_URL_PRESSURE_ITECH = ROOT_URL + "/instru/Pressure/pressure_itech_api.php";
    public static final String UPLOAD_URL_PRESSURE_RADIX = ROOT_URL + "/instru/Pressure/pressure_radix_api.php";
    public static final String UPLOAD_URL_PRESSURE_WIKA = ROOT_URL + "/instru/Pressure/pressure_wika_api.php";
    public static final String UPLOAD_URL_TEMP_WIKA = ROOT_URL + "/instru/Temperature/temp_wika_api.php";
    public static final String UPLOAD_URL_TEMP_RADIX = ROOT_URL + "/instru/Temperature/temp_radix_api.php";
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    //    private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
    private static final String readTempValue =AI_URL_Temp+"readTempValue.php";
    private static final String readPressureValue =AI_URL_Pressure+"readPressureValue.php";

    private static final String BASE_URL_URL2 = BASE_URL + "mgs_dbs_read_transaction.php?apicall=";
    public static final String URL_READ_NOTE = BASE_URL_URL2 + "readNotification";
    String readDBS = BASE_URL_URL + "readDBS";
    public static final String URL_READ_VOLUME = BASE_URL_URL + "readVolume";
    String status = "Pending";
    public static final String URL_NOTIFY3 = BASE_URL_URL + "notifydbs3";
    public static final String URL_DBS = BASE_URL_URL + "updateDBS2";
    char tmp = 0x00B0;
    EditText manual_temp2, manual_pressure2;
    RadioButton radio_auto2, radio_manual2;
    Spinner select_mgs, select_lcv, select_dbs;
    Button manual_submit2, capture2, save2, capture3, save3, calculate, notify;
    LinearLayout linear_manual2, linear_automated2, linear_opt_manual_auto2;
    String encodedTemp2image, encodedPressure2image, temp2, press2;
    String pressure_name2, temp_name2;
    String username;
    TextView lcv_num2, lcv_num;

    TextView mass_sng1;
    TextView temp2_value;
    TextView pressure2_value;
    TextView mass_cascade, mass_sng;
    ImageView temp_gauge2, pressure_gauge2;
    private Spinner spin_temp, spin_pressure;

    int PICK_THIRD_IMAGE = 102;
    int PICK_FOURTH_IMAGE = 103;
    Double Mass1, Mass2, MassFlow;

    String[] temp = {"Radix", "Wikai"};
    String[] pressure = {"General", "ITEC", "Radix", "Wikai"};
    String[] mgs = {"MGS001", "MGS002", "MGS003"};
    String[] dbs = {"DBS001", "DBS002", "DBS003"};
    String[] lcv = {"DL-1MA-5353", "HR-38AB-9008", "HR-38AB-0291", "HR-38AB-35216", "DL-1MA-5216", "UP-17AT-7351", "HR-38AB-7669", "DL-1MA-6137", "DL-1MA-6103",
            "DL-1MA-4669", "DL-1MA-6029", "HR-63E-6118", "DL-1MA-3661", "HR-63E-5616", "DL-1MA-4638", "HR-63E-1684"};
    String lcvNum, mgsId, dbsId, mass_before_emptying;
    int volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbs_after_emptying);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        manual_submit2 = findViewById(R.id.manual_submit2);
        manual_temp2 = findViewById(R.id.manual_temp2);
        manual_pressure2 = findViewById(R.id.manual_pressure2);
        radio_manual2 = findViewById(R.id.radio_manual2);
        radio_auto2 = findViewById(R.id.radio_auto2);
        linear_opt_manual_auto2 = findViewById(R.id.linear_opt_manual_auto2);
        linear_manual2 = findViewById(R.id.manual2);
        linear_automated2 = findViewById(R.id.linear_automated2);


        capture2 = findViewById(R.id.capture2);
        save2 = findViewById(R.id.save2);
        capture3 = findViewById(R.id.capture3);
        save3 = findViewById(R.id.save3);
        calculate = findViewById(R.id.calculate);
        notify = findViewById(R.id.notify);

        lcv_num2 = findViewById(R.id.lcv_num2);
        lcv_num = findViewById(R.id.lcv_num);
        temp2_value = findViewById(R.id.temp2_value);
        pressure2_value = findViewById(R.id.pressure2_value);
        mass_cascade = findViewById(R.id.mass_cascade);
        temp_gauge2 = findViewById(R.id.temp_gauge);
        pressure_gauge2 = findViewById(R.id.pressure_gauge);
        mass_sng = findViewById(R.id.mass_sng);

        spin_temp = findViewById(R.id.spin_temp);
        spin_pressure = findViewById(R.id.spin_pressure);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lcvNum = bundle.getString("lcv_num");
            dbsId = bundle.getString("dbs_id");
            mass_before_emptying = bundle.getString("mass");
            username = bundle.getString("username");

        }
        lcv_num.setText(lcvNum);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(DBS_After_Emptying.this, simple_spinner_item, temp);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spin_temp.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinnerArrayAdapterPressure = new ArrayAdapter<String>(DBS_After_Emptying.this, simple_spinner_item, pressure);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spin_pressure.setAdapter(spinnerArrayAdapterPressure);

        Drawable old_temp_gauge2 = temp_gauge2.getDrawable();
        Drawable old_pressure_gauge2 = pressure_gauge2.getDrawable();
        EnableRuntimePermission();


        manual_submit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp2 = manual_temp2.getText().toString();
                press2 = manual_pressure2.getText().toString();
                if (press2 != null && temp2 != null && temp_gauge2.getDrawable() != old_temp_gauge2 && pressure_gauge2.getDrawable() != old_pressure_gauge2
                ) {
//                temp2 = manual_temp2.getText().toString();
//                press2 = manual_pressure2.getText().toString();
                    temp2_value.setText("Temperature:" + temp2 + (char) 0x00B0 + "C");
                    pressure2_value.setText("Pressure:" + press2 + " bar");
                } else {
                    Toast.makeText(v.getContext(), "Please Capture Temperature,Pressure and Mass Flow Rate Value and Image", Toast.LENGTH_SHORT).show();
                }
            }
        });


        capture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 9);
            }
        });

        capture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
            }
        });

        save2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_THIRD_IMAGE);
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 9);


            }
        });


        save3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_FOURTH_IMAGE);
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 10);


            }
        });


        calculate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                if (press2 != null && temp2 != null && temp_gauge2.getDrawable() != old_temp_gauge2 && pressure_gauge2.getDrawable() != old_pressure_gauge2
                ) {
//                MassFlow = (250 * 1.5);
//                mass_sng.setText(String.format("%.2f", MassFlow));
                    Log.e(TAG, "Volume=" + volume);

                    Log.e(TAG, "Value during calculation of Temp= " + temp2 + "Pressure=" + press2);
                    Mass2 = ((molarmass * Float.parseFloat(press2) * volume) / (83.14 * (Float.parseFloat(temp2) + 273.15)));
//                String Mass_value=Double. toString(Mass2);
                    mass_cascade.setText(String.format("%.2f", Mass2));
                } else {
                    Toast.makeText(v.getContext(), "Please Capture Temperature,Pressure and Mass Flow Rate Value and Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        getDbsrecord(username, lcvNum, dbsId, "6");
        readVolume();
        Log.e(TAG, "In DBS After Emptying");
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Mass2 == null) {
                    Toast.makeText(getApplicationContext(), "Please Capture Temperature,Pressure and Mass Flow Rate Value and Image", Toast.LENGTH_SHORT).show();
                } else {
                    readNotification();

                }


//                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.app.gps.womsolution");
//
//                if (launchIntent != null) {
//                    startActivity(launchIntent);
//                } else {
//                    Toast.makeText(DBS_After_Emptying.this, "There is no package available in android", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }
    private void readGauge(Bitmap bp,int type,ProgressDialog pd){
        String url="";

        if(type==0){
            switch (spin_pressure.getSelectedItemPosition()){
                case 0:
                    url=UPLOAD_URL_PRESSURE_GENERAL;

                    break;
                case 1:
                    url=UPLOAD_URL_PRESSURE_ITECH;

                    break;
                case 2:
                    url=UPLOAD_URL_PRESSURE_RADIX;

                    break;
                case 3:
                    url=UPLOAD_URL_PRESSURE_WIKA;

                    break;

                default:
                    url=UPLOAD_URL_PRESSURE_GENERAL;

            }

        }
        else if(type==1) {

            if (spin_temp.getSelectedItemPosition() == 0) {
                url = UPLOAD_URL_TEMP_RADIX;
            } else {
                url = UPLOAD_URL_TEMP_WIKA;
            }
        }

        Log.d("RESP>>>", url);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        pd.dismiss();
                        String res=new String(response.data, StandardCharsets.UTF_8);
                        try {
                            Log.d("RESP>>>", res);

                            JSONObject json=new JSONObject(res);
                            String value = json.getString("meter_reading");
                            if(type==0) {

                                if (!value.equals("null")) {
                                    pressure2_value.setText("Pressure:" + value + "bar");
                                    manual_pressure2.setText(value);
                                }
                            }
                            else if(type==1){

                                if (!value.equals("null")) {
                                    temp2_value.setText("Temperature:" + value + (char) 0x00B0 + "C");
                                    manual_temp2.setText(value);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        // Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                        if(error != null){
                            Toast.makeText(getApplicationContext(),"No Internet Available !",Toast.LENGTH_LONG).show();
                        }
                        Log.d("RESP>>>err", error.toString());

                        Log.d("RESP>>>err", error.networkResponse.statusCode+"");
                        try {
                            String resBody=new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.d("RESP>>>err", resBody);

                            JSONObject data=new JSONObject(resBody);
                            Log.d("RESP>>>err", data.toString());
                            String message = data.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("RESP>>>err>>err", e.toString());

                        }
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> inputs = new HashMap<>();
                //     params.put("case_id", caseId);

                inputs.put("customer_ca_no","234");
                inputs.put("customer_bp_no","32424");
                inputs.put("customer_lat_long","423");
                inputs.put("customer_meter_id","4234");
                inputs.put("meter_reading_manual","43423");
                inputs.put("time","3243");
                inputs.put("date","4234");
                inputs.put("type","32432");

                //     inputs.put("payment_slip",imageToBase64(bp));
                return inputs;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //    String name=new String();
                params.put("img", new DataPart("img.jpg", "/jpg", bp));
                //  params.put("payment_slip", new DataPart("img.png","/png",getFileDataFromDrawable(bp)));
                return params;
            }

        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //adding the request to volley
        Volley.newRequestQueue(DBS_After_Emptying.this).add(volleyMultipartRequest);
    }

    private void readNotification() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String mgs_id = "NA";
        if (!(lcvNum.equals("NA") || dbsId.equals("NA"))) {
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
                        if (flag.equals("5") && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Fifth transaction MGS");
                            insertData();
                            notify2msg();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            Intent intent = new Intent(DBS_After_Emptying.this, Transaction.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();

                        } else if (flag.equals("5") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending DBS After Emptying");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval After Emptying", Toast.LENGTH_SHORT).show();

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
                    Log.e(TAG, "Notification_LCV=" + lcvNum);
                    Log.e(TAG, "Notification_DBS=" + dbsId);
                    params.put("Notification_LCV", lcvNum);
                    params.put("Notification_MGS", mgs_id);
                    params.put("Notification_DBS", dbsId);


                    return params;
                }
            };

            queue.add(request);
        } else {
            Toast.makeText(DBS_After_Emptying.this, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }

    }

    private void readVolume() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, URL_READ_VOLUME, new com.android.volley.Response.Listener<String>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    jsonObject.getString("Lcv_Num");
                    String vol = jsonObject.getString("Cascade_Capacity");
                    volume = Integer.parseInt(vol);

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

                params.put("lcv_id", lcvNum);
//                params.put("station_id",mgsId);
//                params.put("dbs_station_id", dbsId);
//                params.put("lcv_from_mgs_to_dbs", s);

                return params;
            }
        };

        queue.add(request);

    }

    private void getDbsrecord(String username, String lcv_num, String dbs_id, String s) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, readDBS, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    jsonObject.getString("lcv_id");
                    String end_empty_time = jsonObject.getString("end_empty_time");
                    String lcv_from_mgs_to_dbs = jsonObject.getString("lcv_from_mgs_to_dbs");
                    if (!end_empty_time.equals("null") && lcv_from_mgs_to_dbs.equals("6")) {
//                        lcv_num2.setText(jsonObject.getString("lcv_id"));
                        temp2_value.setText("Temperature:" + jsonObject.getString("after_empty_at_dbs_value_temperature_gauge_read") + (char) 0x00B0 + "C");
                        pressure2_value.setText("Pressure:" + jsonObject.getString("after_empty_at_dbs_value_pressure_gauge_read") + "bar");
                        manual_temp2.setEnabled(false);
                        manual_pressure2.setEnabled(false);
                        manual_temp2.setText(jsonObject.getString("after_empty_at_dbs_value_temperature_gauge_read"));
                        manual_pressure2.setText(jsonObject.getString("after_empty_at_dbs_value_pressure_gauge_read"));
                        mass_sng1.setText(jsonObject.getString("after_empty_at_dbs_mass_cng"));
//                        time.setText(jsonObject.getString("update_date"));
                        Glide.with(getApplicationContext()).load("after_empty_at_dbs_temperature_gauge_img").into(temp_gauge2);
                        Glide.with(getApplicationContext()).load("after_empty_at_dbs_pressure_gauge_img").into(pressure_gauge2);
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

                params.put("operator_id", username);
                params.put("lcv_id", lcv_num);
                params.put("dbs_station_id", dbs_id);
//                params.put("lcv_from_mgs_to_dbs", s);

                return params;
            }
        };

        queue.add(request);

    }

    public void insertData() {


        final String lcv_id = lcvNum;
        final String station_id = dbsId;
        final String before_empty_at_db_mass_cng = String.format("%.2f", Mass1);
        final String after_empty_at_dbs_value_pressure_gauge_read = press2;
        final String after_empty_at_dbs_value_temperature_gauge_read = temp2;
        final String after_empty_at_dbs_mass_cng = mass_cascade.getText().toString();
        final String lcv_from_mgs_to_dbs = "6";


        StringRequest request = new StringRequest(Request.Method.POST, URL_DBS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(DBS_After_Emptying.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(DBS_After_Emptying.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DBS_After_Emptying.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                String lcv_status=lcv_id+" emptied at"+station_id;
                params.put("lcv_status",lcv_status );
                params.put("lcv_id", lcv_id);
                params.put("dbs_station_id", station_id);
                params.put("operator_id_aftr_emptying", username);
                params.put("after_empty_at_dbs_temperature_gauge_img", encodedTemp2image);
                params.put("after_empty_at_dbs_pressure_gauge_img", encodedPressure2image);
                params.put("before_empty_at_db_mass_cng", before_empty_at_db_mass_cng);
                params.put("after_empty_at_dbs_value_pressure_gauge_read", after_empty_at_dbs_value_pressure_gauge_read);
                params.put("after_empty_at_dbs_value_temperature_gauge_read", after_empty_at_dbs_value_temperature_gauge_read);
                params.put("after_empty_at_dbs_mass_cng", after_empty_at_dbs_mass_cng);
                params.put("lcv_from_mgs_to_dbs", lcv_from_mgs_to_dbs);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(DBS_After_Emptying.this);
        requestQueue.add(request);


    }


    public void notify2msg() {


        final String MGS = "NA";
        final String LCV = lcvNum;
        final String DBS = dbsId;
        final String operator_id = username;
        Log.e(TAG, "operator_id in insert Notify =" + operator_id);
        final String Message = "Readings captured After emptying" + LCV + " at " + DBS;
        StringRequest request = new StringRequest(Request.Method.POST, URL_NOTIFY3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(DBS_After_Emptying.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(DBS_After_Emptying.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DBS_After_Emptying.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
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
                params.put("flag", "6");
                params.put("operator_id", operator_id);
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(DBS_After_Emptying.this);
        requestQueue.add(request);


    }

    private String encodebitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] byteofimages = byteArrayOutputStream.toByteArray();
        return (android.util.Base64.encodeToString(byteofimages, Base64.DEFAULT));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            temp_gauge2.setImageBitmap(bitmap);
            encodedTemp2image = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(DBS_After_Emptying.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            pd.show();
            readGauge(bitmap,1,pd);
        }
        else if (requestCode == 8 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            pressure_gauge2.setImageBitmap(bitmap);
            encodedPressure2image = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(DBS_After_Emptying.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            pd.show();
            readGauge(bitmap,0,pd);

        }

/*
        else if (requestCode == 10 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mfm_gauge.setImageBitmap(bitmap);
            encodedMfmImage = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            //   pd.show();
          */
/*  TimerTask ts = new TimerTask() {
                @Override
                public void run() {
                    Log.e(TAG, "bfr calling readCylinderNum encodedCylinderNum = " + encodedPressureimage);
                    readPressureGauge(encodedPressureimage);
                    Log.e(TAG, "aftr calling readCylinderNum encodedCylinderNum = " + encodedPressureimage);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });


                }
            };
            new Timer().schedule(ts, 30000);*//*

        }
*/
        else if (requestCode == PICK_THIRD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            temp_gauge2.setImageURI(imageUri);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialog pd = new ProgressDialog(DBS_After_Emptying.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            pd.show();
            readGauge(bitmap,1,pd);

//            File imageFileName = new File(getRealPathFromNAME(imageUri));
//            temp_name = imageFileName.getName();
//            switch (temp_name) {
//                case "Temperature1.jpeg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            temp1 = "33";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    temp_value.setText("Temperature:" + temp1 + (char) 0x00B0 + "C");
//                                    manual_temp.setText(temp1);
//
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//
//                    break;
//                }
//                case "Temperature2.jpeg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            temp1 = "35";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    temp_value.setText("Temperature:" + temp1 + (char) 0x00B0 + "C");
//                                    manual_temp.setText(temp1);
//
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//                    break;
//                }
//                case "Temperature3.jpeg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            temp1 = "49";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    temp_value.setText("Temperature:" + temp1 + (char) 0x00B0 + "C");
//                                    manual_temp.setText(temp1);
//
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//                    break;
//                }
//                case "Temperature4.jpg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            temp1 = "20";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    temp_value.setText("Temperature:" + temp1 + (char) 0x00B0 + "C");
//                                    manual_temp.setText(temp1);
//
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//
//                    break;
//                }
//                case "Temperature5.jpg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            temp1 = "25";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    temp_value.setText("Temperature:" + temp1 + (char) 0x00B0 + "C");
//                                    manual_temp.setText(temp1);
//
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//
//                    break;
//                }
//                default:
//                    temp_value.setText(" &#xb0; Celsius");
//                    break;
//            }
        }
        else if (requestCode == PICK_FOURTH_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            pressure_gauge2.setImageURI(imageUri);
            File imageFileName = new File(getRealPathFromNAME(imageUri));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            encodedPressure2image = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(DBS_After_Emptying.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            pd.show();
            readGauge(bitmap, 0, pd);
        }

/*
        else if (requestCode == PICK_THIRD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            mfm_gauge.setImageURI(imageUri);
            File imageFileName = new File(getRealPathFromNAME(imageUri));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            encodedMfmImage = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            pd.show();
            TimerTask ts = new TimerTask() {
                @Override
                public void run() {
                    Log.e(TAG, "bfr calling readCylinderNum encodedCylinderNum = " + encodedPressureimage);
                    // readPressureGauge(encodedMfmImage);
                    Log.e(TAG, "aftr calling readCylinderNum encodedCylinderNum = " + encodedPressureimage);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });


                }
            };
            new Timer().schedule(ts, 30000);}
*/

//            pressure_name = imageFileName.getName();
////            pressure_value.setText(pressure_name);
//            switch (pressure_name) {
//                case "Press1.jpg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            press1 = "50";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    pressure_value.setText("Pressure:" + press1 + " bar");
//                                    manual_pressure.setText(press1);
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//
//
//                    break;
//                }
//                case "Press2.jpg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            press1 = "100";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    pressure_value.setText("Pressure:" + press1 + " bar");
//                                    manual_pressure.setText(press1);
//
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//
//                    break;
//                }
//                case "Press3.jpg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            press1 = "150";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    pressure_value.setText("Pressure:" + press1 + " bar");
//                                    manual_pressure.setText(press1);
//
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//
//                    break;
//                }
//                case "Press4.jpg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            press1 = "200";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    pressure_value.setText("Pressure:" + press1 + " bar");
//                                    manual_pressure.setText(press1);
//
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//
//                    break;
//                }
//                case "Press5.jpg": {
//                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
//
//                    pd.setMessage("AI Engine is Processing");
//                    pd.show();
//                    TimerTask ts = new TimerTask() {
//                        @Override
//                        public void run() {
//                            press1 = "250";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    pressure_value.setText("Pressure:" + press1 + " bar");
//                                    manual_pressure.setText(press1);
//
//                                }
//                            });
//
//
//                        }
//                    };
//                    new Timer().schedule(ts, 10000);
//
//                    break;
//                }
//                default:
//                    pressure_value.setText("0" + " bar");
//                    break;
//            }
//            Log.d(TAG, "onActivityResult: pressure_value=" + pressure_value);
//        }

    }


    private void readPressureGauge(String encodedPressure2image) {
        Log.e(TAG, "In readCylinderNum encodedCylinderNum = " + encodedPressure2image);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, readPressureValue, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "In readCylinderNum response received = " + response);

//                    Toast.makeText(MainActivity.this, "Response=" + response, Toast.LENGTH_SHORT).show();
                    jsonObject.getString("message");
                    String pressureValue=jsonObject.getString("message");
                    if (!pressureValue.equals("null")) {
                        pressure2_value.setText("Pressure:" + pressureValue + "bar");
                        manual_pressure2.setText(pressureValue);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pressure2_value.setText("Network Error");
                manual_pressure2.setText("Network Error");
                Toast.makeText(getApplicationContext(), "Failed to get data" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                String pressure_make= (String) spin_pressure.getSelectedItem();
                Log.e(TAG, "Pressure Meter Make = " + pressure_make);


                Map<String, String> params = new HashMap<String, String>();
                Log.e(TAG, "In readCylinderNum cylinderPlate passed as = " + encodedPressure2image);
                params.put("pressure_make", pressure_make);
                params.put("pressure_image", encodedPressure2image);
//
                return params;
            }
        };

        queue.add(request);
    }

    private void readTemperatureGauge(String encodedTemp2image) {
        Log.e(TAG, "In readCylinderNum encodedCylinderNum = " + encodedTemp2image);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, readTempValue, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "In readCylinderNum response received = " + response);

//                    Toast.makeText(MainActivity.this, "Response=" + response, Toast.LENGTH_SHORT).show();
                    jsonObject.getString("message");
                    String tempValue=jsonObject.getString("message");
                    if (!tempValue.equals("null")) {
                            temp2_value.setText("Temperature:" + tempValue + (char) 0x00B0 + "C");
                            manual_temp2.setText(tempValue);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                temp2_value.setText("Network Error");
                manual_temp2.setText("Network Error");
                Toast.makeText(getApplicationContext(), "Failed to get data" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                String tempMeterMake= (String) spin_temp.getSelectedItem();
                Log.e(TAG, "Temperature Meter Make = " + tempMeterMake);


                Map<String, String> params = new HashMap<String, String>();
                Log.e(TAG, "In readCylinderNum cylinderPlate passed as = " + encodedTemp2image);
                params.put("temp_make", tempMeterMake);
                params.put("temp_image", encodedTemp2image);
//
                return params;
            }
        };

        queue.add(request);
    }

    private String getRealPathFromNAME(Uri contentNAME) {
        String result;
        Cursor cursor = getContentResolver().query(contentNAME, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentNAME.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(DBS_After_Emptying.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(DBS_After_Emptying.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(DBS_After_Emptying.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DBS_After_Emptying.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DBS_After_Emptying.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
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