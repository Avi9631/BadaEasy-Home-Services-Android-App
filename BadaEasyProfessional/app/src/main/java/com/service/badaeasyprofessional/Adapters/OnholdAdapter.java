package com.service.badaeasyprofessional.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.service.badaeasyprofessional.HomeActivity;
import com.service.badaeasyprofessional.Models.UpcomingModel;
import com.service.badaeasyprofessional.OngoingActivity;
import com.service.badaeasyprofessional.R;
import com.service.badaeasyprofessional.ViewDetailsActivity;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OnholdAdapter extends RecyclerView.Adapter<OnholdAdapter.MyViewHolder> {

    private Context mContext;
    private List<UpcomingModel> mData ;

    public OnholdAdapter(Context mContext, List<UpcomingModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.onhold_order, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = mData.get(position).getServiceName();
        String price = mData.get(position).getTotPrice();
        String bid = mData.get(position).getBid();
        String prob1 = mData.get(position).getProbStmt1();
        String bookTime= mData.get(position).getBookTime();
        String address =  mData.get(position).getName()+"\n"+ mData.get(position).getMainMobile()+" , "+
                mData.get(position).getMobile()+"\n"+
                mData.get(position).getFlatNo()+"\n"+ mData.get(position).getLocality()+"\n"+
                mData.get(position).getLandmark()+"\n"+
                mData.get(position).getCity()+" - "+
                mData.get(position).getPincode()+"\n"+
                mData.get(position).getState();
        String status= mData.get(position).getStatus();


        holder.setData(name,price, bid, prob1, address,bookTime, status ,position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView serviceName, servicePrice, serviceBID, serviceProb1, serviceBookTime, serviceStatus;
        Button  resume, details;

        private Dialog dialog;
        private TextView title;
        private Button holdDialogBtn, completeDialogBtn;


        public MyViewHolder(View itemView) {
            super(itemView);
            serviceName= itemView.findViewById(R.id.service_name);
            servicePrice= itemView.findViewById(R.id.service_price);
            serviceBID= itemView.findViewById(R.id.bid);
            serviceProb1= itemView.findViewById(R.id.prob_stmt_1);
            serviceBookTime= itemView.findViewById(R.id.book_time);
            resume= itemView.findViewById(R.id.resumeBtn);
            details= itemView.findViewById(R.id.detailsBtn);
            serviceStatus= itemView.findViewById(R.id.status);

            if(HomeActivity.proSwitch.equals("YES")){
                resume.setEnabled(true);
            }
            else {
                resume.setEnabled(false);
            }

            ////////////////////////////////////////////////////////// dialog
            dialog= new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_onhold);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            title= dialog.findViewById(R.id.d_title);
            completeDialogBtn= dialog.findViewById(R.id.d_compl_btn);
            holdDialogBtn= dialog.findViewById(R.id.d_hold_btn);
            ///////////////////////////////////////////////////////// Dialog
        }

        public void setData(String name, String price, final String bid, String prob1, String address, String bookTime, String status, final int position) {
            serviceName.setText(name);
            serviceProb1.setText(prob1);
            serviceBID.setText("BID: "+bid);
            servicePrice.setText(price);
            serviceBookTime.setText("Book Time: "+timeStampToDate(bookTime));
            serviceStatus.setText(status);

            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mContext, ViewDetailsActivity.class);
                    i.putExtra("bookID",bid);
                    i.putExtra("source", 2);
                    mContext.startActivity(i);
                }
            });

            holdDialogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference().child("Bookings")
                            .child(HomeActivity.proCurrOrderId)
                            .child("book_status").setValue("Onhold").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                    .child(HomeActivity.proID)
                                    .child("curr_order_id").setValue(bid.toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            FirebaseDatabase.getInstance().getReference().child("Bookings").child(bid)
                                                    .child("book_status").setValue("Ongoing").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    HomeActivity.proCurrOrderId= bid;
                                                    Intent i=new Intent(mContext, OngoingActivity.class);
                                                    mContext.startActivity(i);
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    dialog.dismiss();
                }
            });

            completeDialogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mContext, OngoingActivity.class);
                    mContext.startActivity(i);

                    dialog.dismiss();
                }
            });

            resume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(HomeActivity.proCurrOrderId)){
                        openAlert(bid);
                    }
                    else {
                        dialog.show();
                    }
                }
            });

        }

        private void openAlert(final String bid) {
            AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
            builder.setMessage("Are you sure you want to activate this order?");
            builder.setCancelable(false);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                            .child(HomeActivity.proID)
                            .child("curr_order_id").setValue(bid.toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    FirebaseDatabase.getInstance().getReference().child("Bookings").child(bid)
                                            .child("book_status").setValue("Ongoing").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            HomeActivity.proCurrOrderId= bid;
                                            Intent i=new Intent(mContext, OngoingActivity.class);
                                            mContext.startActivity(i);
                                        }
                                    });
                                }
                            });                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog= builder.create();
            alertDialog.setTitle("Activate the Onhold order");
            alertDialog.show();
        }


        private  String timeStampToDate(String time){
            Instant ins1=Instant.ofEpochMilli(Long.parseLong(time));

            ZoneId zd= ZoneId.of("Asia/Kolkata");
            ZonedDateTime zdt1= ZonedDateTime.ofInstant(ins1, zd);
            DateTimeFormatter dt= DateTimeFormatter.ofPattern("dd/MM/yyyy , hh:mm:ss")  ;
            return dt.format(zdt1);
        }
    }

}
