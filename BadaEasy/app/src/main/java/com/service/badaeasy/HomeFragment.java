package com.service.badaeasy;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasy.Adapters.BannerSliderAdapter;
import com.service.badaeasy.Adapters.ServiceAdapter;
import com.service.badaeasy.Models.BannerSliderModel;
import com.service.badaeasy.Models.ServicesModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    public static  String  cityfinal,  localityfinal,  flatNofinal,  pincodefinal,  landmarkfinal,
            namefinal,  mobilefinal, mainMobilefinal,  statefinal;

    private ViewPager bannerSliderViewPager;
    private int currentPage;
    private Timer timer;
    final private long DELAY_TIME = 3000;
    final private long PERIOD_TIME = 3000;
    private List<BannerSliderModel> arrangedList;

    private RecyclerView homeRecyclerView;
    private ServiceAdapter homeAdapter;
    public static List<ServicesModel> homeList = new ArrayList<>();

    private Dialog loadingDialog;

    private ImageView adBanner1;

    private RecyclerView serviceItemRecView;
    private ServiceAdapter serviceAdapter;
    private List<ServicesModel> serviceImgList= new ArrayList<>();

    private List<BannerSliderModel> homeList1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /////loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        Toast.makeText(getContext(), UserData.userName+"\n"+UserData.userEmail, Toast.LENGTH_SHORT).show();

        homeRecyclerView = view.findViewById(R.id.home_service_rec);
        homeRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setNestedScrollingEnabled(false);
        homeAdapter = new ServiceAdapter(getContext(), homeList, "Home");
        homeRecyclerView.setAdapter(homeAdapter);
        homeAdapter.notifyDataSetChanged();

        serviceItemRecView = view.findViewById(R.id.img_service_rec);
        LinearLayoutManager l= new LinearLayoutManager(getContext());
        l.setOrientation(RecyclerView.VERTICAL);
        serviceItemRecView.setLayoutManager(l);
        serviceItemRecView.setHasFixedSize(true);
        serviceItemRecView.setNestedScrollingEnabled(false);
        serviceAdapter = new ServiceAdapter(getContext(), serviceImgList, "HOMEIMG");
        serviceItemRecView.setAdapter(serviceAdapter);

        adBanner1= view.findViewById(R.id.ads_1);
        bannerSliderViewPager = view.findViewById(R.id.banner_slider_view_pager);
        loadHomeFragment();

        return view;
    }

    private void loadHomeFragment(){
        if(serviceImgList.size()>0){
            if(homeList1.size()==0) {
                loadingDialog.show();
                loadHomeBannerImg();
            }
        }else {
            loadingDialog.show();
            FirebaseDatabase.getInstance().getReference().child("Services").child(UserData.userLoc).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        serviceImgList.add(new ServicesModel(String.valueOf(d.child("homeIMG").getValue()),
                                String.valueOf(d.child("cattitle").getValue()),
                                String.valueOf(d.getKey()),
                                String.valueOf(d.child("type").getValue()),
                                "", "",
                                "", ""));
                    }
                    Collections.shuffle(serviceImgList);
                    serviceAdapter.notifyDataSetChanged();
                    loadHomeBannerImg();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingDialog.dismiss();
                }
            });
        }

    }

    private void loadHomeBannerImg(){
        homeList1 = new ArrayList<>();
        if(homeList1.size()>0){
            loadingDialog.dismiss();
        }else {
            FirebaseDatabase.getInstance().getReference().child("Banners").child("Display")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String length = String.valueOf(dataSnapshot.child("length").getValue());
                            int l = Integer.parseInt(length);
                            for (int i = 1; i <= l; i++) {
                                homeList1.add(new BannerSliderModel(String.valueOf(dataSnapshot.child(String.valueOf(i)).getValue()), "#000000"));
                            }
                            setBannerSliderViewPager(homeList1);
                            FirebaseDatabase.getInstance().getReference().child("Images")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Glide.with(Objects.requireNonNull(getContext())).load(String.valueOf(dataSnapshot.child("ads_1").getValue()))
                                                        .into(adBanner1);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getContext() == null){


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
                        UserData.userState=  String.valueOf(dataSnapshot.child("userState").getValue());
                        UserData.userLoc=  String.valueOf(dataSnapshot.child("userLocation").getValue());
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });


    }


    ///////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\///////////banner
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
