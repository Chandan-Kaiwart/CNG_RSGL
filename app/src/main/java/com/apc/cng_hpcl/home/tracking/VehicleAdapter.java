package com.apc.cng_hpcl.home.tracking;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.HomeAdmin;
import com.apc.cng_hpcl.home.analytics.Analytics;
import com.apc.cng_hpcl.home.master.Master;
import com.apc.cng_hpcl.home.molarMassDensity.MolarDensity;
import com.apc.cng_hpcl.home.notification.Notification;
import com.apc.cng_hpcl.home.reconciliation.Reconciliation;
import com.apc.cng_hpcl.home.registration.Registration;
import com.apc.cng_hpcl.home.scheduling.Scheduling;
import com.apc.cng_hpcl.home.transaction.Transaction;

import java.util.List;


public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {


    LayoutInflater inflater;
    String username;
    public static int position;


    public VehicleAdapter(Context ctx, String username) {

        this.inflater = LayoutInflater.from(ctx);
        this.username=username;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.vehicle_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView gridIcon;
        Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            title = itemView.findViewById(R.id.textView2);
            gridIcon = itemView.findViewById(R.id.imageView2);
//            position = getAdapterPosition();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent;
                    intent = new Intent(context, Tracking.class);
                    context.startActivity(intent);

                }
            });
        }
    }
}
