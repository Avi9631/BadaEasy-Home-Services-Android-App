package com.service.beadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.beadmin.BookHistoryActivity;
import com.service.beadmin.CompletedBookingActivity;
import com.service.beadmin.Models.BookingModel;
import com.service.beadmin.OrderDetailsActivity;
import com.service.beadmin.R;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> implements  Filterable{

    private Context mContext;
    List<BookingModel> mData, filterList;
    CustomFilter filter;

    public BookingAdapter(Context mContext, List<BookingModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.filterList= mData;
    }

    @NonNull
    @Override
    public BookingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.completed_booking_layout, parent, false);
        return new BookingAdapter.MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.MyViewHolder holder, int position) {
        String id= mData.get(position).getBid();
        String status= mData.get(position).getStatus();
        String service= mData.get(position).getServiceName();
        String price= mData.get(position).getTotPrice();
        String time= mData.get(position).getBookTime();

        holder.setData(id, status, service, price, time, position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter= new CustomFilter(filterList, this);
        }
        return filter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView bookId, bookStatus, serviceName, bookPrice, bookTime;
        Button viewDetailBtn;

        public MyViewHolder(View itemView) {
            super(itemView);

            bookId= itemView.findViewById(R.id.book_id);
            bookPrice= itemView.findViewById(R.id.book_price);
            bookStatus= itemView.findViewById(R.id.book_status);
            bookTime= itemView.findViewById(R.id.book_time);
            serviceName= itemView.findViewById(R.id.service_name);
            viewDetailBtn= itemView.findViewById(R.id.view_details_btn);
        }

        public void setData(final String id, final String status, String service, String price, String time, int position) {

            bookId.setText("Bid: "+id);
            bookPrice.setText(price);
            bookStatus.setText(status);
            bookTime.setText("Booking Time: "+timeStampToDate(time));
            serviceName.setText(service);

            viewDetailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mContext instanceof CompletedBookingActivity) {
                        Intent i = new Intent(mContext, OrderDetailsActivity.class);
                        i.putExtra("status", status);
                        i.putExtra("bookID", id);
                        i.putExtra("type", 1);
                        mContext.startActivity(i);
                    }
                    else if(mContext instanceof BookHistoryActivity) {
                        Intent i = new Intent(mContext, OrderDetailsActivity.class);
                        i.putExtra("status", status);
                        i.putExtra("bookID", id);
                        i.putExtra("type", 2);
                        mContext.startActivity(i);
                    }
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