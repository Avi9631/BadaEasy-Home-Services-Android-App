package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasy.Models.ServicesModel;

import static com.service.badaeasy.HomeFragment.homeList;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar= findViewById(R.id.entry_progress);
        if (isNetworkConnected()){
            FirebaseDatabase.getInstance().getReference().child("AppDetails")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(String.valueOf(dataSnapshot.child("MainInfo").getValue()).equals("NO")) {
                                if (BuildConfig.VERSION_NAME.equals(String.valueOf(dataSnapshot.child("VersionName").getValue()))) {
                                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                        loadUserdata();
                                    } else {
                                        Intent i = new Intent(MainActivity.this, AuthActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                } else {
                                    openAlert();
                                }
                            }
                            else if(String.valueOf(dataSnapshot.child("MainInfo").getValue()).equals("YES")) {
                                openMaintainanceAlert();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        else {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null  && cm.getActiveNetworkInfo().isConnected();
    }


    private void openAlert() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("Update your app for better experience");
        builder.setCancelable(false);
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "update done", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (FirebaseAuth.getInstance().getCurrentUser()!=null){
                    loadUserdata();
                }
                else{
                    Intent i= new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        AlertDialog alertDialog= builder.create();
        alertDialog.setTitle("Update Notification");
        alertDialog.show();
    }

    private void openMaintainanceAlert() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("Bada Easy Servers are under maintainance! Sorry for the inconveniance caused");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog= builder.create();
        alertDialog.setTitle("Under Maintainance");
        alertDialog.show();
    }

    private void loadUserdata(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData.userName= String.valueOf(dataSnapshot.child("userName").getValue());
                        UserData.userDOB= String.valueOf(dataSnapshot.child("userDOB").getValue());
                        UserData.userEmail= String.valueOf(dataSnapshot.child("userEmail").getValue());
                        UserData.userMobile= String.valueOf(dataSnapshot.child("userMobile").getValue());
                        UserData.userRewards=  String.valueOf(dataSnapshot.child("rewards").getValue());
                        UserData.userLoc=  String.valueOf(dataSnapshot.child("userLocation").getValue());
                        UserData.userState=  String.valueOf(dataSnapshot.child("userState").getValue());
                        loadData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void loadData() {
        FirebaseDatabase.getInstance().getReference().child("Services").child(UserData.userLoc).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    homeList.add(new ServicesModel(String.valueOf(d.child("icon").getValue()),
                            String.valueOf(d.child("cattitle").getValue()),
                            String.valueOf(d.getKey()), String.valueOf(d.child("type").getValue()), "", "",
                            "", ""));
                }
                Intent i= new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}
