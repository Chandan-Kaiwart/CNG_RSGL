package com.apc.cng_hpcl.home.scheduling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.ImageUpload.VolleyMultipartRequest;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.transaction.MgsDbsModel;
import com.apc.cng_hpcl.home.transaction.Transaction;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.home.transaction.subtabs.TransCityGateStation.RequestPermissionCode;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;
import static com.apc.cng_hpcl.util.Constant.molarmass;

public class SchedularStationaryCascade extends AppCompatActivity {
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_Dispneser = BASE_URL_URL + "insertSchedularCascade";
    public static final String URL_MgsDbs = BASE_URL_URL + "readMgsDbs";
    public static final String URL_Cascade=BASE_URL_URL+"readAllStationaryCascade";
    private Spinner spin_temp, spin_pressure;
    public Uri mUri = null;
    String mCurrentPhotoPath;
    public static final String URL_READ_VOLUME = BASE_URL_URL + "readReorderPoint";
    private static final String ROOT_URL = "https://cng-suvidha.in/";
    public static final String UPLOAD_URL_PRESSURE_GENERAL = ROOT_URL + "/instru/Pressure/pressure_general_api.php";
    public static final String UPLOAD_URL_PRESSURE_ITECH = ROOT_URL + "/instru/Pressure/pressure_itech_api.php";
    public static final String UPLOAD_URL_PRESSURE_RADIX = ROOT_URL + "/instru/Pressure/pressure_radix_api.php";
    public static final String UPLOAD_URL_PRESSURE_WIKA = ROOT_URL + "/instru/Pressure/pressure_wika_api.php";
    public static final String UPLOAD_URL_TEMP_WIKA = ROOT_URL + "/instru/Temperature/temp_wika_api.php";
    public static final String UPLOAD_URL_TEMP_RADIX = ROOT_URL + "/instru/Temperature/temp_radix_api.php";
    public static final String UPLOAD_URL_TEMP_BAUMER = ROOT_URL + "/instru/Temperature/temp_baumer_api.php";

    Spinner select_station, select_dispenser,select_cascade;
    String[] dispenser = {"1", "2", "3", "4"};
    //    String[] dbs = {"DBS001", "DBS002", "DBS003"};
    String[] cascade = {"1", "2", "3", "4"};
    String[] temper = {"Radix", "Wikai"};
    String[] pressure = {"General", "ITEC", "Radix", "Wikai"};
    ImageView disp_image, temp_guage, pressure_guage;
    Button capture_disp, save_disp, capture4, save4, capture5, save5, proceed, submit, calculate2,
            manual_submit, manualDisp_submit;
    TextView temp_value, pressure_value, disp_value, mass_sng1, lcv_num2;
    String encodedTempimage, encodedPressureimage, encodedDispimage,
            disp_name, pressure_name, temp_name, temp, press, disp = "177.3";
    ;
    RadioButton radio_auto, radio_manual;
    int PICK_FIRST_IMAGE = 100;
    int PICK_SECOND_IMAGE = 101;
    int PICK_THIRD_IMAGE = 102;
    Double Mass1;
    String username,station;
    EditText manual_disp, manual_temp, manual_pressure;
    RelativeLayout dispenser_details;
    LinearLayout linear_edit, linear_disp_manual, disp_auto, linear_auto, linear_manual;
    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> dbs = new ArrayList<>();
    int reOrderPoint;
    String station_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedular_stationary_cascade);
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            station = extras.getString("station");

            Log.e(TAG, "Username from Transaction to DBS=" + username+","+station);
        }
        spin_temp = findViewById(R.id.spin_temp);
        spin_pressure = findViewById(R.id.spin_pressure);
        select_cascade=findViewById(R.id.select_cascade);

        temp_guage = findViewById(R.id.temp_guage);
        pressure_guage = findViewById(R.id.pressure_guage);
        capture_disp = findViewById(R.id.capture_disp);
        save_disp = findViewById(R.id.save_disp);
        capture4 = findViewById(R.id.capture4);
        save4 = findViewById(R.id.save4);
        capture5 = findViewById(R.id.capture5);
        mass_sng1 = findViewById(R.id.mass_sng1);
        save5 = findViewById(R.id.save5);
        manual_submit = findViewById(R.id.manual_submit);
        manual_temp = findViewById(R.id.manual_temp);
        manual_pressure = findViewById(R.id.manual_pressure);
        calculate2 = findViewById(R.id.calculate2);
        proceed = findViewById(R.id.proceed);

        temp_value = findViewById(R.id.temp_value);
        pressure_value = findViewById(R.id.pressure_value);

        select_station = findViewById(R.id.select_station);
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(SchedularStationaryCascade.this, android.R.layout.simple_spinner_dropdown_item, dbs);
//        select_station.setAdapter(adapter1);


