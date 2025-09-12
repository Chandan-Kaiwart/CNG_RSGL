package com.apc.cng_hpcl.home.transaction.subtabs.mgslcv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.ImageUpload.VolleyMultipartRequest;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.scheduling.SchedularStationaryCascade;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_After_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying_MFM;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

public class MGS_Before_Filling extends AppCompatActivity {
    //    private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_NOTIFY_STATUS = BASE_URL_URL + "readNotifyStatusBfrFill";
    public static final String URL_UPDATEMGS1 = BASE_URL_URL + "updateMGS1";
    public static final String URL_NOTIFY2 = BASE_URL_URL + "notify2";
    public static final String URL_READ_VOLUME = BASE_URL_URL +"readVolume";
    public static final String URL_READ_CASC_CAP = BASE_URL_URL +"readStationEquipInfo";

    private static final String readTempValue =AI_URL_Temp+"readTempValue.php";
    private static final String readPressureValue =AI_URL_Pressure+"readPressureValue.php";
    private static final String ROOT_URL = "https://cng-suvidha.in/";
    public static final String UPLOAD_URL_PRESSURE_GENERAL = ROOT_URL + "/instru/Pressure/pressure_general_api.php";
    public static final String UPLOAD_URL_PRESSURE_ITECH = ROOT_URL + "/instru/Pressure/pressure_itech_api.php";
    public static final String UPLOAD_URL_PRESSURE_RADIX = ROOT_URL + "/instru/Pressure/pressure_radix_api.php";
    public static final String UPLOAD_URL_PRESSURE_WIKA = ROOT_URL + "/instru/Pressure/pressure_wika_api.php";
    public static final String UPLOAD_URL_TEMP_WIKA = ROOT_URL + "/instru/Temperature/temp_wika_api.php";
    public static final String UPLOAD_URL_TEMP_RADIX = ROOT_URL + "/instru/Temperature/temp_radix_api.php";

    public Uri mUri = null;
    String mCurrentPhotoPath;
    private static final String BASE_URL_URL2 = BASE_URL + "mgs_dbs_read_transaction.php?apicall=";
    public static final String URL_READ_NOTE = BASE_URL_URL2 + "readNotification";
    String readMGSBrfFill = BASE_URL_URL+"readMGSBfrFill";
    EditText manual_temp, manual_pressure, manual_mfm;
    RadioButton radio_auto, radio_manual, radio_auto2, radio_manual2;
    Spinner select_mgs, select_lcv, select_dbs;
    Button manual_submit,
            capture4, save4, capture5, save5, calculate2, notify1,capture6,save6;
    String username;
    RelativeLayout before_refilling;
    LinearLayout linear_edit, linear_manual, linear_automated, linear_opt_manual_auto,
            linear_manual2, linear_automated2, linear_opt_manual_auto2;
    String encodedTempimage, encodedPressureimage, tempImage,encodedTemp2image, encodedPressure2image,
            encodedMfmImage,temp_name, pressure_name, temp1, press1, mfm1;

    LocalDateTime timeBeforeFilling;
    Context context;
    private Spinner spin_temp,spin_pressure;

    TextView temp_value, lcv_num2,mfm_value;
    TextView pressure_value;
    TextView mass_sng1, time,title6;
    ZonedDateTime zdt;
    TextView mass_cascade, mass_sng, mass_flow, time_fill_lcv;
    ImageView temp_guage, pressure_guage,mfm_gauge;
    int PICK_FIRST_IMAGE = 100;
    int PICK_SECOND_IMAGE = 101;
    int PICK_THIRD_IMAGE = 102;
    int PICK_FOURTH_IMAGE = 103;
    Double Mass1, MassFlow;
    Timestamp prevRead, timestamp;
    long differenceInMilliSeconds, differenceInHours;
    long minutes;
    int seconds;
    String timeBeforeFilling1;
    String[] temp = {"Radix", "Wikai"};
    String[] pressure = {"General", "ITEC", "Radix", "Wikai"};

