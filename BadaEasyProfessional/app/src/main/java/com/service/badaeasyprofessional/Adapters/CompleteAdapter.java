package com.service.badaeasyprofessional.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.service.badaeasyprofessional.Models.CompleteModel;
import com.service.badaeasyprofessional.OrderDetailsActivity;
import com.service.badaeasyprofessional.R;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CompleteAdapter extends RecyclerView.Adapter<CompleteAdapter.MyViewHolder> {

    private Context mContext;
    private List<CompleteModel> mData;

    public CompleteAdapter(Context mContext, List<CompleteModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CompleteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.completed_booking_layout, parent, false);
        return new CompleteAdapter.MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CompleteAdapter.MyViewHolder holder, int position) {
        String id= mData.get(position).getBid();
        String status= mData.get(position).getStatus();
        String service= mData.get(position).getServiceName();
        String price= mData.get(position).getTotPrice();
        String time= mData.get(position).getBookTime();
        String earn= mData.get(position).getProEarning();

        holder.setData(id, status, service, price, time,earn, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView bookId, bookStatus, serviceName, bookPrice, bookTime, proEarn;
        Button viewDetailBtn;

        public MyViewHolder(View itemView) {
            super(itemView);

            bookId= itemView.findViewById(R.id.book_id);
            bookPrice= itemView.findViewById(R.id.book_price);
            bookStatus= itemView.findViewById(R.id.book_status);
            bookTime= itemView.findViewById(R.id.book_time);
            proEarn= itemView.findViewById(R.id.proEarn);
            serviceName= itemView.findViewById(R.id.service_name);
            viewDetailBtn= itemView.findViewById(R.id.view_details_btn);
        }

        public void setData(final String id, final String status, String service, String price, String time, String earn, int position) {

            bookId.setText("Bid: "+id);
            bookPrice.setText(price);
            bookStatus.setText(status);
            bookTime.setText("Booking Time: "+timeStampToDate(time));
            serviceName.setText(service);
            if(status.equals("Completed")){
                proEarn.setVisibility(View.VISIBLE);
                proEarn.setText("Earning: Rs."+earn+"/-");
            }else{
                proEarn.setVisibility(View.GONE);
            }

            viewDetailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mContext, OrderDetailsActivity.class);
                    i.putExtra("status", status);
                    i.putExtra("bookID", id);
                    mContext.startActivity(i);
                }
            });

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