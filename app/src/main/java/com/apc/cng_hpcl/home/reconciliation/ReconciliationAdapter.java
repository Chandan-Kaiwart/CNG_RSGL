package com.apc.cng_hpcl.home.reconciliation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.reconciliation.subtabs.DbsLevel;
import com.apc.cng_hpcl.home.reconciliation.subtabs.MgsLevel;
import com.apc.cng_hpcl.home.reconciliation.subtabs.OrganizationalLevel;
import com.apc.cng_hpcl.home.reconciliation.subtabs.TransportationLevel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ReconciliationAdapter extends RecyclerView.Adapter<ReconciliationAdapter.ViewHolder> {

    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;
    public static int position;

    public ReconciliationAdapter(Context ctx, List<String> titles, List<Integer> images){
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
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
                    switch (getAdapterPosition()){
                        case 0:
                            intent = new Intent(context, OrganizationalLevel.class);
                            break;

                        case 1:
                            intent = new Intent(context, MgsLevel.class);
                            break;
                        case 2:
                            intent = new Intent(context, DbsLevel.class);
                            break;
                        case 3:
                            intent = new Intent(context, TransportationLevel.class);
                            break;
//                        case 4:
//                            intent = new Intent(context, TestActivity.class);
//
//                            break;



                        default:
                            intent = new Intent(context, Reconciliation.class);
                            break;
                    }
                    context.startActivity(intent);
                }
            });
        }
    }
}
