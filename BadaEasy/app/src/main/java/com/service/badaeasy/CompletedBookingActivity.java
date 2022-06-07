package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasy.Adapters.BookingAdapter;
import com.service.badaeasy.Models.BookingModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompletedBookingActivity extends AppCompatActivity {

    private RecyclerView bookRecView;
    private BookingAdapter bookAdapter;
    private List<BookingModel> bookingList= new ArrayList<>();

    private List<String> bookIDS= new ArrayList<>();

    private Dialog loadingDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_booking);
//        final String statusType= getIntent().getStringExtra("statusType");

        /////loading Dialog
        loadingDialog = new Dialog(CompletedBookingActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        swipeRefreshLayout= findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBookingData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        Toolbar toolbar= findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Bookings");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        bookRecView = findViewById(R.id.completeBookRec);
        LinearLayoutManager l= new LinearLayoutManager(CompletedBookingActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        bookRecView.setLayoutManager(l);
        bookRecView.setHasFixedSize(true);
        bookRecView.setNestedScrollingEnabled(false);
        bookAdapter = new BookingAdapter(CompletedBookingActivity.this, bookingList);
        bookRecView.setAdapter(bookAdapter);
    }

    private void loadBookingData(){
        if((bookingList.size()>0)&&(bookIDS.size()>0)){
            bookingList.clear();
            bookIDS.clear();
        }
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userBooking")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot d: dataSnapshot.getChildren()) {
                            bookIDS.add(d.getKey());
                        }
                        FirebaseDatabase.getInstance().getReference().child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot k: dataSnapshot.getChildren()){
                                    if(bookIDS.contains(String.valueOf(k.getKey()))){
//                                if ((String.valueOf(k.child("book_status").getValue()).equals(statusType))) {
                                        bookingList.add(new BookingModel("",
                                                "",
                                                "",
                                                String.valueOf(k.child("serviceName").getValue()),
                                                String.valueOf(k.child("serviceTotPrice").getValue()),
                                                String.valueOf(k.child("servicePrice").getValue()),
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                String.valueOf(k.child("book_id").getValue()),
                                                "",
                                                "",
                                                "",
                                                "",
                                                String.valueOf(k.child("book_time").getValue()),
                                                String.valueOf(k.child("book_status").getValue()),
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                ""));
//                                }
                                    }
                                }
                                Collections.sort(bookingList, new SortByBookTime());
                                bookAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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
        if (bookingList.size()>0) {
            bookingList.clear();
        }
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
                        loadBookingData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });
    }
}

class SortByBookTime implements Comparator<BookingModel> {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compare(BookingModel a1, BookingModel a2) {
        Instant ins1=Instant.ofEpochMilli(Long.parseLong(a1.getBookTime()));
        Instant ins2=Instant.ofEpochMilli(Long.parseLong(a2.getBookTime()));

        ZoneId zd= ZoneId.of("Asia/Kolkata");
        ZonedDateTime zdt1= ZonedDateTime.ofInstant(ins1, zd);
        ZonedDateTime zdt2= ZonedDateTime.ofInstant(ins2, zd);
        if(zdt1.isBefore(zdt2)){
            return 1;
        }else{
            return -1;
        }
//        long l=(Long.parseLong(a2.getBookTime()) - Long.parseLong(a1.getBookTime()));
//        if(l>0){
//            return 1;
//        }else{
//            return 0;
//        }
    }

}
