package com.apc.cng_hpcl.home.registration.subtabs;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.apc.cng_hpcl.home.HomeAdmin;
import com.apc.cng_hpcl.home.registration.Registration;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;


public class EmployeeRegFragment extends Fragment {


    public EmployeeRegFragment() {
        // Required empty public constructor
    }

    private static final String BASE_URL_URL = BASE_URL + "admin_tab.php?apicall=";
    public static final String URL_EMP_REG = BASE_URL_URL + "emp_reg";
    String[] organization = {"HPCL", "ABC", "DEF", "GHI", "JKL"};
    boolean isAllFieldsChecked = false;
    EditText etFirstName, etLastName, etEmail, etPhone, emp_id, etMiddleName,etage;
    CheckBox checkBox;
    Button bProceed, bCancel;
    Spinner spin_org;
    String empType="Employee";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employee, container, false);

        spin_org = root.findViewById(R.id.organization);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, organization);
        spin_org.setAdapter(adapter1);

        checkBox = root.findViewById(R.id.checkbox_emp_reg);
        TextView title = root.findViewById(R.id.title);

        bProceed = root.findViewById(R.id.proceedButton);
        bCancel = root.findViewById(R.id.cancelButton);

        emp_id = root.findViewById(R.id.emp_id);
        etFirstName = root.findViewById(R.id.firstName);
        etMiddleName = root.findViewById(R.id.middleName);
        etage=root.findViewById(R.id.age);
        etLastName = root.findViewById(R.id.lastName);
        etEmail = root.findViewById(R.id.email);
        etPhone = root.findViewById(R.id.phone);



//        LinearLayout myCheckbox = (LinearLayout) root.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    title.setText("Register as Partner Employee");
                    empType="Partner";
                }
                if (!checked) {
                    title.setText("Register as  Employee");
                    empType="Employee";
                }

            }
        });



        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
                Intent intent = new Intent(getContext(), HomeAdmin.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

//        bProceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // store the returned value of the dedicated function which checks
//                // whether the entered data is valid or if any fields are left blank.
//                isAllFieldsChecked = CheckAllFields();
//
//                // the boolean variable turns to be true then
//                // only the user must be proceed to the activity2
//                if (isAllFieldsChecked) {
//                    Intent i = new Intent(getActivity(), Registration.class);
//                    startActivity(i);
//                }
//            }
//        });
//
//        bCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().finish();
//                System.exit(0);
//            }
//        });


        return root;
    }

    private void insertData() {

        final String emp_Type =empType;
        final String empId = emp_id.getText().toString();
        final String empFN = etFirstName.getText().toString();
        final String empLN = etLastName.getText().toString();
        final String empEmail = etEmail.getText().toString();
        final String empPhone = etPhone.getText().toString();
        final String empMN = etMiddleName.getText().toString();
        final String empAge = etage.getText().toString();

        final String organizationId = spin_org.getSelectedItem().toString();


        StringRequest request = new StringRequest(Request.Method.POST, URL_EMP_REG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.e(TAG, "insertData Response = " + response);
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
                params.put("Emp_id", empId);
                params.put("Emp_Orgnization_id", organizationId);
                params.put("Emp_Type", emp_Type);
                params.put("Emp_First_Name", empFN);
                params.put("Emp_Middle_Name", empMN);
                params.put("Emp_Last_Name", empLN);
                params.put("Emp_Contact_Number",empPhone );
                params.put("Emp_Email_Id",empEmail );
                params.put("Emp_Age", empAge);
                params.put("status","inactive");


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);


    }


//    private boolean CheckAllFields() {
//        if (etFirstName.length() == 0) {
//            etFirstName.setError("This field is required");
//            return false;
//        }
//
//        if (etLastName.length() == 0) {
//            etLastName.setError("This field is required");
//            return false;
//        }
//
//        if (etEmail.length() == 0) {
//            etEmail.setError("Email is required");
//            return false;
//        }
//
//        if (etPhone.length() == 0) {
//            etPhone.setError("Phone is required");
//            return false;
//        }
//
//        // after all validation return true.
//        return true;
//    }


}