package com.service.badaeasy.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.service.badaeasy.BookingActivity;
import com.service.badaeasy.HomeFragment;
import com.service.badaeasy.Models.AddressModel;

import com.service.badaeasy.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder>{

    private Context mContext;
    private List<AddressModel> mData ;

    public AddressAdapter(Context mContext, List<AddressModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.address_item_book_, parent, false);
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

        public MyViewHolder(View itemView) {
            super(itemView);
            addressName= itemView.findViewById(R.id.address_name);
            addressMobile= itemView.findViewById(R.id.address_mobile);
            addressAddress= itemView.findViewById(R.id.address_address);
            addressCity= itemView.findViewById(R.id.address_city);
            addressState= itemView.findViewById(R.id.address_state);
            addressLayout= itemView.findViewById(R.id.address_const_layout);
        }

        public void setData(String city, String locality, String flatNo, String pincode, String landmark,
                            String name, String mobile, String altMobile, String state, final int position) {

                addressName.setText(name);
                addressMobile.setText(mobile);
                addressAddress.setText(flatNo + ", " + locality+"\n"+landmark);
                addressCity.setText(city + " - " + pincode);
                addressState.setText(state);

            if (mData.get(position).isCheck()){
                addressLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            }
            else {
                addressLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (AddressModel a : mData){
                        a.setCheck(false);
                    }
                    mData.get(position).setCheck(true);
                    BookingActivity.addressAdapter.notifyDataSetChanged();
                    HomeFragment.cityfinal=mData.get(position).getCity();
                    HomeFragment.localityfinal=mData.get(position).getLocality();
                    HomeFragment.flatNofinal=mData.get(position).getFlatNo();
                    HomeFragment.pincodefinal=mData.get(position).getPincode();
                    HomeFragment.landmarkfinal=mData.get(position).getLandmark();
                    HomeFragment.namefinal=mData.get(position).getName();
                    HomeFragment.mobilefinal=mData.get(position).getMobile();
                    HomeFragment.statefinal=mData.get(position).getState();


                }
            });

        }
    }
}
