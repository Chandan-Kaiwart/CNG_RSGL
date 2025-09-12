package com.apc.cng_hpcl.home.registration.subtabs;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
import com.apc.cng_hpcl.home.HomeAdmin;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class EmployeeEditFragment extends Fragment {

    private static final String BASE_URL_URL = BASE_URL + "admin_tab.php?apicall=";
    //    public static final String URL_EMP_READ = BASE_URL_URL + "emp_readData";
    public static final String URL_EMP_READ = BASE_URL + "read_emp.php";
    public static final String URL_EMP_UPDATE = BASE_URL_URL + "emp_update";


    public EmployeeEditFragment() {
        // Required empty public constructor
    }

    //    String[] organization = { "HPCL","ABC","DEF","GHI","JKL" };
    boolean isAllFieldsChecked = false;
    EditText idEdt, title, organization, id, firstName, middleName, lastName, age, phone, email;
    Button idBtnGet, edit, cancelButton, proceedButton;
    ScrollView scrollView;

    String Emp_Type, Emp_firstName, Emp_middleName, Emp_lastName, Emp_age,
            Emp_phone, Emp_email;
    String Emp_id, Emp_organization;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_employee_edit, container, false);
        id = root.findViewById(R.id.id);
        firstName = root.findViewById(R.id.firstName);
        middleName = root.findViewById(R.id.middleName);
        lastName = root.findViewById(R.id.lastName);
        age = root.findViewById(R.id.age);
        phone = root.findViewById(R.id.phone);
        email = root.findViewById(R.id.email);


        idBtnGet = root.findViewById(R.id.idBtnGet);
        idEdt = root.findViewById(R.id.idEdt);
        scrollView = root.findViewById(R.id.scrollView);

        organization = root.findViewById(R.id.organization);


        title = root.findViewById(R.id.title);
        edit = root.findViewById(R.id.edit);
        proceedButton = root.findViewById(R.id.proceedButton);
        cancelButton = root.findViewById(R.id.cancelButton);

        idBtnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking if the id text field is empty or not.
                if (TextUtils.isEmpty(idEdt.getText().toString())) {
                    Toast.makeText(getContext(), "Please enter Mobile number ", Toast.LENGTH_SHORT).show();
                    return;
                }
                // calling method to load data.
                getDetails(idEdt.getText().toString());
//                Toast.makeText(getContext(), "Employee data updated", Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(getContext(), HomeAdmin.class);
//                startActivity(intent);
//                getActivity().finish();
            }
        });


//        getDetails("35");

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails(phone.getText().toString());
//                Toast.makeText(getContext(), "Employee data updated", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), HomeAdmin.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return root;
    }

    private void updateDetails(String Emp_Id) {


        Emp_Type = title.getText().toString();
        Emp_firstName = firstName.getText().toString();
        Emp_middleName = middleName.getText().toString();
        Emp_lastName = lastName.getText().toString();
        Emp_age = age.getText().toString();
        Emp_phone = phone.getText().toString();
        Emp_email = email.getText().toString();
        Emp_id = id.getText().toString();
        Emp_organization = organization.getText().toString();


        StringRequest request = new StringRequest(Request.Method.POST, URL_EMP_UPDATE,
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
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Emp_Type", Emp_Type);
                params.put("Emp_First_Name", Emp_firstName);
                params.put("Emp_Middle_Name", Emp_middleName);
                params.put("Emp_Last_Name", Emp_lastName);
                params.put("Emp_Age", Emp_age);
                params.put("Emp_Contact_Number", Emp_phone);
                params.put("Emp_Email_Id", Emp_email);
                params.put("Emp_id", Emp_id);
                params.put("Emp_Orgnization_id", Emp_organization);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);


    }

    private void getDetails(String empPhone) {

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, URL_EMP_READ, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // on below line passing our response to json object.
                    JSONObject jsonObject = new JSONObject(response);
                    // on below line we are checking if the response is null or not.
                    if (jsonObject.getString("Emp_Type") == null) {
                        // displaying a toast message if we get error
                        Toast.makeText(getContext(), "Please enter valid id.", Toast.LENGTH_SHORT).show();
                    } else {
                        title.setText(jsonObject.getString("Emp_Type"));
                        firstName.setText(jsonObject.getString("Emp_First_Name"));
                        middleName.setText(jsonObject.getString("Emp_Middle_Name"));
                        lastName.setText(jsonObject.getString("Emp_Last_Name"));
                        age.setText(jsonObject.getString("Emp_Age"));
                        phone.setText(jsonObject.getString("Emp_Contact_Number"));
                        id.setText(jsonObject.getString("Emp_id"));
                        organization.setText(jsonObject.getString("Emp_Orgnization_id"));
                        email.setText(jsonObject.getString("Emp_Email_Id"));
                        idBtnGet.setVisibility(View.GONE);
                        idEdt.setVisibility(View.GONE);
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
                params.put("Emp_Contact_Number", empPhone);

                // at last we are returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }
}