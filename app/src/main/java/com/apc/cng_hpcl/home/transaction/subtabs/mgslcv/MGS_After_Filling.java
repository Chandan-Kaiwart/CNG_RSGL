package com.apc.cng_hpcl.home.transaction.subtabs.mgslcv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_After_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying_MFM;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

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
import static com.apc.cng_hpcl.home.AdapterOperator.position;
import static com.apc.cng_hpcl.home.transaction.subtabs.TransCityGateStation.RequestPermissionCode;
import static com.apc.cng_hpcl.util.Constant.AI_URL_Pressure;
import static com.apc.cng_hpcl.util.Constant.AI_URL_Temp;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;
import static com.apc.cng_hpcl.util.Constant.molarmass;

public class MGS_After_Filling extends AppCompatActivity {
    private static final String ROOT_URL = "https://cng-suvidha.in/";
    public static final String UPLOAD_URL_PRESSURE_GENERAL = ROOT_URL + "/instru/Pressure/pressure_general_api.php";
    public static final String UPLOAD_URL_PRESSURE_ITECH = ROOT_URL + "/instru/Pressure/pressure_itech_api.php";
    public static final String UPLOAD_URL_PRESSURE_RADIX = ROOT_URL + "/instru/Pressure/pressure_radix_api.php";
    public static final String UPLOAD_URL_PRESSURE_WIKA = ROOT_URL + "/instru/Pressure/pressure_wika_api.php";
    public static final String UPLOAD_URL_TEMP_WIKA = ROOT_URL + "/instru/Temperature/temp_wika_api.php";
    public static final String UPLOAD_URL_TEMP_RADIX = ROOT_URL + "/instru/Temperature/temp_radix_api.php";
    //    private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    private static final String BASE_URL_URL2 = BASE_URL + "mgs_dbs_read_transaction.php?apicall=";
    private static final String readTempValue =AI_URL_Temp+"readTempValue.php";
    private static final String readPressureValue =AI_URL_Pressure+"readPressureValue.php";

    public static final String URL_READ_NOTE = BASE_URL_URL2 + "readNotification";
    public static final String URL_NOTIFY1 = BASE_URL_URL + "notify1";
    public static final String URL_UPDATEMGS2 = BASE_URL_URL + "updateMGS2";
    String readMGSBrfFill = BASE_URL_URL + "readMGSBfrFill";
    String mfmPrev="0";
    public static final String URL_NOTIFY3 = BASE_URL_URL + "notify3";
    public static final String URL_READ_VOLUME = BASE_URL_URL + "readVolume";
    EditText manual_temp2, manual_pressure2, manual_mfm;
    RadioButton radio_auto2, radio_manual2;
    Spinner select_mgs, select_lcv, select_dbs;
    Button manual_submit2, capture2, save2, capture3, save3,capture4, save4, calculate, notify;
    RelativeLayout after_refilling;
    LinearLayout linear_edit, linear_manual, linear_automated, linear_opt_manual_auto,
            linear_manual2, linear_automated2, linear_opt_manual_auto2;
    String encodedTempimage, encodedPressureimage, encodedTemp2image, encodedPressure2image,encodedmfmimage,
            temp_name, pressure_name, temp_name2, pressure_name2, temp2, press2, mfm,mfm_name;
    String timeTakenToFillLCV;
    LocalDateTime timeBeforeFilling, timeAfterFilling;
    String username;

    private Spinner spin_temp,spin_pressure;

