package com.apc.cng_hpcl.home.master.subtabs.mothergasstation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.apc.cng_hpcl.home.transaction.MgsDbsModel;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class EditMGSGeneralInfoFragment extends Fragment {

    public EditMGSGeneralInfoFragment() {
    }

    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_MgsDbs = BASE_URL_URL + "readMgsDbs";
    private static final String BASE_URL_URL_UPDATE = BASE_URL + "master_reg_edit.php?apicall=";
    public static final String URL_GEN_INFOUPDATE = BASE_URL_URL_UPDATE + "updateGenInfo";

    EditText name_mgs, address, name_incharge, phone, bays_num, disp_bays, mgs_id;
    TextInputLayout edit_mgsId;
    ImageView locate_me;
    TextView lat_long;
    Button cancelButton, proceedButton;
    Spinner station_type, station_id;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> stationIdCGS = new ArrayList<>();
    ArrayList<String> stationIdMGS = new ArrayList<>();
    ArrayList<String> stationIdDBS = new ArrayList<>();
    ArrayList<String> stationType = new ArrayList<>();


//    String[] stationType = {"City Gas Station", "Mother Gas Station", "Daughter Booster Station"};
//
//    String[] stationIdCGS = {"CGS001", "CGS002", "CGS003"};
//    String[] stationIdMGS = {"MGS001", "MGS002", "MGS003"};
//    String[] stationIdDBS = {"DBS001", "DBS002", "DBS003"};


    FusedLocationProviderClient client;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_m_g_s_general_info, container, false);

        name_mgs = root.findViewById(R.id.name_mgs);
        address = root.findViewById(R.id.address);
        name_incharge = root.findViewById(R.id.name_incharge);
        phone = root.findViewById(R.id.phone);
        mgs_id = root.findViewById(R.id.mgs_id);
        bays_num = root.findViewById(R.id.bays_num);
        disp_bays = root.findViewById(R.id.disp_bays);
        locate_me = root.findViewById(R.id.locate_me);
        lat_long = root.findViewById(R.id.lat_long);
        cancelButton = root.findViewById(R.id.cancelButton);
        proceedButton = root.findViewById(R.id.proceedButton);
        station_type = root.findViewById(R.id.station_type);
        station_id = root.findViewById(R.id.station_id);
        edit_mgsId = root.findViewById(R.id.edit_mgsId);
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
                updateMgsDbs();
//                Intent intent = new Intent(getActivity(), MotherGasStation.class);
//                startActivity(intent);
            }
        });


        return root;
    }

    private void updateMgsDbs() {
        final String stationType = station_type.getSelectedItem().toString();
        final String stationId = station_id.getSelectedItem().toString();
        final String mgsId = mgs_id.getText().toString().trim();
        Log.e(TAG, "MGS_Id=" + mgsId);
        final String mgsName = name_mgs.getText().toString().trim();
        final String Address = address.getText().toString();
        final String inchargeName = name_incharge.getText().toString().trim();
        final String inchargeContact = phone.getText().toString().trim();
        final String NumBays = bays_num.getText().toString().trim();
        final String DispBays = disp_bays.getText().toString().trim();
        final String latLong = lat_long.getText().toString().trim();


        StringRequest request = new StringRequest(Request.Method.POST, URL_GEN_INFOUPDATE,
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
                Log.e(TAG, "stationId=" + stationId);
                Log.e(TAG, "stationType=" + stationType);
                Log.e(TAG, "mgs_Id=" + mgsId);
                Log.e(TAG, "mgsName=" + mgsName);
//                Log.e(TAG, "Notification_approver_ID=" + noteApprover);
                Log.e(TAG, "Address=" + Address);
                Log.e(TAG, "inchargeName=" + inchargeName);
                Log.e(TAG, "inchargeContact=" + inchargeContact);
                Log.e(TAG, "NumBays=" + NumBays);


                params.put("Station_id", stationId);
                params.put("Station_type", stationType);
                params.put("mgsId", mgsId);
                params.put("Station_Name", mgsName);
//                params.put("notification_approver_id", noteApprover);
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
                                        edit_mgsId.setVisibility(View.VISIBLE);
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

    private void getDetails(String sationId) {

        String url = BASE_URL + "read_master_gen_info.php";
        RequestQueue queue = Volley.newRequestQueue(getContext());


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    jsonObject.getString("Station_Name");

                    mgs_id.setText(jsonObject.getString("mgsId"));
                    name_mgs.setText(jsonObject.getString("Station_Name"));
                    address.setText(jsonObject.getString("Station_Address"));
                    name_incharge.setText(jsonObject.getString("Station_In_Charge_Name"));
                    phone.setText(jsonObject.getString("Station_In_Charge_Contact_Number"));
                    bays_num.setText(jsonObject.getString("Number_Filling_Bays"));
                    disp_bays.setText(jsonObject.getString("Number_Dispenser_Per_Bay"));
                    lat_long.setText(jsonObject.getString("Latitude_Longitude"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Failed to get data" + error, Toast.LENGTH_SHORT).show();
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