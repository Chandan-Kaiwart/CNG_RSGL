package com.apc.cng_hpcl.home.master.subtabs.lcv;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.apc.cng_hpcl.home.transaction.LCVModel;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class EditLcvGeneralInfoFragment extends Fragment {


    public EditLcvGeneralInfoFragment() {
        // Required empty public constructor
    }

    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_LCV = BASE_URL_URL + "readLCV";
    private static final String BASE_URL_URL2 = BASE_URL + "reg_lcv.php?apicall=";
    String URL_UPDATE_LCV_GENINFO = BASE_URL_URL2+"updateLcvGenInfo";

    //String ServerURL = "http://192.168.1.6:8080/HPCLdemo/reg_lcv.php";
//    String[] owners = {"Anmol Transport","Guru Kripa Gas"};
//    String[] anmol_lcv= {"DL-1MA-5353","HR-38AB-9008","HR-38AB-0291","HR-38AB-35216","DL-1MA-5216","UP-17AT-7351","HR-38AB-7669","DL-1MA-6137","DL-1MA-6103"};
//    String[] guru_lcv = {"DL-1MA-4669","DL-1MA-6029","HR-63E-6118","DL-1MA-3661","HR-63E-5616","DL-1MA-4638","HR-63E-1684"};

    private ArrayList<LCVModel> lcvModelArrayList;
    ArrayList<String> lcv = new ArrayList<>();
    ArrayList<String> lcv_reg = new ArrayList<>();

    EditText vehicle_type, chassis_num, engine_num, cascade_capacity, lcv_maker, fuel_type;
    Button cancelButton, proceedButton;
    String Lcv_Num, Lcv_Registered_To, Vechicle_Type, Chassis_Num, Engine_Num, Cascade_Capacity, Lcv_Maker, Fuel_Type,
            create_User_Id, modified_User_Id;

    Spinner lcv_registered_to,lcv_num;
    TextView select_org,select_lcv_num;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_edit_lcv_general_info, container, false);


        lcv_registered_to = root.findViewById(R.id.lcv_registered_to);
        lcv_num = root.findViewById(R.id.lcv_num);

//        ArrayAdapter<String> owner = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, owners);
//        lcv_registered_to.setAdapter(owner);
//
//        lcv_registered_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (lcv_registered_to.getSelectedItem().toString().equals("Anmol Transport")){
//                    ArrayAdapter<String> anmol = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, anmol_lcv);
//                    lcv_num.setAdapter(anmol);
//                }else {
//                    ArrayAdapter<String> guru = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, guru_lcv);
//                    lcv_num.setAdapter(guru);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        readLCV();


        lcv_num.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDetails(lcv_num.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        select_org = root.findViewById(R.id.select_org);
        select_lcv_num = root.findViewById(R.id.select_lcv_num);

        //lcv_registered_to = root.findViewById(R.id.lcv_registered_to);
        vehicle_type = root.findViewById(R.id.vehicle_type);
        chassis_num = root.findViewById(R.id.chassis_num);
        engine_num = root.findViewById(R.id.engine_num);
        cascade_capacity = root.findViewById(R.id.cascade_capacity);
        lcv_maker = root.findViewById(R.id.lcv_maker);
        fuel_type = root.findViewById(R.id.fuel_type);

 //       create_User_Id = SimpleDateFormat.getDateTimeInstance().format(new Date());
//                org_abr.getText().toString();
   //     modified_User_Id = lcv_num.getSelectedItem().toString();//OrgType is considered here
//
        cancelButton = root.findViewById(R.id.cancelButton);
        proceedButton = root.findViewById(R.id.proceedButton);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLcvGenInfo();
            }
        });

        return root;
    }

    private void updateLcvGenInfo() {
        final String Lcv_Num = lcv_num.getSelectedItem().toString();
        final String Lcv_Registered_To = lcv_registered_to.getSelectedItem().toString();
        final String Vechicle_Type = vehicle_type.getText().toString();
        final String Chassis_Num = chassis_num.getText().toString();
        final String Engine_Num = engine_num.getText().toString();
        final String Cascade_Capacity = cascade_capacity.getText().toString();
        final String Lcv_Maker = lcv_maker.getText().toString();
        final String Fuel_Type = fuel_type.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE_LCV_GENINFO,
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

    private void readLCV() {
//        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

                                for (int i = 0; i < lcvModelArrayList.size(); i++) {
                                    lcv.add(lcvModelArrayList.get(i).getLcv_Num());
                                    lcv_reg.add(lcvModelArrayList.get(i).getLcv_Registered_To());
                                }

                                ArrayAdapter<String> LcvArrayAdapter = new ArrayAdapter<String>(getContext(), simple_spinner_item, lcv);
                                LcvArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                lcv_num.setAdapter(LcvArrayAdapter);

                                ArrayAdapter<String> lcvRegisterArrayAdapter = new ArrayAdapter<String>(getContext(), simple_spinner_item, lcv_reg);
                                lcvRegisterArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                lcv_registered_to.setAdapter(lcvRegisterArrayAdapter);
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
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        requestQueue.add(stringRequest);
    }


    private void getDetails(String lcvNum) {

        String url = BASE_URL+"read_lcv_gen_info.php";

        RequestQueue queue = Volley.newRequestQueue(getContext());


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("Vechicle_Type") == null) {
                        Toast.makeText(getContext(), "Please enter valid id.", Toast.LENGTH_SHORT).show();
                    } else {

                        vehicle_type.setText(jsonObject.getString("Vechicle_Type"));
                        chassis_num.setText(jsonObject.getString("Chassis_Num"));
                        engine_num.setText(jsonObject.getString("Engine_Num"));
                        cascade_capacity.setText(jsonObject.getString("Cascade_Capacity"));
                        lcv_maker.setText(jsonObject.getString("Lcv_Maker"));
                        fuel_type.setText(jsonObject.getString("Fuel_Type"));
                        //create_User_Id.set
                        //nameValuePairs.add(new BasicNameValuePair("Create_User_Id", create_User_Id));
                        //nameValuePairs.add(new BasicNameValuePair("Modified_User_Id", modified_User_Id));



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Fail to get course" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("id", lcvNum);

                return params;
            }
        };

        queue.add(request);
    }
}