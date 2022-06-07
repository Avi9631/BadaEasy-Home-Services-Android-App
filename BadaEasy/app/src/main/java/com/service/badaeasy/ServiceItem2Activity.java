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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasy.Adapters.ReviewAdapter;
import com.service.badaeasy.Adapters.ServiceAdapter;
import com.service.badaeasy.Models.ServicesModel;
import com.service.badaeasy.Models.SummaryModel;

import java.util.ArrayList;
import java.util.List;

public class ServiceItem2Activity extends AppCompatActivity {

    private RecyclerView serviceItem2RecView;
    private ServiceAdapter service2Adapter;
    private List<ServicesModel> serviceItem2List= new ArrayList<>();

    private ReviewAdapter reviewAdapter;
    private RecyclerView reviewRecyclerView;
    public List<SummaryModel> reviewList = new ArrayList<>();

    private TextView custText;
    private List<String> commentID= new ArrayList<>();


    private Dialog loadingDialog;

    private ImageView stImage2;
    private ImageView img1,img2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_item2);

        Toolbar toolbar= findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("key"));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img1= findViewById(R.id.img1_2);
        img2= findViewById(R.id.img2_2);

        /////loading Dialog
        loadingDialog = new Dialog(ServiceItem2Activity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        custText= findViewById(R.id.cust_text_rev);
        stImage2= findViewById(R.id.st_2_img);
        reviewRecyclerView = findViewById(R.id.rev_rec);

        serviceItem2RecView = findViewById(R.id.serviceitem2_rec);
        LinearLayoutManager l= new LinearLayoutManager(ServiceItem2Activity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        serviceItem2RecView.setLayoutManager(l);
        serviceItem2RecView.setNestedScrollingEnabled(false);
        serviceItem2RecView.setHasFixedSize(true);

        if (getIntent().getStringExtra("type").equals("3")){
            custText.setVisibility(View.GONE);
            reviewRecyclerView.setVisibility(View.GONE);
            loadDataforType3();
        }
        else if (getIntent().getStringExtra("type").equals("2")){
            custText.setVisibility(View.VISIBLE);
            reviewRecyclerView.setVisibility(View.VISIBLE);
            loadDataforType2();
        }

    }

    private void loadDataforType2() {
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Services")
                .child(UserData.userLoc)
                .child(getIntent().getStringExtra("key"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if (dataSnapshot1.hasChildren()  && !dataSnapshot1.getKey().toString().equals("comments")) {
                        serviceItem2List.add(new ServicesModel(String.valueOf(dataSnapshot1.child("icon").getValue()),
                                String.valueOf(dataSnapshot1.child("stitle").getValue()),
                                String.valueOf(dataSnapshot1.getKey()),
                                getIntent().getStringExtra("key"), getIntent().getStringExtra("type"),
                                getIntent().getStringExtra("title"), "", ""));
                    }
                    Glide.with(ServiceItem2Activity.this)
                            .load(String.valueOf(dataSnapshot.child("Img_1").getValue())).into(img1);
                    Glide.with(ServiceItem2Activity.this)
                            .load(String.valueOf(dataSnapshot.child("Img_2").getValue())).into(img2);
                }
                service2Adapter = new ServiceAdapter(ServiceItem2Activity.this, serviceItem2List, "Service2");
                serviceItem2RecView.setAdapter(service2Adapter);
                service2Adapter.notifyDataSetChanged();

                FirebaseDatabase.getInstance().getReference().child("Images")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Glide.with(ServiceItem2Activity.this)
                                        .load(String.valueOf(dataSnapshot.child(getIntent().getStringExtra("key")).getValue()))
                                        .into(stImage2);
                                loadingDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                loadingDialog.dismiss();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });

        reviewRecLoad();
    }

    private void loadDataforType3() {
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Services").child(getIntent().getStringExtra("previouskey"))
                .child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if (dataSnapshot1.hasChildren()) {
                        serviceItem2List.add(new ServicesModel(String.valueOf(dataSnapshot1.child("icon").getValue()),
                                String.valueOf(dataSnapshot1.child("stitle").getValue()),
                                String.valueOf(dataSnapshot1.getKey()),
                                getIntent().getStringExtra("key"), getIntent().getStringExtra("previouskey"),
                                getIntent().getStringExtra("title"), getIntent().getStringExtra("title2"),""));
                    }
                    Glide.with(ServiceItem2Activity.this)
                            .load(String.valueOf(dataSnapshot.child("Img_1").getValue())).into(img1);
                    Glide.with(ServiceItem2Activity.this)
                            .load(String.valueOf(dataSnapshot.child("Img_2").getValue())).into(img2);
                }
                service2Adapter = new ServiceAdapter(ServiceItem2Activity.this, serviceItem2List, "Service2");
                serviceItem2RecView.setAdapter(service2Adapter);
                service2Adapter.notifyDataSetChanged();
                FirebaseDatabase.getInstance().getReference().child("Images")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Glide.with(ServiceItem2Activity.this)
                                        .load(String.valueOf(dataSnapshot.child(getIntent().getStringExtra("key")).getValue()))
                                        .into(stImage2);
                                loadingDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                loadingDialog.dismiss();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    private void reviewRecLoad(){
        LinearLayoutManager layoutManager= new LinearLayoutManager(ServiceItem2Activity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setNestedScrollingEnabled(false);
        reviewAdapter = new ReviewAdapter(ServiceItem2Activity.this, reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);
        uploadReview();
    }

    private void uploadReview() {
        if (reviewList.size()>0){
            reviewList.clear();
            custText.setVisibility(View.VISIBLE);
        }else{
            custText.setVisibility(View.GONE);
        }
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Services").child(getIntent().getStringExtra("key"))
                .child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    commentID.add(String.valueOf(dataSnapshot1.getKey()));
                }
                FirebaseDatabase.getInstance().getReference().child("Comments")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot d: dataSnapshot.getChildren()){
                                    if(commentID.contains(String.valueOf(d.getKey()))) {
                                        reviewList.add(new SummaryModel(String.valueOf(d.child("reviewName").getValue()),
                                                String.valueOf(d.child("reviewText").getValue())));
                                    }
                                }
                                reviewAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reviewList.clear();
        serviceItem2List.clear();
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
