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
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasy.Adapters.ServiceBuyAdapter;
import com.service.badaeasy.Models.ServiceBuyModel;
import java.util.ArrayList;
import java.util.List;

public class ServicesBuyActivity extends AppCompatActivity {

    private RecyclerView serviceBuyRecView;
    private ServiceBuyAdapter serviceBuyAdapter;
    private List<ServiceBuyModel> serviceBuyList= new ArrayList<>();

    private Dialog loadingDialog;
    private ImageView img1, img2, bannerSliderViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_buy);

        Toolbar toolbar= findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img1= findViewById(R.id.img1_buy);
        img2= findViewById(R.id.img2_buy);
        bannerSliderViewPager = findViewById(R.id.buy_vp);

        /////loading Dialog
        loadingDialog = new Dialog(ServicesBuyActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        serviceBuyRecView = findViewById(R.id.buy_rec_view);
        LinearLayoutManager l= new LinearLayoutManager(ServicesBuyActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        serviceBuyRecView.setLayoutManager(l);
        serviceBuyRecView.setNestedScrollingEnabled(false);
        serviceBuyRecView.setHasFixedSize(false);
        if (getIntent().getStringExtra("verypreviouskey").equals("2")){
            loadDataBuyIf();
        }
        else{
            loadDataBuyElse();
        }

    }

    private void loadDataBuyIf() {
        loadingDialog.show();
        getSupportActionBar().setTitle(getIntent().getStringExtra("key"));
        FirebaseDatabase.getInstance().getReference().child("Services")
                .child(UserData.userLoc)
                .child(getIntent().getStringExtra("previouskey"))
                .child(getIntent().getStringExtra("key"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot f: dataSnapshot.getChildren()){
                            if (f.hasChildren()) {
                                serviceBuyList.add(new ServiceBuyModel(String.valueOf(f.child("mtitle").getValue()),
                                        String.valueOf(f.child("mprice").getValue()),
                                        String.valueOf(f.child("mMRP").getValue()),
                                        String.valueOf(f.child("mDes").getValue()),
                                        "",
                                        getIntent().getStringExtra("title"),
                                        getIntent().getStringExtra("title2"),
                                        getIntent().getStringExtra("title3")));
                            }
                            Glide.with(ServicesBuyActivity.this)
                                    .load(String.valueOf(dataSnapshot.child("Img_1").getValue())).into(img1);
                            Glide.with(ServicesBuyActivity.this)
                                    .load(String.valueOf(dataSnapshot.child("Img_2").getValue())).into(img2);
                            Glide.with(ServicesBuyActivity.this)
                                    .load(String.valueOf(dataSnapshot.child("Img_main").getValue())).into(bannerSliderViewPager);
                        }
                        serviceBuyAdapter = new ServiceBuyAdapter(ServicesBuyActivity.this, serviceBuyList);
                        serviceBuyRecView.setAdapter(serviceBuyAdapter);
                        serviceBuyAdapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });
    }

    private void loadDataBuyElse() {
        loadingDialog.show();
        getSupportActionBar().setTitle(getIntent().getStringExtra("key"));
        FirebaseDatabase.getInstance().getReference().child("Services")
                .child(UserData.userLoc)
                .child(getIntent().getStringExtra("verypreviouskey"))
            .child(getIntent().getStringExtra("previouskey")).child(getIntent().getStringExtra("key"))
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot f: dataSnapshot.getChildren()){
                        if (f.hasChildren()) {
                            serviceBuyList.add(new ServiceBuyModel(String.valueOf(f.child("mtitle").getValue()),
                                    String.valueOf(f.child("mprice").getValue()),
                                    String.valueOf(f.child("mMRP").getValue()),
                                    String.valueOf(f.child("mDes").getValue()),
                                    "",
                                    getIntent().getStringExtra("title"),
                                    getIntent().getStringExtra("title2"),
                                    getIntent().getStringExtra("title3")));
                        }
                        Glide.with(ServicesBuyActivity.this)
                                .load(String.valueOf(dataSnapshot.child("Img_1").getValue())).into(img1);
                        Glide.with(ServicesBuyActivity.this)
                                .load(String.valueOf(dataSnapshot.child("Img_2").getValue())).into(img2);
                        Glide.with(ServicesBuyActivity.this)
                                .load(String.valueOf(dataSnapshot.child("Img_main").getValue())).into(bannerSliderViewPager);
                    }
                    serviceBuyAdapter = new ServiceBuyAdapter(ServicesBuyActivity.this, serviceBuyList);
                    serviceBuyRecView.setAdapter(serviceBuyAdapter);
                    serviceBuyAdapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingDialog.dismiss();
                }
            });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        serviceBuyList.clear();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData.userName= String.valueOf(dataSnapshot.child("userName").getValue());
                        UserData.userDOB= String.valueOf(dataSnapshot.child("userDOB").getValue());
                        UserData.userEmail= String.valueOf(dataSnapshot.child("userEmail").getValue());
                        UserData.userMobile= String.valueOf(dataSnapshot.child("userMobile").getValue());
                        UserData.userRewards=  String.valueOf(dataSnapshot.child("rewards").getValue());
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });
    }

}
