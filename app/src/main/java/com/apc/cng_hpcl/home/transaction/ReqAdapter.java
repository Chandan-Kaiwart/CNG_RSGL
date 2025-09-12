package com.apc.cng_hpcl.home.transaction;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation.URL_MGS;
import static com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation.URL_NOTIFY1;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.newTrans.TransListFragDirections;
import com.apc.cng_hpcl.home.notification.DataModel;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_After_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReqAdapter extends RecyclerView.Adapter<ReqAdapter.ViewHolder> {
    private final ArrayList<DataModel> dataModels;
    private final Activity activity;
    private NavController navController;
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
    private static final String BASE_URL_URL2 = BASE_URL + "mgs_dbs_read_transaction.php?apicall=";
    public static final String URL_READ_NOTE = BASE_URL_URL2 + "readNotification";
    String status = "Approved";
//    private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
//    private static final String BASE_URL_URL = "http://192.168.43.43/LUAG_HPCL/msg_dbs_transaction.php?apicall=";
    ReqAdapter adapter;
    public static final String URL_MGR_NOTIFY = BASE_URL_URL + "mgrNotify";
    String username;

    public ReqAdapter(Activity activity, ArrayList<DataModel> dataModels, String username,NavController navController1) {
        this.dataModels = dataModels;
        this.activity = activity;
        this.username = username;
        this.navController=navController1;
        Log.e(TAG,"Username="+username);


    }
//    public void updateList (List<DataModel> items) {
//        if (items != null && items.size() > 0) {
//            dataModels.clear();
//            dataModels.addAll(items);
//            notifyDataSetChanged();
//        }
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.req_list_item, viewGroup, false);
        return new ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.date.setText("Date Time: " + dataModels.get(position).getDate());
        holder.time.setText("LCV Number: " + dataModels.get(position).getLcvnum());
        holder.msg.setText("Stage: " + dataModels.get(position).getMgs());
        holder.status.setText("Status: " + dataModels.get(position).getStatus());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Toast.makeText(activity,dataModels.get(position).getLcvnum(),Toast.LENGTH_LONG).show();
                readNotification(dataModels.get(position),navController);

            }
        });
     //   holder.container.setOnClickListener(onClickListener(position));
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(TextView date, TextView time, TextView mgs, TextView dbs, TextView lcv, TextView msg, int position) {
        date.setText("Date: " + (dataModels.get(position).getDate()));
        time.setText("Time: " + (dataModels.get(position).getTime()));

        lcv.setText("LCV Number: " + (dataModels.get(position).getLcvnum()));
        mgs.setText("Mother Gas Station: " + (dataModels.get(position).getMgs()));
        dbs.setText("Daughter Booster Station: " + (dataModels.get(position).getDbs()));

        msg.setText("Request: " + (dataModels.get(position).getMsg()));
    }

    @Override
    public int getItemCount() {
        return (null != dataModels ? dataModels.size() : 0);
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Username="+username);
                String emp_type = username.toLowerCase().substring(0, 3);
                if (!emp_type.equals("ope")) {
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.popup);
                    dialog.setTitle("Position" + position);
                    dialog.setCancelable(true);
                    TextView date = (TextView) dialog.findViewById(R.id.tv_date);
                    TextView time = (TextView) dialog.findViewById(R.id.tv_time);
                    TextView mgs = (TextView) dialog.findViewById(R.id.tv_mgs);
                    TextView lcv = (TextView) dialog.findViewById(R.id.tv_lcv);
                    TextView dbs = (TextView) dialog.findViewById(R.id.tv_dbs);
                    TextView note = (TextView) dialog.findViewById(R.id.tv_note);
                    Button cancel = (Button) dialog.findViewById(R.id.cancel);
                    Button approve = (Button) dialog.findViewById(R.id.approve);
                    setDataToView(date, time, mgs, dbs, lcv, note, position);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    approve.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onClick(View v) {

                            updatestatus(date, time, mgs, dbs, lcv, note, position);
//                            updateList(dataModels);
//                           notifyDataSetChanged();
                            notifyItemChanged(position);
                            dialog.dismiss();


                        }
                    });

                    dialog.show();
                }