    TextView temp_value, lcv_num;
    TextView mfm_value;
    TextView pressure_value;
    TextView mass_sng1;
    TextView temp2_value;
    TextView pressure2_value;
    TextView mass_cascade, mass_sng, mass_flow, time_fill_lcv;
    ImageView temp_gauge2, pressure_gauge2, mfm_image;
    int PICK_FIRST_IMAGE = 100;
    int PICK_SECOND_IMAGE = 101;
    int PICK_THIRD_IMAGE = 102;
    int PICK_FOURTH_IMAGE = 103;
    int PICK_FIFTH_IMAGE = 104;
    Double Mass1, Mass2, MassFlow;
    String[] mgs = {"MGS001", "MGS002", "MGS003"};
    String[] dbs = {"DBS001", "DBS002", "DBS003"};
    String[] lcv = {"DL-1MA-5353", "HR-38AB-9008", "HR-38AB-0291", "HR-38AB-35216", "DL-1MA-5216", "UP-17AT-7351", "HR-38AB-7669", "DL-1MA-6137", "DL-1MA-6103",
            "DL-1MA-4669", "DL-1MA-6029", "HR-63E-6118", "DL-1MA-3661", "HR-63E-5616", "DL-1MA-4638", "HR-63E-1684"};
    String status = "Pending";
    int volume;
    String[] temp = {"Radix", "Wikai", "Baumer"};
    String[] pressure = {"General", "ITEC", "Radix", "Wikai", "Baumer"};
    String lcvNum, mgsId, dbsId, timeBeforeFill, mass_before_filling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgs_after_filling);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        manual_submit2 = findViewById(R.id.manual_submit2);
        manual_temp2 = findViewById(R.id.manual_temp2);
        manual_pressure2 = findViewById(R.id.manual_pressure2);
        manual_mfm = findViewById(R.id.manual_mfm);
        radio_manual2 = findViewById(R.id.radio_manual2);
        radio_auto2 = findViewById(R.id.radio_auto2);
        linear_opt_manual_auto2 = findViewById(R.id.linear_opt_manual_auto2);
        linear_manual2 = findViewById(R.id.manual2);
        linear_automated2 = findViewById(R.id.linear_automated2);
        mfm_image=findViewById(R.id.mfm_image);
        capture4 = findViewById(R.id.capture4);
        save4 = findViewById(R.id.save4);
        capture2 = findViewById(R.id.capture2);
        save2 = findViewById(R.id.save2);
        capture3 = findViewById(R.id.capture3);
        save3 = findViewById(R.id.save3);
        calculate = findViewById(R.id.calculate);
        notify = findViewById(R.id.notify);
        after_refilling = findViewById(R.id.after_refilling);
        lcv_num = findViewById(R.id.lcv_num);
        mfm_value = findViewById(R.id.mfm_value);
        spin_temp=findViewById(R.id.spin_temp);
        spin_pressure=findViewById(R.id.spin_pressure);
        temp2_value = findViewById(R.id.temp2_value);
        pressure2_value = findViewById(R.id.pressure2_value);
        mass_cascade = findViewById(R.id.mass_cascade);
        temp_gauge2 = findViewById(R.id.temp_gauge);
        pressure_gauge2 = findViewById(R.id.pressure_gauge);
        mass_sng = findViewById(R.id.mass_sng);
        mass_flow = findViewById(R.id.mass_flow);
        time_fill_lcv = findViewById(R.id.time_fill_lcv);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
            lcvNum = bundle.getString("lcv_num");
            mgsId = bundle.getString("mgs_id");
            dbsId = bundle.getString("dbs_id");
//            timeBeforeFill = bundle.getString("timeBeforeFilling");
//            mass_before_filling = bundle.getString("mass");

        }

        lcv_num.setText(lcvNum);

