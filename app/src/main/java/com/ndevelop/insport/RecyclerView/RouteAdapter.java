package com.ndevelop.insport.RecyclerView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ndevelop.insport.Fragments.RouteInfoFragment;
import com.ndevelop.insport.NavigationActivity;
import com.ndevelop.insport.R;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    private ArrayList<RouteInfo> routes;
    public RouteAdapter(ArrayList<RouteInfo> routes) {
        this.routes = routes;
    }
    RouteInfoFragment routeInfoFragment = new RouteInfoFragment();
    class RouteViewHolder extends RecyclerView.ViewHolder {
        //ImageView img;
        TextView dist;
        TextView time;

        RouteViewHolder(View view) {
            super(view);
            // img=view.findViewById(R.id.routeImage);
            dist = view.findViewById(R.id.routeDistance);
            time = view.findViewById(R.id.routeTime);

        }


    }



    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pattern, parent, false);
        return new RouteViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RouteAdapter.RouteViewHolder holder, final int position) {
        holder.dist.setText(String.valueOf(routes.get(position).getDist()));
//holder.img.setImageResource(routes.get(position).getImg());
        holder.time.setText(String.valueOf(routes.get(position).getTime()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity =(Activity)view.getContext();

                if (activity != null && !activity.isFinishing() && activity instanceof NavigationActivity) {
                    ((NavigationActivity) activity).fromFragmentData(position);
                }

            }
        });
    }




    @Override
    public int getItemCount() {
        return routes.size();
    }



}
