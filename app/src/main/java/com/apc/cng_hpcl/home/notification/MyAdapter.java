package com.apc.cng_hpcl.home.notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final ArrayList<DataModel> dataModels;
    private final Activity activity;
    String status = "Approved";
    private static final String BASE_URL_URL = BASE_URL + "msg_dbs_transaction.php?apicall=";
//    private static final String BASE_URL_URL = BASE_URL+"msg_dbs_transaction.php?apicall=";
//    private static final String BASE_URL_URL = "http://192.168.43.43/LUAG_HPCL/msg_dbs_transaction.php?apicall=";
    MyAdapter  adapter;
    public static final String URL_MGR_NOTIFY = BASE_URL_URL + "mgrNotify";
    String username;

    public MyAdapter(Activity activity, ArrayList<DataModel> dataModels, String username) {
        this.dataModels = dataModels;
        this.activity = activity;
        this.username = username;
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
        View view = inflater.inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.date.setText("Date Time: " + dataModels.get(position).getCreate_date());
        holder.time.setText("LCV Number: " + dataModels.get(position).getLcvnum());
        holder.msg.setText("Info: " + dataModels.get(position).getMsg());
        holder.status.setText("Status: " + dataModels.get(position).getStatus());

        holder.container.setOnClickListener(onClickListener(position));
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
                            if(dataModels.get(position).getFlag()==1){
                                reachedLcv(dataModels.get(position).getLcvnum().toString());

                            }
                            if(dataModels.get(position).getFlag()==7){
                                reachedLcv(dataModels.get(position).getLcvnum().toString());

                            }
                            if(dataModels.get(position).getFlag()==3||dataModels.get(position).getFlag()==4){
                                reachedLcvAtDbs(dataModels.get(position).getLcvnum().toString());

                            }
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
    private void reachedLcv(String lcv) {
        Log.d("REACHED>>", "1");


        StringRequest request = new StringRequest(Request.Method.POST, "BASE_URLreached_at_mgs.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("REACHED>>", "2");

                        Log.d("RESP>>", response);



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("REACHED>>", "3");
                Log.d("REACHED>>", error.toString());


                //     Toast.makeText(mContext, "failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("REACHED>>", "4");

                params.put("lcv_id", lcv);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(request);


    }
    private void reachedLcvAtDbs(String lcv) {
        Log.d("REACHED>>", "1");


        StringRequest request = new StringRequest(Request.Method.POST, "BASE_URLreached_at_dbs.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("REACHED>>", "2");

                        Log.d("RESP>>", response);



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("REACHED>>", "3");
                Log.d("REACHED>>", error.toString());


                //     Toast.makeText(mContext, "failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("REACHED>>", "4");

                params.put("lcv_id", lcv);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(request);


    }


}