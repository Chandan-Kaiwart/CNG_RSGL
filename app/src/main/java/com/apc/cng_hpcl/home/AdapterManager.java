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
import com.apc.cng_hpcl.home.analytics.Analytics;
import com.apc.cng_hpcl.home.master.Master;
import com.apc.cng_hpcl.home.molarMassDensity.MolarDensity;
import com.apc.cng_hpcl.home.newDisp.NewDispActivity;
import com.apc.cng_hpcl.home.newTrans.NewTransActivity;
import com.apc.cng_hpcl.home.notification.Notification;
import com.apc.cng_hpcl.home.reconciliation.Reconciliation;
import com.apc.cng_hpcl.home.registration.Registration;
import com.apc.cng_hpcl.home.scheduling.Scheduling;
import com.apc.cng_hpcl.home.suvidha.MainActivity;
import com.apc.cng_hpcl.home.tracking.Tracking;
import com.apc.cng_hpcl.home.transaction.Transaction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterManager extends RecyclerView.Adapter<AdapterManager.ViewHolder> {

    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;
    CardView cardview;
    public static int position;
    String username,station;
    boolean isDbs;

    public AdapterManager(Context ctx, List<String> titles, List<Integer> images, String username,String station1,boolean isDbs1){
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
        this.username=username;
        this.station=station1;
        this.isDbs=isDbs1;
    }


    @NonNull
    @Override
    public AdapterManager.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout,parent,false);
        cardview = view.findViewById(R.id.cardview);
        return new AdapterManager.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AdapterManager.ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.gridIcon.setImageResource(images.get(position));
        if ((position == 0) ||(position==4)||(position==1)||(position==3)||(position==5)||(position==7)){
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

    public class ViewHolder extends RecyclerView.ViewHolder{
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
                    switch (getAdapterPosition()){
                      /*    case 1:
                            intent = new Intent(context, NewTransActivity.class);
                            intent.putExtra("username", username);
                            context.startActivity(intent);
                            break;
                            */
                        case  2 :
                            intent = new Intent(context, MainActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("station_id", station);
                            context.startActivity(intent);
                            break;


//
                    /*    case 3:
                            intent = new Intent(context, MolarDensity.class);
                            context.startActivity(intent);
                            break;*/
                        case 6:
                            intent  = new Intent(context, Analytics.class);
                            intent.putExtra("username", username);
                            context.startActivity(intent);
                            break;

                    /*    case 6:
                            intent = new Intent(context, Reconciliation.class);
                            intent.putExtra("username", username);
                            context.startActivity(intent);
                            break;*/
                        case 8:
                            if(isDbs){
                                intent = new Intent(context, NewTransActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("station", station);
                                intent.putExtra("isSch", true);
                               /* intent = new Intent(context, Scheduling.class);
                                intent.putExtra("username", username);
                                intent.putExtra("station", station);*/
                                context.startActivity(intent);
                            }
                            else{
                                Toast.makeText(context,"Action only allowed at DBS",Toast.LENGTH_LONG).show();
                            }


                            break;

                        case 9:

                            intent = new Intent(context, Notification.class);
                            intent.putExtra("username", username);
                            intent.putExtra("station", station);
                            intent.putExtra("isDbs", isDbs);


                            context.startActivity(intent);
                            break;

                        default:
                            Toast.makeText(v.getContext(), "You are not Authorised to access this module", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            });
        }
    }
}
