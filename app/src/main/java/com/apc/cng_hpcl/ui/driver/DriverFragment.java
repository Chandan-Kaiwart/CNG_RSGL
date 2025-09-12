package com.apc.cng_hpcl.ui.driver;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation.URL_READ_NOTE;
import static com.apc.cng_hpcl.ui.login.LoginFragment.URL_LOGIN;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.HomeAdmin;
import com.apc.cng_hpcl.home.HomeManager;
import com.apc.cng_hpcl.home.HomeOperator;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_After_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling;
import com.apc.cng_hpcl.util.ProgressDia;
import com.apc.cng_hpcl.util.ProgressDiaLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DriverFragment extends Fragment {
    AutoCompleteTextView veh1;
    EditText    veh2,veh3,veh4,state1;
    Button sub1,sub2,sub3;
    TextView detTv;
    String mgs,dbs;
    Context mContext;
    View root;
    CardView mgsCard;
    Spinner mgsSpinner;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_driver, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        veh1=root.findViewById(R.id.veh1);
        veh2=root.findViewById(R.id.veh2);
        veh3=root.findViewById(R.id.veh3);
        veh4=root.findViewById(R.id.veh4);
        mgsCard=root.findViewById(R.id.mgsCard);
        state1=root.findViewById(R.id.state1);
        sub1=root.findViewById(R.id.button1);
        sub2=root.findViewById(R.id.button2);
        sub3=root.findViewById(R.id.button3);
        detTv=root.findViewById(R.id.textView3);
        mgsSpinner=root.findViewById(R.id.select_mgs);
        veh1.setFilters(new InputFilter[] {new InputFilter.AllCaps()});       // readAllLcv();
        sub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lcv1=veh1.getText().toString().trim();
                String lcv2=veh2.getText().toString().trim();
                String lcv3=veh3.getText().toString().trim();
                String lcv4=veh4.getText().toString().trim();
                String lcv=lcv1+lcv2+lcv3+lcv4;
                readLcv(lcv);
              /*  if(lcv1.length()<2 && lcv2.length()<2 && lcv3.length()<2 && lcv4.length()<2){
                    Toast.makeText(mContext,"Enter proper vehicle !",Toast.LENGTH_LONG).show();
                }
                else{
                    readLcv(lcv);
                }*/

            }
        });

    }
    private void readLcv(String lcv) {
        ProgressDiaLoading pd=new ProgressDiaLoading();
        pd.show(getChildFragmentManager(),"progress");

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL+"msg_dbs_transaction.php?apicall=readLCVStage",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Log.d("RESP>>", response);
                        try {

                            JSONObject obj=new JSONObject(response);
                            boolean err=obj.getBoolean("error");

                            if(err){
                                Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                hideKeyboard(requireActivity());
                                int stage=  obj.getInt("stage");

                                if(stage==0){
                                    mgsCard.setVisibility(View.VISIBLE);
                                    sub3.setVisibility(View.GONE);
                                 //   state1.setVisibility(View.VISIBLE);
                                    sub2.setVisibility(View.VISIBLE);
                                    sub2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            state1.setText(mgsSpinner.getSelectedItem().toString());

                                                notifymsg("",state1.getText().toString().trim(),"",lcv,"pending");


                                        }
                                    });

                                }
                                else if(stage==3|| stage==6 || stage== 9){
                                    mgsCard.setVisibility(View.GONE);

                                    sub2.setVisibility(View.VISIBLE);
                                    String source= obj.getString("source");
                                    String dest= obj.getString("destination");
                                    if(!dest.trim().isEmpty()&&!dest.equals("null")){
                                        detTv.setVisibility(View.VISIBLE);
                                        detTv.setText("Destination = "+dest);
                                    }
                                    else{
                                        detTv.setVisibility(View.VISIBLE);
                                        detTv.setText("current location = "+source);

                                    }
                                    sub2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            boolean isHopped = false;
                                            if(stage==3){
                                                mgs=source;
                                                dbs=dest;
                                            }
                                            else{
                                                try {
                                                    isHopped = obj.getBoolean("isHopped");
                                                    if(isHopped){
                                                        mgs=source;
                                                        dbs=dest;
                                                    }
                                                    else{
                                                        mgs=dest;
                                                        dbs=source;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    mgs=dest;
                                                    dbs=source;
                                                }



                                            }
                                            try {
                                                readNotification(obj.getString("transaction_id"),lcv,dest,source,"admsumit",isHopped);
                                            } catch (JSONException e) {
                                                readNotification("",lcv,dest,source,"admsumit",isHopped);
                                                e.printStackTrace();
                                                pd.dismiss();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Log.d("here>>", "else1");
                                    mgsCard.setVisibility(View.GONE);

                                    String source= obj.getString("source");
                                    String dest= obj.getString("destination");
                                    sub2.setVisibility(View.VISIBLE);


                                    if(!dest.trim().isEmpty()&&!dest.equals("null")){
                                        if(stage==1||stage==2){
                                            mgs=source;
                                            userLogin(mgs);
                                        }
                                        else{
                                            dbs=source;
                                            userLogin(dbs);
                                        }

                                    }
                                    else{
                                        userLogin(source);


                                    }
                                 /*   sub2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            readNotification("",lcv,dbs,"admsumit",false);

                                            //   notifymsg("",state1.getText().toString().trim(),"",lcv,"pending");

                                        }
                                    });*/



                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            pd.dismiss();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                if (error instanceof TimeoutError) {
                    // Handle timeout error
            Toast.makeText(mContext, "TimeoutError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    // Handle no connection error
                Toast.makeText(mContext, "NoConnectionError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    // Handle authentication failure error
                  Toast.makeText(mContext, "AuthFailureError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    // Handle server error
               Toast.makeText(mContext, "ServerError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    // Handle network error
              Toast.makeText(mContext, "NetworkError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    // Handle parse error
               Toast.makeText(mContext, "ParseError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    // Handle other errors
          Toast.makeText(mContext, "UnknownError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }

                // Print the full stack trace for more details
                error.printStackTrace();
          //      progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lcv_id", lcv);
                Log.d("PARAMS>>", params.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // Initial timeout in milliseconds (30 seconds)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Number of retries (default is 1)
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier (default is 1.0)
        ));
        requestQueue.add(request);


    }
/*
    private void readAllLcv() {

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL+"v2/app/get_lcv.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESP>>", response);



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(request);


    }
*/

    private void reachedLcv(String lcv) {
        Log.d("REACHED>>", "1");


        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL+"reached_at_mgs.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("REACHED>>", "2");

                        Log.d("RESP>>", response);



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("REACHED>>", "3");
                Log.d("REACHED>>", error.toString());


                //     Toast.makeText(mContext, "failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("REACHED>>", "4");

                params.put("lcv_id", lcv);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(request);


    }

    private void readNotification(String transId,String lcv_num_val,String dest,String source,String username,boolean isHopped) {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        if (!(lcv_num_val.equals("NA") || dest.equals("NA"))) {
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
                            Toast.makeText(mContext, "Waiting for Manager Approval", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("2") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(mContext, "Waiting for Manager Approval Before Filling", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("3") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(mContext, "Waiting for Manager Approval After Filling", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("4") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(mContext, "Waiting for Manager Approval Before Emptying", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("5") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(mContext, "Waiting for Manager Approval After Emptying", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("6") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(mContext, "Waiting for Manager Approval Before Emptying", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("7") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(mContext, "Waiting for Manager Approval After Emptying", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("8") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(mContext, "Waiting for Manager Approval Before Emptying", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("9") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(mContext, "Waiting for Manager Approval After Emptying", Toast.LENGTH_SHORT).show();

                        }




                        else if (flag.equals("1") && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Approved First Level MGS");

                            Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", source);
                            bundle.putString("dbs_id", dest);
                            Intent intent = new Intent(mContext, MGS_Before_Filling.class);
                            intent.putExtras(bundle);

                          //  startActivity(intent);
                         //   finish();
                        } else if (flag.equals("2") && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Approved Second Level MGS");

                            Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", source);
                            bundle.putString("dbs_id", dest);
                            Intent intent = new Intent(mContext, MGS_After_Filling.class);

//                           Intent intent = new Intent(TransMotherGasStation.this, MGS_After_Filling_MFM.class);
                            intent.putExtras(bundle);

                            //   startActivity(intent);
                            //  finish();
                        } else if ((flag.equals("3") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Third Level MGS");

                     //       Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", source);
                            bundle.putString("dbs_id", dest);
                            Intent intent = new Intent(mContext, TransDaughterBoosterStation.class);
                            intent.putExtras(bundle);
                            if(dest.isEmpty()||dest.equals("null")){
                                Toast.makeText(mContext,"LCV not scheduled !",Toast.LENGTH_LONG).show();
                            }
                            else{
                                notifydbs(source,lcv_num_val,dest,transId,"4");
                                insertDbs(lcv_num_val,dest,username,transId);

                            }

                            //  startActivity(intent);
                            //  finish();
                        }

                        else if ((flag.equals("4") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", source);
                            bundle.putString("dbs_id", dest);
                            Intent intent = new Intent(mContext, DBS_Before_Emptying.class);

//                           Intent intent = new Intent(TransMotherGasStation.this, DBS_Before_Emptying_MFM.class);
                            intent.putExtras(bundle);
                            //  startActivity(intent);
                            //  finish();
                        }
                        else if ((flag.equals("5") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", dest);
                            bundle.putString("dbs_id", source);
                            Intent intent = new Intent(mContext, DBS_After_Emptying.class);
                            intent.putExtras(bundle);

                            //     startActivity(intent);
                            //    finish();
                        }
                        else if (flag.equals("6") && noteStatus.equals("Approved")) {
                            if(isHopped){
                                if(dest.isEmpty()||dest.equals("null")){
                                    Toast.makeText(mContext,"LCV not scheduled !",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    notifydbs(source, lcv_num_val, dest, transId, "7");
                                    insertSec(dest,lcv_num_val,transId,"admsumit");

                                }

                            }
                            else{
                                Log.e(TAG, "In if First transaction MGS");
                                notifymsg(transId,dest,source,lcv_num_val,"Pending");
                            }

                            //       reachedLcv(lcv_num_val);
                        }
                        else if ((flag.equals("7") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", dest);
                            bundle.putString("dbs_id", source);
                            Intent intent = new Intent(mContext, DBS_Before_Emptying.class);

//                           Intent intent = new Intent(TransMotherGasStation.this, DBS_Before_Emptying_MFM.class);
                            intent.putExtras(bundle);

                            //  startActivity(intent);
                            //  finish();
                        }
                        else if ((flag.equals("8") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", lcv_num_val);
                            bundle.putString("mgs_id", dest);
                            bundle.putString("dbs_id", source);
                            Intent intent = new Intent(mContext, DBS_After_Emptying.class);
                            intent.putExtras(bundle);

                            //     startActivity(intent);
                            //    finish();
                        }
                        else {

                                Log.e(TAG, "In if First transaction MGS");
                                notifymsg(transId,dest,"",lcv_num_val,"Pending");


                            //       reachedLcv(lcv_num_val);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Failed to get data" + error, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {

                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("Notification_LCV", lcv_num_val);
                    params.put("Notification_MGS", source);
                    params.put("Notification_DBS", dest);
                    params.put("transaction_id", transId);

                    Log.d("DBS>>", "getParams: "+params.toString());
                    return params;
                }
            };

            queue.add(request);
        } else {
            Toast.makeText(mContext, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }


    }

    public void insertDbs(String lcv_id,String station_id,String username,String transId) {




        final String lcv_from_mgs_to_dbs = "4";

        if (!(station_id.equals("NA") || lcv_id.equals("NA"))) {


            StringRequest request = new StringRequest(Request.Method.POST, BASE_URL+"v2/cng_transaction_api_v2.php?apicall=insertDBS",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                    Log.d("DBS>>inDbs", response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("message") == null) {
                                    Toast.makeText(mContext, "Invalid Operation", Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
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
                    params.put("transaction_id", transId);
                    Log.d("DBS>>inDbsP", params.toString());

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(request);
        } else {
            Toast.makeText(mContext, "Please select valid LCV and DBS station ", Toast.LENGTH_SHORT).show();
        }

    }


    public void insertData(String station_id,String dbs_station_id,String lcv_id,String trans_id) {


        if (!(station_id.equals("NA") && lcv_id.equals("NA") && dbs_station_id.equals("NA"))) {
            StringRequest request = new StringRequest(Request.Method.POST, BASE_URL+"v2/cng_transaction_api_v2.php?apicall=insertMGS",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e(TAG, "insertData Response = " + response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("error")) {
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                    userLogin(station_id);


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
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
                    params.put("transaction_id",trans_id);
                    params.put("dbs_station_id", dbs_station_id);
                    params.put("lcv_from_mgs_to_dbs", lcv_from_mgs_to_dbs);
                    params.put("operator_id", "admsumit");
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(request);
        } else {
            Toast.makeText(mContext, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }


    }

    public void insertSec(String sec_dbs,String lcv_id,String trans_id,String username) {


        if (!(sec_dbs.equals("NA") && lcv_id.equals("NA") && trans_id.equals("NA"))) {
            StringRequest request = new StringRequest(Request.Method.POST, BASE_URL+"v2/cng_transaction_api_v2.php?apicall=insertSecDBS",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e(TAG, "insertData Response = " + response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("message") == null) {
                                    Toast.makeText(mContext, "Invalid Operation", Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
                }
            }

            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();

                    String lcv_from_mgs_to_dbs = "7";
                    String lcv_status=lcv_id+ " waiting for emptying at "+ sec_dbs;
                    params.put("lcv_status",lcv_status );
                    params.put("lcv_id", lcv_id);
                    params.put("secondary_dbs_id", sec_dbs);
                    params.put("transaction_id", trans_id);
                    params.put("lcv_from_mgs_to_dbs", lcv_from_mgs_to_dbs);
                    params.put("secondary_dbs_operator_id", "admsumit");
                    Log.e("DBS>>", params.toString());
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(request);
        } else {
            Toast.makeText(mContext, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }


    }

    public void notifydbs(String source,String lcv,String dest,String transId,String stage) {


        final String MGS = source;
        final String LCV = lcv;
        final String DBS =dest;
        final String operator_id = "admsumit";
        Log.e("DBS>>", "operator_id in insert Notify =" + DBS);

        final String Message = LCV + " " + "Reached at Daughter Booster Station" + ":" + DBS;
        StringRequest request = new StringRequest(Request.Method.POST,BASE_URL+"v2/cng_transaction_api_v2.php?apicall=notify1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("DBS>>", response);

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(mContext, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                userLogin(dbs);
                                String message = jsonObject.getString("message");
                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                params.put("Notification_LCV", LCV);
                params.put("Notification_MGS", source);
                params.put("Notification_DBS", dest);
                params.put("Notification_Message", Message);
                params.put("status", "Pending");
                params.put("flag", stage);
                params.put("operator_id", operator_id);
                params.put("transaction_id",transId);
                Log.e("DBS>>", params.toString());

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(request);


    }


    public void notifymsg(String transId,String mgs_id,String dbs_id,String lcv_num_val,String status) {


        final String MGS = mgs_id;
        final String LCV = lcv_num_val;
        final String DBS = dbs_id;
        final String operator_id = "admsumit";

        Log.e(TAG, "operator_id in insert Notify =" + operator_id);


        final String Message = LCV + " " + "Reached at Mother Gas Station READY FOR RE-FILLING" + ":" + MGS;
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL+"v2/cng_transaction_api_v2.php?apicall=notify1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e(TAG, "notifymsg Response = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(mContext, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                try{
                                    String transId1=jsonObject.getString("transaction_id");
                                    insertData(mgs_id,"",lcv_num_val,transId1);

                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                    if(!transId.isEmpty()){
                                        insertData(mgs_id,"",lcv_num_val,transId);
                                    }
                                }
                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                if(!transId.isEmpty())
                    params.put("transaction_id",transId);
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


        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(request);

    }
    private void userLogin(String username) {

       ProgressDiaLoading pd=new ProgressDiaLoading();
       pd.show(getChildFragmentManager(),"");
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());


        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getContext(), "Please enter your username", Toast.LENGTH_SHORT).show();

            return;
        }




        StringRequest request = new StringRequest(Request.Method.POST, URL_LOGIN, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {

                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);

//                    Log.e(TAG, "username = " + jsonObject.getString("username"));

//                    boolean success = jsonObject.getBoolean("success");
                    boolean err = jsonObject.getBoolean("error");
                    if (!err) {
                        String usernameJO = jsonObject.getString("username");
                        String emp_type = usernameJO.toLowerCase().substring(0, 3);
                        String station="NA";
                        boolean isDBS=false;
                        String nam=jsonObject.getString("note_approver_mgs");
                        String nad=jsonObject.getString("note_approver_dbs");
                        if(!nam.equals(station)){
                            station=nam;
                        }
                        else if(!nad.equals(station)){
                            station=nad;
                            isDBS=true;
                        }
                        Log.e(TAG, "emp_type = " + emp_type);
                        Log.e(TAG, "station = " + station);
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login",MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("username", usernameJO);
                        myEdit.putString("station", station);
                        myEdit.putString("loc", station);

                        myEdit.putString("lcv", veh1.getText().toString().trim());
                        myEdit.putBoolean("isDbs",isDBS);
                        myEdit.putBoolean("isLoggedIn",true);
                        myEdit.apply();

                        switch (emp_type) {
                            case "ope":
                                Intent intent = new Intent(getContext(), HomeOperator.class);
                                intent.putExtra("username", usernameJO);
                                intent.putExtra("station", station);
                                intent.putExtra("isDbs",isDBS);

                                startActivity(intent);
                                getActivity().finish();

                                break;
                            case "man":
                                Intent intent1 = new Intent(getContext(), HomeManager.class);
                                intent1.putExtra("username", usernameJO);
                                intent1.putExtra("station", station);
                                intent1.putExtra("isDbs",isDBS);
                                startActivity(intent1);
                                getActivity().finish();
                                break;
                            case "adm":

                                Intent intent2 = new Intent(getContext(), HomeAdmin.class);
                                intent2.putExtra("username", usernameJO);
                                startActivity(intent2);
                                getActivity().finish();

                                break;

                        }
                    } else {
                        Toast.makeText(getContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    pd.dismiss();
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(getContext(), "Failed to get data" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", "1234");
                Log.e(TAG, "params = " + params.toString());
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    public static void hideKeyboard(Activity activity) {
        View viewKeyboard = activity.getCurrentFocus();
        if (viewKeyboard != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewKeyboard.getWindowToken(), 0);
        }
    }

}
