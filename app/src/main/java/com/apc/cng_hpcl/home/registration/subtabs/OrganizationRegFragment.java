package com.apc.cng_hpcl.home.registration.subtabs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.apc.cng_hpcl.dummy.MysqlDb;
import com.apc.cng_hpcl.home.HomeAdmin;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class OrganizationRegFragment extends Fragment {


    public OrganizationRegFragment() {
        // Required empty public constructor
    }

//    String ServerURL = BASE_URL+"Aug3rd2021/reg_main_org.php";

//    String ServerURL = BASE_URL+"reg_main_org.php";
private static final String BASE_URL_URL = BASE_URL + "admin_tab.php?apicall=";
    public static final String URL_ORG_REG = BASE_URL_URL + "org_reg";
//    String[] organization = {"Hindustan Petroleum Corporation Limited", "Gas Authority of India Ltd", "DEF", "GHI", "JKL"};
    boolean isAllFieldsChecked = false;
    EditText org_abr, sector, address1, address2, address3, city, state, postal_code, name_cp,
            mobile, landline,org_id;
    Button cancelButton, proceedButton;
    TextView lat_long, org_type;
    ImageView locate_me;
//    Spinner org_id;
    String Org_Type,Org_Full_Name, Org_Short_Name, Org_Sector, Org_Full_Address, Address1, Address2, Address3, City, State, Pincode,
            Org_Contact_Person, Org_Location,
            Create_User_Id, Modified_User_Id;

    String  Org_Landline_Number, Org_Mobile_Number;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;

    FusedLocationProviderClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_organization, container, false);
        org_id = root.findViewById(R.id.organization);
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, organization);
//set the spinners adapter to the previously created one.
//        org_id.setAdapter(adapter1);

        CheckBox checkBox = root.findViewById(R.id.checkbox_reg_org);

        org_type = root.findViewById(R.id.title);
        org_abr = root.findViewById(R.id.org_abr);
        sector = root.findViewById(R.id.sector);
        address1 = root.findViewById(R.id.address1);
        address2 = root.findViewById(R.id.address2);
        address3 = root.findViewById(R.id.address3);
        city = root.findViewById(R.id.city);
        state = root.findViewById(R.id.state);
        postal_code = root.findViewById(R.id.postal_code);
        name_cp = root.findViewById(R.id.name_cp);
        mobile = root.findViewById(R.id.mobile);
        landline = root.findViewById(R.id.landline);
        locate_me = root.findViewById(R.id.locate_me);
        lat_long = root.findViewById(R.id.lat_long);
        cancelButton = root.findViewById(R.id.cancelButton);
        proceedButton = root.findViewById(R.id.proceedButton);

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


        checkBox.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    Org_Type="1";
                    org_type.setText("Register as Partner Organization");
                Log.d(TAG, "Org_Type: "+Org_Type);
                }
                if (!checked) {
                    Org_Type="0";
                    org_type.setText("Register as Main Organization ");
                Log.d(TAG, "Org_Type: "+Org_Type);
                }

            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                GetData();
                Log.d(TAG, "Org_Type: "+Org_Type);
                insertData();
                Intent intent = new Intent(getContext(), HomeAdmin.class);
                startActivity(intent);
                getActivity().finish();

