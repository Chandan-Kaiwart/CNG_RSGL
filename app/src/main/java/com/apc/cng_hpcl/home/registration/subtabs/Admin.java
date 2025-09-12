package com.apc.cng_hpcl.home.registration.subtabs;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.NoteApproverModel;
import com.apc.cng_hpcl.home.transaction.MgsDbsModel;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.layout.simple_spinner_item;
import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class Admin extends AppCompatActivity {
    private static final String BASE_URL_URL = BASE_URL + "admin_tab.php?apicall=";
    public static final String URL_ADMIN_MOD = BASE_URL_URL + "admin_module";
    private static final String BASE_URL_URL2 = BASE_URL + "msg_dbs_transaction.php?apicall=";
    public static final String URL_NoteApprover = BASE_URL_URL2 + "readNoteApprover";
    public static final String URL_MgsDbs = BASE_URL_URL2 + "readMgsDbs";

    private Spinner emp_id, organization, user_role, note_approver_dbs, note_approver_mgs;
    private static final String[] role = {"Operator", "Manager", "Admin"};
    //    private static final String[] org = {"HPCL", "ABC", "DEF"};
//    private static final String[] empId = {"OPR001", "OPR002", "OPR002", "MGR001", "MGR002", "MGR002"};
//
//    private static final String[] mgs = {"MGS001", "MGS002", "MGS002","NA"};
//
//    private static final String[] dbs = {"DBS001", "DBS002", "DBS002","NA"};
    private ArrayList<NoteApproverModel> noteApproverModelArrayList;
    ArrayList<String> noteApprovedId = new ArrayList<>();
    private ArrayList<MgsDbsModel> mgsdbsModelArrayList;
    ArrayList<String> mgs = new ArrayList<>();
    ArrayList<String> dbs = new ArrayList<>();
    ArrayList<String> org = new ArrayList<>();


    Button submit;
    TextInputLayout mgs_note_layout, dbs_note_layout;
    String username, empId;
    String empName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ActionBar actionBar = getSupportActionBar();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");

        }
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        organization = findViewById(R.id.organization);
        user_role = findViewById(R.id.user_role);

        mgs_note_layout = findViewById(R.id.mgs_note_layout);
        dbs_note_layout = findViewById(R.id.dbs_note_layout);

        note_approver_dbs = findViewById(R.id.note_approver_dbs);
        note_approver_mgs = findViewById(R.id.note_approver_mgs);
        submit = findViewById(R.id.submit);
        emp_id = findViewById(R.id.emp_id);
        readMgsDbs();
        readNoteApprover();


        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, role);

        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_role.setAdapter(adapter3);

//        empName=emp_id.getSelectedItem().toString();
//        Log.e(TAG,"Employee_ID="+emp_id.getSelectedItem().toString());
        emp_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (user_role.getSelectedItem()
                        .toString()) {
                    case "Operator":
                        empId = "ope" + emp_id.getSelectedItem().toString();;
                        break;
                    case "Manager":
                        empId = "man" + emp_id.getSelectedItem().toString();;
                        break;
                    case "Admin":
                        empId = "adm" + emp_id.getSelectedItem().toString();;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, mgs);
//
//        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        note_approver_mgs.setAdapter(adapter4);


//        note_approver_mgs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                dbs_note_layout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, dbs);
//
//        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        note_approver_dbs.setAdapter(adapter5);

