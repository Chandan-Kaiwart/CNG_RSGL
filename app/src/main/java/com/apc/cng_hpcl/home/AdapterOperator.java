package com.apc.cng_hpcl.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.molarMassDensity.MolarDensity;
import com.apc.cng_hpcl.home.newDisp.NewDispActivity;
import com.apc.cng_hpcl.home.newTrans.NewTransActivity;
import com.apc.cng_hpcl.home.notification.Notification;
import com.apc.cng_hpcl.home.scheduling.Scheduling;
import com.apc.cng_hpcl.home.suvidha.MainActivity;
import com.apc.cng_hpcl.home.transaction.Transaction;
import com.apc.cng_hpcl.home.transaction.subtabs.dbsdispenser.DispenserHome;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.apc.cng_hpcl.R.color.Primary;

public class AdapterOperator extends RecyclerView.Adapter<AdapterOperator.ViewHolder> {

    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;
    CardView cardview;
    public static int position;
    String username;
    String station;
    boolean isDbs;

    public AdapterOperator(Context ctx, List<String> titles, List<Integer> images, String username,String station1,boolean isDbs1) {
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
        this.username = username;
        this.station=station1;
        this.isDbs=isDbs1;

    }


    @NonNull
    @Override
    public AdapterOperator.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);
        cardview = view.findViewById(R.id.cardview);
        return new AdapterOperator.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AdapterOperator.ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.gridIcon.setImageResource(images.get(position));
        if (position != 1 && position != 2) {
            cardview.getBackground().setTint(Color.GRAY);
//            cardview.setCardBackgroundColor(Primary);
//            holder.gridIcon.setBackgroundColor(R.color.Accent);
            holder.title.setTextColor(R.color.Accent);
        }
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
                //                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    final Intent intent;

                    if (getAdapterPosition() == 1) {
                        intent = new Intent(context, NewTransActivity.class);
                        intent.putExtra("username", username);

                        context.startActivity(intent);
                    }
                    else if (getAdapterPosition() == 2) {
                        intent = new Intent(context, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("station_id", station);
                        context.startActivity(intent);

                    }

                    /*   intent = new Intent(context, NewTransActivity.class);
                        intent.putExtra("username", username);*//*
                        context.startActivity(intent);
                    }*/
                 /*   else if (getAdapterPosition() == 4) {
                        intent = new Intent(context, MolarDensity.class);
                        intent.putExtra("username", username);
                        context.startActivity(intent);
                    }*/
              /*      else if (getAdapterPosition() == 8 && isDbs) {
                        intent = new Intent(context, Scheduling.class);
                        intent.putExtra("username", username);
                        intent.putExtra("station", station);

                        context.startActivity(intent);
                    }*/
                    else {

                        Toast.makeText(v.getContext(), "You are not Authorised to access this module", Toast.LENGTH_SHORT).show();
//                        intent = new Intent(context, HomeOperator.class);
                    }

                }
            });


        }
    }
}