//        select_dispenser = findViewById(R.id.select_dispenser);
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(SchedularStationaryCascade.this, android.R.layout.simple_spinner_dropdown_item, dispenser);
//        select_dispenser.setAdapter(adapter2);


        EnableRuntimePermission();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SchedularStationaryCascade.this, simple_spinner_item, temper);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spin_temp.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinnerArrayAdapterPressure = new ArrayAdapter<String>(SchedularStationaryCascade.this, simple_spinner_item, pressure);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spin_pressure.setAdapter(spinnerArrayAdapterPressure);

        Drawable old_temp_guage = temp_guage.getDrawable();
        Drawable old_pressure_guage = pressure_guage.getDrawable();
//        Drawable old_disp_image = disp_image.getDrawable();

//        capture_disp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 7);
//            }
//        });


        manual_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press = manual_pressure.getText().toString();
                temp = manual_temp.getText().toString();
                if (press != null && temp != null && temp_guage.getDrawable() != old_temp_guage && pressure_guage.getDrawable() != old_pressure_guage) {

//                    temp = manual_temp.getText().toString();
//                press = manual_pressure.getText().toString();
                    temp_value.setText("Temperature:" + temp + (char) 0x00B0 + "C");
                    pressure_value.setText("Pressure:" + press + " bar");
                } else {
                    Toast.makeText(v.getContext(), "Please Capture Temperature and Pressure Value and Image", Toast.LENGTH_SHORT).show();
                }
            }
        });


        calculate2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                if (press != null && temp != null && temp_guage.getDrawable() != old_temp_guage && pressure_guage.getDrawable() != old_pressure_guage) {
                    Mass1 = ((molarmass * Float.parseFloat(press) * 3000) / (83.14 * (Float.parseFloat(temp) + 273.15)));
//                    Mass1 = ((molarmass * Float.parseFloat(press) * volume) / (83.14 * (Float.parseFloat(temp) + 273.15)));

                    mass_sng1.setText(String.format("%.2f", Mass1));
                } else {
                    Toast.makeText(SchedularStationaryCascade.this, "Please Capture Temperature and Pressure Gauge reading", Toast.LENGTH_SHORT).show();
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
                        mUri = FileProvider.getUriForFile(SchedularStationaryCascade.this,
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
                        mUri = FileProvider.getUriForFile(SchedularStationaryCascade.this,
                                "com.apc.cng_hpcl.provider",
                                photoFile);
                        Log.d("PHOTOURI>>>", mUri.toString());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                    }
                }
                startActivityForResult(intent, 8);
            }
        });

