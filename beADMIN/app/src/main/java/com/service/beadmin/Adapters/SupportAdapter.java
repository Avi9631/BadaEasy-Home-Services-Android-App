package com.service.beadmin.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.service.beadmin.BookHistoryActivity;
import com.service.beadmin.Models.SupportModel;
import com.service.beadmin.Models.UserModel;
import com.service.beadmin.R;
import com.service.beadmin.SupportActivity;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class SupportAdapter extends RecyclerView.Adapter<SupportAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    List<SupportModel> mData, filterList;
    CustomFilter4 filter;

    public SupportAdapter(Context mContext, List<SupportModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.filterList = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.support_layout, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String reqID = mData.get(position).getReqID();
        String exeID = mData.get(position).getExeID();
        String userID = mData.get(position).getUserID();
        String phone = mData.get(position).getPhone();
        String status = mData.get(position).getStatus();
        String timeReq = mData.get(position).getTimeReq();
        String timeRes = mData.get(position).getTimeRes();
        holder.setData(reqID, exeID, userID, phone, status, timeReq, timeRes);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter4(filterList, this);
        }
        return filter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView TreqID, TexeID, TuserID, Tphone, Tstatus, TtimeReq, TtimeRes;
        Button call, hold, done;
        Dialog loadingDialog;

        public MyViewHolder(View itemView) {
            super(itemView);

            TreqID = itemView.findViewById(R.id.reqId);
            TexeID = itemView.findViewById(R.id.exeID);
            TuserID = itemView.findViewById(R.id.userID);
            Tstatus = itemView.findViewById(R.id.Sstatus);
            TtimeReq = itemView.findViewById(R.id.tReq);
            TtimeRes = itemView.findViewById(R.id.tRes);
            call = itemView.findViewById(R.id.calSup);
            hold = itemView.findViewById(R.id.holdSup);
            done = itemView.findViewById(R.id.doneSup);


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

        private String timeStampToDate(String time) {
            Instant ins1=Instant.ofEpochMilli(Long.parseLong(time));

            ZoneId zd= ZoneId.of("Asia/Kolkata");
            ZonedDateTime zdt1= ZonedDateTime.ofInstant(ins1, zd);
            DateTimeFormatter dt= DateTimeFormatter.ofPattern("dd/MM/yyyy , hh:mm:ss")  ;
            return dt.format(zdt1);
        }

        private void setData(final String reqID, String exeID, final String userID, final String phone, String status, String timeReq, String timeRes) {

            TreqID.setText("Req ID: "+reqID);
            TuserID.setText("UserID: "+userID);
            TtimeReq.setText("Time Req: "+timeStampToDate(timeReq));
            if(!timeRes.equals("")) {
                TtimeRes.setText("Time Res: "+timeStampToDate(timeRes));
                TtimeRes.setVisibility(View.VISIBLE);
            }else {
                TtimeRes.setVisibility(View.GONE);
            }
            Tstatus.setText("Status: "+status);
            TexeID.setText("ExeID: "+exeID);

            call.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel:" + phone));
                    mContext.startActivity(i);
                }
            });

            hold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hold.setEnabled(false);
                    loadingDialog.show();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userID)
                            .child("userCallReq").setValue("false")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseDatabase.getInstance().getReference().child("CallCustReq")
                                                .child(reqID).child("resStatus").setValue("Hold")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseDatabase.getInstance().getReference().child("CallCustReq")
                                                                .child(reqID).child("timeRes").setValue(ServerValue.TIMESTAMP)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        loadingDialog.dismiss();
                                                                        hold.setEnabled(true);
                                                                        SupportActivity.loadSupportData();

                                                                    }
                                                                });
                                                    }
                                                });
                                    }else{
                                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    done.setEnabled(false);
                    loadingDialog.show();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userID)
                            .child("userCallReq").setValue("false")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseDatabase.getInstance().getReference().child("CallCustReq")
                                                .child(reqID).child("resStatus").setValue("Done")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseDatabase.getInstance().getReference().child("CallCustReq")
                                                                .child(reqID).child("timeRes").setValue(ServerValue.TIMESTAMP)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        loadingDialog.dismiss();
                                                                        done.setEnabled(true);
                                                                        SupportActivity.loadSupportData();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }else{
                                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            });

        }
    }


}

class CustomFilter4 extends Filter {

    SupportAdapter adapter;
    List<SupportModel> filterList;

    public CustomFilter4(List<SupportModel> filterList, SupportAdapter userAdapter) {
        this.adapter=userAdapter;
        this.filterList= filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults=new FilterResults();
        if(constraint!=null || constraint.length()>0){
            constraint=constraint.toString().toLowerCase();
            List<SupportModel> list= new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getReqID().toLowerCase().contains(constraint)){
                    list.add(filterList.get(i));
                }
                else if(filterList.get(i).getUserID().toLowerCase().contains(constraint)){
                    list.add(filterList.get(i));
                }
                else if(filterList.get(i).getPhone().toLowerCase().contains(constraint)){
                    list.add(filterList.get(i));
                }
                else if(filterList.get(i).getStatus().toLowerCase().contains(constraint)){
                    list.add(filterList.get(i));
                }
                else if(filterList.get(i).getExeID().toLowerCase().contains(constraint)){
                    list.add(filterList.get(i));
                }
            }
            filterResults.count= list.size();
            filterResults.values= list;
        }else {
            filterResults.count= filterList.size();
            filterResults.values= filterList;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.mData= (List<SupportModel>) results.values;
        adapter.notifyDataSetChanged();
    }


}