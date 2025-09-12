package com.apc.cng_hpcl.home.master.subtabs.lcv;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

import android.app.DatePickerDialog;
import android.os.Bundle;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class RegLcvCascadeInfoFragment extends Fragment {


    public RegLcvCascadeInfoFragment() {
        // Required empty public constructor
    }

//    String[] owners = {"Anmol Transport","Guru Kripa Gas"};
//    String[] anmol_lcv= {"DL-1MA-5353","HR-38AB-9008","HR-38AB-0291","HR-38AB-35216","DL-1MA-5216","UP-17AT-7351","HR-38AB-7669","DL-1MA-6137","DL-1MA-6103"};
//    String[] guru_lcv = {"DL-1MA-4669","DL-1MA-6029","HR-63E-6118","DL-1MA-3661","HR-63E-5616","DL-1MA-4638","HR-63E-1684"};
//
//    String[] temp_select_make={"Baumer"};
//    String[] pressure_select_make={"Baumer","ITEC"};
//    String[] cascade_select_make={"Jiolat Auto Gas Industries","Rama Cylinder Pvt. Ltd.","Everest Kanto Cylinder Pvt.Ltd."};
//    String[] cascade_select_model={"HPCL-M-40","HPCL-S-40","HPCL/260-M-40","HPCL"};
    private static final String BASE_URL_URL = BASE_URL + "reg_lcv.php?apicall=";
    String URL_REG_LCV_CASCADE = BASE_URL_URL+"insertLcvCascadeInfo";

    EditText lcv_registered_to,lcv_num,tempmake,pressuremake,cascademake,cascademodel;
    TextView select_org,select_lcv_num,select_temp_make,select_pressure_make,select_cascade_make,select_cascade_model;

    EditText temp_id,temp_model,temp_calib,pressure_id,pressure_model,pressure_calib;
    TextView date_temp,date_press;
    Button cancelButton, proceedButton;

    EditText cascade_id, cascade_serial, cascade_status, cascade_capacity;
    TextView date_status, date_install;
    String dateStatus;

    private int year, month, day, hour, minute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_reg_lcv_cascade_info, container, false);

        lcv_registered_to = root.findViewById(R.id.lcv_registered_to);
        lcv_num = root.findViewById(R.id.lcv_num);
        tempmake = root.findViewById(R.id.temp_make);
        pressuremake = root.findViewById(R.id.pressure_make);
        cascademake=root.findViewById(R.id.cascade_make);
        cascademodel=root.findViewById(R.id.cascade_model);



        select_org = root.findViewById(R.id.select_org);
        select_lcv_num = root.findViewById(R.id.select_lcv_num);


        date_temp = root.findViewById(R.id.date_temp);
        date_press = root.findViewById(R.id.date_press);

        temp_id = root.findViewById(R.id.temp_id);
        pressure_id = root.findViewById(R.id.pressure_id);
        temp_model = root.findViewById(R.id.temp_model);
        temp_calib = root.findViewById(R.id.temp_calib);

        pressure_model = root.findViewById(R.id.pressure_model);
        pressure_calib = root.findViewById(R.id.pressure_calib);

        cancelButton = root.findViewById(R.id.cancelButton);
        proceedButton = root.findViewById(R.id.proceedButton);

        date_status = root.findViewById(R.id.date_status);
        date_install = root.findViewById(R.id.date_install);
        cascade_id = root.findViewById(R.id.cascade_id);

        cascade_serial = root.findViewById(R.id.cascade_serial);
        cascade_status = root.findViewById(R.id.cascade_status);
        cascade_capacity = root.findViewById(R.id.cascade_capacity);

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

        date_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateStatus=day + "/" + (month + 1) + "/" + year;
                        date_status.setText(day + "/" + (month + 1) + "/" + year);
                        Log.d(TAG, "onDateSet: Date_Install="+dateStatus);
                    }
                }, year, month, day);
                picker.show();
            }
        });


        date_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date_install.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });



        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertData();
