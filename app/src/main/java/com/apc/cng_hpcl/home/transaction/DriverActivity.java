package com.apc.cng_hpcl.home.transaction;

import static android.content.ContentValues.TAG;

import static com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation.URL_READ_NOTE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_After_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DriverActivity extends AppCompatActivity {
EditText veh1,veh2,veh3,veh4;
Button sub1,sub2;
TextView detTv;
String username,lcv_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_driver);
        veh1=findViewById(R.id.veh1);
        veh2=findViewById(R.id.veh2);
        veh3=findViewById(R.id.veh3);
        veh4=findViewById(R.id.veh4);
        sub1=findViewById(R.id.button1);
        sub2=findViewById(R.id.button2);
        detTv=findViewById(R.id.textView3);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            lcv_num = extras.getString("lcv_num");

          //  Log.e(TAG, "Username from Transaction to DBS=" + username);
        }
        sub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lcv1=veh1.getText().toString().trim();
                String lcv2=veh2.getText().toString().trim();
                String lcv3=veh3.getText().toString().trim();
                String lcv4=veh4.getText().toString().trim();
                String lcv=lcv1+lcv2+lcv3+lcv4;
                if(lcv1.length()<2 && lcv2.length()<2 && lcv3.length()<2 && lcv4.length()<2){
                    Toast.makeText(DriverActivity.this,"Enter proper vehicle !",Toast.LENGTH_LONG).show();
                }
                else{
                    readLcv(lcv);
                }

            }
        });



    }
    private void readLcv(String lcv) {

        StringRequest request = new StringRequest(Request.Method.POST, "BASE_URLmsg_dbs_transaction.php?apicall=readLCVStage",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DriverActivity.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lcv_id", lcv);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(DriverActivity.this);
        requestQueue.add(request);


    }
    private void readNotification(String mgs_id,String lcv_num_val,String dbs_id,String username) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        if (!(mgs_id.equals("NA") || lcv_num_val.equals("NA") || dbs_id.equals("NA"))) {
            StringRequest request = new StringRequest(Request.Method.POST, URL_READ_NOTE, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(TAG, "getNotificationResponse = " + response);
                        JSONObject jsonObject = new JSONObject(response);



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
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(DriverActivity.this, MGS_Before_Filling.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else if (flag.equals("2") && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Approved Second Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(DriverActivity.this, MGS_After_Filling.class);

//                           Intent intent = new Intent(TransMotherGasStation.this, MGS_After_Filling_MFM.class);
                            intent.putExtras(bundle);
                         //   startActivity(intent);
                          //  finish();
                        } else if ((flag.equals("3") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Third Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(DriverActivity.this, TransDaughterBoosterStation.class);
                            intent.putExtras(bundle);
                          //  startActivity(intent);
                          //  finish();
                        }

                        else if ((flag.equals("4") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(DriverActivity.this, DBS_Before_Emptying.class);

//                           Intent intent = new Intent(TransMotherGasStation.this, DBS_Before_Emptying_MFM.class);
                            intent.putExtras(bundle);
                          //  startActivity(intent);
                          //  finish();
                        }
                        else if ((flag.equals("5") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(getApplicationContext(), "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", mgs_id);
                            bundle.putString("dbs_id", dbs_id);
                            Intent intent = new Intent(DriverActivity.this, DBS_After_Emptying.class);
                            intent.putExtras(bundle);
                       //     startActivity(intent);
                        //    finish();
                        }  else  {
                            Log.e(TAG, "In if First transaction MGS");
                          //  notifymsg();
                        //    insertData();
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
                    params.put("Notification_MGS", mgs_id);
                    params.put("Notification_DBS", dbs_id);


                    return params;
                }
            };

            queue.add(request);
        } else {
            Toast.makeText(DriverActivity.this, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }

    }


}