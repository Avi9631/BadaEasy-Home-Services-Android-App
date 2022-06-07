package com.service.beadmin.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.beadmin.Models.ProModel;
import com.service.beadmin.Models.ServicesModel;
import com.service.beadmin.OrderDetailsActivity;
import com.service.beadmin.R;
import com.service.beadmin.ServiceItem2Activity;
import com.service.beadmin.ServiceItemActivity;
import com.service.beadmin.ServicesActivity;
import com.service.beadmin.ServicesBuyActivity;

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
            View row = LayoutInflater.from(mContext).inflate(R.layout.pro_item_layout, parent, false);
            return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String proID= mData.get(position).getProID();
        String proStatus=mData.get(position).getProStatus();
        String proCategory =mData.get(position).getProCategory();
        String proLocation =mData.get(position).getProLocation();
        String proName =mData.get(position).getProName();
        String proMobile =mData.get(position).getProMobile();
        String bid= mData.get(position).getBookID();

        holder.setData(proID, proStatus, proCategory, proLocation, proName, proMobile, bid);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView id, name, status, mob, loc, cat;
        Button assignBtn;
        Dialog loadingDialog;

        public MyViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.d_pro_id);
            name = itemView.findViewById(R.id.d_pro_name);
            status = itemView.findViewById(R.id.d_pro_ststus);
            mob = itemView.findViewById(R.id.d_pro_mob);
            cat = itemView.findViewById(R.id.d_pro_cat);
            assignBtn= itemView.findViewById(R.id.assign_btn);
            /////loading Dialog
            loadingDialog = new Dialog(mContext);
            loadingDialog.setContentView(R.layout.loading);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                loadingDialog.getWindow().setBackgroundDrawable(mContext.getDrawable(R.drawable.circle));
            }
            loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            loadingDialog.setCancelable(false);
            /////loading Dialog
        }

        public void setData(final String proID, String proStatus, String proCategory,
                            String proLocation, final String proName, final String proMobile, final String bid) {

            id.setText("PID: "+proID);
            name.setText(proName);
            status.setText(proStatus);
            mob.setText(proMobile);

            loadingDialog.show();
            FirebaseDatabase.getInstance().getReference().child("Bookings")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int i1=0, i2=0, i3=0;
                            for(DataSnapshot dataSnapshot5: dataSnapshot.getChildren()) {
                                if (String.valueOf(dataSnapshot5.child("book_professional_id").getValue()).equals(proID)) {
                                    if (String.valueOf(dataSnapshot5.child("book_status").getValue()).equals("Ready")) {
                                        i1++;
                                    } else if (String.valueOf(dataSnapshot5.child("book_status").getValue()).equals("Ongoing")) {
                                        i2++;
                                    } else if (String.valueOf(dataSnapshot5.child("book_status").getValue()).equals("Onhold")) {
                                        i3++;
                                    }
                                }
                            }
                            cat.setText("U:"+i1+" G:"+i2+" H:"+i3);
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            loadingDialog.dismiss();
                        }
                    });

            assignBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAlert(proName, proMobile, proID, bid);
                }
            });

        }

        private void openAlert(String proName, String proMobile, final String proID, final String bid) {
            AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
            builder.setMessage("Are you sure to assign this order to\n ID:"+proID+"\n"+" Name: "+proName+"\n"+" Mob: "+proMobile);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {

                    FirebaseDatabase.getInstance().getReference().child("Bookings").child(bid)
                            .child("book_professional_id").setValue(proID)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(mContext, "assigned", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            OrderDetailsActivity.dialog.dismiss();
                        }
                    });

                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    OrderDetailsActivity.dialog.dismiss();
                }
            });
            AlertDialog alertDialog= builder.create();
            alertDialog.setTitle("Assign Professional");
            alertDialog.show();
        }
    }
}