//        note_approver_dbs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mgs_note_layout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertdata();
            }
        });
    }

    private void readMgsDbs() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_MgsDbs,
                new Response.Listener<String>() {
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
                                    mgsDbsModel.setStation_Name(dataobj.getString("Station_Name"));
                                    mgsDbsModel.setStation_Id(dataobj.getString("Station_Id"));
                                    mgsDbsModel.setMgsId(dataobj.getString("mgsId"));


                                    mgsdbsModelArrayList.add(mgsDbsModel);

                                }
                                dbs.add(0, "NA");
                                mgs.add(0, "NA");


                                for (int i = 0; i < mgsdbsModelArrayList.size(); i++) {
                                    String stationId = mgsdbsModelArrayList.get(i).getStation_Id().toLowerCase().substring(0, 3);

                                    org.add(mgsdbsModelArrayList.get(i).getStation_Name());
//                                    if (stationId.equals("dbs")) {
                                    String daughterBoosterSataion=mgsdbsModelArrayList.get(i).getStation_Id();
                                    if (!dbs.contains(daughterBoosterSataion)) {
                                        dbs.add(mgsdbsModelArrayList.get(i).getStation_Id().toUpperCase());

                                    }

                                    String motherGasSataion=mgsdbsModelArrayList.get(i).getMgsId();
                                    if (!mgs.contains(motherGasSataion)) {
                                        mgs.add(mgsdbsModelArrayList.get(i).getMgsId().toUpperCase());

                                    }
//                                    }
//                                    if (!mgsdbsModelArrayList.get(i).getMgsId().equals("")) {
//                                        String mgsId = mgsdbsModelArrayList.get(i).getMgsId().toLowerCase().substring(0, 3);
////                                        if (mgsId.equals("mgs")) {
//                                            mgs.add(mgsdbsModelArrayList.get(i).getMgsId().toUpperCase());
////                                        }
//                                    }
                                }
                                ArrayAdapter<String> spinnerArrayAdapterOrgName = new ArrayAdapter<String>(Admin.this, simple_spinner_item, org);
                                spinnerArrayAdapterOrgName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                organization.setAdapter(spinnerArrayAdapterOrgName);

                                ArrayAdapter<String> spinnerArrayAdapterMgs = new ArrayAdapter<String>(Admin.this, simple_spinner_item, mgs);
                                spinnerArrayAdapterMgs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                note_approver_mgs.setAdapter(spinnerArrayAdapterMgs);

                                ArrayAdapter<String> spinnerArrayAdapterDbs = new ArrayAdapter<String>(Admin.this, simple_spinner_item, dbs);
                                spinnerArrayAdapterDbs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                note_approver_dbs.setAdapter(spinnerArrayAdapterDbs);
//                                removeSimpleProgressDialog();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        requestQueue.add(stringRequest);
    }

    private void readNoteApprover() {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
                                    noteApproverModel.setEmp_First_Name(dataobj.getString("Emp_First_Name"));
                                    noteApproverModel.setEmp_id(dataobj.getString("Emp_id"));
                                    noteApproverModelArrayList.add(noteApproverModel);

                                }
                                noteApprovedId.add(0, "NA");

                                for (int i = 0; i < noteApproverModelArrayList.size(); i++) {
//                                    String emp_id = noteApproverModelArrayList.get(i).getEmp_id().toLowerCase().substring(0, 3);
                                    noteApprovedId.add(noteApproverModelArrayList.get(i).getEmp_First_Name());

                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapterMgs = new ArrayAdapter<String>(getApplicationContext(), simple_spinner_item, noteApprovedId);
                            spinnerArrayAdapterMgs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            emp_id.setAdapter(spinnerArrayAdapterMgs);



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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        requestQueue.add(stringRequest);
    }


    private void insertdata() {

        final String emp_Id = empId;
        final String organizationId = organization.getSelectedItem().toString();
        final String user_roleId = user_role.getSelectedItem().toString();
        final String note_approver_dbsId = note_approver_dbs.getSelectedItem().toString();
        final String note_approver_mgsId = note_approver_mgs.getSelectedItem().toString();


        StringRequest request = new StringRequest(Request.Method.POST, URL_ADMIN_MOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e(TAG,"Insert Mapping"+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(Admin.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(Admin.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Admin.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_Id);
                params.put("organization", organizationId);
                params.put("user_role", user_roleId);
                params.put("note_approver_dbs", note_approver_dbsId);
                params.put("note_approver_mgs", note_approver_mgsId);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Admin.this);
        requestQueue.add(request);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}