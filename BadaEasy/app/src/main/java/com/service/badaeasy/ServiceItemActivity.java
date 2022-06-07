package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasy.Adapters.BannerSliderAdapter;
import com.service.badaeasy.Adapters.ReviewAdapter;
import com.service.badaeasy.Adapters.ServiceAdapter;
import com.service.badaeasy.Models.BannerSliderModel;
import com.service.badaeasy.Models.ServicesModel;
import com.service.badaeasy.Models.SummaryModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceItemActivity extends AppCompatActivity {

    private RecyclerView serviceItemRecView;
    private ServiceAdapter serviceAdapter;
    private List<ServicesModel> serviceItemList= new ArrayList<>();

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    public List<SummaryModel> reviewList = new ArrayList<>();

    private ViewPager bannerSliderViewPager;
    private int currentPage;
    private Timer timer;
    final private long DELAY_TIME = 3000;
    final private long PERIOD_TIME = 3000;
    private List<BannerSliderModel> arrangedList;

    private List<String> commentID= new ArrayList<>();
    private ImageView img1, img2;
    private TextView custText;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_item);

        Toolbar toolbar= findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("key"));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img1= findViewById(R.id.img1_1);
        img2= findViewById(R.id.img2_1);
        custText= findViewById(R.id.cust_text_rev);

        /////loading Dialog
        loadingDialog = new Dialog(ServiceItemActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        serviceItemRecView = findViewById(R.id.serviceitem_rec);
        LinearLayoutManager l= new LinearLayoutManager(ServiceItemActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        serviceItemRecView.setLayoutManager(l);
        serviceItemRecView.setHasFixedSize(true);
        serviceItemRecView.setNestedScrollingEnabled(false);

        loadingDialog.show();
        /////////////////////////////banner
        bannerSliderViewPager = findViewById(R.id.viewpager_st_1);
        final List<BannerSliderModel> homeList= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Banners").child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String length= String.valueOf(dataSnapshot.child("length").getValue()) ;
                int l=Integer.parseInt(length);
                for (int i=1; i< l; i++){
                    homeList.add(new BannerSliderModel(String.valueOf(dataSnapshot.child(String.valueOf(i)).getValue()), "#000000"));
                }
                setBannerSliderViewPager(homeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /////////////////////////////banner

        FirebaseDatabase.getInstance().getReference().child("Services")
                .child(UserData.userLoc)
                .child(getIntent().getStringExtra("key"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d: dataSnapshot.getChildren()){
                            if (d.hasChildren() && !d.getKey().toString().equals("comments")) {
                                serviceItemList.add(new ServicesModel(String.valueOf(d.child("icon").getValue()),
                                        String.valueOf(d.child("title").getValue()),
                                        String.valueOf(d.getKey()), getIntent().getStringExtra("key"),
                                        "", getIntent().getStringExtra("title"), "", ""));
                            }
                            Glide.with(ServiceItemActivity.this)
                                    .load(String.valueOf(dataSnapshot.child("Img_1").getValue())).into(img1);
                            Glide.with(ServiceItemActivity.this)
                                    .load(String.valueOf(dataSnapshot.child("Img_2").getValue())).into(img2);
                        }
                        serviceAdapter = new ServiceAdapter(ServiceItemActivity.this, serviceItemList, "Service");
                        serviceItemRecView.setAdapter(serviceAdapter);
                        serviceAdapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });

        reviewRecyclerView = findViewById(R.id.cust_review_rec);
        LinearLayoutManager layoutManager= new LinearLayoutManager(ServiceItemActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setNestedScrollingEnabled(false);
        reviewAdapter = new ReviewAdapter(ServiceItemActivity.this, reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);
        uploadReview();

    }

    private void uploadReview() {
        if (reviewList.size()>0){
            custText.setVisibility(View.VISIBLE);
        }
        else {
            custText.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference().child("Services")
                    .child(UserData.userLoc)
                    .child(getIntent().getStringExtra("key"))
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
                                    if(reviewList.size()>0){
                                        custText.setVisibility(View.VISIBLE);
                                    }else{
                                        custText.setVisibility(View.GONE);
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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reviewList.clear();
        serviceItemList.clear();
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////banner
    private void setBannerSliderViewPager(final List<BannerSliderModel> sliderModelList) {
        currentPage = 2;
        if (timer != null) {
            timer.cancel();
        }

        arrangedList = new ArrayList<>();
        for (int x = 0; x < sliderModelList.size(); x++) {
            arrangedList.add(x, sliderModelList.get(x));
        }
        arrangedList.add(0, sliderModelList.get(sliderModelList.size() - 2));
        arrangedList.add(1, sliderModelList.get(sliderModelList.size() - 1));
        arrangedList.add(sliderModelList.get(0));
        arrangedList.add(sliderModelList.get(1));


        BannerSliderAdapter sliderAdapter = new BannerSliderAdapter(arrangedList);
        bannerSliderViewPager.setAdapter(sliderAdapter);
        bannerSliderViewPager.setClipToPadding(false);
        bannerSliderViewPager.setPageMargin(20);
        bannerSliderViewPager.setCurrentItem(currentPage);


        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    pageLooper(arrangedList);
                }
            }
        };
        bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);
        startbannerSlideShow(arrangedList);

        bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pageLooper(arrangedList);
                stopbannerSlideShow();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startbannerSlideShow(arrangedList);
                }
                return false;
            }
        });
    }

    private void pageLooper(List<BannerSliderModel> sliderModelList) {
        if (currentPage == sliderModelList.size() - 2) {
            currentPage = 2;
            bannerSliderViewPager.setCurrentItem(currentPage, false);
        }
        if (currentPage == 1) {
            currentPage = sliderModelList.size() - 3;
            bannerSliderViewPager.setCurrentItem(currentPage, false);
        }


    }

    private void startbannerSlideShow(final List<BannerSliderModel> sliderModelList) {
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage >= sliderModelList.size()) {
                    currentPage = 1;
                }
                bannerSliderViewPager.setCurrentItem(currentPage++, true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_TIME, PERIOD_TIME);
    }

    private void stopbannerSlideShow() {
        timer.cancel();
    }
}
