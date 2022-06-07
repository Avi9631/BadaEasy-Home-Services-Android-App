package com.service.beadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.beadmin.Adapters.SupportAdapter;
import com.service.beadmin.Adapters.UserAdapter;
import com.service.beadmin.Models.SupportModel;
import com.service.beadmin.Models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity {

    private RecyclerView userRec;
    private static  SupportAdapter userAdapter;
    private static  List<SupportModel> userModelList= new ArrayList<>();

    private static Dialog loadingDialog;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);


        loadingDialog = new Dialog(SupportActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        }
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        searchView= findViewById(R.id.searchViewSupp);
        userRec = findViewById(R.id.custReqRec);
        LinearLayoutManager l= new LinearLayoutManager(SupportActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        userRec.setLayoutManager(l);
        userRec.setHasFixedSize(true);
        userRec.setNestedScrollingEnabled(false);
        userAdapter = new SupportAdapter(SupportActivity.this, userModelList);
        userRec.setAdapter(userAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog.show();

        loadSupportData();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    public static void loadSupportData() {
        if(userModelList.size()>0){
            userModelList.clear();
        }
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("CallCustReq").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot k: dataSnapshot.getChildren()){
                    if(!String.valueOf(k.child("resStatus").getValue()).equals("Done")) {
                        userModelList.add(new SupportModel(String.valueOf(k.getKey()),
                                String.valueOf(k.child("exeID").getValue()),
                                String.valueOf(k.child("userID").getValue()),
                                String.valueOf(k.child("phone").getValue()),
                                String.valueOf(k.child("resStatus").getValue()),
                                String.valueOf(k.child("timeReq").getValue()),
                                String.valueOf(k.child("timeRes").getValue())
                        ));
                    }
                }
                userAdapter.notifyDataSetChanged();
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
        if (userModelList.size()>0) {
            userModelList.clear();
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