    String status = "Pending", lcv_num, mgs_id, dbs_id;
    String time_val;
 int volume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgs_before_filling);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lcv_num = bundle.getString("lcv_num");
            mgs_id = bundle.getString("mgs_id");
            dbs_id = bundle.getString("dbs_id");
            username = bundle.getString("username");
        }
        spin_temp=findViewById(R.id.spin_temp);
        spin_pressure=findViewById(R.id.spin_pressure);
        manual_submit = findViewById(R.id.manual_submit);
        manual_temp = findViewById(R.id.manual_temp);
        manual_pressure = findViewById(R.id.manual_pressure);
        radio_manual = findViewById(R.id.radio_manual);
        radio_auto = findViewById(R.id.radio_auto);
        linear_opt_manual_auto = findViewById(R.id.linear_opt_manual_auto);
        linear_manual = findViewById(R.id.manual);
        linear_automated = findViewById(R.id.automated);
        capture4 = findViewById(R.id.capture4);
        save4 = findViewById(R.id.save4);
        capture5 = findViewById(R.id.capture5);
        title6 = findViewById(R.id.title6);
        save6 = findViewById(R.id.save6);
        capture6 = findViewById(R.id.capture6);
        save5 = findViewById(R.id.save5);
        calculate2 = findViewById(R.id.calculate2);
        notify1 = findViewById(R.id.notify1);
        manual_mfm=findViewById(R.id.manual_mfm);
        before_refilling = findViewById(R.id.before_refilling);
        linear_edit = findViewById(R.id.linear_edit);
        lcv_num2 = findViewById(R.id.lcv_num2);
        temp_value = findViewById(R.id.temp_value);
        pressure_value = findViewById(R.id.pressure_value);
        mass_sng1 = findViewById(R.id.mass_sng1);
        temp_guage = findViewById(R.id.temp_guage);
        pressure_guage = findViewById(R.id.pressure_guage);
        mfm_gauge=findViewById(R.id.mfm_image);
        mfm_value=findViewById(R.id.mfm_value);
        time = findViewById(R.id.time);
        lcv_num2.setText(lcv_num);

        time.setVisibility(View.GONE);
        Drawable old_temp_guage = temp_guage.getDrawable();
        Drawable old_pressure_guage = pressure_guage.getDrawable();
        Drawable old_mfm_gauge=mfm_gauge.getDrawable();

        EnableRuntimePermission();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MGS_Before_Filling.this, simple_spinner_item, temp);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spin_temp.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinnerArrayAdapterPressure = new ArrayAdapter<String>(MGS_Before_Filling.this, simple_spinner_item, pressure);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spin_pressure.setAdapter(spinnerArrayAdapterPressure);

        manual_submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                temp1 = manual_temp.getText().toString();
                press1 = manual_pressure.getText().toString();
                mfm1 = manual_mfm.getText().toString();

                if (press1 != null && temp1 != null && temp_guage.getDrawable() != old_temp_guage && pressure_guage.getDrawable() != old_pressure_guage) {

//                    temp1 = manual_temp.getText().toString();
//                    press1 = manual_pressure.getText().toString();

                    temp_value.setText("Temperature:" + temp1 + (char) 0x00B0 + "C");
                    pressure_value.setText("Pressure:" + press1 + " bar");
                    mfm_value.setText("Mass :" + mfm1 + " kg");

                } else {
                    Toast.makeText(v.getContext(), "Please Capture Temperature,Pressure and Mass Flow Rate Value and Image", Toast.LENGTH_SHORT).show();
                }
            }
        });


        capture4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mUri = FileProvider.getUriForFile(MGS_Before_Filling.this,
                                "com.apc.cng_hpcl.provider",
                                photoFile);
                        Log.d("PHOTOURI>>>", mUri.toString());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                    }
                }
                startActivityForResult(intent, 7);
            }
        });
        capture5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mUri = FileProvider.getUriForFile(MGS_Before_Filling.this,
                                "com.apc.cng_hpcl.provider",
                                photoFile);
                        Log.d("PHOTOURI>>>", mUri.toString());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                    }
                }
                startActivityForResult(intent, 8);
            }
        });
        capture6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
            }
        });

        //Upload Temperature before Filling

        save4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_FIRST_IMAGE);
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 7);

            }
        });

        //Upload Pressure before Filling

        save5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_SECOND_IMAGE);
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 8);


            }
        });
        save6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_SECOND_IMAGE);
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 8);


            }
        });


