package com.service.badaeasyprofessional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasyprofessional.Adapters.CompleteAdapter;
import com.service.badaeasyprofessional.Adapters.UpcomingAdapter;
import com.service.badaeasyprofessional.Models.CompleteModel;
import com.service.badaeasyprofessional.Models.UpcomingModel;

import java.util.ArrayList;
import java.util.List;

public class CompletedActivity extends AppCompatActivity {

    private RecyclerView bookRecView;
    private CompleteAdapter bookAdapter;
    private List<CompleteModel> bookingList= new ArrayList<>();

    private Dialog loadingDialog;
    private List<String> bidList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);

        /////loading Dialog
        loadingDialog = new Dialog(CompletedActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bookRecView = findViewById(R.id.completeBookRec);
        LinearLayoutManager l= new LinearLayoutManager(CompletedActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        bookRecView.setLayoutManager(l);
        bookRecView.setHasFixedSize(true);
        bookRecView.setNestedScrollingEnabled(false);

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                .child(HomeActivity.proID).child("proOrder")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    bidList.add(String.valueOf(d.getKey()));
                }
                FirebaseDatabase.getInstance().getReference().child("Bookings")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot d) {
                                for(String i: bidList) {
//                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        if ((String.valueOf(d.child(i).child("book_professional_id").getValue()).equals(HomeActivity.proID)) &&
                                                String.valueOf(d.child(i).child("book_status").getValue()).equals("Completed")) {
                                            bookingList.add(new CompleteModel("",
                                                    "",
                                                    "",
                                                    String.valueOf(d.child(i).child("serviceName").getValue()),
                                                    String.valueOf(d.child(i).child("serviceTotPrice").getValue()),
                                                    String.valueOf(d.child(i).child("servicePrice").getValue()),
                                                    String.valueOf(d.child(i).child("serviceQty").getValue()),
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    String.valueOf(d.child(i).child("book_id").getValue()),
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    String.valueOf(d.child(i).child("book_time").getValue()),
                                                    String.valueOf(d.child(i).child("book_status").getValue()),
                                                    "",
                                                    "",
                                                    String.valueOf(d.child(i).child("prob_stmt_1").getValue()),
                                                    "",
                                                    "",
                                                    String.valueOf(d.child(i).child("hq_near").getValue()),
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    String.valueOf(d.child(i).child("proEarning").getValue())));
                                        }
//                                    }
                                }
                                bookAdapter = new CompleteAdapter(CompletedActivity.this, bookingList);
                                bookRecView.setAdapter(bookAdapter);
                                bookAdapter.notifyDataSetChanged();
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

            }
        });
    }
}
