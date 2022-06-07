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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{

    private Context mContext;
    private List<SummaryModel> mData ;

    public ReviewAdapter(Context mContext, List<SummaryModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.cust_review_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String name = mData.get(position).getItemName();
        String sumcomment = mData.get(position).getItemPrice();

        holder.setData(name, sumcomment);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView reviewName, reviewTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            reviewName= itemView.findViewById(R.id.review_name);
            reviewTitle= itemView.findViewById(R.id.review_text);
        }

        public void setData(String name, String sumcomment) {
            reviewTitle.setText(sumcomment);
            reviewName.setText(name);
        }
    }
}
