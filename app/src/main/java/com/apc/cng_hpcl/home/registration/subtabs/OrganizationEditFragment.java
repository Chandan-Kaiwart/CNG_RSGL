package com.apc.cng_hpcl.home.registration.subtabs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.apc.cng_hpcl.dummy.VolleyDb;
import com.apc.cng_hpcl.home.HomeAdmin;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class OrganizationEditFragment extends Fragment {
    String Org_Type, Org_Full_Name, Org_Short_Name, Org_Sector, Org_Full_Address,
            Org_Contact_Person, Org_Location;
    String Org_Landline_Number, Org_Mobile_Number,Modified_User_Id;
    TextView lat_long;
    EditText iDEdt,title, org_full_name, org_abr, sector, address, name_cp, phone, location, landline;
    Button bedit, bProceed, bCancel,getOrgDetailsBtn;
ScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_organization_edit, container, false);

        getOrgDetailsBtn = root.findViewById(R.id.idBtnGet);
        iDEdt = root.findViewById(R.id.idEdt);
        scrollView = root.findViewById(R.id.scrollView);

        org_full_name = root.findViewById(R.id.organization);


        title = root.findViewById(R.id.title);
        bedit = root.findViewById(R.id.edit);
        bProceed = root.findViewById(R.id.proceedButton);
        bCancel = root.findViewById(R.id.cancelButton);


        org_abr = root.findViewById(R.id.org_abr);
        sector = root.findViewById(R.id.sector);
        address = root.findViewById(R.id.address);
        name_cp = root.findViewById(R.id.name_cp);
        phone = root.findViewById(R.id.mobile);
        landline = root.findViewById(R.id.landline);
        lat_long = root.findViewById(R.id.lat_long);

        getOrgDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking if the id text field is empty or not.
                if (TextUtils.isEmpty(iDEdt.getText().toString())) {
                    Toast.makeText(getContext(), "Please enter Mobile number ", Toast.LENGTH_SHORT).show();
                    return;
                }
                // calling method to load data.
                getDetails(iDEdt.getText().toString());

//                Toast.makeText(getContext(), "Organization data updated", Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(getContext(), HomeAdmin.class);
//                startActivity(intent);
//                getActivity().finish();

            }
        });


//        getDetails("35");
    
        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails(phone.getText().toString());
                Toast.makeText(getContext(), "Organization data updated", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), HomeAdmin.class);
                startActivity(intent);
                getActivity().finish();
            }


        });

        return root;
    }

    private void updateDetails(String Org_Id) {


        Org_Type = title.getText().toString();
        Org_Full_Name = org_full_name.getText().toString();
        Org_Short_Name = org_abr.getText().toString();
        Org_Sector = sector.getText().toString();
        Org_Full_Address = address.getText().toString();
        Org_Contact_Person = name_cp.getText().toString();
        Org_Landline_Number = landline.getText().toString();
        Org_Mobile_Number = phone.getText().toString();
        Org_Location = lat_long.getText().toString();
        Modified_User_Id= SimpleDateFormat.getDateTimeInstance().format(new Date());



        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL+"update_main_org.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getActivity(), "Data Inserted Successfully ", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "Insertion failed please try again", Toast.LENGTH_SHORT).show();


            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<String,String>();

//                params.put("id",id);
//                params.put("name",name);
//                params.put("email",email);
//                params.put("contact",contact);
//                params.put("address",address);

//                params.put("id",Org_Id);
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
                params.put("Modified_User_Id", Modified_User_Id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);


    }

    private void getDetails(String orgId) {

        String url = BASE_URL+"read_main_org.php";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // on below line passing our response to json object.
                    JSONObject jsonObject = new JSONObject(response);
                    // on below line we are checking if the response is null or not.
                    if (jsonObject.getString("Org_Type") == null) {
                        // displaying a toast message if we get error
                        Toast.makeText(getContext(), "Please enter valid id.", Toast.LENGTH_SHORT).show();
                    } else {

                        title.setText(jsonObject.getString("Org_Type"));
                        org_full_name.setText(jsonObject.getString("Org_Full_Name"));
                        org_abr.setText(jsonObject.getString("Org_Short_Name"));
                        sector.setText(jsonObject.getString("Org_Sector"));
                        address.setText(jsonObject.getString("Org_Full_Address"));
                        name_cp.setText(jsonObject.getString("Org_Contact_Person"));
                        landline.setText(jsonObject.getString("Org_Landline_Number"));
                        phone.setText(jsonObject.getString("Org_Mobile_Number"));
                        lat_long.setText(jsonObject.getString("Org_Location"));
                        getOrgDetailsBtn.setVisibility(View.GONE);
                        iDEdt.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Failed to get data Please Try after sometime" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                // below line we are creating a map for storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key and value pair to our parameters.
                params.put("id", orgId);

                // at last we are returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }
}
