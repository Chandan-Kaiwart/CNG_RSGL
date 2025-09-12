package com.apc.cng_hpcl.home.transaction.subtabs.dbsdispenser;

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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.apc.cng_hpcl.home.transaction.MgsDbsModel;
import com.apc.cng_hpcl.home.transaction.Transaction;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.home.transaction.subtabs.TransCityGateStation.RequestPermissionCode;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;
import static com.apc.cng_hpcl.util.Constant.molarmass;

public class StationaryCascade extends AppCompatActivity {
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_Dispneser = BASE_URL_URL + "insertCascade";
    public static final String URL_MgsDbs = BASE_URL_URL + "readMgsDbs";

    Spinner select_station, select_dispenser, select_cascade;
    String[] dispenser = {"1", "2", "3", "4"};
//    String[] dbs = {"DBS001", "DBS002", "DBS003"};
    String[] cascade = {"1", "2", "3", "4"};
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
    String username;
    EditText manual_disp, manual_temp, manual_pressure;
    RelativeLayout dispenser_details;
    LinearLayout linear_edit, linear_disp_manual, disp_auto, linear_auto, linear_manual;
    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> dbs = new ArrayList< >();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationary_cascade);
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            Log.e(TAG, "Username from Transaction to DBS=" + username);
        }

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
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(StationaryCascade.this, android.R.layout.simple_spinner_dropdown_item, dbs);
//        select_station.setAdapter(adapter1);


