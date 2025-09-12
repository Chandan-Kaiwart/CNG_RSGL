package com.apc.cng_hpcl.home.master.subtabs.mothergasstation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class EditMGSInstrumentsInfoFragment extends Fragment {

    private int year, month, day, hour, minute;
    private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
    public static final String URL_MgsDbs = BASE_URL_URL + "readMgsDbs";

    private static final String BASE_URL_URL_UPDATE = BASE_URL + "master_reg_edit.php?apicall=";
    public static final String URL_INSTRU_INFOUPDATE=BASE_URL_URL_UPDATE + "updateInstrumentInfo";

    EditText temp_id,temp_make,temp_model,temp_calib,pressure_id,
            pressure_make, pressure_model,pressure_calib,mgs_id,
            mfm_id,mfm_make, mfm_model,mfm_serial,mfm_calib;
    EditText date_mfm, date_temp,date_press;
    Button cancelButton, proceedButton;
    Spinner station_type, station_id;
    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> stationIdCGS = new ArrayList<>();
    ArrayList<String> stationIdMGS = new ArrayList<>();
    ArrayList<String> stationIdDBS = new ArrayList<>();
    ArrayList<String> stationType = new ArrayList< >();
    TextInputLayout edit_mgsId;
//    String[] stationType = {"City Gas Station", "Mother Gas Station", "Daughter Booster Station"};
//
//    String[] stationIdCGS = {"CGS001", "CGS002", "CGS003"};
//    String[] stationIdMGS = {"MGS001", "MGS002", "MGS003"};
//    String[] stationIdDBS = {"DBS001", "DBS002", "DBS003"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_edit_m_g_s_instruments_info, container, false);
        date_mfm = root.findViewById(R.id.date_mfm);
        date_temp = root.findViewById(R.id.date_temp);
        date_press = root.findViewById(R.id.date_press);
        station_type = root.findViewById(R.id.station_type);
        station_id = root.findViewById(R.id.station_id);
        mgs_id= root.findViewById(R.id.mgs_id);
        temp_id = root.findViewById(R.id.temp_id);
        pressure_id = root.findViewById(R.id.pressure_id);
        mfm_id = root.findViewById(R.id.mfm_id);
        temp_make = root.findViewById(R.id.temp_make);
        temp_model = root.findViewById(R.id.temp_model);
        temp_calib = root.findViewById(R.id.temp_calib);
        pressure_make = root.findViewById(R.id.pressure_make);
        pressure_model = root.findViewById(R.id.pressure_model);
        pressure_calib = root.findViewById(R.id.pressure_calib);
        mfm_make = root.findViewById(R.id.mfm_make);
        mfm_model = root.findViewById(R.id.mfm_model);
        mfm_serial = root.findViewById(R.id.mfm_serial);
        mfm_calib = root.findViewById(R.id.mfm_calib);
        cancelButton = root.findViewById(R.id.cancelButton);
        proceedButton = root.findViewById(R.id.proceedButton);
//        ArrayAdapter<String> sationTypeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationType);
//        station_type.setAdapter(sationTypeAdapter);
//
//        station_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (station_type.getSelectedItem()
//                        .toString()
//                        .equals("City Gas Station")) {
//                    ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdCGS);
//                    station_id.setAdapter(sationIdAdapter);
//                } else if (station_type.getSelectedItem()
//                        .toString()
//                        .equals("Mother Gas Station")) {
//                    ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdMGS);
//                    station_id.setAdapter(sationIdAdapter);
//                } else {
//                    ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdDBS);
//                    station_id.setAdapter(sationIdAdapter);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        station_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                getDetails(station_id.getSelectedItem().toString());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        readMgsDbs();
//        station_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (station_type.getSelectedItem()
//                        .toString()
//                        .equals("City Gas Station")) {
//                    ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationId);
//                    station_id.setAdapter(sationIdAdapter);
////                    edit_mgsId.setVisibility(View.GONE);
//
//                } else if (station_type.getSelectedItem()
//                        .toString()
//                        .equals("Mother Gas Station")) {
//                    ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationId);
//                    station_id.setAdapter(sationIdAdapter);
////                    edit_mgsId.setVisibility(View.GONE);
//
//                } else {
//                    ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationId);
//                    station_id.setAdapter(sationIdAdapter);
////                    edit_mgsId.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        station_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDetails(station_id.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMgsDbs();
//                Intent intent = new Intent(getActivity(), MotherGasStation.class);
//                startActivity(intent);
//                finish();
            }
        });

        return root;
    }

    private void updateMgsDbs() {

        final String stationType = station_type.getSelectedItem().toString();
        final String stationId = station_id.getSelectedItem().toString();
        final String mgsId = mgs_id.getText().toString().trim();
        Log.e(TAG,"MGS_Id="+mgsId);

        final String tempId = temp_id.getText().toString().trim();
        final String tempMake = temp_make.getText().toString();
        final String tempModel = temp_model.getText().toString();
        final String tempCalib = temp_calib.getText().toString();
        final String pressureId = pressure_id.getText().toString().trim();
        final String pressureMake = pressure_make.getText().toString();
        final String pressureModel = pressure_model.getText().toString();
        final String pressureCalib = pressure_calib.getText().toString();
        final String mfmId = mfm_id.getText().toString().trim();
        final String mfmMake = mfm_make.getText().toString().trim();
        final String mfmModel = mfm_model.getText().toString().trim();
        final String mfmSerial = mfm_serial.getText().toString().trim();
        final String mfmCalib = mfm_calib.getText().toString().trim();
        final String dateTemp = date_temp.getText().toString().trim();
        final String datePress = date_press.getText().toString().trim();
        final String dateMfm = date_mfm.getText().toString().trim();

//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Loading...");

//        if(name_mgs.isEmpty()){
//            Toast.makeText(getContext(), "Enter Name", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        else if(add1.isEmpty()){
//            Toast.makeText(getContext(), "Enter Email", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        else if(name_incharge.isEmpty()){
//            Toast.makeText(getContext(), "Enter Contact", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        else if(phone.isEmpty()){
//            Toast.makeText(getContext(), "Enter Address", Toast.LENGTH_SHORT).show();
//            return;
//        }

//         else if(bays_num.isEmpty()){
//            Toast.makeText(getContext(), "Enter Address", Toast.LENGTH_SHORT).show();
//            return;
//        }

        //         else if(disp_bays.isEmpty()){
//            Toast.makeText(getContext(), "Enter Address", Toast.LENGTH_SHORT).show();
//            return;
//        }

        //         else if(lat_long.isEmpty()){
//            Toast.makeText(getContext(), "Enter Address", Toast.LENGTH_SHORT).show();
//            return;
//        }

//
//        else{
//        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, URL_INSTRU_INFOUPDATE,
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

//                        if (response.equalsIgnoreCase("Data Inserted")) {
//                            Toast.makeText(getContext(), "Data Insertion Sucessfull", Toast.LENGTH_SHORT).show();
////                            progressDialog.dismiss();
//                        } else  if (response.equalsIgnoreCase("Station Id already exists")){
//                            Toast.makeText(getContext(), "Station Id already exists", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG,"Error="+response);
////                            progressDialog.dismiss();
//
//                        }else  if (response.equalsIgnoreCase("Insertion Failed")){
//                            Toast.makeText(getContext(), "Insertion Failed", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG,"Error="+response);
////                            progressDialog.dismiss();
//
//                        }

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

                params.put("Station_id", stationId);
                params.put("Station_type", stationType);
                params.put("mgsId",mgsId);
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
                params.put("mass_flow_meter_id",mfmId);
                params.put("mass_flow_make",mfmMake);
                params.put("mass_flow_model",mfmModel);
                params.put("mass_flow_serial_number",mfmSerial);
                params.put("mass_flow_calibration_date",dateMfm);
                params.put("mass_flow_calibration_cycle",mfmCalib);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);


    }

    private void readMgsDbs() {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_MgsDbs,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
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
                                    mgsDbsModel.setStation_type(dataobj.getString("Station_type"));


                                    mgsdbsModelArrayList.add(mgsDbsModel);

                                }

                                for (int i = 0; i < mgsdbsModelArrayList.size(); i++) {
                                    String station_Type = mgsdbsModelArrayList.get(i).getStation_type();
                                    String station_id = mgsdbsModelArrayList.get(i).getStation_Id().toLowerCase().substring(0, 3);

                                    stationType.add(mgsdbsModelArrayList.get(i).getStation_type());


                                    if (station_id.equals("cgs")) {
                                        stationIdCGS.add(mgsdbsModelArrayList.get(i).getStation_Id());

                                    } else if (station_id.equals("mgs")) {
                                        stationIdMGS.add(mgsdbsModelArrayList.get(i).getStation_Id());

                                    } else if (station_id.equals("dbs")) {
                                        stationIdDBS.add(mgsdbsModelArrayList.get(i).getStation_Id());
                                    }


//                                    String station_Id = mgsdbsModelArrayList.get(i).getMgsId().toLowerCase().substring(0, 3);

                                }
                            }
                            ArrayList uniqueStationType = (ArrayList) stationType.stream().distinct().collect(Collectors.toList());
