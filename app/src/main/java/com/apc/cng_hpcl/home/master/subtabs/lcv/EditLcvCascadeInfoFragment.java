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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.layout.simple_spinner_item;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class EditLcvCascadeInfoFragment extends Fragment {



    public EditLcvCascadeInfoFragment() {
        // Required empty public constructor
    }
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_LCV = BASE_URL_URL + "readLCV";
    private static final String BASE_URL_URL2 = BASE_URL + "reg_lcv.php?apicall=";
    String URL_UPDATE_LCV_CASCADE = BASE_URL_URL2+"updateLcvCascadeInfo";

//    String[] owners = {"Anmol Transport","Guru Kripa Gas"};
//    String[] anmol_lcv= {"DL-1MA-5353","HR-38AB-9008","HR-38AB-0291","HR-38AB-35216","DL-1MA-5216","UP-17AT-7351","HR-38AB-7669","DL-1MA-6137","DL-1MA-6103"};
//    String[] guru_lcv = {"DL-1MA-4669","DL-1MA-6029","HR-63E-6118","DL-1MA-3661","HR-63E-5616","DL-1MA-4638","HR-63E-1684"};

    private ArrayList<LCVModel> lcvModelArrayList;
    ArrayList<String> lcv = new ArrayList<>();
    ArrayList<String> lcv_reg = new ArrayList<>();

    Spinner lcv_registered_to,lcv_num;
    EditText temp_id,temp_make,temp_model,temp_calib,pressure_id,pressure_make, pressure_model,pressure_calib;
    EditText date_temp,date_press;

    EditText cascade_id, cascade_make, cascade_model, cascade_serial, cascade_status, cascade_capacity;
    EditText date_status, date_install;

    Button cancelButton, proceedButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_edit_lcv_cascade_info, container, false);


        date_temp = root.findViewById(R.id.date_temp);
        date_press = root.findViewById(R.id.date_press);

        lcv_registered_to = root.findViewById(R.id.lcv_registered_to);
        lcv_num = root.findViewById(R.id.lcv_num);

        temp_id = root.findViewById(R.id.temp_id);
        pressure_id = root.findViewById(R.id.pressure_id);

        temp_make = root.findViewById(R.id.temp_make);
        temp_model = root.findViewById(R.id.temp_model);
        temp_calib = root.findViewById(R.id.temp_calib);
        pressure_make = root.findViewById(R.id.pressure_make);
        pressure_model = root.findViewById(R.id.pressure_model);
        pressure_calib = root.findViewById(R.id.pressure_calib);

        date_status = root.findViewById(R.id.date_status);
        date_install = root.findViewById(R.id.date_install);
        cascade_id = root.findViewById(R.id.cascade_id);
        cascade_make = root.findViewById(R.id.cascade_make);
        cascade_model = root.findViewById(R.id.cascade_model);
        cascade_serial = root.findViewById(R.id.cascade_serial);
        cascade_status = root.findViewById(R.id.cascade_status);
        cascade_capacity = root.findViewById(R.id.cascade_capacity);

        cancelButton = root.findViewById(R.id.cancelButton);
        proceedButton = root.findViewById(R.id.proceedButton);

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

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLcvCascade();
            }
        });

        return root;
    }

    private void updateLcvCascade() {

        final String Lcv_Num = lcv_num.getSelectedItem().toString();
        final String Lcv_Registered_To = lcv_registered_to.getSelectedItem().toString();
        final String tempId = temp_id.getText().toString().trim();
        final String tempMake = temp_make.getText().toString();
        final String tempModel = temp_model.getText().toString();
        final String tempCalib = temp_calib.getText().toString();
        final String pressureId = pressure_id.getText().toString().trim();
        final String pressureMake = pressure_make.getText().toString();
        final String pressureModel = pressure_model.getText().toString();
        final String pressureCalib = pressure_calib.getText().toString();
        final String dateTemp = date_temp.getText().toString().trim();
        final String datePress = date_press.getText().toString().trim();
        final String cascadeId = cascade_id.getText().toString().trim();
        final String cascadeMake = cascade_make.getText().toString();
        final String cascadeModel = cascade_model.getText().toString();
        final String cascadeSerial = cascade_serial.getText().toString();
        final String cascadeStatus = cascade_status.getText().toString();
        final String cascadeCapacity = cascade_capacity.getText().toString();
        final String dateInstall = date_install.getText().toString();
        final String dateStatus = date_status.getText().toString();



        StringRequest request = new StringRequest(Request.Method.POST,  URL_UPDATE_LCV_CASCADE,
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

                params.put("lcv_num", Lcv_Num);
                params.put("lcv_registered_to", Lcv_Registered_To);

                params.put("temperature_gauge_id",tempId);
                params.put("temperature_gauge_make",tempMake);
                params.put("temperature_model",tempModel);
                params.put("temperature_last_calibration_date",dateTemp);
                params.put("temperature_claibration_cycle",tempCalib);

                params.put("pressure_gauge_id",pressureId);
                params.put("pressure_gauge_make",pressureMake);
                params.put("pressure_gauge_model",pressureModel);
                params.put("pressure_gauge_claibration_date",datePress);
                params.put("pressure_gauge_calibration_cycle",pressureCalib);

                params.put("stationary_cascade_id", cascadeId);
                params.put("stationary_cascade_make", cascadeMake);
                params.put("stationary_cascade_model", cascadeModel);
                params.put("stationary_cascade_serial_number", cascadeSerial);
                params.put("stationary_hydrotest_status", cascadeStatus);
                params.put("stationary_cascade_capacity", cascadeCapacity);

                params.put("stationary_cascade_hydrotest_status_date", dateStatus);
                params.put("stationary_cascade_installation_date", dateInstall);

                //params.put("Create_User_Id", Create_User_Id);
                //params.put("Modified_User_Id", Modified_User_Id);
//

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


    private void getDetails(String lcvnum) {

        String url = BASE_URL+"read_lcv_instrument_info.php";

        RequestQueue queue = Volley.newRequestQueue(getContext());


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("temperature_gauge_make") == null) {
                        Toast.makeText(getContext(), "Please enter valid id.", Toast.LENGTH_SHORT).show();
                    } else {

                        temp_id.setText(jsonObject.getString("temperature_gauge_id"));
                        temp_make.setText(jsonObject.getString("temperature_gauge_make"));
                        temp_model.setText(jsonObject.getString("temperature_model"));
                        date_temp.setText(jsonObject.getString("temperature_last_calibration_date"));
                        temp_calib.setText(jsonObject.getString("temperature_claibration_cycle"));

                        pressure_id.setText(jsonObject.getString("pressure_gauge_id"));
                        pressure_make.setText(jsonObject.getString("pressure_gauge_make"));
                        pressure_model.setText(jsonObject.getString("pressure_gauge_model"));
                        date_press.setText(jsonObject.getString("pressure_gauge_claibration_date"));
                        pressure_calib.setText(jsonObject.getString("pressure_gauge_calibration_cycle"));

                        date_status.setText(jsonObject.getString("stationary_cascade_hydrotest_status_date"));
                        date_install.setText(jsonObject.getString("stationary_cascade_installation_date"));
                        cascade_id .setText(jsonObject.getString("stationary_cascade_id"));
                        cascade_make .setText(jsonObject.getString("stationary_cascade_make"));
                        cascade_model .setText(jsonObject.getString("stationary_cascade_model"));
                        cascade_serial.setText(jsonObject.getString("stationary_cascade_serial_number"));
                        cascade_status .setText(jsonObject.getString("stationary_hydrotest_status"));
                        cascade_capacity .setText(jsonObject.getString("stationary_cascade_capacity"));

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

                params.put("id", lcvnum);

                return params;
            }
        };

        queue.add(request);
    }
}