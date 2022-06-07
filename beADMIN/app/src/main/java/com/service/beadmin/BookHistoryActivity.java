package com.service.beadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.beadmin.Adapters.BookingAdapter;
import com.service.beadmin.Models.BookingModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BookHistoryActivity extends AppCompatActivity {

    private RecyclerView bookRecView;
    private BookingAdapter bookAdapter;
    private List<BookingModel> bookingList= new ArrayList<>();

    private Dialog loadingDialog;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_history);

        /////loading Dialog
        loadingDialog = new Dialog(BookHistoryActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        }
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        searchView= findViewById(R.id.searchView);
        bookRecView = findViewById(R.id.hist_book);
        LinearLayoutManager l= new LinearLayoutManager(BookHistoryActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        bookRecView.setLayoutManager(l);
        bookRecView.setHasFixedSize(true);
        bookRecView.setNestedScrollingEnabled(false);
        bookAdapter = new BookingAdapter(BookHistoryActivity.this, bookingList);
        bookRecView.setAdapter(bookAdapter);
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
                        if ((String.valueOf(k.child("book_status").getValue()).equals(getIntent().getStringExtra("type")))
                                && !(String.valueOf(k.child("book_professional_id").getValue())).equals("")) {
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
                loadingDialog.dismiss();
            }
        });

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
}