//        Calculate Mass of Cascade before filling
        calculate2.setOnClickListener(new View.OnClickListener() {
            //            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                if (press1 != null && temp1 != null && temp_guage.getDrawable() != old_temp_guage && pressure_guage.getDrawable() != old_pressure_guage) {
                    Log.e(TAG,"Volume="+volume);
                    Mass1 = ((molarmass * Float.parseFloat(press1) * volume) / (83.14 * (Float.parseFloat(temp1) + 273.15)));
                mass_sng1.setText(String.format("%.2f", Mass1));
                    timeBeforeFilling = LocalDateTime.now();
                    ZonedDateTime zdt = timeBeforeFilling.atZone(ZoneOffset.UTC);
                    time_val = zdt.toString();
                    time.setText(time_val);
                time.setVisibility(View.GONE);
                Log.e(TAG, "Time before filling" + timeBeforeFilling);
                } else {
                    Toast.makeText(v.getContext(), "Please Capture Temperature and Pressure Value and Image", Toast.LENGTH_SHORT).show();
                }

            }
        });
        getMgsrecord(username,lcv_num,mgs_id,dbs_id,"2");
        readVolume();

        notify1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Mass1 != null) {
//                    notify1msg();
//                    updateData1();
//                getMgrResponse();
                readNotification();

            }
        });
    }

    private void readNotification() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        if (!(mgs_id.equals("NA") || lcv_num.equals("NA") || dbs_id.equals("NA"))) {
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
                        if ( flag.equals("1")&& noteStatus.equals("Approved") ) {
                            Log.e(TAG, "In if Second transaction MGS");
                            notify1msg();
                            updateData1();
                        } else if (flag.equals("2") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS");
                            Toast.makeText(getApplicationContext(), "Waiting for Manager Approval Before Filling", Toast.LENGTH_SHORT).show();

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
                         else if (flag.equals("2") && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Approved Second Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num);
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(MGS_Before_Filling.this, MGS_After_Filling.class);

//                            Intent intent = new Intent(MGS_Before_Filling.this, MGS_After_Filling_MFM.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else if ((flag.equals("3") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Third Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num);
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(MGS_Before_Filling.this, TransDaughterBoosterStation.class);
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
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(MGS_Before_Filling.this, DBS_Before_Emptying_MFM.class);
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
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(MGS_Before_Filling.this, DBS_After_Emptying.class);
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
            Toast.makeText(MGS_Before_Filling.this, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
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
                    volume=Integer.parseInt(vol);

                } catch (Exception e) {
                    e.printStackTrace();
                 //   volume=0;
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

                params.put("lcv_id", lcv_num);
//                params.put("station_id",mgsId);
//                params.put("dbs_station_id", dbsId);
//                params.put("lcv_from_mgs_to_dbs", s);

                return params;
            }
        };

        queue.add(request);

    }
    private void getMgrResponse() {


        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String MGS = mgs_id;
        final String LCV = lcv_num;
        final String DBS = dbs_id;
        Log.e(TAG, "MGS=" + MGS);
        Log.e(TAG, "LCV=" + LCV);
        Log.e(TAG, "DBS=" + DBS);

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
                    if (noteStatus.equals("null") && create_date.equals("null")) {
                        Log.e(TAG, "In first loop");
                        if (Mass1 == null) {
                            Toast.makeText(getApplicationContext(), "Please Capture Temperature,Pressure and Mass Flow Rate Value and Image", Toast.LENGTH_SHORT).show();
                        } else {
                        notify1msg();
                        updateData1();}
                    } else if (minutes > 60 ) {
                        Log.e(TAG, "In if diff hours");
                        if (Mass1 == null) {
                            Toast.makeText(getApplicationContext(), "Please Capture Temperature,Pressure and Mass Flow Rate Value and Image", Toast.LENGTH_SHORT).show();
                        } else {
                        notify1msg();
                        updateData1();}
                    } else if (noteStatus.equals("Approved")) {
                        Log.e(TAG, "In MGS Before Filling Notification Approved");

                        String mass = String.format("%.2f", Mass1);
                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);

                        bundle.putString("lcv_num", lcv_num);
                        bundle.putString("mgs_id", mgs_id);
                        bundle.putString("dbs_id", dbs_id);
                        bundle.putString("timeBeforeFilling",time.getText().toString());
                        bundle.putString("mass", mass);
                        Intent intent = new Intent(MGS_Before_Filling.this, MGS_After_Filling.class);

//                        Intent intent = new Intent(MGS_Before_Filling.this, MGS_After_Filling_MFM.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else if (noteStatus.equals("Pending")) {
                        Log.e(TAG, "In MGS Before Filling Notification Approved");
                        Toast.makeText(getApplicationContext(), "Waiting for Manager Approvel", Toast.LENGTH_SHORT).show();

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
                Log.e(TAG, "Notification_MGS=" + MGS);
                Log.e(TAG, "Notification_LCV=" + LCV);
                Log.e(TAG, "Notification_DBS=" + DBS);

                params.put("Notification_LCV", LCV);
                params.put("Notification_MGS", MGS);
                params.put("Notification_DBS", DBS);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MGS_Before_Filling.this);
        queue.add(request);

    }

    private void getMgsrecord(String username, String lcv_num, String mgs_id, String dbs_id, String s) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, readMGSBrfFill, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    String update_date=jsonObject.getString("update_date");
                    String lcv_from_mgs_to_dbs=jsonObject.getString("lcv_from_mgs_to_dbs");
                    if(!update_date.equals("null")&& lcv_from_mgs_to_dbs.equals("2")) {
                        lcv_num2.setText(jsonObject.getString("lcv_id"));
                        temp_value.setText("Temperature:" + jsonObject.getString("before_filing_at_mgs_value_temperature_gauge_read") + (char) 0x00B0 + "C");
                        pressure_value.setText("Pressure:" + jsonObject.getString("before_filing_at_mgs_value_pressure_gauge_read") + "bar");
                        manual_temp.setEnabled(false);
                        manual_pressure.setEnabled(false);
                        manual_temp.setText(jsonObject.getString("before_filing_at_mgs_value_temperature_gauge_read"));
                        manual_pressure.setText(jsonObject.getString("before_filing_at_mgs_value_pressure_gauge_read"));
//                        mass_sng1.setText(jsonObject.getString("before_filing_at_mgs_mass_cng"));
//                        time.setText(jsonObject.getString("update_date"));

                        String tempImg= jsonObject.getString("before_filing_at_mgs_temperature_gauge_img");
                        String url_pressImg= jsonObject.getString("before_filing_at_mgs_pressure_gauge_img");
                        Picasso.get().load(tempImg).placeholder(R.drawable.temp_guage).into(temp_guage);
                        Picasso.get().load(url_pressImg).placeholder(R.drawable.pressure_guage).into(pressure_guage);
//                        Glide.with(getApplicationContext()).load("before_filing_at_mgs_temperature_gauge_img").into(temp_guage);
//                        Glide.with(getApplicationContext()).load("before_filing_at_mgs_pressure_gauge_img").into(pressure_guage);
                    }
