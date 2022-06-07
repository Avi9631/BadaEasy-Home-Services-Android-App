package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasy.Adapters.RewardsAdapter;
import com.service.badaeasy.Models.RewardModel;
import java.util.ArrayList;
import java.util.List;

public class RewardsActivity extends AppCompatActivity {

    private RecyclerView rewardsRecyclerView;
    private RewardsAdapter myRewardsAdapter;
    private List<RewardModel> rewardsModelList=new ArrayList<>();

    private Dialog loadingDialog;

    private EditText priCoupon;
    private Button priCouponAdd;
    private TextView priStatusText;

    boolean sta=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        /////loading Dialog
        loadingDialog = new Dialog(RewardsActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        Toolbar toolbar= findViewById(R.id.toolbarR);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Rewards");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        priCoupon= findViewById(R.id.privateCouponCode);
        priCouponAdd= findViewById(R.id.privateCouponAdd);
        priStatusText= findViewById(R.id.privateCouponStatus);
        priStatusText.setVisibility(View.GONE);

        rewardsRecyclerView= findViewById(R.id.my_rewards_recyclerview);
        LinearLayoutManager layoutManager= new LinearLayoutManager(RewardsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rewardsRecyclerView.setLayoutManager(layoutManager);
        myRewardsAdapter= new RewardsAdapter(RewardsActivity.this, rewardsModelList);
        rewardsRecyclerView.setAdapter(myRewardsAdapter);

        loadRewardandData();

        priCouponAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("PrivateCoupons")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d: dataSnapshot.getChildren()){
                            String key=String.valueOf(d.getKey());
                            if((key.equals(priCoupon.getText().toString().toUpperCase()))){
                                final String val=String.valueOf(dataSnapshot.child(key).getValue());
                                if(val.substring(0,val.indexOf(',')).equals("available")){
                                    final String z= val.substring(val.indexOf(',')+1) ;
                                    FirebaseDatabase.getInstance().getReference().child("PrivateCoupons")
                                            .child(key)
                                            .setValue("used,"+z)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("rewards").setValue(UserData.userRewards + z + ",")
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sta=true;
                                                                    addCoupon();
                                                                    Toast.makeText(RewardsActivity.this,"OK", Toast.LENGTH_SHORT).show();
                                                                    loadingDialog.dismiss();
                                                                }
                                                            });
                                                }
                                            });
                                    break;
                                }else {
                                    sta=false;
                                    addCoupon();
                                    break;
                                }
                            }
                        }
                        addCoupon();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });
            }
        });

    }

    private void loadRewardandData() {
        if(rewardsModelList.size()>0){
            rewardsModelList.clear();
        }
            loadingDialog.show();
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserData.userName = String.valueOf(dataSnapshot.child("userName").getValue());
                            UserData.userDOB = String.valueOf(dataSnapshot.child("userDOB").getValue());
                            UserData.userEmail = String.valueOf(dataSnapshot.child("userEmail").getValue());
                            UserData.userMobile = String.valueOf(dataSnapshot.child("userMobile").getValue());
                            UserData.userRewards = String.valueOf(dataSnapshot.child("rewards").getValue());
                            if (!UserData.userRewards.equals("")) {
                                String[] rewardArr = UserData.userRewards.split(",");
                                for (String s : rewardArr) {
                                    rewardsModelList.add(new RewardModel(s));
                                }
                            }
                            myRewardsAdapter.notifyDataSetChanged();
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            loadingDialog.dismiss();
                        }
                    });

    }

    private void addCoupon() {
        priStatusText.setVisibility(View.VISIBLE);
        if(sta){
            priStatusText.setText("*Coupon applied successfully");
            loadRewardandData();
        }else {
            priStatusText.setText("*Invalid coupon");
            loadRewardandData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
