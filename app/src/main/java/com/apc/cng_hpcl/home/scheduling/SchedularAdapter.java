package com.apc.cng_hpcl.home.scheduling;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.master.Master;
import com.apc.cng_hpcl.home.newTrans.NewTransActivity;
import com.apc.cng_hpcl.home.transaction.subtabs.dbsdispenser.StationaryCascade;
import com.apc.cng_hpcl.home.transaction.subtabs.dbsdispenser.TransDispenser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SchedularAdapter extends RecyclerView.Adapter<SchedularAdapter.ViewHolder>{
    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;
    public static int position;
    String username;
    String station;


    public SchedularAdapter(Context ctx, List<String> titles, List<Integer> images, String username,String station1) {
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
        this.username=username;
        this.station=station1;

    }

    @NonNull
    @Override
    public SchedularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout,parent,false);
        return new SchedularAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedularAdapter.ViewHolder holder, int position) {
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
                    switch (getAdapterPosition()){
                        case 0:
                        //    intent = new Intent(context, SchedularStationaryCascade.class);
                            intent = new Intent(context, NewTransActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("station", station);
                            intent.putExtra("isSch", true);

                            break;

                        case 1:
                            intent = new Intent(context, SchedularStationaryCascadeInfo.class);
                            intent.putExtra("username", username);

                            break;

                        default:
                            intent = new Intent(context, Master.class);
                            break;
                    }
                    context.startActivity(intent);
                }
            });
        }
    }
}