//                FragmentTransaction fr = getFragmentManager().beginTransaction();
//                fr.replace(R.id.container1,new OrganizationEditFragment());
//                fr.commit();

            }
        });


        return root;
    }

    private void insertData() {
        Org_Type = org_type.getText().toString();
//        Org_Id = org_abr.getText().toString();
        Org_Full_Name = org_id.getText().toString();

//        Org_Full_Name = org_id.getSelectedItem().toString();
        Org_Short_Name = org_abr.getText().toString();
        Org_Sector = sector.getText().toString();
        Address1 = address1.getText().toString();
        Address2 = address2.getText().toString();
        Address3 = address3.getText().toString();
        City = city.getText().toString();
        State = state.getText().toString();
        Pincode = postal_code.getText().toString();
        Org_Full_Address = (Address1 + ',' + Address2 + ',' + Address3 + ',' + City + ',' + State + ',' + Pincode);
        Org_Contact_Person = name_cp.getText().toString();
        Org_Landline_Number = landline.getText().toString();
        Org_Mobile_Number = mobile.getText().toString();
        Org_Location = lat_long.getText().toString();
//        Log.d(TAG, "Org_Type: "+Org_Type);
        Create_User_Id = SimpleDateFormat.getDateTimeInstance().format(new Date());
//                org_abr.getText().toString();
        Modified_User_Id = org_type.getText().toString();//OrgType is considered here
//        Date_Time_Stamp = SimpleDateFormat.getDateTimeInstance().format(new Date());


        StringRequest request = new StringRequest(Request.Method.POST, URL_ORG_REG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        if (response.equalsIgnoreCase("Data Inserted")) {
                            Toast.makeText(getContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Data Inserted Successfully ", Toast.LENGTH_SHORT).show();
                            Log.e(TAG,"Error="+response);
//                            progressDialog.dismiss();
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
                params.put("Org_type", Org_Type);
//               params.put("Org_Id", id);
                params.put("Org_Full_Name", Org_Full_Name);
                params.put("Org_Short_Name", Org_Short_Name);
                params.put("Org_Sector", Org_Sector);
                params.put("Org_Full_Address", Org_Full_Address);
                params.put("Org_Contact_Person", Org_Contact_Person);
                params.put("Org_Landline_Number", Org_Landline_Number);
                params.put("Org_Mobile_Number", Org_Mobile_Number);
                params.put("Org_Location", Org_Location);
//                params.put("Create_User_Id", Create_User_Id);
//                params.put("Modified_User_Id", Modified_User_Id);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);


    }

//Print Address based on latitude and longitude
//    public void onLocationChanged(Location location) {
//
//        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(getContext(), Locale.getDefault());
//
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//
//        Log.e("latitude", "latitude--" + latitude);
//
//        try {
//            Log.e("latitude", "inside latitude--" + latitude);
//            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//
//            if (addresses != null && addresses.size() > 0) {
//                String address = addresses.get(0).getAddressLine(0);
//                String city = addresses.get(0).getLocality();
//                String state = addresses.get(0).getAdminArea();
//                String country = addresses.get(0).getCountryName();
//                String postalCode = addresses.get(0).getPostalCode();
//                String knownName = addresses.get(0).getFeatureName();
//
//                lat_long.setText(address + " " + city + " " + country);
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }


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


//    public void GetData() {
//
//        Org_Type = org_type.getText().toString();
////        Org_Id = org_abr.getText().toString();
//        Org_Full_Name = org_id.getSelectedItem().toString();
//        Org_Short_Name = org_abr.getText().toString();
//        Org_Sector = sector.getText().toString();
//        Address1 = address1.getText().toString();
//        Address2 = address2.getText().toString();
//        Address3 = address3.getText().toString();
//        City = city.getText().toString();
//        State = state.getText().toString();
//        Pincode = postal_code.getText().toString();
//        Org_Full_Address = (Address1 + ',' + Address2 + ',' + Address3 + ',' + City + ',' + State + ',' + Pincode);
//
//        Org_Contact_Person = name_cp.getText().toString();
//
//        Org_Landline_Number = landline.getText().toString();
//        Org_Mobile_Number = mobile.getText().toString();
//        Org_Location = lat_long.getText().toString();
////        Log.d(TAG, "Org_Type: "+Org_Type);
//        Create_User_Id = SimpleDateFormat.getDateTimeInstance().format(new Date());
////                org_abr.getText().toString();
//        Modified_User_Id = org_type.getText().toString();//OrgType is considered here
////        Date_Time_Stamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
//
//
//    }
//
//    public void InsertData(final String orgtype, final String name, final String shortname, final String sector,
//                           final String address, final String contactPerson, final String landline,
//                           final String mobile, final String location, final String userid, final String modifiedUid
////            , final String datetimestamp
//    ) {
//
//        @SuppressLint("StaticFieldLeak")
//        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
//            @Override
//            protected String doInBackground(String... params) {
//
//
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
////                Log.d(TAG, "Insert method Org_Type: "+orgtype);
//               nameValuePairs.add(new BasicNameValuePair("Org_type", orgtype));
////               nameValuePairs.add(new BasicNameValuePair("Org_Id", id));
//               nameValuePairs.add(new BasicNameValuePair("Org_Full_Name", name));
//               nameValuePairs.add(new BasicNameValuePair("Org_Short_Name", shortname));
//               nameValuePairs.add(new BasicNameValuePair("Org_Sector", sector));
//               nameValuePairs.add(new BasicNameValuePair("Org_Full_Address", address));
//               nameValuePairs.add(new BasicNameValuePair("Org_Contact_Person", contactPerson));
//               nameValuePairs.add(new BasicNameValuePair("Org_Landline_Number", landline));
//               nameValuePairs.add(new BasicNameValuePair("Org_Mobile_Number", mobile));
//               nameValuePairs.add(new BasicNameValuePair("Org_Location", location));
//               nameValuePairs.add(new BasicNameValuePair("Create_User_Id", userid));
//               nameValuePairs.add(new BasicNameValuePair("Modified_User_Id", modifiedUid));
////               nameValuePairs.add(new BasicNameValuePair("Date_Time_Stamp", DateTimeHolder));
//
//                try {
//                    HttpClient httpClient = new DefaultHttpClient();
//
//                    HttpPost httpPost = new HttpPost(ServerURL);
//
//                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                    HttpResponse httpResponse = httpClient.execute(httpPost);
//
//                    HttpEntity httpEntity = httpResponse.getEntity();
//
//
//                } catch (ClientProtocolException e) {
//                    Toast.makeText(getContext(), "ClientProtocolException", Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    Toast.makeText(getContext(), "IOException", Toast.LENGTH_SHORT).show();
//                }
//                return "Data Inserted Successfully";
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//
//                super.onPostExecute(result);
//
//                Toast.makeText(getContext(), "Data Submit Successfully", Toast.LENGTH_LONG).show();
//
//            }
//        }
//
//        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
//
//        sendPostReqAsyncTask.execute(orgtype, name, shortname, sector, address, contactPerson, landline,
//                mobile, location, userid, modifiedUid
//        );
//    }

}