//                            List<String> uniqueStationType = stationType.stream().distinct().collect(Collectors.toList());;
                            ArrayAdapter<String> spinnerArrayAdapterMgs = new ArrayAdapter<String>(getContext(), simple_spinner_item, uniqueStationType);
                            spinnerArrayAdapterMgs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            station_type.setAdapter(spinnerArrayAdapterMgs);


                            station_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (station_type.getSelectedItem()
                                            .toString()
                                            .equals("City Gas Station")) {
                                        ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdCGS);
                                        station_id.setAdapter(sationIdAdapter);
//                                        edit_mgsId.setVisibility(View.GONE);

                                    } else if (station_type.getSelectedItem()
                                            .toString()
                                            .equals("Mother Gas Station")) {
                                        ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdMGS);
                                        station_id.setAdapter(sationIdAdapter);
//                                        edit_mgsId.setVisibility(View.GONE);

                                    } else {
                                        ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdDBS);
                                        station_id.setAdapter(sationIdAdapter);
//                                        edit_mgsId.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });


//                            ArrayAdapter<String> spinnerArrayAdapterDbs = new ArrayAdapter<String>(getContext(), simple_spinner_item, stationId);
//                            spinnerArrayAdapterDbs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
//                            station_id.setAdapter(spinnerArrayAdapterDbs);
//                                removeSimpleProgressDialog();
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

    private void getDetails(String sationId) {

        String url = BASE_URL+"read_master_instrument_info.php";

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
                        mfm_id.setText(jsonObject.getString("mass_flow_meter_id"));
                        mfm_make.setText(jsonObject.getString("mass_flow_make"));
                        mfm_model.setText(jsonObject.getString("mass_flow_model"));
                        mfm_serial.setText(jsonObject.getString("mass_flow_serial_number"));
                        date_mfm.setText(jsonObject.getString("mass_flow_calibration_date"));
                        mfm_calib.setText(jsonObject.getString("mass_flow_calibration_cycle"));





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

                params.put("id", sationId);

                return params;
            }
        };

        queue.add(request);
    }
}