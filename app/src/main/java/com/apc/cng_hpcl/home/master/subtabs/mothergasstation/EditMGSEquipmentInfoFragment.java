package com.apc.cng_hpcl.home.master.subtabs.mothergasstation;

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
import android.widget.EditText;
import android.widget.Spinner;
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
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class EditMGSEquipmentInfoFragment extends Fragment {

    EditText cascade_id, cascade_make, cascade_model, cascade_serial, cascade_status, cascade_capacity,
            comp_id, comp_make, comp_model, comp_serial, comp_type, disp_id,cascade_reorder_point,
            disp_make, disp_model, disp_type;
    EditText date_status, date_install, mgs_id;
    Button cancelButton, proceedButton;
    Spinner station_type, station_id;
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_MgsDbs = BASE_URL_URL + "readMgsDbs";
    private static final String BASE_URL_URL_UPDATE = BASE_URL + "master_reg_edit.php?apicall=";
    public static final String URL_EQUIP_INFOUPDATE= BASE_URL_URL_UPDATE + "updateEquipInfo";
    private int year, month, day;
    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> stationIdCGS = new ArrayList<>();
    ArrayList<String> stationIdMGS = new ArrayList<>();
    ArrayList<String> stationIdDBS = new ArrayList<>();
    ArrayList<String> stationType = new ArrayList<>();
    TextInputLayout edit_mgsId;

    //    String[] stationType = {"City Gas Station", "Mother Gas Station", "Daughter Booster Station"};
//
//    String[] stationIdCGS = {"CGS001", "CGS002", "CGS003"};
//    String[] stationIdMGS = {"MGS001", "MGS002", "MGS003"};
//    String[] stationIdDBS = {"DBS001", "DBS002", "DBS003"};
    public EditMGSEquipmentInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_m_g_s_equipment_info, container, false);
        date_status = root.findViewById(R.id.date_status);
        station_type = root.findViewById(R.id.station_type);
        station_id = root.findViewById(R.id.station_id);
        mgs_id = root.findViewById(R.id.mgs_id);
        date_install = root.findViewById(R.id.date_install);
        cascade_id = root.findViewById(R.id.cascade_id);
        cascade_make = root.findViewById(R.id.cascade_make);
        cascade_model = root.findViewById(R.id.cascade_model);
        cascade_serial = root.findViewById(R.id.cascade_serial);
        cascade_status = root.findViewById(R.id.cascade_status);
        cascade_capacity = root.findViewById(R.id.cascade_capacity);
        cascade_reorder_point=root.findViewById(R.id.cascade_reorder_point);

        comp_id = root.findViewById(R.id.comp_id);
        comp_make = root.findViewById(R.id.comp_make);
        comp_model = root.findViewById(R.id.comp_model);
        comp_serial = root.findViewById(R.id.comp_serial);
        comp_type = root.findViewById(R.id.comp_type);
        disp_id = root.findViewById(R.id.disp_id);
        disp_make = root.findViewById(R.id.disp_make);
        disp_model = root.findViewById(R.id.disp_model);
        disp_type = root.findViewById(R.id.disp_type);
        proceedButton = root.findViewById(R.id.proceedButton);
        edit_mgsId = root.findViewById(R.id.edit_mgsId);
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
//                    edit_mgsId.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
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

        final String cascadeReorderPoint=cascade_reorder_point.getText().toString().trim();
        final String cascadeId = cascade_id.getText().toString().trim();
        final String cascadeMake = cascade_make.getText().toString();
        final String cascadeModel = cascade_model.getText().toString();
        final String cascadeSerial = cascade_serial.getText().toString();
        final String cascadeStatus = cascade_status.getText().toString();
        final String cascadeCapacity = cascade_capacity.getText().toString();
        final String compId = comp_id.getText().toString();
        final String compMake = comp_make.getText().toString().trim();
        final String compModel = comp_model.getText().toString().trim();
        final String compSerial = comp_serial.getText().toString().trim();
        final String compType = comp_type.getText().toString().trim();
        final String dispId = disp_id.getText().toString().trim();
        final String dispMake = disp_make.getText().toString().trim();
        final String dispModel = disp_model.getText().toString().trim();
        final String dispType = disp_type.getText().toString().trim();
//        final String dateStatus = date_status.getText().toString();
        final String dateInstall = date_install.getText().toString();
        final String dateStatus=date_status.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, URL_EQUIP_INFOUPDATE,
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

                params.put("stationary_cascade_reorder_point",cascadeReorderPoint);
                params.put("Station_id", stationId);
                params.put("Station_type", stationType);
                params.put("mgsId",mgsId);
                params.put("stationary_cascade_id", cascadeId);
                params.put("stationary_cascade_make", cascadeMake);
                params.put("stationary_cascade_model", cascadeModel);
                params.put("stationary_cascade_serial_number", cascadeSerial);
                params.put("stationary_hydrotest_status", cascadeStatus);
                params.put("stationary_cascade_capacity", cascadeCapacity);
                params.put("compressor_id", compId);
                params.put("compressor_make", compMake);
                params.put("compressor_model", compModel);
                params.put("compressor_serial_number", compSerial);
                params.put("compressor_type", compType);
                params.put("dispenser_id", dispId);
                params.put("dispenser_make", dispMake);
                params.put("dispenser_model", dispModel);
                params.put("dispenser_type", dispType);
                params.put("stationary_cascade_hydrotest_status_date", dateStatus);
                params.put("stationary_cascade_installation_date", dateInstall);




                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);


    }

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
//
//
//        return root;
//
//    }

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

        String url = BASE_URL + "read_master_equipment_info.php";

        RequestQueue queue = Volley.newRequestQueue(getContext());


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("stationary_cascade_make") == null) {
                        Toast.makeText(getContext(), "Please enter valid id.", Toast.LENGTH_SHORT).show();
                    } else {

                        date_status.setText(jsonObject.getString("stationary_cascade_hydrotest_status_date"));
                        date_install.setText(jsonObject.getString("stationary_cascade_installation_date"));
                        cascade_id.setText(jsonObject.getString("stationary_cascade_id"));
                        cascade_make.setText(jsonObject.getString("stationary_cascade_make"));
                        cascade_model.setText(jsonObject.getString("stationary_cascade_model"));
                        cascade_serial.setText(jsonObject.getString("stationary_cascade_serial_number"));
                        cascade_status.setText(jsonObject.getString("stationary_hydrotest_status"));
                        cascade_capacity.setText(jsonObject.getString("stationary_cascade_capacity")+"Kg");

                        cascade_reorder_point.setText(jsonObject.getString("stationary_cascade_reorder_point")+"Kg");

                        comp_id.setText(jsonObject.getString("compressor_id"));
                        comp_make.setText(jsonObject.getString("compressor_make"));
                        comp_model.setText(jsonObject.getString("compressor_model"));
                        comp_serial.setText(jsonObject.getString("compressor_serial_number"));
                        comp_type.setText(jsonObject.getString("compressor_type"));
                        disp_id.setText(jsonObject.getString("dispenser_id"));
                        disp_make.setText(jsonObject.getString("dispenser_make"));
                        disp_model.setText(jsonObject.getString("dispenser_model"));
                        disp_type.setText(jsonObject.getString("dispenser_type"));


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