package com.service.badaeasyprofessional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasyprofessional.Adapters.GridAdapter;
import com.service.badaeasyprofessional.Adapters.UpcomingAdapter;
import com.service.badaeasyprofessional.Models.HomeModel;
import com.service.badaeasyprofessional.Models.UpcomingModel;

import java.util.ArrayList;
import java.util.List;

public class UpcomingActivity extends AppCompatActivity {

    private RecyclerView homeRecyclerView;
    private UpcomingAdapter homeAdapter;
    public static List<UpcomingModel> homeList = new ArrayList<>();

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);

        /////loading Dialog
        loadingDialog = new Dialog(UpcomingActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        homeRecyclerView = findViewById(R.id.upcoming_rec);
        LinearLayoutManager l= new LinearLayoutManager(UpcomingActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        homeRecyclerView.setLayoutManager(l);
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(homeList.size()>0){
            homeList.clear();
        }

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Bookings")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d: dataSnapshot.getChildren()){
                            if((String.valueOf(d.child("book_professional_id").getValue()).equals(HomeActivity.proID)) &&
                                    String.valueOf(d.child("book_status").getValue()).equals("Ready")) {
                                homeList.add(new UpcomingModel("",
                                        "",
                                        "",
                                        String.valueOf(d.child("serviceName").getValue()),
                                        String.valueOf(d.child("serviceTotPrice").getValue()),
                                        String.valueOf(d.child("servicePrice").getValue()),
                                        String.valueOf(d.child("serviceQty").getValue()),
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        String.valueOf(d.child("book_id").getValue()),
                                        "",
                                        "",
                                        "",
                                        "",
                                        String.valueOf(d.child("book_time").getValue()),
                                        String.valueOf(d.child("book_status").getValue()),
                                        "",
                                        "",
                                        String.valueOf(d.child("prob_stmt_1").getValue()),
                                        "",
                                        "",
                                        String.valueOf(d.child("hq_near").getValue()),
                                        "",
                                        "",
                                        "",
                                        "",
                                        ""));
                            }
                        }
                        homeAdapter = new UpcomingAdapter(UpcomingActivity.this, homeList);
                        homeRecyclerView.setAdapter(homeAdapter);
                        homeAdapter.notifyDataSetChanged();
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
        homeList.clear();
    }
}
