package com.apc.cng_hpcl.home.transaction;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.master.Master;
import com.apc.cng_hpcl.home.transaction.subtabs.TransCityGateStation;
import com.apc.cng_hpcl.home.transaction.subtabs.dbsdispenser.DispenserHome;
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation;
import com.apc.cng_hpcl.home.transaction.subtabs.dbsdispenser.TransDispenser;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling_MFM;
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;
    public static int position;
    String username,station_id;

    public TransactionAdapter(String station_id1,Context ctx, List<String> titles, List<Integer> images, String username){
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
        this.username=username;
        this.station_id=station_id1;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout,parent,false);
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
                    if(!username.contains("adm")){
                        switch (getAdapterPosition()){
                            case 0:
                                intent = new Intent(context, Transaction.class);
                                intent.putExtra("username", username);
                                intent.putExtra("station_id", station_id);

                                break;


                            case 1:
                                if(titles.get(getAdapterPosition()).contains("DBS")) {
                                    intent = new Intent(context, TransMotherGasStation.class);
                                    intent.putExtra("station_id", station_id);

                                }
                                else{
                                    intent = new Intent(context, TransMotherGasStation.class);
                                    intent.putExtra("station_id", station_id);

                                }
                                intent.putExtra("username", username);
                                intent.putExtra("station_id", station_id);

                                break;
                            case 2:
                                intent = new Intent(context, DispenserHome.class);
                                intent.putExtra("username", username);
                                intent.putExtra("station_id", station_id);

                                break;



                            default:
                                intent = new Intent(context, Master.class);

                                break;
                        }
                    }
                    else{
                        switch (getAdapterPosition()){
                            case 0:
                                intent = new Intent(context, Transaction.class);
                                intent.putExtra("username", username);
                                intent.putExtra("station_id", station_id);
                                intent.putExtra("type", 3);


                                break;


                            case 1:
                                intent = new Intent(context, TransMotherGasStation.class);
                                intent.putExtra("username", username);
                                intent.putExtra("station_id", station_id);
                                intent.putExtra("type", 1);


                                break;
                            case 2:
                                intent = new Intent(context, TransMotherGasStation.class);
                                intent.putExtra("username", username);
                                intent.putExtra("station_id", station_id);
                                intent.putExtra("type", 2);


                                break;
                            case 3:
                                intent = new Intent(context, DispenserHome.class);
                                intent.putExtra("username", username);
                                intent.putExtra("station_id", station_id);
                                intent.putExtra("type", 1);


                                break;



                            default:
                                intent = new Intent(context, Master.class);
                                intent.putExtra("station_id", station_id);

                                break;
                        }

                    }
                    context.startActivity(intent);
                }
            });
        }
    }
}