//        timeBeforeFilling=LocalDateTime.parse(timeBeforeFill);
//        ZonedDateTime zdt = ZonedDateTime.parse(timeBeforeFill);
//        LocalDateTime timeBeforeFilling = zdt.toLocalDateTime();
        Drawable old_temp_gauge2 = temp_gauge2.getDrawable();
        Drawable old_pressure_gauge2 = pressure_gauge2.getDrawable();


        EnableRuntimePermission();
        getMgsrecord(username, lcvNum, mgsId, dbsId, "3");
        readVolume();
        Log.e(TAG, "Volume=" + volume);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MGS_After_Filling.this, simple_spinner_item, temp);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spin_temp.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinnerArrayAdapterPressure = new ArrayAdapter<String>(MGS_After_Filling.this, simple_spinner_item, pressure);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spin_pressure.setAdapter(spinnerArrayAdapterPressure);

        manual_submit2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                mfm = manual_mfm.getText().toString();
                temp2 = manual_temp2.getText().toString();
                press2 = manual_pressure2.getText().toString();
                if (press2 != null && temp2 != null && mfm != null && temp_gauge2.getDrawable() != old_temp_gauge2 && pressure_gauge2.getDrawable() != old_pressure_gauge2
                ) {
//                mfm = manual_mfm.getText().toString();
//                temp2 = manual_temp2.getText().toString();
//                press2 = manual_pressure2.getText().toString();
                    mfm_value.setText(mfm+"Kg/hr");
                    try {
                        int diff=Integer.parseInt(mfm)-Integer.parseInt(mfmPrev);
                        mass_flow.setText("Mass Flow Rate Difference : " + diff + "Kg/hr");
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    temp2_value.setText("Temperature:" + temp2 + (char) 0x00B0 + "C");
                    pressure2_value.setText("Pressure:" + press2 + " bar");
                } else {
                    Toast.makeText(v.getContext(), "Please Capture Temperature,Pressure and Mass Flow Rate Value and Image", Toast.LENGTH_SHORT).show();
                }
            }

        });


        //Capture Temperature after Filling

        capture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 9);
            }
        });
        //Capture Pressure after Filling


        capture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
            }
        });
//Capture Mass Flow Meter
        capture4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 11);
            }
        });

        //Upload Temperature after Filling

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
        //Upload Pressure after Filling
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

        //Upload Mass Flow Meter
        save4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_FIFTH_IMAGE);
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 10);

            }
        });


        //        Calculate Mass of Cascade after filling

        calculate.setOnClickListener(new View.OnClickListener() {
            //            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
//                timeAfterFilling = LocalDateTime.now();
//                Duration diff = Duration.between(timeBeforeFilling, timeAfterFilling);
//                long hours = diff.toHours();
//                diff = diff.minusHours(hours);
//                long minutes = diff.toMinutes();
//                diff = diff.minusMinutes(minutes);
//                long seconds = diff.getSeconds();
//                timeTakenToFillLCV = String.format("%02d:%02d:%02d",
//                        hours,
//                        minutes,
//                        seconds);
                if (press2 != null && temp2 != null && mfm != null && temp_gauge2.getDrawable() != old_temp_gauge2 && pressure_gauge2.getDrawable() != old_pressure_gauge2
                ) {
                    Log.e(TAG, "Volume=" + volume);
                    MassFlow = (Float.parseFloat(mfm) * 1.276);
                    mass_sng.setText(String.format("%.2f", MassFlow));
                    Mass2 = ((molarmass * Float.parseFloat(press2) * volume) / (83.14 * (Float.parseFloat(temp2) + 273.15)));
//                String Mass_value=Double. toString(Mass2);
                    mass_cascade.setText(String.format("%.2f", Mass2));

//                    time_fill_lcv.setText("Time taken to fill gas to LCV :" + timeTakenToFillLCV + " hr ");
                } else {
                    Toast.makeText(v.getContext(), "Please Capture Temperature,Pressure and Mass Flow Rate Value and Image", Toast.LENGTH_SHORT).show();
                }
            }
        });


        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Mass2 == null) {
                    Toast.makeText(getApplicationContext(), "Please Capture Temperature,Pressure and Mass Flow Rate Value and Image", Toast.LENGTH_SHORT).show();
                } else {
                    readNotification();
//                    notify2msg();
//                    updateData2();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("username", username);
//                    Intent intent = new Intent(MGS_After_Filling.this, Transaction.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                    finish();
                }
