package com.service.badaeasyprofessional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasyprofessional.Adapters.GridAdapter;
import com.service.badaeasyprofessional.Models.HomeModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static String proID, proName, proEmail, proCurrOrderId, proSwitch, proDeviceDetail;

    private RecyclerView homeRecyclerView;
    private GridAdapter homeAdapter;
    private List<HomeModel> homeList = new ArrayList<>();

    private SwitchCompat switchDuty;
    private String sp;
    private Button helpCenter;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /////loading Dialog
        loadingDialog = new Dialog(HomeActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        switchDuty= findViewById(R.id.switchDuty);
        helpCenter= findViewById(R.id.call_help_cntr);
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                .child(HomeActivity.proID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String s= String.valueOf(dataSnapshot.child("dutyStart").getValue());
                        if(s.equals("YES")){
                            proSwitch= "YES";
                            switchDuty.setChecked(true);
                            loadingDialog.dismiss();
                        }
                        else {
                            proSwitch= "NO";
                            switchDuty.setChecked(false);
                            loadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });

        switchDuty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loadingDialog.show();
                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                if(isChecked){
                    proSwitch= "YES";
                    FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                            .child(HomeActivity.proID).child("dutyStart").setValue("YES")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                            .child(HomeActivity.proID)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    sp= String.valueOf(dataSnapshot.child("dutyTime").getValue());
                                                    FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                                            .child(HomeActivity.proID).child("dutyTime").setValue(sp+timestamp+"&")
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
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
                            });

                } else {
                    proSwitch= "NO";
                    FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                            .child(HomeActivity.proID).child("dutyStart").setValue("NO")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                            .child(HomeActivity.proID)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    sp= String.valueOf(dataSnapshot.child("dutyTime").getValue());
                                                    FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                                            .child(HomeActivity.proID).child("dutyTime").setValue(sp+timestamp+";")
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
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
                            });
                }
            }
        });


        homeRecyclerView = findViewById(R.id.home_rec);
        homeRecyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setNestedScrollingEnabled(false);
        homeAdapter = new GridAdapter(HomeActivity.this, homeList);
        homeRecyclerView.setAdapter(homeAdapter);

        homeList.add(new HomeModel("Upcoming Orders"));
        homeList.add(new HomeModel("Ongoing Orders"));
        homeList.add(new HomeModel("Onhold Orders"));
        homeList.add(new HomeModel("Completed Orders"));
        homeList.add(new HomeModel("My Profile"));
        homeList.add(new HomeModel("My Dashboard"));

        homeAdapter.notifyDataSetChanged();

        helpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+"9631045873"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                        return;
                    }
                    startActivity(i);
                }
                else{
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        homeList.clear();
    }
}
