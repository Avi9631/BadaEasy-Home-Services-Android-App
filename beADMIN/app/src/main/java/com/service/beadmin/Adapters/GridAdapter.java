package com.service.beadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.beadmin.BookHistoryActivity;
import com.service.beadmin.CompletedBookingActivity;
import com.service.beadmin.Models.HomeModel;
import com.service.beadmin.OrderDetailsActivity;
import com.service.beadmin.R;
import com.service.beadmin.ServicesActivity;
import com.service.beadmin.SupportActivity;
import com.service.beadmin.UserDetailsActivity;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {


    private Context mContext;
    private List<HomeModel> mData ;


    public GridAdapter(Context mContext, List<HomeModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.grid_item_layout, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String title = mData.get(position).getName();
        holder.setData(title, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Button posttitle;
        public MyViewHolder(View itemView) {
            super(itemView);
            posttitle= itemView.findViewById(R.id.grid_title);
        }

        public void setData(final String title, int position) {
            posttitle.setText(title);
            posttitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (title.equals("Manage Services")){
                        Intent i=new Intent(mContext, ServicesActivity.class);
                        mContext.startActivity(i);
                    }
                    else if (title.equals("Upcoming Bookings")){
                        Intent i=new Intent(mContext, CompletedBookingActivity.class);
                        mContext.startActivity(i);
                    }
                    else  if(title.equals("Completed Bookings")){
                        Intent i=new Intent(mContext, BookHistoryActivity.class);
                        i.putExtra("type", "Completed");
                        mContext.startActivity(i);
                    }
                    else  if(title.equals("Onhold Bookings")){
                        Intent i=new Intent(mContext, BookHistoryActivity.class);
                        i.putExtra("type", "Onhold");
                        mContext.startActivity(i);
                    }
                    else  if(title.equals("Ongoing Bookings")){
                        Intent i=new Intent(mContext, BookHistoryActivity.class);
                        i.putExtra("type", "Ongoing");
                        mContext.startActivity(i);
                    }
                    else  if(title.equals("Ready Orders")){
                        Intent i=new Intent(mContext, BookHistoryActivity.class);
                        i.putExtra("type", "Ready");
                        mContext.startActivity(i);
                    }
                    else  if(title.equals("Users")){
                        Intent i=new Intent(mContext, UserDetailsActivity.class);
                        mContext.startActivity(i);
                    }else if(title.equals("Customer Service")){
                        Intent i=new Intent(mContext, SupportActivity.class);
                        mContext.startActivity(i);
                    }
                }
            });
        }
    }

}