//                    else if(!update_date.equals("null")&& lcv_from_mgs_to_dbs.equals("3")){
//                        Bundle bundle = new Bundle();
//                        bundle.putString("username", username);
//                        bundle.putString("lcv_num", lcv_num);
//                        bundle.putString("mgs_id", mgs_id);
//                        bundle.putString("dbs_id", dbs_id);
//                        Intent intent = new Intent(MGS_Before_Filling.this, MGS_After_Filling_MFM.class);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        finish();
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

                params.put("operator_id", username);
                params.put("lcv_id", lcv_num);
                params.put("station_id",mgs_id);
                params.put("dbs_station_id", dbs_id);
                return params;
            }
        };

        queue.add(request);

    }

    public void updateData1() {
        final String lcv_id = lcv_num;
        final String station_id = mgs_id;
        final String dbs_station_id = dbs_id;
        final String before_filing_at_mgs_value_pressure_gauge_read = press1;
        final String before_filing_at_mgs_value_temperature_gauge_read = temp1;
        final String before_filing_at_mgs_value_mfm_read = mfm1;

        final String before_filing_at_mgs_mass_cng = mass_sng1.getText().toString();


        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATEMGS1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("RESP>>>1", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(MGS_Before_Filling.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("RESP>>>1", jsonObject.toString());

                                String message = jsonObject.getString("message");
                                Toast.makeText(MGS_Before_Filling.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            Log.d("RESP>>>1", e.toString());

                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MGS_Before_Filling.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();




                String lcv_from_mgs_to_dbs = "2";
                String lcv_status=lcv_id+" filling at"+station_id;
                params.put("lcv_status",lcv_status );
                params.put("lcv_id", lcv_id);
                params.put("station_id", station_id);
                params.put("dbs_station_id", dbs_station_id);
                params.put("operator_id_bfr_filling", username);


//                params.put("parameter_name", new DataPart("image_image.jpg", getFileDataFromDrawable( imageView.getDrawable()), "image/jpeg"));
                params.put("before_filing_at_mgs_temperature_gauge_img", encodedTempimage);
                params.put("before_filing_at_mgs_pressure_gauge_img", encodedPressureimage);
//                params.put("after_filling_at_mgs_temperature_gauge_img", encodedTemp2image);
//                params.put("after_filling_at_mgs_pressure_gauge_img", encodedPressure2image);
                params.put("before_filing_at_mgs_value_pressure_gauge_read", before_filing_at_mgs_value_pressure_gauge_read);
                params.put("before_filing_at_mgs_value_temperature_gauge_read", before_filing_at_mgs_value_temperature_gauge_read);
                params.put("before_filing_at_mgs_mass_cng", before_filing_at_mgs_mass_cng);

//                params.put("after_filling_at_mgs_value_pressure_gauge_read", after_filling_at_mgs_value_pressure_gauge_read);
//                params.put("after_filling_at_mgs_value_temperature_gauge_read", after_filling_at_mgs_value_temperature_gauge_read);
//                params.put("after_filling_at_mgs_mass_cng", after_filling_at_mgs_mass_cng);
//                params.put("time_taken_to_fill_lcv", time_taken_to_fill_lcv);
                params.put("lcv_from_mgs_to_dbs", lcv_from_mgs_to_dbs);
                params.put("before_filing_at_mgs_mfm_img", encodedMfmImage);
                params.put("before_filing_at_mgs_value_mfm_read", before_filing_at_mgs_value_mfm_read);


                Log.d("RESP>>>2", before_filing_at_mgs_value_temperature_gauge_read);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MGS_Before_Filling.this);
        requestQueue.add(request);


    }

    public void notify1msg() {


        final String MGS = mgs_id;
        final String LCV = lcv_num;
        final String DBS = dbs_id;

        final String Message = "Readings captured before filling " + LCV + " at " + MGS;
        StringRequest request = new StringRequest(Request.Method.POST, URL_NOTIFY2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(MGS_Before_Filling.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(MGS_Before_Filling.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MGS_Before_Filling.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
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
                params.put("flag", "2");
                params.put("operator_id", username);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(MGS_Before_Filling.this);
        requestQueue.add(request);


    }

    private String encodebitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] byteofimages = byteArrayOutputStream.toByteArray();
        return (android.util.Base64.encodeToString(byteofimages, Base64.DEFAULT));
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        //First decode with inJustDecodeBounds=true to check dimensions

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image

        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..

            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            // There are no request codes
            // doSomeOperations();
            Uri uriImage = null;
            Bitmap rotatedBitmap = null;

            try {
                uriImage = data.getData();
                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
                    ExifInterface ei = null;
                    try {
                        Log.d("MURI>>", "URI>>"+mUri);
                        ei = new ExifInterface(mUri.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    temp_guage.setImageBitmap(rotatedBitmap);
                    encodedTempimage = encodebitmap(rotatedBitmap);
                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    readGauge(rotatedBitmap, 1, pd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
                    ExifInterface ei = null;

                    Log.d("MURI>>", "URI>>"+mUri);
                    try (InputStream inputStream = getContentResolver().openInputStream(mUri)) {
                        ExifInterface exif = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            exif = new ExifInterface(inputStream);
                        }
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);


                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    temp_guage.setImageBitmap(rotatedBitmap);
                    encodedTempimage = encodebitmap(rotatedBitmap);
                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    readGauge(rotatedBitmap, 1, pd);

                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }

                e.printStackTrace();

            }




        }

        else if (requestCode == 8 && resultCode == RESULT_OK) {
            // There are no request codes
            // doSomeOperations();
            Uri uriImage = null;
            Bitmap rotatedBitmap = null;

            try {
                uriImage = data.getData();
                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
                    ExifInterface ei = null;
                    try {
                        Log.d("MURI>>", "URI>>"+mUri);
                        ei = new ExifInterface(mUri.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    pressure_guage.setImageBitmap(rotatedBitmap);
                    encodedPressureimage = encodebitmap(rotatedBitmap);
                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    readGauge(rotatedBitmap, 0, pd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
                    ExifInterface ei = null;

                    Log.d("MURI>>", "URI>>"+mUri);
                    try (InputStream inputStream = getContentResolver().openInputStream(mUri)) {
                        ExifInterface exif = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            exif = new ExifInterface(inputStream);
                        }
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);


                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    pressure_guage.setImageBitmap(rotatedBitmap);
                    encodedPressureimage = encodebitmap(rotatedBitmap);
                    ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    readGauge(rotatedBitmap, 0, pd);

                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }

                e.printStackTrace();

            }




        }

        else if (requestCode == PICK_FIRST_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            temp_guage.setImageURI(imageUri);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
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
            pressure_guage.setImageURI(imageUri);
            File imageFileName = new File(getRealPathFromNAME(imageUri));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            encodedPressureimage = encodebitmap(bitmap);
            ProgressDialog pd = new ProgressDialog(MGS_Before_Filling.this, R.style.NewDialog);
            pd.setMessage("AI Engine is Processing");
            pd.show();
            readGauge(bitmap, 0, pd);
        }


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
        if (ActivityCompat.shouldShowRequestPermissionRationale(MGS_Before_Filling.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(MGS_Before_Filling.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MGS_Before_Filling.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MGS_Before_Filling.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MGS_Before_Filling.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
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

        String url="";
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
        Log.e(TAG, "In readCylinderNum encodedCylinderNum = " + encodedPressure2image);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "In readCylinderNum response received = " + response);

//                    Toast.makeText(MainActivity.this, "Response=" + response, Toast.LENGTH_SHORT).show();
                    String pressureValue=jsonObject.getString("meter_reading");
                    if (!pressureValue.equals("null")) {
                        pressure_value.setText("Pressure:" + pressureValue + "bar");
                        manual_pressure.setText(pressureValue);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pressure_value.setText("Network Error");
                manual_pressure.setText("Network Error");
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

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url="";
        if (spin_temp.getSelectedItemPosition() == 1) {
            url = UPLOAD_URL_TEMP_RADIX;
        } else {
            url = UPLOAD_URL_TEMP_WIKA;
        }
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "In readCylinderNum response received = " + response);

//                    Toast.makeText(MainActivity.this, "Response=" + response, Toast.LENGTH_SHORT).show();
                    String tempValue=jsonObject.getString("meter_reading");
                    if (!tempValue.equals("null")) {
                        temp_value.setText("Temperature:" + tempValue + (char) 0x00B0 + "C");
                        manual_temp.setText(tempValue);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                temp_value.setText("Network Error");
                manual_temp.setText("Network Error");
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

                                if (!value.equals("null") && !value.isEmpty()) {
                                    pressure_value.setText("Pressure:" + value + "bar");
                                    manual_pressure.setText(value);
                                }
                                else{
                                    Toast.makeText(MGS_Before_Filling.this,"Gauge image not valid !",Toast.LENGTH_LONG).show();
                                }
                            }
                            else if(type==1){

                                if (!value.equals("null") && !value.isEmpty()) {
                                    temp_value.setText("Temperature:" + value + (char) 0x00B0 + "C");
                                    manual_temp.setText(value);
                                }
                                else{
                                    Toast.makeText(MGS_Before_Filling.this,"Gauge image not valid !",Toast.LENGTH_LONG).show();

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

                inputs.put("customer_ca_no","luag"+System.currentTimeMillis());
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
        Volley.newRequestQueue(MGS_Before_Filling.this).add(volleyMultipartRequest);
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
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}