//                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.app.gps.womsolution");
//
//                if (launchIntent != null) {
//                    startActivity(launchIntent);
//                } else {
//                    Toast.makeText(TransMotherGasStation.this, "There is no package available in android", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    private void readNotification() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        if (!(mgsId.equals("NA") || lcvNum.equals("NA") || dbsId.equals("NA"))) {
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
                        if ( flag.equals("2")&& noteStatus.equals("Approved") ) {
                            Log.e(TAG, "In if Third transaction MGS");
                            notify2msg();
                            updateData2();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            Intent intent = new Intent(MGS_After_Filling.this, Transaction.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }


                        else if (flag.equals("3") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS After Filling");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval After Filling", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("4") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending Before Emptying");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval Before Emptying", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("5") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending DBS After Emptying");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval After Emptying", Toast.LENGTH_SHORT).show();

                        }
                        else if ((flag.equals("3") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Third Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcvNum);
                            bundle.putString("mgs_id", mgsId);
                            bundle.putString("dbs_id", dbsId);
                            Intent intent = new Intent(MGS_After_Filling.this, TransDaughterBoosterStation.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }

                        else if ((flag.equals("4") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcvNum);
                            bundle.putString("mgs_id", mgsId);
                            bundle.putString("dbs_id", dbsId);
                            Intent intent = new Intent(MGS_After_Filling.this, DBS_Before_Emptying_MFM.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        else if ((flag.equals("5") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcvNum);
                            bundle.putString("mgs_id", mgsId);
                            bundle.putString("dbs_id", dbsId);
                            Intent intent = new Intent(MGS_After_Filling.this, DBS_After_Emptying.class);
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
                    Log.e(TAG, "Notification_MGS=" + mgsId);
                    Log.e(TAG, "Notification_LCV=" + lcvNum);
                    Log.e(TAG, "Notification_DBS=" + dbsId);
                    params.put("Notification_LCV", lcvNum);
                    params.put("Notification_MGS", mgsId);
                    params.put("Notification_DBS", dbsId);


                    return params;
                }
            };

            queue.add(request);
        } else {
            Toast.makeText(MGS_After_Filling.this, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MGS_After_Filling.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(MGS_After_Filling.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MGS_After_Filling.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                params.put("Notification_LCV", lcvNum);
                params.put("Notification_MGS", mgsId);
                params.put("Notification_DBS", dbsId);
                params.put("Notification_Message", message);
                params.put("status", updateStatus);
                params.put("flag", updateFlag);
                params.put("operator_id", operator_id);


                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(MGS_After_Filling.this);
        requestQueue.add(request);

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

                } catch (Exception e) {
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


    private void getMgsrecord(String username, String lcv_num_val, String mgs_id, String dbs_id, String s) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        Log.d("BFR>>>", readMGSBrfFill);
        StringRequest request = new StringRequest(Request.Method.POST, readMGSBrfFill, new com.android.volley.Response.Listener<String>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("data");
                    jsonObject.getString("lcv_id");
                    mfmPrev=jsonObject.getString("before_filing_at_mgs_mfm_gauge_read");
                    String update_date = jsonObject.getString("update_date");
                    String lcv_from_mgs_to_dbs = jsonObject.getString("lcv_from_mgs_to_dbs");
                    if (!lcv_from_mgs_to_dbs.equals("3")) {
                        timeBeforeFill = jsonObject.getString("update_date");
                    } else {
                        lcv_num.setText(jsonObject.getString("lcv_id"));
                        temp2_value.setText("Temperature:" + jsonObject.getString("after_filling_at_mgs_value_temperature_gauge_read") + (char) 0x00B0 + "C");
                        pressure2_value.setText("Pressure:" + jsonObject.getString("after_filling_at_mgs_value_pressure_gauge_read") + "bar");
                        manual_temp2.setEnabled(false);
                        manual_pressure2.setEnabled(false);
                        manual_mfm.setEnabled(false);
                        manual_temp2.setText(jsonObject.getString("after_filling_at_mgs_value_temperature_gauge_read"));
                        manual_pressure2.setText(jsonObject.getString("after_filling_at_mgs_value_pressure_gauge_read"));
                        mass_sng.setText(String.format("%.2f", (jsonObject.getDouble("after_filling_at_mgs_mfm_value_read") * 1.276)));
                        manual_mfm.setText(jsonObject.getString("after_filling_at_mgs_mfm_value_read"));
                        time_fill_lcv.setText(jsonObject.getString("update_date"));
//                        timeBeforeFill=jsonObject.getString("update_date");
                        mass_cascade.setText(jsonObject.getString("after_filling_at_mgs_mass_cng"));
                        Glide.with(getApplicationContext()).load("after_filling_at_mgs_temperature_gauge_img").into(temp_gauge2);
                        Glide.with(getApplicationContext()).load("after_filling_at_mgs_pressure_gauge_img").into(pressure_gauge2);
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
                params.put("lcv_id", lcv_num_val);
                params.put("station_id", mgs_id);
                params.put("dbs_station_id", dbs_id);
                Log.d("BFR>>>", params.toString());

//                params.put("lcv_from_mgs_to_dbs", s);

                return params;
            }
        };

        queue.add(request);

    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
////        String s1 = sh.getString("timeBeforeFilling", "");
//        String s1 = sh.getString("mass_flow", "");
//        String s2 = sh.getString("mass", "");
//
//        String s3 = sh.getString("press1", "");
//        String s4 = sh.getString("temp1", "");
//        String s5 = sh.getString("time_taken_to_fill", "");
//
//        String s6 = sh.getString("manual_pressure2", "");
//        String s7 = sh.getString("manual_temp2", "");
//
//        Log.e(TAG, "Shared Pref: mass=" + s2 + "temp=" + s4 + "Press=" + s4);
////        Toast.makeText(MGS_After_Filling.this , "In restore state", Toast.LENGTH_SHORT).show();
//        mass_sng.setText(s1);
//        mass_cascade.setText(s2);
//        pressure2_value.setText(s3);
//        temp2_value.setText(s4);
//        manual_temp2.setText(s7);
//        manual_pressure2.setText(s6);
//        time_fill_lcv.setText(s5);
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
//        SharedPreferences.Editor myEdit = sharedPreferences.edit();
//        myEdit.putString("mass_flow", mass_sng.getText().toString());
//        myEdit.putString("mass", mass_cascade.getText().toString());
//        myEdit.putString("press1", pressure2_value.getText().toString());
//        myEdit.putString("temp1", temp2_value.getText().toString());
//
//        myEdit.putString("manual_pressure2", press2);
//        myEdit.putString("manual_temp2", temp2);
//        myEdit.putString("time_taken_to_fill", timeTakenToFillLCV);
////        myEdit.putString("time_after_fill", timeAfterFilling);
//        myEdit.apply();
////        Toast.makeText(MGS_After_Filling.this , "Successfully saved info ", Toast.LENGTH_SHORT).show();
////        myEdit.apply();
//    }


    public void updateData2() {


        final String lcv_id = lcvNum;
        final String station_id = mgsId;
        final String dbs_station_id = dbsId;
//        final String before_filing_at_mgs_mass_cng = mass_before_filling;
        final String after_filling_at_mgs_mfm_value_read = mfm;
        final String after_filling_at_mgs_value_pressure_gauge_read = press2;
        final String after_filling_at_mgs_value_temperature_gauge_read = temp2;
        final String after_filling_at_mgs_mass_cng = mass_cascade.getText().toString();
//        final String time_taken_to_fill_lcv = timeTakenToFillLCV;


        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATEMGS2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(MGS_After_Filling.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(MGS_After_Filling.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MGS_After_Filling.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                String lcv_from_mgs_to_dbs = "3";
                String lcv_status=lcv_id+" in Transit between"+station_id+"to"+dbs_station_id;
                params.put("lcv_status",lcv_status );
                params.put("lcv_id", lcv_id);
                params.put("station_id", station_id);
                params.put("dbs_station_id", dbs_station_id);
                params.put("operator_id_aftr_filling", username);

//                params.put("before_filing_at_mgs_mass_cng", before_filing_at_mgs_mass_cng);
                params.put("after_filling_at_mgs_mfm_img",encodedmfmimage);
                params.put("after_filling_at_mgs_temperature_gauge_img", encodedTemp2image);
                params.put("after_filling_at_mgs_pressure_gauge_img", encodedPressure2image);
                params.put("after_filling_at_mgs_mfm_value_read", after_filling_at_mgs_mfm_value_read);
                params.put("after_filling_at_mgs_value_pressure_gauge_read", after_filling_at_mgs_value_pressure_gauge_read);
                params.put("after_filling_at_mgs_value_temperature_gauge_read", after_filling_at_mgs_value_temperature_gauge_read);
                params.put("after_filling_at_mgs_mass_cng", after_filling_at_mgs_mass_cng);
//                params.put("time_taken_to_fill_lcv", time_taken_to_fill_lcv);
                params.put("lcv_from_mgs_to_dbs", lcv_from_mgs_to_dbs);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MGS_After_Filling.this);
        requestQueue.add(request);


    }

    public void notify2msg() {


        final String MGS = mgsId;
        final String LCV = lcvNum;
        final String DBS = dbsId;

        final String Message = "Readings captured after filling " + LCV + " at " + MGS + " and started its journey to " + DBS;
        StringRequest request = new StringRequest(Request.Method.POST, URL_NOTIFY3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(MGS_After_Filling.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(MGS_After_Filling.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MGS_After_Filling.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
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
                params.put("flag", "3");
                params.put("operator_id", username);


                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(MGS_After_Filling.this);
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
            encodedTempimage = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(MGS_After_Filling.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            pd.show();
            readGauge(bitmap,1,pd);
        }
        else if (requestCode == 8 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            pressure_gauge2.setImageBitmap(bitmap);
            encodedPressureimage = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(MGS_After_Filling.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            pd.show();
            readGauge(bitmap,0,pd);

        }

        else if (requestCode == 10 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mfm_image.setImageBitmap(bitmap);
            encodedmfmimage = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(MGS_After_Filling.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            //   pd.show();
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
            new Timer().schedule(ts, 30000);*/
        }
        else if (requestCode == PICK_FIRST_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            temp_gauge2.setImageURI(imageUri);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialog pd = new ProgressDialog(MGS_After_Filling.this, R.style.NewDialog);
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
        else if (requestCode == PICK_SECOND_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            pressure_gauge2.setImageURI(imageUri);
            File imageFileName = new File(getRealPathFromNAME(imageUri));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            encodedPressureimage = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(MGS_After_Filling.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            pd.show();
            readGauge(bitmap, 0, pd);
        }

        else if (requestCode == PICK_THIRD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            mfm_image.setImageURI(imageUri);
            File imageFileName = new File(getRealPathFromNAME(imageUri));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            encodedmfmimage = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(MGS_After_Filling.this, R.style.NewDialog);
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


    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MGS_After_Filling.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(MGS_After_Filling.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MGS_After_Filling.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MGS_After_Filling.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MGS_After_Filling.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
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
                                    pressure_value.setText("Pressure:" + value + "bar");
                                    manual_pressure2.setText(value);
                                }
                            }
                            else if(type==1){

                                if (!value.equals("null")) {
                                    temp_value.setText("Temperature:" + value + (char) 0x00B0 + "C");
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
        Volley.newRequestQueue(MGS_After_Filling.this).add(volleyMultipartRequest);
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