package com.service.badaeasyprofessional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasyprofessional.Adapters.OnholdAdapter;
import com.service.badaeasyprofessional.Adapters.UpcomingAdapter;
import com.service.badaeasyprofessional.Models.UpcomingModel;

import java.util.ArrayList;
import java.util.List;

import static com.service.badaeasyprofessional.UpcomingActivity.homeList;

public class OnholdActivity extends AppCompatActivity {

    private RecyclerView homeRecyclerView;
    private OnholdAdapter homeAdapter;
    public static List<UpcomingModel> onholdList = new ArrayList<>();

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onhold);

        /////loading Dialog
        loadingDialog = new Dialog(OnholdActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        homeRecyclerView = findViewById(R.id.onhold_rec);
        LinearLayoutManager l= new LinearLayoutManager(OnholdActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        homeRecyclerView.setLayoutManager(l);
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onholdList.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(onholdList.size()>0){
            onholdList.clear();
        }

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Bookings")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d: dataSnapshot.getChildren()){
                            if((String.valueOf(d.child("book_professional_id").getValue()).equals(HomeActivity.proID)) &&
                                    String.valueOf(d.child("book_status").getValue()).equals("Onhold")) {

                                onholdList.add(new UpcomingModel(String.valueOf(d.child("cat1").getValue()),
                                        String.valueOf(d.child("cat2").getValue()),
                                        String.valueOf(d.child("cat3").getValue()),
                                        String.valueOf(d.child("serviceName").getValue()),
                                        String.valueOf(d.child("serviceTotPrice").getValue()),
                                        String.valueOf(d.child("servicePrice").getValue()),
                                        String.valueOf(d.child("serviceQty").getValue()),
                                        String.valueOf(d.child("serviceUserID").getValue()),
                                        String.valueOf(d.child("book_city").getValue()),
                                        String.valueOf(d.child("book_locality").getValue()),
                                        String.valueOf(d.child("book_flatNo").getValue()),
                                        String.valueOf(d.child("book_pincode").getValue()),
                                        String.valueOf(d.child("book_landmark").getValue()),
                                        String.valueOf(d.child("book_id").getValue()),
                                        String.valueOf(d.child("book_name").getValue()),
                                        String.valueOf(d.child("book_mobile").getValue()),
                                        String.valueOf(d.child("book_mainMobile").getValue()),
                                        String.valueOf(d.child("book_state").getValue()),
                                        String.valueOf(d.child("book_time").getValue()),
                                        String.valueOf(d.child("book_status").getValue()),
                                        String.valueOf(d.child("book_complete_time").getValue()),
                                        String.valueOf(d.child("book_professional_id").getValue()),
                                        String.valueOf(d.child("prob_stmt_1").getValue()),
                                        String.valueOf(d.child("prob_stmt_2").getValue()),
                                        String.valueOf(d.child("prob_stmt_3").getValue()),
                                        String.valueOf(d.child("book_hq").getValue()),
                                        String.valueOf(d.child("admin_publish").getValue()),
                                        String.valueOf(d.child("book_parts").getValue()),
                                        String.valueOf(d.child("book_price").getValue()),
                                        String.valueOf(d.child("payment_mode").getValue()),
                                        String.valueOf(d.child("payment_status").getValue())));
                            }
                        }
                        homeAdapter = new OnholdAdapter(OnholdActivity.this, onholdList);
                        homeRecyclerView.setAdapter(homeAdapter);
                        homeAdapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(OnholdActivity.this, "Error occurerd", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
    }
}