//        save_disp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
//                gallery.setType("image/*");
//                startActivityForResult(gallery, PICK_FIRST_IMAGE);
//
//
//            }
//        });
        save4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_SECOND_IMAGE);


            }
        });


        save5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_THIRD_IMAGE);


            }
        });
        //readMgsDbs();
        dbs=new ArrayList<>();
        dbs.add(station);
        ArrayAdapter<String> spinnerArrayAdapterDbs = new ArrayAdapter<String>(SchedularStationaryCascade.this, simple_spinner_item, dbs);
        spinnerArrayAdapterDbs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        select_station.setAdapter(spinnerArrayAdapterDbs);
        select_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                readCascade(select_station.getSelectedItem().toString());
              //  readReorderPoint();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(TAG, "Select Station ID ");

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                Intent intent = new Intent(SchedularStationaryCascade.this, Scheduling.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }


        });
    }


  /*  private void readMgsDbs() {

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

                                for (int i = 0; i < mgsdbsModelArrayList.size(); i++) {
                                    String stationId = mgsdbsModelArrayList.get(i).getStation_Id().toLowerCase().substring(0, 3);
//                                    if (stationId.equals("dbs")) {
                                    dbs.add(mgsdbsModelArrayList.get(i).getStation_Id());
//                                    }

                                }


                                ArrayAdapter<String> spinnerArrayAdapterDbs = new ArrayAdapter<String>(SchedularStationaryCascade.this, simple_spinner_item, dbs);
                                spinnerArrayAdapterDbs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                select_station.setAdapter(spinnerArrayAdapterDbs);
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
    }*/


    private void readReorderPoint() {
        final String station_id = select_station.getSelectedItem().toString();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, URL_READ_VOLUME, new com.android.volley.Response.Listener<String>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    jsonObject.getString("station_id");
                    String dbs = jsonObject.getString("station_id");
                    String vol = jsonObject.getString("stationary_cascade_reorder_point");
                    if (vol.equals(null) || vol.equals("null") || vol.isEmpty()) {
                        Toast.makeText(SchedularStationaryCascade.this, "Please set the Reorder Point for " + dbs, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Please set the Reorder Point for  " + dbs);

                    } else if (vol != null) {

                        reOrderPoint = Integer.parseInt(vol);
                        Log.e(TAG, "reOrderPoint = " + reOrderPoint);
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

                params.put("station_id", station_id);
//                params.put("station_id",mgsId);
//                params.put("dbs_station_id", dbsId);
//                params.put("lcv_from_mgs_to_dbs", s);

                return params;
            }
        };

        queue.add(request);

    }

    private void insertData() {

        final String station_id = select_station.getSelectedItem().toString();
//        final String dispenser_id = select_dispenser.getSelectedItem().toString();
//        final String dispenser_read = disp;
        final String operator_id = username;
//        final String stationary_cascade_id = select_cascade.getSelectedItem().toString();
        final String mp_stationary_cascade_pressure_gauge_value = press;
        final String hp_stationary_cascade_pressure_gauge_value = "0";
        final String lp_stationary_cascade_pressure_gauge_value = "0";
        final String stationary_cascade_value_temperature_gauge = temp;
        final String mass_of_gas = mass_sng1.getText().toString();


        StringRequest request = new StringRequest(Request.Method.POST, URL_Dispneser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESP>>>", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(SchedularStationaryCascade.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message1 = jsonObject.getString("message");
                                String message2 = jsonObject.getString("req_message");
                                Toast.makeText(SchedularStationaryCascade.this, message1, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SchedularStationaryCascade.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                params.put("station_id", station_id);
                params.put("operator_id", operator_id);
                params.put("stationary_cascade_id", select_cascade.getSelectedItem().toString());

//                params.put("dispenser_id", dispenser_id);
//                params.put("dispenser_read", dispenser_read);

//                params.put("dispenser_img", encodedDispimage);
//                params.put("stationary_cascade_id", stationary_cascade_id);
                params.put("mp_stationary_cascade_pressure_gauge_value", mp_stationary_cascade_pressure_gauge_value);
                params.put("hp_stationary_cascade_pressure_gauge_value", hp_stationary_cascade_pressure_gauge_value);
                params.put("lp_stationary_cascade_pressure_gauge_value", lp_stationary_cascade_pressure_gauge_value);
                params.put("stationary_cascade_value_temperature_gauge", stationary_cascade_value_temperature_gauge);
                params.put("mass_of_gas", mass_of_gas);


                Log.d("RESP>>", params.toString());
                params.put("stationary_cascade_temperature_gauge_img", encodedTempimage);
                params.put("mp_stationary_cascade_pressure_gauge_img", encodedPressureimage);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SchedularStationaryCascade.this);
        requestQueue.add(request);


    }
    private void readCascade(String dbs) {
        Log.d("RESP>>>", URL_Cascade);

        StringRequest request = new StringRequest(Request.Method.POST, URL_Cascade,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESP>>>", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            if (obj.optString("error").equals("false")) {

                                List<String> cas = new ArrayList<>();
                                JSONArray dataArray = obj.getJSONArray("cascades");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    cas.add(dataArray.getString(i));
                                }
                                ArrayAdapter<String> spinnerArrayAdapterDbs = new ArrayAdapter<String>(SchedularStationaryCascade.this, simple_spinner_item, cas);
                                spinnerArrayAdapterDbs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                select_cascade.setAdapter(spinnerArrayAdapterDbs);
//                                removeSimpleProgressDialog();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SchedularStationaryCascade.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("station_id", dbs);
                Log.d("Res>>>", "getParams: "+dbs);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SchedularStationaryCascade.this);
        requestQueue.add(request);


    }

    private String encodebitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] byteofimages = byteArrayOutputStream.toByteArray();
        return (android.util.Base64.encodeToString(byteofimages, Base64.DEFAULT));
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
                    ProgressDialog pd = new ProgressDialog(SchedularStationaryCascade.this, R.style.NewDialog);
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
                    ProgressDialog pd = new ProgressDialog(SchedularStationaryCascade.this, R.style.NewDialog);
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
                    ProgressDialog pd = new ProgressDialog(SchedularStationaryCascade.this, R.style.NewDialog);
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
                    ProgressDialog pd = new ProgressDialog(SchedularStationaryCascade.this, R.style.NewDialog);
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
            ProgressDialog pd = new ProgressDialog(SchedularStationaryCascade.this, R.style.NewDialog);
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
            ProgressDialog pd = new ProgressDialog(SchedularStationaryCascade.this, R.style.NewDialog);
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
                                    Toast.makeText(SchedularStationaryCascade.this,"Gauge image not valid !",Toast.LENGTH_LONG).show();
                                }
                            }
                            else if(type==1){

                                if (!value.equals("null") && !value.isEmpty()) {
                                    temp_value.setText("Temperature:" + value + (char) 0x00B0 + "C");
                                    manual_temp.setText(value);
                                }
                                else{
                                    Toast.makeText(SchedularStationaryCascade.this,"Gauge image not valid !",Toast.LENGTH_LONG).show();

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
        Volley.newRequestQueue(SchedularStationaryCascade.this).add(volleyMultipartRequest);
    }


    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SchedularStationaryCascade.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(SchedularStationaryCascade.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SchedularStationaryCascade.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SchedularStationaryCascade.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SchedularStationaryCascade.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
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
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}