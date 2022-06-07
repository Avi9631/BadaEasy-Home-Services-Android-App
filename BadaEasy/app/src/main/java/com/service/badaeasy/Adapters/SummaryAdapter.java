package com.service.badaeasy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.service.badaeasy.Models.SummaryModel;
import com.service.badaeasy.R;

import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.MyViewHolder>{

    private Context mContext;
    private List<SummaryModel> mData ;

    public SummaryAdapter(Context mContext, List<SummaryModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.summary_bill_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String summItem = mData.get(position).getItemName();
        String summRate = mData.get(position).getItemPrice();

        holder.setData(summItem, summRate);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView billItem, billRate;

        public MyViewHolder(View itemView) {
            super(itemView);
            billItem= itemView.findViewById(R.id.summary_name);
            billRate= itemView.findViewById(R.id.summary_cost);
        }

        public void setData(String summItem, String summRate) {
            billItem.setText(summItem);
            billRate.setText(summRate);
        }
    }
}
