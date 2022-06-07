package com.service.badaeasy;

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
import com.service.badaeasy.Adapters.AddressAdapter;
import com.service.badaeasy.Adapters.SavedAddressAdapter;
import com.service.badaeasy.Models.AddressModel;
import com.service.badaeasy.Models.SummaryModel;

import java.util.ArrayList;
import java.util.List;

public class SavedAddressActivity extends AppCompatActivity {

    private RecyclerView summRecyclerView;
    private SavedAddressAdapter summAdapter;
    private List<AddressModel> summList=new ArrayList<>();
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_address);
        /////loading Dialog
        loadingDialog = new Dialog(SavedAddressActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        summRecyclerView = findViewById(R.id.my_rewards_recyclerview);
        LinearLayoutManager l= new LinearLayoutManager(SavedAddressActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        summRecyclerView.setLayoutManager(l);
        summRecyclerView.setHasFixedSize(true);
        summRecyclerView.setNestedScrollingEnabled(false);
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d: dataSnapshot.child("address").getChildren()){
                            if (d.hasChildren()){
                                summList.add(new AddressModel(String.valueOf(d.child("city").getValue()),
                                        String.valueOf(d.child("locality").getValue()),
                                        String.valueOf(d.child("flatNo").getValue()),
                                        String.valueOf(d.child("pincode").getValue()),
                                        String.valueOf(d.child("landmark").getValue()),
                                        UserData.userName,
                                        UserData.userMobile,
                                        "",
                                        String.valueOf(d.child("state").getValue()),
                                        false));
                            }
                        }
                        summAdapter = new SavedAddressAdapter(SavedAddressActivity.this, summList);
                        summRecyclerView.setAdapter(summAdapter);
                        summAdapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });

    }
}
