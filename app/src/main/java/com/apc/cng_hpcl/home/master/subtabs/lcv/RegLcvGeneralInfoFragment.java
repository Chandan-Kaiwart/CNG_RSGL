package com.apc.cng_hpcl.home.master.subtabs.lcv;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegLcvGeneralInfoFragment extends Fragment {


    public RegLcvGeneralInfoFragment() {
        // Required empty public constructor
    }
    private static final String BASE_URL_URL = BASE_URL + "reg_lcv.php?apicall=";
    String URL_REG_LCV = BASE_URL_URL+"insertLcvGenInfo";
//    String[] owners = {"Anmol Transport","Guru Kripa Gas"};
//    String[] anmol_lcv= {"DL-1MA-5353","HR-38AB-9008","HR-38AB-0291","HR-38AB-35216","DL-1MA-5216","UP-17AT-7351","HR-38AB-7669","DL-1MA-6137","DL-1MA-6103"};
//    String[] guru_lcv = {"DL-1MA-4669","DL-1MA-6029","HR-63E-6118","DL-1MA-3661","HR-63E-5616","DL-1MA-4638","HR-63E-1684"};

    EditText vehicle_type, chassis_num, engine_num, cascade_capacity, lcv_maker, fuel_type,lcv_num,lcv_registered_to;
    Button cancelButton, proceedButton;
    String Lcv_Num, Lcv_Registered_To, Vechicle_Type, Chassis_Num, Engine_Num, Cascade_Capacity, Lcv_Maker, Fuel_Type;

    TextView select_org,select_lcv_num;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reg_lcv_general_info, container, false);

        lcv_registered_to = root.findViewById(R.id.lcv_registered_to);


        lcv_num = root.findViewById(R.id.lcv_num);


        select_org = root.findViewById(R.id.select_org);
        select_lcv_num = root.findViewById(R.id.select_lcv_num);

        //lcv_registered_to = root.findViewById(R.id.lcv_registered_to);
        vehicle_type = root.findViewById(R.id.vehicle_type);
        chassis_num = root.findViewById(R.id.chassis_num);
        engine_num = root.findViewById(R.id.engine_num);
        cascade_capacity = root.findViewById(R.id.cascade_capacity);
        lcv_maker = root.findViewById(R.id.lcv_maker);
        fuel_type = root.findViewById(R.id.fuel_type);

        cancelButton = root.findViewById(R.id.cancelButton);
        proceedButton = root.findViewById(R.id.proceedButton);


        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertData();



            }
        });


        return root;
    }


    private void insertData() {

        final String Lcv_Num = lcv_num.getText().toString();
        final String Lcv_Registered_To = lcv_registered_to.getText().toString();
        final String Vechicle_Type = vehicle_type.getText().toString();
        final String Chassis_Num = chassis_num.getText().toString();
        final String Engine_Num = engine_num.getText().toString();
        final String Cascade_Capacity = cascade_capacity.getText().toString();
        final String Lcv_Maker = lcv_maker.getText().toString();
        final String Fuel_Type = fuel_type.getText().toString();


        if (TextUtils.isEmpty(lcv_num.getText())) {
            Toast.makeText(getContext(), "Enter LCV Number", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(lcv_registered_to.getText())) {
            Toast.makeText(getContext(), "Enter LCV Registered to", Toast.LENGTH_SHORT).show();
            return;
        } else  if (TextUtils.isEmpty(vehicle_type.getText())) {
            Toast.makeText(getContext(), "Enter Vehicle Type", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(chassis_num.getText())) {
            Toast.makeText(getContext(), "Enter Chassis Number", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(engine_num.getText())) {
            Toast.makeText(getContext(), "Enter Engine Number", Toast.LENGTH_SHORT).show();
            return;
        } else
        if (TextUtils.isEmpty(cascade_capacity.getText())) {
            Toast.makeText(getContext(), "Enter Cascade Capacity ", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(lcv_maker.getText())) {
            Toast.makeText(getContext(), "Enter LCV Maker", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(fuel_type.getText())) {
            Toast.makeText(getContext(), "Enter Fuel Type", Toast.LENGTH_SHORT).show();
            return;
        }


        StringRequest request = new StringRequest(Request.Method.POST, URL_REG_LCV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(getContext(), "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message=jsonObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                Log.e(TAG, "Lcv_Num=" + Lcv_Num);
                Log.e(TAG, "Lcv_Registered_To=" + Lcv_Registered_To);
                Log.e(TAG, "Vechicle_Type=" + Vechicle_Type);
                Log.e(TAG, "Chassis_Num=" + Chassis_Num);
                Log.e(TAG, "Engine_Num=" + Engine_Num);
                Log.e(TAG, "Cascade_Capacity=" + Cascade_Capacity);
                Log.e(TAG, "Lcv_Maker=" + Lcv_Maker);
                Log.e(TAG, "Fuel_Type=" + Fuel_Type);



                params.put("Lcv_Num" , Lcv_Num);
                params.put("Lcv_Registered_To" , Lcv_Registered_To);
                params.put("Vechicle_Type" , Vechicle_Type);
                params.put("Chassis_Num" , Chassis_Num);
                params.put("Engine_Num" , Engine_Num);
                params.put("Cascade_Capacity" , Cascade_Capacity);
                params.put("Lcv_Maker" , Lcv_Maker);
                params.put("Fuel_Type" , Fuel_Type);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);


    }




}
