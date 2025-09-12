package com.apc.cng_hpcl.home.master.subtabs.mothergasstation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class MGSInstrumentsInfoFragment extends Fragment {


    public MGSInstrumentsInfoFragment() {
        // Required empty public constructor
    }
//        private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
    private static final String BASE_URL_URL = BASE_URL+"master_reg_edit.php?apicall=";
    public static final String URL_INSTRU_INFO = BASE_URL_URL + "insertInstrumentInfo";
    private static final String BASE_URL_URL2 = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_MgsDbs = BASE_URL_URL2 + "readMgsDbs";
    String stationId;
    private int year, month, day, hour, minute;
    EditText temp_id,temp_make,temp_model,temp_calib,pressure_id,
            pressure_make, pressure_model,pressure_calib,
            mfm_id,mfm_make, mfm_model,mfm_serial,mfm_calib, mgs_id;
    TextView date_mfm, date_temp,date_press;
    Button cancelButton, proceedButton;
    Spinner station_type, station_id;
//    String[] stationType = {"City Gas Station", "Mother Gas Station", "Daughter Booster Station"};
    LinearLayout mgs_for_dbs;
//    String[] stationIdCGS = {"CGS001", "CGS002", "CGS003"};
//    String[] stationIdMGS = {"MGS001", "MGS002", "MGS003"};
//    String[] stationIdDBS = {"DBS001", "DBS002", "DBS003"};
//    String mgsId="0",stationId,station_Type;

    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> stationIdCGS = new ArrayList<>();
    ArrayList<String> stationIdMGS = new ArrayList<>();
    ArrayList<String> stationIdDBS = new ArrayList<>();
    ArrayList<String> stationType = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_m_g_s_instruments_info, container, false);

        date_mfm = root.findViewById(R.id.date_mfm);
        date_temp = root.findViewById(R.id.date_temp);
        date_press = root.findViewById(R.id.date_press);
        station_type = root.findViewById(R.id.station_type);
        station_id = root.findViewById(R.id.station_id);

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
        mgs_for_dbs=root.findViewById(R.id.mgs_for_dbs);
        mgs_id=root.findViewById(R.id.mgs_id);
        readMgsDbs();
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
////                    station_id.setAdapter(sationIdAdapter);
//                    mgs_for_dbs.setVisibility(View.GONE);
//                } else if (station_type.getSelectedItem()
//                        .toString()
//                        .equals("Mother Gas Station")) {
//                    ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdMGS);
////                    station_id.setAdapter(sationIdAdapter);
//                    mgs_for_dbs.setVisibility(View.GONE);
//                } else {
//                    ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdDBS);
////                    station_id.setAdapter(sationIdAdapter);
//                    mgs_for_dbs.setVisibility(View.GONE);
////                    ArrayAdapter<String> MGS_DBSIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdMGS);
////                    mgs_id.setAdapter(MGS_DBSIdAdapter);
////                    mgsId = mgs_id.getText().toString();
//
//
//
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        ArrayAdapter<String> MGS_DBSIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdMGS);
//        mgs_id.setAdapter(MGS_DBSIdAdapter);
//        mgs_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mgsId = mgs_id.getSelectedItem().toString();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                mgsId="0";
//            }
//        });
//


        date_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date_temp.setText(day+"/"+(month+1)+"/"+year);
                    }
                }, year, month,day);
                picker.show();
            }
        });



        date_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date_press.setText(day+"/"+(month+1)+"/"+year);
                    }
                }, year, month,day);
                picker.show();
            }
        });



        date_mfm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date_mfm.setText(day+"/"+(month+1)+"/"+year);
                    }
                }, year, month,day);
                picker.show();
            }
        });

//        time_mfm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                final Calendar c = Calendar.getInstance();
//                hour = c.get(Calendar.HOUR_OF_DAY);
//                minute = c.get(Calendar.MINUTE);
//
//                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener()
//                {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
//                        tv_time.setText(hour+":"+min);
//                    }
//                },hour,minute,false);
//                timePickerDialog.show();
//            }
//        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertData();
//                Intent intent = new Intent(getActivity(), MotherGasStation.class);
//                startActivity(intent);
//                finish();
//                getDetails(phone.getText().toString());
            }
        });
        return root;
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

    private void insertData() {
        final String stationType = station_type.getSelectedItem().toString();
        switch (stationType) {
            case "City Gas Station":
                stationId = "CGS" + station_id.getSelectedItem().toString();
                break;
            case "Mother Gas Station":
                stationId = "MGS" + station_id.getSelectedItem().toString();
                break;
            case "Daughter Booster Station":
                stationId = "DBS" + station_id.getSelectedItem().toString();
                break;
        }
//        final String stationId = station_id.getText().toString();
//        final String mgsId = mgs_id.getText().toString().trim();
        final String mgsId ="0";

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

         if (TextUtils.isEmpty(temp_id.getText())) {
            Toast.makeText(getContext(), "Enter Temperature Id", Toast.LENGTH_SHORT).show();
            return;
        } else  if (TextUtils.isEmpty(temp_make.getText())) {
            Toast.makeText(getContext(), "Enter Temperature Make", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(temp_model.getText())) {
            Toast.makeText(getContext(), "Enter Temperature Model", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(temp_calib.getText())) {
            Toast.makeText(getContext(), "Enter Temperature Calibration Date", Toast.LENGTH_SHORT).show();
            return;
        } else
        if (TextUtils.isEmpty(pressure_id.getText())) {
            Toast.makeText(getContext(), "Enter Pressure ID", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(pressure_make.getText())) {
            Toast.makeText(getContext(), "Enter Pressure Make", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(pressure_model.getText())) {
            Toast.makeText(getContext(), "Enter Pressure Model", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(pressure_calib.getText())) {
            Toast.makeText(getContext(), "Enter Pressure Calibration", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(mfm_id.getText())) {
            Toast.makeText(getContext(), "Enter Mass Flow Meter Id", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(mfm_make.getText())) {
            Toast.makeText(getContext(), "Enter Mass Flow Meter Make", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(mfm_model.getText())) {
            Toast.makeText(getContext(), "Mass Flow Meter Model", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(mfm_serial.getText())) {
            Toast.makeText(getContext(), "Mass Flow Meter Serial Number", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(mfm_calib.getText())) {
            Toast.makeText(getContext(), "Mass Flow Meter Calibration Cycle", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(date_temp.getText())) {
            Toast.makeText(getContext(), "Select Temperature Calibration Date", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(date_press.getText())) {
            Toast.makeText(getContext(), "Select Pressure Calibration Date", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(date_mfm.getText())) {
            Toast.makeText(getContext(), "Select Mass Flow Meter Calibration Date", Toast.LENGTH_SHORT).show();
            return;
        }
        StringRequest request = new StringRequest(Request.Method.POST, URL_INSTRU_INFO,
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


}