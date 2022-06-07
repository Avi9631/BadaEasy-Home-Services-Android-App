package com.service.badaeasy.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.service.badaeasy.BookingActivity;
import com.service.badaeasy.HomeFragment;
import com.service.badaeasy.Models.AddressModel;
import com.service.badaeasy.R;

import java.util.List;

public class SavedAddressAdapter extends RecyclerView.Adapter<SavedAddressAdapter.MyViewHolder>{

    private Context mContext;
    private List<AddressModel> mData ;

    public SavedAddressAdapter(Context mContext, List<AddressModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.item_address_layout, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String city= mData.get(position).getCity();
        String locality= mData.get(position).getLocality();
        String flatNo= mData.get(position).getFlatNo();
        String pincode= mData.get(position).getPincode();
        String landmark= mData.get(position).getLandmark();
        String name= mData.get(position).getName();
        String mobile= mData.get(position).getMobile();
        String altMobile= mData.get(position).getAltMobile();
        String state= mData.get(position).getState();

        holder.setData(city,locality,flatNo,pincode,landmark,name,mobile,altMobile,state, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView addressName, addressMobile, addressAddress, addressCity, addressState;
        ConstraintLayout addressLayout;
        Button edit, del;

        public MyViewHolder(View itemView) {
            super(itemView);
            addressName= itemView.findViewById(R.id.address_name);
            addressMobile= itemView.findViewById(R.id.address_mobile);
            addressAddress= itemView.findViewById(R.id.address_address);
            addressCity= itemView.findViewById(R.id.address_city);
            addressState= itemView.findViewById(R.id.address_state);
            addressLayout= itemView.findViewById(R.id.address_const_layout);
            edit= itemView.findViewById(R.id.button2);
            del= itemView.findViewById(R.id.button3);
        }

        public void setData(String city, String locality, String flatNo, String pincode, String landmark,
                            String name, String mobile, String altMobile, String state, final int position) {

                addressName.setText(name);
                addressMobile.setText(mobile);
                addressAddress.setText(flatNo + ", " + locality+"\n"+landmark);
                addressCity.setText(city + " - " + pincode);
                addressState.setText(state);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        }
    }
}