//                else{
//
//                }
            }


        };
    }

    private void updatestatus(TextView date, TextView time, TextView mgs, TextView dbs, TextView lcv, TextView note, int position) {


        final String MGS = dataModels.get(position).getMgs().toString();
        final String LCV = dataModels.get(position).getLcvnum().toString();
        final String DBS = dataModels.get(position).getDbs().toString();
        final String Message = dataModels.get(position).getMsg().toString();
        final String note_time = dataModels.get(position).getTime().toString();
        final String note_date = dataModels.get(position).getDate().toString();


        StringRequest request = new StringRequest(Request.Method.POST, URL_MGR_NOTIFY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.e(TAG, "notify updatestatus Response = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(activity.getApplicationContext(), "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(), "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                params.put("Notification_LCV", LCV);
                params.put("Notification_MGS", MGS);
                params.put("Notification_DBS", DBS);
                params.put("Notification_Message", Message);
                params.put("Notification_Date", note_date);
                params.put("Notification_Time", note_time);
                params.put("Notification_approver",username);
                params.put("status", status);


                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
        requestQueue.add(request);


    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView time;
        TextView msg, status;
        private View container;

        public ViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            msg = (TextView) view.findViewById(R.id.task);
            status = (TextView) view.findViewById(R.id.status);
            container = view.findViewById(R.id.card_view);
        }
    }
    private void readNotification(DataModel dm,NavController navController) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        if (!(dm.getMgs().equals("NA") || dm.getDbs().equals("NA") || dm.getLcvnum().equals("NA"))) {
            StringRequest request = new StringRequest(Request.Method.POST, URL_READ_NOTE, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(TAG, "getNotificationResponse = " + response);
                        JSONObject jsonObject = new JSONObject(response);

                        JSONObject myJsonObject = new JSONObject(response);


                        jsonObject.getString("status");
                        String flag = jsonObject.getString("flag");
                        String noteStatus = jsonObject.getString("status");
                        String create_date = jsonObject.getString("create_date");
                        Log.e(TAG, "Flag,NoteStatus,Create_date=" + flag + noteStatus + create_date);
                        if (flag.equals("1") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS");
                            Toast.makeText(activity, "Waiting for Manager Approval", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("2") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(activity, "Waiting for Manager Approval Before Filling", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("3") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(activity, "Waiting for Manager Approval After Filling", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("4") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(activity, "Waiting for Manager Approval Before Emptying", Toast.LENGTH_SHORT).show();

                        }
                        else if (flag.equals("5") && noteStatus.equals("Pending")) {
                            Log.e(TAG, "In if Notification Pending MGS Before Filling");
                            Toast.makeText(activity, "Waiting for Manager Approval After Emptying", Toast.LENGTH_SHORT).show();

                        }


                        else if (flag.equals("1") && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Approved First Level MGS");

                            Toast.makeText(activity, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", dm.getLcvnum());
                            bundle.putString("mgs_id", dm.getMgs());
                            bundle.putString("dbs_id", dm.getDbs());
                            Intent intent = new Intent(activity, MGS_Before_Filling.class);
                            intent.putExtras(bundle);
                            navController.navigate(TransListFragDirections.actionTransListFragToReadingFrag(username,dm.getLcvnum(), dm.getTransId(), dm.getMgs(),dm.getDbs()));

                            //    activity.startActivity(intent);
                         //   activity.finish();
                        } else if (flag.equals("2") && noteStatus.equals("Approved")) {
                            Log.e(TAG, "In if Approved Second Level MGS");

                            Toast.makeText(activity, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num",dm.getLcvnum());
                            bundle.putString("mgs_id", dm.getMgs());
                            bundle.putString("dbs_id", dm.getDbs());
                            Intent intent = new Intent(activity, MGS_After_Filling.class);

//                           Intent intent = new Intent(TransMotherGasStation.this, MGS_After_Filling_MFM.class);
                            intent.putExtras(bundle);
                            navController.navigate(TransListFragDirections.actionTransListFragToReadingFrag(username,dm.getLcvnum(), dm.getTransId(), dm.getMgs(),dm.getDbs()));

                            //     activity. startActivity(intent);
                         //   activity.finish();
                        } else if ((flag.equals("3") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Third Level MGS");

                            Toast.makeText(activity, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", dm.getLcvnum());
                            bundle.putString("mgs_id", dm.getMgs());
                            bundle.putString("dbs_id", dm.getDbs());
                            Intent intent = new Intent(activity, TransDaughterBoosterStation.class);
                            intent.putExtras(bundle);
                           //activity.startActivity(intent);
                         //   activity.finish();
                            navController.navigate(TransListFragDirections.actionTransListFragToReadingFrag(username,dm.getLcvnum(), dm.getTransId(), dm.getMgs(),dm.getDbs()));
                        }

                        else if ((flag.equals("4") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(activity, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", dm.getLcvnum());
                            bundle.putString("mgs_id", dm.getMgs());
                            bundle.putString("dbs_id", dm.getDbs());
                            Intent intent = new Intent(activity, DBS_Before_Emptying.class);

//                           Intent intent = new Intent(TransMotherGasStation.this, DBS_Before_Emptying_MFM.class);
                            intent.putExtras(bundle);
                         //   activity.startActivity(intent);
                           // activity.finish();
                            navController.navigate(TransListFragDirections.actionTransListFragToReadingFrag(username,dm.getLcvnum(), dm.getTransId(), dm.getMgs(),dm.getDbs()));

                        }
                        else if ((flag.equals("5") && noteStatus.equals("Approved"))) {
                            Log.e(TAG, "In if Approved Fourth Level MGS");

                            Toast.makeText(activity, "Manager Approval Done", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("lcv_num", dm.getLcvnum());
                            bundle.putString("mgs_id", dm.getMgs());
                            bundle.putString("dbs_id", dm.getDbs());
                            Intent intent = new Intent(activity, DBS_After_Emptying.class);
                            intent.putExtras(bundle);
                       //     activity.startActivity(intent);
                         //   activity.finish();
                            navController.navigate(TransListFragDirections.actionTransListFragToReadingFrag(username,dm.getLcvnum(), dm.getTransId(), dm.getMgs(),dm.getDbs()));

                        }  else  {
                            Log.e(TAG, "In if First transaction MGS");
                            notifymsg(dm);
                            insertData(dm);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, "Failed to get data" + error, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {

                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("Notification_LCV", dm.getLcvnum());
                    params.put("Notification_MGS", dm.getMgs());
                    params.put("Notification_DBS", dm.getDbs());


                    return params;
                }
            };

            queue.add(request);
        } else {
            Toast.makeText(activity, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }

    }
    public void insertData(DataModel dm) {
        final String station_id = dm.getMgs();
        final String lcv_id = dm.getLcvnum();
        final String dbs_station_id = dm.getDbs();


        if (!(station_id.equals("NA") && lcv_id.equals("NA") && dbs_station_id.equals("NA"))) {
            StringRequest request = new StringRequest(Request.Method.POST, URL_MGS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e(TAG, "insertData Response = " + response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("message") == null) {
                                    Toast.makeText(activity, "Invalid Operation", Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
                }
            }

            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();

                    String lcv_from_mgs_to_dbs = "1";
                    String lcv_status=lcv_id+ " waiting for Filling at "+ station_id;
                    params.put("lcv_status",lcv_status );
                    params.put("lcv_id", lcv_id);
                    params.put("station_id", station_id);
                    params.put("dbs_station_id", dbs_station_id);
                    params.put("lcv_from_mgs_to_dbs", lcv_from_mgs_to_dbs);
                    params.put("operator_id", username);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            requestQueue.add(request);
        } else {
            Toast.makeText(activity, "Please select valid LCV,MGS and DBS station ", Toast.LENGTH_SHORT).show();
        }


    }


    public void notifymsg(DataModel dm) {


        final String MGS = dm.getMgs();
        final String LCV = dm.getLcvnum();
        final String DBS = dm.getDbs();
        final String operator_id = username;

        Log.e(TAG, "operator_id in insert Notify =" + operator_id);


        final String Message = LCV + " " + "Reached at Mother Gas Station READY FOR RE-FILLING" + ":" + MGS;
        StringRequest request = new StringRequest(Request.Method.POST, URL_NOTIFY1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e(TAG, "notifymsg Response = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(activity, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                params.put("Notification_LCV", LCV);
                params.put("Notification_MGS", MGS);
                params.put("Notification_DBS", DBS);
                params.put("Notification_Message", Message);
                params.put("status", status);
                params.put("flag", "1");
                params.put("operator_id", operator_id);


                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(request);

    }

}