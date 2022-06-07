package com.service.beadmin;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.beadmin.Adapters.BookingAdapter;
import com.service.beadmin.Models.BookingModel;

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

    private Dialog loadingDialog;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_booking);

        /////loading Dialog
        loadingDialog = new Dialog(CompletedBookingActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        }
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        searchView= findViewById(R.id.searchView3);
        bookRecView = findViewById(R.id.completeBookRec);
        LinearLayoutManager l= new LinearLayoutManager(CompletedBookingActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        bookRecView.setLayoutManager(l);
        bookRecView.setHasFixedSize(true);
        bookRecView.setNestedScrollingEnabled(false);
        bookAdapter = new BookingAdapter(CompletedBookingActivity.this, bookingList);
        bookRecView.setAdapter(bookAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bookAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog.show();
        if(bookingList.size()>0){
            bookingList.clear();
        }
        FirebaseDatabase.getInstance().getReference().child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot k: dataSnapshot.getChildren()){
                    if((String.valueOf(k.child("book_city").getValue()).equals("Jamshedpur"))){
                        if ((String.valueOf(k.child("book_status").getValue()).equals("Ready"))
                                && (String.valueOf(k.child("book_professional_id").getValue())).equals("")) {
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
                        } else {
                        }
                    } else {
                    }
                }
                Collections.sort(bookingList, new SortByBookTime());
                bookAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CompletedBookingActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
}

class SortByBookTime implements Comparator<BookingModel> {

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
    }

}
