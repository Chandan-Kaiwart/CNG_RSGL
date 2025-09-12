package com.apc.cng_hpcl.home.master.subtabs.mothergasstation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class MGSGeneralInfoFragment extends Fragment {

    public MGSGeneralInfoFragment() {
        // Required empty public constructor
    }

    private static final String BASE_URL_URL = BASE_URL + "master_reg_edit.php?apicall=";
    public static final String URL_GEN_INFO = BASE_URL_URL + "insertGenInfo";
    private static final String BASE_URL_URL2 = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_MgsDbs = BASE_URL_URL2 + "readMgsDbs";
    public static final String URL_NoteApprover = BASE_URL_URL2 + "readNoteApprover";
    EditText station_id, name_mgs, add1, add2, add3, city, state, pincode, name_incharge, phone, bays_num, disp_bays;
    ImageView locate_me;
    TextView lat_long;
    Button cancelButton, proceedButton;
    Spinner station_type, mgs_id,note_approver_id;
     String stationId;
    LinearLayout mgs_for_dbs;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    String[] stationType = {"City Gas Station", "Mother Gas Station", "Daughter Booster Station"};
    private ArrayList<NoteApproverModel> noteApproverModelArrayList;
    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> stationIdMGS = new ArrayList<>();
    ArrayList<String> noteApprovedId = new ArrayList<>();
    String MgsId = "NA";

    FusedLocationProviderClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_m_g_s_general_info, container, false);
        note_approver_id = root.findViewById(R.id.note_approver_id);
        mgs_for_dbs = root.findViewById(R.id.mgs_for_dbs);
        mgs_id = root.findViewById(R.id.mgs_id);
        name_mgs = root.findViewById(R.id.name_mgs);
        add1 = root.findViewById(R.id.add1);
        add2 = root.findViewById(R.id.add2);
        add3 = root.findViewById(R.id.add3);
        city = root.findViewById(R.id.city);
        state = root.findViewById(R.id.state);
        pincode = root.findViewById(R.id.pincode);
        name_incharge = root.findViewById(R.id.name_incharge);
        phone = root.findViewById(R.id.phone);
        bays_num = root.findViewById(R.id.bays_num);
        disp_bays = root.findViewById(R.id.disp_bays);
        locate_me = root.findViewById(R.id.locate_me);
        lat_long = root.findViewById(R.id.lat_long);
        cancelButton = root.findViewById(R.id.cancelButton);
        proceedButton = root.findViewById(R.id.proceedButton);
        station_type = root.findViewById(R.id.station_type);
        station_id = root.findViewById(R.id.station_id);


        readMgsDbs();
        readNoteApprover();



        client = LocationServices.getFusedLocationProviderClient(getActivity());
        locate_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity()
                        , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });


        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertData();
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
                                    String station_id = mgsdbsModelArrayList.get(i).getStation_Id().toLowerCase().substring(0, 3);


                                    if (station_id.equals("mgs")) {
                                        stationIdMGS.add(mgsdbsModelArrayList.get(i).getStation_Id());

                                    }

                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapterMgs = new ArrayAdapter<String>(getContext(), simple_spinner_item, stationType);
                            spinnerArrayAdapterMgs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            station_type.setAdapter(spinnerArrayAdapterMgs);


                            station_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (station_type.getSelectedItem()
                                            .toString()
                                            .equals("City Gas Station")) {

                                        mgs_for_dbs.setVisibility(View.GONE);

                                    } else if (station_type.getSelectedItem()
                                            .toString()
                                            .equals("Mother Gas Station")) {

                                        mgs_for_dbs.setVisibility(View.GONE);

                                    } else {
                                        ArrayAdapter<String> sationIdAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stationIdMGS);
                                        mgs_id.setAdapter(sationIdAdapter);
                                        mgs_for_dbs.setVisibility(View.VISIBLE);
                                        mgs_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                MgsId = mgs_id.getSelectedItem().toString();
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {
                                                MgsId = "NA";
                                            }
                                        });
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


    private void readNoteApprover() {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_NoteApprover,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {


                        Log.e("strrrrr", ">>" + response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            if (obj.optString("status").equals("true")) {

                                noteApproverModelArrayList = new ArrayList<>();
                                JSONArray dataArray = obj.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    NoteApproverModel noteApproverModel = new NoteApproverModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);

                                    noteApproverModel.setEmp_id(dataobj.getString("Emp_id"));
                                    noteApproverModelArrayList.add(noteApproverModel);

                                }

                                for (int i = 0; i < noteApproverModelArrayList.size(); i++) {
//                                    String emp_id = noteApproverModelArrayList.get(i).getEmp_id().toLowerCase().substring(0, 3);
                                    noteApprovedId.add(noteApproverModelArrayList.get(i).getEmp_id());

                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapterMgs = new ArrayAdapter<String>(getContext(), simple_spinner_item, noteApprovedId);
                            spinnerArrayAdapterMgs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            note_approver_id.setAdapter(spinnerArrayAdapterMgs);




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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().
                getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
//                        onLocationChanged(location);
                        lat_long.setText(String.valueOf(location.getLatitude()) + ',' + String.valueOf(location.getLongitude()));
                    } else {
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
                                setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                lat_long.setText(String.valueOf(location.getLatitude()) + ',' + String.valueOf(location.getLongitude()));
                            }
                        };

                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


    private void insertData() {
//        name_mgs, add1, add2, add3, city, state, pincode, name_incharge, phone, bays_num, disp_bays, lat_long
        final String stationType = station_type.getSelectedItem().toString();

        switch (stationType) {
            case "City Gas Station":
                stationId = "CGS" + station_id.getText().toString();
                break;
            case "Mother Gas Station":
                stationId = "MGS" + station_id.getText().toString();
                break;
            case "Daughter Booster Station":
                stationId = "DBS" + station_id.getText().toString();
                break;
        }

        final String mgsId = MgsId;
        Log.e(TAG, "MGS_Id=" + MgsId);
        final String noteApprover = note_approver_id.getSelectedItem().toString();
        final String mgsName = name_mgs.getText().toString().trim();
        final String Address1 = add1.getText().toString();
        final String Address2 = add2.getText().toString();
        final String Address3 = add3.getText().toString();
        final String City = city.getText().toString();
        final String State = state.getText().toString();
        final String Pincode = pincode.getText().toString();
        final String Address = (Address1 + ',' + Address2 + ',' + Address3 + ',' + City + ',' + State + ',' + Pincode);
        final String inchargeName = name_incharge.getText().toString().trim();
        final String inchargeContact = phone.getText().toString().trim();
        final String NumBays = bays_num.getText().toString().trim();
        final String DispBays = disp_bays.getText().toString().trim();
        final String latLong = lat_long.getText().toString().trim();

//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Loading...");
        if (TextUtils.isEmpty(pincode.getText())) {
            Toast.makeText(getContext(), "Enter Pincode", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(state.getText())) {
            Toast.makeText(getContext(), "Enter State", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(city.getText())) {
            Toast.makeText(getContext(), "Enter City", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(station_id.getText())) {
            Toast.makeText(getContext(), "Enter Station Id", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(name_mgs.getText())) {
            Toast.makeText(getContext(), "Enter Name of Master Gas Station", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(add1.getText())) {
            Toast.makeText(getContext(), "Enter Address", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(name_incharge.getText())) {
            Toast.makeText(getContext(), "Enter Name Incharge", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(phone.getText())) {
            Toast.makeText(getContext(), "Enter Phone Number", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(bays_num.getText())) {
            Toast.makeText(getContext(), "Enter Bays Number", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(disp_bays.getText())) {
            Toast.makeText(getContext(), "Enter Dispenser Bays", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(lat_long.getText())) {
            Toast.makeText(getContext(), "Click on Latitude and Longitude Button", Toast.LENGTH_SHORT).show();
            return;
        }

//
//        else{
//        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, URL_GEN_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(getContext(), "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Log.e(TAG, "response=" + response);
//                        if (response.equalsIgnoreCase("Data Inserted")) {
//                            Toast.makeText(getContext(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
////                            progressDialog.dismiss();
//                        } else if (response.equalsIgnoreCase("Station Id already exists")) {
//                            Toast.makeText(getContext(), "Station Id already exists", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Error=" + response);
////                            progressDialog.dismiss();
//
//                        } else if (response.equalsIgnoreCase("Insertion Failed")) {
//                            Toast.makeText(getContext(), "Insertion Failed", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Error=" + response);
////                            progressDialog.dismiss();
//
//                        }
//                        else  {
//                            Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Error=" + response);
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
                Log.e(TAG, "stationId=" + stationId);
                Log.e(TAG, "stationType=" + stationType);
                Log.e(TAG, "mgs_Id=" + mgsId);
                Log.e(TAG, "mgsName=" + mgsName);
                Log.e(TAG, "Notification_approver_ID=" + noteApprover);
                Log.e(TAG, "Address=" + Address);
                Log.e(TAG, "inchargeName=" + inchargeName);
                Log.e(TAG, "inchargeContact=" + inchargeContact);
                Log.e(TAG, "NumBays=" + NumBays);


                params.put("Station_id", stationId);
                params.put("Station_type", stationType);
                params.put("mgsId", mgsId);
                params.put("Station_Name", mgsName);
                params.put("notification_approver_id", noteApprover);
                params.put("Station_Address", Address);
                params.put("Station_In_Charge_Name", inchargeName);
                params.put("Station_In_Charge_Contact_Number", inchargeContact);
                params.put("Number_Filling_Bays", NumBays);
                params.put("Number_Dispenser_Per_Bay", DispBays);
                params.put("Latitude_Longitude", latLong);
//                params.put("Create_User_Id", name);
//                    "Modified_User_Id"
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);


    }


}


//}