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
import com.service.beadmin.Models.UserModel;
import com.service.beadmin.OrderDetailsActivity;
import com.service.beadmin.R;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> implements  Filterable{

    private Context mContext;
    List<UserModel> mData, filterList;
    CustomFilter3 filter;

    public UserAdapter(Context mContext, List<UserModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.filterList= mData;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.user_details_layout, parent, false);
        return new UserAdapter.MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        String id= mData.get(position).getId();
        String name= mData.get(position).getName();
        String email= mData.get(position).getEmail();
        String phone= mData.get(position).getPhone();
        holder.setData(id, name,email, phone, position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter= new CustomFilter3(filterList, this);
        }
        return filter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView uName, uId, uEmail, uPhone;

        public MyViewHolder(View itemView) {
            super(itemView);

            uEmail= itemView.findViewById(R.id.user_email);
            uId= itemView.findViewById(R.id.uid);
            uPhone= itemView.findViewById(R.id.user_mobile);
            uName= itemView.findViewById(R.id.user_name);
        }

        private  String timeStampToDate(String time){
            Instant ins1=Instant.ofEpochMilli(Long.parseLong(time));

            ZoneId zd= ZoneId.of("Asia/Kolkata");
            ZonedDateTime zdt1= ZonedDateTime.ofInstant(ins1, zd);
            DateTimeFormatter dt= DateTimeFormatter.ofPattern("dd/MM/yyyy , hh:mm:ss")  ;
            return dt.format(zdt1);
        }

        public void setData(String id, String name, String email, String phone, int position) {
            uPhone.setText(phone);
            uName.setText(name);
            uEmail.setText(email);
            uId.setText("UID: "+id);
        }
    }
}

class CustomFilter3 extends Filter {

    UserAdapter adapter;
    List<UserModel> filterList;

    public CustomFilter3(List<UserModel> filterList, UserAdapter userAdapter) {
        this.adapter=userAdapter;
        this.filterList= filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults=new FilterResults();
        if(constraint!=null || constraint.length()>0){
            constraint=constraint.toString().toLowerCase();
            List<UserModel> list= new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getId().toLowerCase().contains(constraint)){
                    list.add(filterList.get(i));
                }
                else if(filterList.get(i).getName().toLowerCase().contains(constraint)){
                    list.add(filterList.get(i));
                }
                else if(filterList.get(i).getPhone().toLowerCase().contains(constraint)){
                    list.add(filterList.get(i));
                }
                else if(filterList.get(i).getEmail().toLowerCase().contains(constraint)){
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
        adapter.mData= (List<UserModel>) results.values;
        adapter.notifyDataSetChanged();
    }


}