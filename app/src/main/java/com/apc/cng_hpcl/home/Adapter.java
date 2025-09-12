package com.apc.cng_hpcl.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apc.cng_hpcl.R;

import com.apc.cng_hpcl.camera.CameraActivity;
import com.apc.cng_hpcl.dummy.CaptureImage;
import com.apc.cng_hpcl.dummy.ImageCaptureUpload;
import com.apc.cng_hpcl.dummy.ImageUpload;
import com.apc.cng_hpcl.dummy.MysqlDb;
import com.apc.cng_hpcl.dummy.VolleyDb;
import com.apc.cng_hpcl.home.analytics.Analytics;
import com.apc.cng_hpcl.home.master.Master;
import com.apc.cng_hpcl.home.molarMassDensity.MolarDensity;
import com.apc.cng_hpcl.home.newTrans.NewTransActivity;
import com.apc.cng_hpcl.home.notification.Notification;
import com.apc.cng_hpcl.home.reconciliation.Reconciliation;
import com.apc.cng_hpcl.home.registration.Registration;
import com.apc.cng_hpcl.home.scheduling.Scheduling;
import com.apc.cng_hpcl.home.tracking.TrackableVehiclesList;
import com.apc.cng_hpcl.home.tracking.Tracking;
import com.apc.cng_hpcl.home.transaction.Transaction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;
    String username;
    public static int position;


    public Adapter(Context ctx, List<String> titles, List<Integer> images, String username) {
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
        this.username=username;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.gridIcon.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
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
                    switch (getAdapterPosition()) {
                        case 0:
                            intent = new Intent(context, Registration.class);
                            intent.putExtra("username", username);
                            break;

                        case 1:
                            intent = new Intent(context, NewTransActivity.class);
                            intent.putExtra("username", username);
                            break;
                        case 2:
                            intent = new Intent(context, Master.class);
                            intent.putExtra("username", username);
                            break;
                        case 3:
                            intent = new Intent(context, MolarDensity.class);
                            intent.putExtra("username", username);
                            break;
                        case 4:
                            intent = new Intent(context, TrackableVehiclesList.class);
                            intent.putExtra("username", username);
                            break;
                        case 5:
                            intent = new Intent(context, Analytics.class);
                            intent.putExtra("username", username);
                            break;

                        case 6:
                            intent = new Intent(context, Reconciliation.class);
                            intent.putExtra("username", username);
                            break;
                  /*      case 7:
                            intent = new Intent(context, Scheduling.class);
                            intent.putExtra("username", username);
                            break;*/

                        case 8:
                            intent = new Intent(context, Notification.class);
                            intent.putExtra("username", username);
                            break;

//                        case 8:
//                            intent = new Intent(context, MysqlDb.class);
//                            break;
//
//                        case 9:
//                            intent = new Intent(context, VolleyDb.class);
//                            break;


                        default:
                            intent = new Intent(context, HomeAdmin.class);
                            break;
                    }
                    context.startActivity(intent);
                }
            });
        }
    }
}
