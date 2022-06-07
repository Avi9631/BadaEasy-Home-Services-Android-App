package com.service.badaeasy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.badaeasy.BookingActivity;
import com.service.badaeasy.Models.ServiceBuyModel;
import com.service.badaeasy.R;


import java.util.List;

public class ServiceBuyAdapter extends RecyclerView.Adapter<ServiceBuyAdapter.MyViewHolder>{

    private Context mContext;
    private List<ServiceBuyModel> mData ;

    public ServiceBuyAdapter(Context mContext, List<ServiceBuyModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.service_buy_item, parent, false);
        return new MyViewHolder(row);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String buyTitle = mData.get(position).getBuyTitle();
        String buyRate = mData.get(position).getBuyRate();
        String addToCartBtn = mData.get(position).getAddToCart();
        String buyMRP= mData.get(position).getBuyMRP();
        String buyDes= mData.get(position).getBuyDes();

        holder.setData(buyTitle,buyRate, addToCartBtn, buyMRP, buyDes, position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView buyRate, buyTitle, addedCart, mrp, des;
        TextView addToCartBtn;

        public MyViewHolder(View itemView) {
            super(itemView);

            buyRate= itemView.findViewById(R.id.buy_rate);
            buyTitle= itemView.findViewById(R.id.buy_title);
            addToCartBtn= itemView.findViewById(R.id.addToCart);
            mrp= itemView.findViewById(R.id.mrp);
            des= itemView.findViewById(R.id.des);
        }

        public void setData(final String buyTitle1, final String buyRate1, String addToCartBtn1, String buyMRP, String buyDes, final int position) {

            buyTitle.setText(buyTitle1);
            buyRate.setText(buyRate1);
            mrp.setText(buyMRP);
            des.setText("* "+buyDes+" *");

            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCartBtn.setEnabled(false);
                    Toast.makeText(mContext, mData.get(position).getAdd1()+"\n"+
                            mData.get(position).getAdd2()+"\n"+
                            mData.get(position).getAdd3()+"\n"+
                            buyTitle1+"\n"+
                            buyRate1, Toast.LENGTH_SHORT).show();
                    Intent i= new Intent(mContext, BookingActivity.class);
                    i.putExtra("add1", mData.get(position).getAdd1());
                    i.putExtra("add2",mData.get(position).getAdd2());
                    i.putExtra("add3", mData.get(position).getAdd3());
                    i.putExtra("buyName", buyTitle1);
                    i.putExtra("buyPrice", buyRate1);
                    mContext.startActivity(i);
                    addToCartBtn.setEnabled(true);
                }
            });

        }
    }
}