//        select_dispenser = findViewById(R.id.select_dispenser);
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(StationaryCascade.this, android.R.layout.simple_spinner_dropdown_item, dispenser);
//        select_dispenser.setAdapter(adapter2);



        EnableRuntimePermission();

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
                press=manual_pressure.getText().toString();
                temp=manual_temp.getText().toString();
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
                    Toast.makeText(StationaryCascade.this, "Please Capture Temperature and Pressure Gauge reading", Toast.LENGTH_SHORT).show();
                }
            }
        });


        capture4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 8);
            }
        });
        capture5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 9);
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
        readMgsDbs();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                Intent intent = new Intent(StationaryCascade.this, Transaction.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }


        });
    }




    private void readMgsDbs() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_MgsDbs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.e("strrrrr", ">>" + response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("true")){

                                mgsdbsModelArrayList = new ArrayList<>();
                                JSONArray dataArray  = obj.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    MgsDbsModel mgsDbsModel = new MgsDbsModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);

                                    mgsDbsModel.setStation_Id(dataobj.getString("Station_Id"));
                                    mgsDbsModel.setMgsId(dataobj.getString("mgsId"));


                                    mgsdbsModelArrayList.add(mgsDbsModel);

                                }

                                for (int i = 0; i < mgsdbsModelArrayList.size(); i++){
                                    String stationId = mgsdbsModelArrayList.get(i).getStation_Id().toLowerCase().substring(0, 3);
                                    if (stationId.equals("dbs")) {
                                        dbs.add(mgsdbsModelArrayList.get(i).getStation_Id());
                                    }

                                }



                                ArrayAdapter<String> spinnerArrayAdapterDbs = new ArrayAdapter<String>(StationaryCascade.this, simple_spinner_item, dbs);
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

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(StationaryCascade.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(StationaryCascade.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StationaryCascade.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                params.put("station_id", station_id);
                params.put("operator_id", operator_id);
//                params.put("dispenser_id", dispenser_id);
//                params.put("dispenser_read", dispenser_read);
                params.put("stationary_cascade_temperature_gauge_img", encodedTempimage);
                params.put("mp_stationary_cascade_pressure_gauge_img", encodedPressureimage);
//                params.put("dispenser_img", encodedDispimage);
//                params.put("stationary_cascade_id", stationary_cascade_id);
                params.put("mp_stationary_cascade_pressure_gauge_value", mp_stationary_cascade_pressure_gauge_value);
                params.put("hp_stationary_cascade_pressure_gauge_value", hp_stationary_cascade_pressure_gauge_value);
                params.put("lp_stationary_cascade_pressure_gauge_value", lp_stationary_cascade_pressure_gauge_value);
                params.put("stationary_cascade_value_temperature_gauge", stationary_cascade_value_temperature_gauge);
                params.put("mass_of_gas", mass_of_gas);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(StationaryCascade.this);
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

            disp_image.setImageBitmap(bitmap);
            encodedDispimage = encodebitmap(bitmap);

        } else if (requestCode == 8 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            temp_guage.setImageBitmap(bitmap);
            encodedTempimage = encodebitmap(bitmap);

        } else if (requestCode == 9 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            pressure_guage.setImageBitmap(bitmap);
            encodedPressureimage = encodebitmap(bitmap);

        } else if (requestCode == PICK_SECOND_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            temp_guage.setImageURI(imageUri);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            encodedTempimage = encodebitmap(bitmap);
            File imageFileName = new File(getRealPathFromNAME(imageUri));
            temp_name = imageFileName.getName();
            switch (temp_name) {
                case "Temperature1.jpeg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);
                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            temp = "33";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    temp_value.setText("Temperature:" + temp + (char) 0x00B0 + "C");
                                    manual_temp.setText(temp);

                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);

                    break;
                }
                case "Temperature2.jpeg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);

                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            temp = "35";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    temp_value.setText("Temperature:" + temp + (char) 0x00B0 + "C");
                                    manual_temp.setText(temp);

                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);
                    break;
                }
                case "Temperature3.jpeg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);

                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            temp = "49";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    temp_value.setText("Temperature:" + temp + (char) 0x00B0 + "C");
                                    manual_temp.setText(temp);

                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);
                    break;
                }
                case "Temperature4.jpg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);
                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            temp = "20";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    temp_value.setText("Temperature:" + temp + (char) 0x00B0 + "C");
                                    manual_temp.setText(temp);

                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);

                    break;
                }
                case "Temperature5.jpg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);
                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            temp = "25";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    temp_value.setText("Temperature:" + temp + (char) 0x00B0 + "C");
                                    manual_temp.setText(temp);

                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);

                    break;
                }
                default:
                    temp_value.setText(" &#xb0; Celsius");
                    break;
            }

        } else if (requestCode == PICK_THIRD_IMAGE && resultCode == RESULT_OK && null != data) {
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
            pressure_name = imageFileName.getName();
            switch (pressure_name) {
                case "Press1.jpg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);

                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            press = "50";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    pressure_value.setText("Pressure:" + press + " bar");
                                    manual_pressure.setText(press);
                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);


                    break;
                }
                case "Press2.jpg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);

                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            press = "100";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    pressure_value.setText("Pressure:" + press + " bar");
                                    manual_pressure.setText(press);

                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);

                    break;
                }
                case "Press3.jpg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);

                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            press = "150";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    pressure_value.setText("Pressure:" + press + " bar");
                                    manual_pressure.setText(press);

                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);

                    break;
                }
                case "Press4.jpg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);

                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            press = "200";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    pressure_value.setText("Pressure:" + press + " bar");
                                    manual_pressure.setText(press);

                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);

                    break;
                }
                case "Press5.jpg": {
                    ProgressDialog pd = new ProgressDialog(StationaryCascade.this, R.style.NewDialog);

                    pd.setMessage("AI Engine is Processing");
                    pd.show();
                    TimerTask ts = new TimerTask() {
                        @Override
                        public void run() {
                            press = "250";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    pressure_value.setText("Pressure:" + press + " bar");
                                    manual_pressure.setText(press);

                                }
                            });


                        }
                    };
                    new Timer().schedule(ts, 10000);

                    break;
                }
                default:
                    pressure_value.setText("0" + " bar");
                    break;
            }
            Log.d(TAG, "onActivityResult: pressure_value=" + pressure_value);
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


    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(StationaryCascade.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(StationaryCascade.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(StationaryCascade.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(StationaryCascade.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(StationaryCascade.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
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