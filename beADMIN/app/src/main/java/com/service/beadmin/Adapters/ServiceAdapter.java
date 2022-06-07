package com.service.beadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.service.beadmin.Models.ServicesModel;
import com.service.beadmin.R;
import com.service.beadmin.ServiceItem2Activity;
import com.service.beadmin.ServiceItemActivity;
import com.service.beadmin.ServicesActivity;
import com.service.beadmin.ServicesBuyActivity;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder>{

    private Context mContext;
    private List<ServicesModel> mData ;
    private String TYPE;


    public ServiceAdapter(Context mContext, List<ServicesModel> mData, String TYPE) {
        this.mContext = mContext;
        this.mData = mData;
        this.TYPE = TYPE;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(TYPE.equals("Home")) {
            View row = LayoutInflater.from(mContext).inflate(R.layout.service_item, parent, false);
            return new MyViewHolder(row);
        }
        else {
            View row = LayoutInflater.from(mContext).inflate(R.layout.serviceitem_item, parent, false);
            return new MyViewHolder(row);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String image = mData.get(position).getServiceIcon();
        String title = mData.get(position).getServiceTitle();
        String key= mData.get(position).getServicekey();
        holder.setData(image, title, key, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView im;
        TextView te;

        public MyViewHolder(View itemView) {
            super(itemView);
            im = itemView.findViewById(R.id.service_image);
            te = itemView.findViewById(R.id.service_title);
        }

        public void setData(String image, final String name, final String key, final int position) {
            Glide.with(mContext).load(image).into(im);
            te.setText(name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof ServicesActivity){
                        if (mData.get(position).getServicetype().equals("3")) {
                            Intent i = new Intent(mContext, ServiceItemActivity.class);
                            i.putExtra("key", key);
                            i.putExtra("type",mData.get(position).getServicetype());
                            i.putExtra("title", name);
                            mContext.startActivity(i);
                        }
                        else if (mData.get(position).getServicetype().equals("2")){
                            Intent i = new Intent(mContext, ServiceItem2Activity.class);
                            i.putExtra("key", key);
                            i.putExtra("type",mData.get(position).getServicetype());
                            i.putExtra("title", name);
                            mContext.startActivity(i);
                        }
                        else {
                            Toast.makeText(mContext, "Please update your app for new features", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if((mContext instanceof ServiceItemActivity)){
                        Intent i = new Intent(mContext, ServiceItem2Activity.class);
                        i.putExtra("key", key);
                        i.putExtra("type", "3");
                        i.putExtra("title", mData.get(position).getServiceadd1());
                        i.putExtra("title2", name);
                        i.putExtra("previouskey", mData.get(position).getServicetype());
                        mContext.startActivity(i);
                    }
                    else if((mContext instanceof ServiceItem2Activity)){
                        Intent i = new Intent(mContext, ServicesBuyActivity.class);
                        i.putExtra("key", key);
                        i.putExtra("title", mData.get(position).getServiceadd1());
                        i.putExtra("title2", mData.get(position).getServiceadd2());
                        i.putExtra("title3", name);
                        i.putExtra("previouskey", mData.get(position).getServicetype());
                        i.putExtra("verypreviouskey", mData.get(position).getServicekeylast());
                        mContext.startActivity(i);
                    }
                    else {
                        Toast.makeText(mContext, "Please update your app for new features", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
