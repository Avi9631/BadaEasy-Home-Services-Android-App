package com.service.badaeasy.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.service.badaeasy.AuthActivity;
import com.service.badaeasy.BookingDetails;
import com.service.badaeasy.CompletedBookingActivity;
import com.service.badaeasy.MainActivity;
import com.service.badaeasy.Models.ProModel;
import com.service.badaeasy.R;
import com.service.badaeasy.RewardsActivity;
import com.service.badaeasy.SavedAddressActivity;
import com.service.badaeasy.SupportActivity;

import java.util.List;

public class ProAdapter extends RecyclerView.Adapter<ProAdapter.MyViewHolder>{

    private Context mContext;
    private List<ProModel> mData ;

    public ProAdapter(Context mContext, List<ProModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.service_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String proTitle = mData.get(position).getProTitle();
        int proIcon = mData.get(position).getProIcon();

        holder.setData(proIcon, proTitle);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon= itemView.findViewById(R.id.service_image);
            title= itemView.findViewById(R.id.service_title);

        }

        public void setData(int proIcon, final String proTitle) {
            title.setText(proTitle);
            icon.setImageResource(proIcon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (proTitle.equals("My Booking")){
                        Intent i=new Intent(mContext, CompletedBookingActivity.class);
//                        i.putExtra("statusType", "Completed");
                        mContext.startActivity(i);
                    }
//                    else if (proTitle.equals("Ongoing Bookings")){
//                        Intent i=new Intent(mContext, CompletedBookingActivity.class);
//                        i.putExtra("statusType", "Ongoing");
//                        mContext.startActivity(i);
//                    }
                    else if (proTitle.equals("Rewards")){
                        Intent i=new Intent(mContext, RewardsActivity.class);
                        mContext.startActivity(i);
                    }
                    else if (proTitle.equals("Support")){
                        Intent i=new Intent(mContext, SupportActivity.class);
                        mContext.startActivity(i);
                    }
                    else if (proTitle.equals("Log out")){
                        AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
                        builder.setMessage("Are you sure to Log out?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                FirebaseAuth.getInstance().signOut();
                                Intent i=new Intent(mContext, MainActivity.class);
                                mContext.startActivity(i);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog= builder.create();
                        alertDialog.setTitle("Log out");
                        alertDialog.show();
                    }
                    else if (proTitle.equals("Saved Address")){
                        Intent i=new Intent(mContext, SavedAddressActivity.class);
                        mContext.startActivity(i);
                    }
//                    else if(proTitle.equals("Onhold Bookings")){
//                        Intent i=new Intent(mContext, CompletedBookingActivity.class);
//                        i.putExtra("statusType", "Onhold");
//                        mContext.startActivity(i);
//                    }
                }
            });
        }
    }
}