//                getDetails(phone.getText().toString());
            }
        });


        return root;
    }

    private void insertData() {

        final String lcvnum= lcv_num.getText().toString();
        final String lcvregisteredto = lcv_registered_to.getText().toString();
        final String tempId = temp_id.getText().toString().trim();
        final String tempMake = tempmake.getText().toString();
        final String tempModel = temp_model.getText().toString();
        final String tempCalib = temp_calib.getText().toString();
        final String pressureId = pressure_id.getText().toString().trim();
        final String pressureMake = pressuremake.getText().toString();
        final String pressureModel = pressure_model.getText().toString();
        final String pressureCalib = pressure_calib.getText().toString();
        final String dateTemp = date_temp.getText().toString().trim();
        final String datePress = date_press.getText().toString().trim();
        final String cascadeId = cascade_id.getText().toString().trim();
        final String cascadeMake = cascademake.getText().toString();
        final String cascadeModel = cascademodel.getText().toString();
        final String cascadeSerial = cascade_serial.getText().toString();
        final String cascadeStatus = cascade_status.getText().toString();
        final String cascadeCapacity = cascade_capacity.getText().toString();
        final String dateInstall = date_install.getText().toString();

        if (TextUtils.isEmpty(lcv_num.getText())) {
            Toast.makeText(getContext(), "Enter LCV Number", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(lcv_registered_to.getText())) {
            Toast.makeText(getContext(), "Enter LCV Registered to", Toast.LENGTH_SHORT).show();
            return;
        } else  if (TextUtils.isEmpty(temp_id.getText())) {
            Toast.makeText(getContext(), "Enter Temperature Id", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(tempmake.getText())) {
            Toast.makeText(getContext(), "Enter Temperature Make", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(temp_model.getText())) {
            Toast.makeText(getContext(), "Enter Temperature Model", Toast.LENGTH_SHORT).show();
            return;
        } else
        if (TextUtils.isEmpty(temp_calib.getText())) {
            Toast.makeText(getContext(), "Enter Temperature Calibration", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(pressure_id.getText())) {
            Toast.makeText(getContext(), "Enter Pressure Id", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(pressuremake.getText())) {
            Toast.makeText(getContext(), "Enter Pressure Make", Toast.LENGTH_SHORT).show();
            return;
        }


        else if (TextUtils.isEmpty(pressure_model.getText())) {
            Toast.makeText(getContext(), "Enter Pressure Model", Toast.LENGTH_SHORT).show();
            return;
        } else  if (TextUtils.isEmpty(pressure_calib.getText())) {
            Toast.makeText(getContext(), "Enter Pressure Calibration", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(date_temp.getText())) {
            Toast.makeText(getContext(), "Select Temperature Date", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(date_press.getText())) {
            Toast.makeText(getContext(), "Select Pressure Date", Toast.LENGTH_SHORT).show();
            return;
        } else
        if (TextUtils.isEmpty(cascade_id.getText())) {
            Toast.makeText(getContext(), "Enter Cascade Id ", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(cascademake.getText())) {
            Toast.makeText(getContext(), "Enter Cascade Make", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(cascademodel.getText())) {
            Toast.makeText(getContext(), "Enter Cascade Model", Toast.LENGTH_SHORT).show();
            return;
        }


        else if (TextUtils.isEmpty(cascade_serial.getText())) {
            Toast.makeText(getContext(), "Enter Cascade Serial", Toast.LENGTH_SHORT).show();
            return;
        } else
        if (TextUtils.isEmpty(cascade_status.getText())) {
            Toast.makeText(getContext(), "Enter Cascade Status ", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(cascade_capacity.getText())) {
            Toast.makeText(getContext(), "Enter Cascade Capacity", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(date_install.getText())) {
            Toast.makeText(getContext(), "Select Date of Installation", Toast.LENGTH_SHORT).show();
            return;
        }


        StringRequest request = new StringRequest(Request.Method.POST,  URL_REG_LCV_CASCADE,
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

                params.put("lcv_num", lcvnum);
                params.put("lcv_registered_to", lcvregisteredto);

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

                params.put("stationary_cascade_id", cascadeId);
                params.put("stationary_cascade_make", cascadeMake);
                params.put("stationary_cascade_model", cascadeModel);
                params.put("stationary_cascade_serial_number", cascadeSerial);
                params.put("stationary_hydrotest_status", cascadeStatus);
                params.put("stationary_cascade_capacity", cascadeCapacity);

                params.put("stationary_cascade_hydrotest_status_date", dateStatus);
                params.put("stationary_cascade_installation_date", dateInstall);

                //params.put("Create_User_Id", Create_User_Id);
                //params.put("Modified_User_Id", Modified_User_Id);
//

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);




    }
}