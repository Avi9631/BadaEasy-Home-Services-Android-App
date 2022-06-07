package com.service.badaeasy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.badaeasy.BookingActivity;
import com.service.badaeasy.HomeFragment;
import com.service.badaeasy.Models.RewardModel;
import com.service.badaeasy.R;
import com.service.badaeasy.UserData;

import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.MyViewHolder>{

    private Context mContext;
    private List<RewardModel> mData ;

    public RewardsAdapter(Context mContext, List<RewardModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.rewards_item_layout, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String reward = mData.get(position).getAmt();
        holder.setData(reward);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView rewA;

        public MyViewHolder(View itemView) {
            super(itemView);
            rewA= itemView.findViewById(R.id.coupon_body);
        }

        public void setData(final String rewardAmt) {
             rewA.setText("Get Rs."+ rewardAmt+"/- off on any order above Rs.100/-");

             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if(mContext instanceof  BookingActivity) {
                         Toast.makeText(mContext, rewardAmt, Toast.LENGTH_SHORT).show();
                         if(!UserData.userRewards.equals("")){
                             BookingActivity.appliedCoupon = Double.parseDouble(rewardAmt);
                         }
                         BookingActivity.addOfferNotation(Double.parseDouble(rewardAmt));
                         BookingActivity.dialog.dismiss();
                     }
                 }
             });

        }
    }
}
