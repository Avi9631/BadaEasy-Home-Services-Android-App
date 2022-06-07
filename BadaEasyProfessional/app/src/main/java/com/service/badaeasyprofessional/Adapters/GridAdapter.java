package com.service.badaeasyprofessional.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasyprofessional.AttendanceActivity;
import com.service.badaeasyprofessional.CompletedActivity;
import com.service.badaeasyprofessional.HomeActivity;
import com.service.badaeasyprofessional.Models.HomeModel;
import com.service.badaeasyprofessional.OngoingActivity;
import com.service.badaeasyprofessional.OnholdActivity;
import com.service.badaeasyprofessional.R;
import com.service.badaeasyprofessional.UpcomingActivity;

import java.util.Arrays;
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
                    if (title == ("Upcoming Orders")){
                        Intent i=new Intent(mContext, UpcomingActivity.class);
                        mContext.startActivity(i);
                    }
                    if(title == "Ongoing Orders"){
                        Intent i=new Intent(mContext, OngoingActivity.class);
                        if (HomeActivity.proCurrOrderId.equals("")){
                            Toast.makeText(mContext, "No Ongoing orders..!", Toast.LENGTH_SHORT).show();
                        }else {
                            mContext.startActivity(i);
                        }

                    }
                    if(title == "Onhold Orders"){
                        Intent i=new Intent(mContext, OnholdActivity.class);
                        mContext.startActivity(i);
                    }
                    if (title == "Completed Orders"){
                        Intent i=new Intent(mContext, CompletedActivity.class);
                        mContext.startActivity(i);
                    }
                    if (title == "My Profile"){
                        FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                .child(HomeActivity.proID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                AttendanceActivity.atten= String.valueOf(dataSnapshot.child("dutyTime").getValue());
                                Intent i=new Intent(mContext, AttendanceActivity.class);
                                mContext.startActivity(i);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
            });
        }
    }

}
