package com.apc.cng_hpcl.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.HomeAdmin;
import com.apc.cng_hpcl.home.HomeOperator;
import com.apc.cng_hpcl.home.HomeManager;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class LoginFragment extends Fragment {

    //    private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
    private static final String BASE_URL_URL = BASE_URL + "luag_login.php?apicall=";
    public static final String URL_LOGIN = BASE_URL_URL + "login";
    EditText user_name, u_password;
    Button login;
    private static final String[] paths = {"Operator", "Manager", "Admin"};
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;
    String username, password;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);



        user_name = (EditText) root.findViewById(R.id.username);
        u_password = (EditText) root.findViewById(R.id.password);
        login = root.findViewById(R.id.login);

        emailError = root.findViewById(R.id.emailError);
        passError = root.findViewById(R.id.passError);
//        Spinner spinner = root.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//                        login.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                userLogin();
//
//                                Intent intent = new Intent(getContext(), HomeOperator.class);
//                                startActivity(intent);
//                            }
//                        });
//                        break;
//                    case 1:
//                        login.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
////                SetValidation();
//
//                                Intent intent = new Intent(getContext(), HomeManager.class);
//                                startActivity(intent);
//                            }
//                        });
//                        break;
//                    case 2:
//                        login.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
////                SetValidation();
//
//                                Intent intent = new Intent(getContext(), HomeAdmin.class);
//                                startActivity(intent);
//                            }
//                        });
//                        break;
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        return root;
    }

    private void userLogin() {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        username = user_name.getText().toString();
        password = u_password.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getContext(), "Please enter your username", Toast.LENGTH_SHORT).show();

            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_SHORT).show();

//            etPassword.setError("Please enter your password");
//            etPassword.requestFocus();
            return;
        }


        StringRequest request = new StringRequest(Request.Method.POST, URL_LOGIN, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response = " + response);
                    JSONObject jsonObject = new JSONObject(response);

//                    Log.e(TAG, "username = " + jsonObject.getString("username"));

//                    boolean success = jsonObject.getBoolean("success");
                    boolean err = jsonObject.getBoolean("error");
                    if (!err) {
                        String usernameJO = jsonObject.getString("username");
                        String emp_type = usernameJO.toLowerCase().substring(0, 3);
                        String station="NA";
                        boolean isDBS=false;
                        String nam=jsonObject.getString("note_approver_mgs");
                        String nad=jsonObject.getString("note_approver_dbs");
                        String loc="";
                        try {
                            loc=jsonObject.getString("lat_long");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        if(!nam.equals(station)){
                            station=nam;
                        }
                        else if(!nad.equals(station)){
                            station=nad;
                            isDBS=true;
                        }
                        Log.e(TAG, "emp_type = " + emp_type);
                        Log.e(TAG, "station = " + station);
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login",MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("username", usernameJO);
                        myEdit.putString("station", station);
                        myEdit.putString("loc", loc);

                        myEdit.putBoolean("isDbs",isDBS);
                        myEdit.putBoolean("isLoggedIn",true);
                        myEdit.apply();

                        switch (emp_type) {
                            case "ope":
                                Intent intent = new Intent(getContext(), HomeOperator.class);
                                intent.putExtra("username", usernameJO);
                                intent.putExtra("station", station);
                                intent.putExtra("isDbs",isDBS);
                                startActivity(intent);
                                getActivity().finish();

                                break;
                            case "man":
                                Intent intent1 = new Intent(getContext(), HomeManager.class);
                                intent1.putExtra("username", usernameJO);
                                intent1.putExtra("station", station);
                                intent1.putExtra("isDbs",isDBS);
                                startActivity(intent1);
                                getActivity().finish();
                                break;
                            case "adm":

                                Intent intent2 = new Intent(getContext(), HomeAdmin.class);
                                intent2.putExtra("username", usernameJO);
                                startActivity(intent2);
                                getActivity().finish();

                                break;

                        }
                    } else {
                        Toast.makeText(getContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();

                    }

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }


//    public void SetValidation() {
    // Check for a valid email address.
//        if (user_name.getText().toString().isEmpty()) {
//            emailError.setError(getResources().getString(R.string.email_error));
//            isEmailValid = false;
//        } else if (!Patterns.EMAIL_ADDRESS.matcher(user_name.getText().toString()).matches()) {
//            emailError.setError(getResources().getString(R.string.error_invalid_email));
//            isEmailValid = false;
//        } else  {
//            isEmailValid = true;
//            emailError.setErrorEnabled(false);
//        }

    // Check for a valid password.
//        if (u_password.getText().toString().isEmpty()) {
//            passError.setError(getResources().getString(R.string.password_error));
//            isPasswordValid = false;
//        } else if (u_password.getText().length() < 6) {
//            passError.setError(getResources().getString(R.string.error_invalid_password));
//            isPasswordValid = false;
//        } else  {
//            isPasswordValid = true;
//            passError.setErrorEnabled(false);
//        }
//
//        if (isEmailValid && isPasswordValid) {
//            Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
//        }
//
//    }
}