package com.apc.cng_hpcl.home.transaction.subtabs.mgslcv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.apc.cng_hpcl.home.transaction.Transaction;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_After_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying_MFM;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.home.transaction.subtabs.TransCityGateStation.RequestPermissionCode;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class MGS_After_Filling_MFM extends AppCompatActivity {

    TextView lcv_num,mfm_value;
    ImageView mfm_image;
    Button capture_mfm,save_mfm,manual_submit2,notify;
    EditText manual_mfm;
    String username,lcvNum, mgsId, dbsId,mfm,encodedMFMimage;
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    String readMGSBrfFill = BASE_URL_URL + "readMGSBfrFill";
    private static final String BASE_URL_URL2 = BASE_URL + "mgs_dbs_read_transaction.php?apicall=";
    public static final String URL_READ_NOTE = BASE_URL_URL2 + "readNotification";
    public static final String URL_NOTIFY3 = BASE_URL_URL + "notify3";
    public static final String URL_UPDATEMGSMFM = BASE_URL_URL + "updateMGSMFM";
    String status = "Pending";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgs_after_filling_mfm);

        lcv_num=findViewById(R.id.lcv_num);
        mfm_value=findViewById(R.id.mfm_value);
        mfm_image=findViewById(R.id.mfm_image);
        capture_mfm=findViewById(R.id.capture_mfm);
        save_mfm=findViewById(R.id.save_mfm);
        manual_submit2=findViewById(R.id.manual_submit2);
        notify=findViewById(R.id.notify);
        manual_mfm=findViewById(R.id.manual_mfm);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
            lcvNum = bundle.getString("lcv_num");
            mgsId = bundle.getString("mgs_id");
            dbsId = bundle.getString("dbs_id");
        }
        EnableRuntimePermission();


        lcv_num.setText(lcvNum);
        Drawable old_mfm_image = mfm_image.getDrawable();
        manual_submit2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                mfm = manual_mfm.getText().toString();

                if ( mfm != null && mfm_image.getDrawable() != old_mfm_image
                ) {
                    mfm_value.setText( mfm + "Kg/hr");

                } else {
                    Toast.makeText(v.getContext(), "Please Capture  Mass Flow Meter Value and Image", Toast.LENGTH_SHORT).show();
                }
            }

        });
        //Capture Temperature after Filling

        capture_mfm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 9);
            }
        });
        //Capture Pressure after Filling


        save_mfm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 9);
            }
        });

        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mfm == null) {
                    Toast.makeText(getApplicationContext(), "Please Mass Flow Meter Value and Image", Toast.LENGTH_SHORT).show();
                } else {
                    readNotification();
                }
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
                            Intent intent = new Intent(MGS_After_Filling_MFM.this, Transaction.class);
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
                            Intent intent = new Intent(MGS_After_Filling_MFM.this, TransDaughterBoosterStation.class);
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
                            Intent intent = new Intent(MGS_After_Filling_MFM.this, DBS_Before_Emptying_MFM.class);
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
                            Intent intent = new Intent(MGS_After_Filling_MFM.this, DBS_After_Emptying.class);
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
            Toast.makeText(MGS_After_Filling_MFM.this, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }

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
                                Toast.makeText(MGS_After_Filling_MFM.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(MGS_After_Filling_MFM.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MGS_After_Filling_MFM.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
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


        RequestQueue requestQueue = Volley.newRequestQueue(MGS_After_Filling_MFM.this);
        requestQueue.add(request);


    }
    public void updateData2() {


        final String lcv_id = lcvNum;
        final String station_id = mgsId;
        final String dbs_station_id = dbsId;
//        final String before_filing_at_mgs_mass_cng = mass_before_filling;
        final String after_filling_at_mgs_mfm_value_read = mfm;

//        final String time_taken_to_fill_lcv = timeTakenToFillLCV;


        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATEMGSMFM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(MGS_After_Filling_MFM.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(MGS_After_Filling_MFM.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MGS_After_Filling_MFM.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                String lcv_from_mgs_to_dbs = "3";
                params.put("lcv_id", lcv_id);
                params.put("station_id", station_id);
                params.put("dbs_station_id", dbs_station_id);
                params.put("operator_id_aftr_filling", username);

//                params.put("before_filing_at_mgs_mass_cng", before_filing_at_mgs_mass_cng);
                params.put("after_filling_at_mgs_mfm_img", encodedMFMimage);
                params.put("after_filling_at_mgs_mfm_value_read", after_filling_at_mgs_mfm_value_read);

                params.put("lcv_from_mgs_to_dbs", lcv_from_mgs_to_dbs);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MGS_After_Filling_MFM.this);
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
        if (requestCode == 9 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mfm_image.setImageBitmap(bitmap);
            encodedMFMimage = encodebitmap(bitmap);
        }
    }

    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MGS_After_Filling_MFM.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(MGS_After_Filling_MFM.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MGS_After_Filling_MFM.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MGS_After_Filling_MFM.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MGS_After_Filling_MFM.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
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