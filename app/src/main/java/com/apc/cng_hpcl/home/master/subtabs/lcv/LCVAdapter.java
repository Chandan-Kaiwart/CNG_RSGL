package com.apc.cng_hpcl.home.master.subtabs.lcv;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.master.subtabs.lcv.EditLcv;
import com.apc.cng_hpcl.home.master.subtabs.lcv.RegLcv;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.EditMotherGasStation;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MotherGasStation;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MotherGasStationAdapter;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.RegMotherGasStation;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class LCVAdapter extends RecyclerView.Adapter<LCVAdapter.ViewHolder> {

    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;
    public static int position;

    public LCVAdapter(Context ctx, List<String> titles, List<Integer> images){
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
    }


    @NonNull
    @Override
    public LCVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout,parent,false);
        return new LCVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LCVAdapter.ViewHolder holder, int position) {
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
                            intent = new Intent(context, RegLcv.class);
                            break;

                        case 1:
                            intent = new Intent(context, EditLcv.class);
                            break;




                        default:
                            intent = new Intent(context, LCV.class);
                            break;
                    }
                    context.startActivity(intent);
                }
            });
        }
    }
}
