package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    private Spinner city;
    private EditText locality;
    private EditText flatNo;
    private EditText pincode;
    private EditText landmark;
    private Spinner stateSpinner;
    private Button saveBtn;

    private String selectedState;
    private List<String> slist= new ArrayList<>();

    private String selectedCity;
    private List<String> cityList= new ArrayList<>();

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Address");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////loading Dialog
        loadingDialog = new Dialog(InfoActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        city= findViewById(R.id.city_spinner);
        locality= findViewById(R.id.locality);
        flatNo= findViewById(R.id.flat_no);
        pincode= findViewById(R.id.pincode);
        landmark= findViewById(R.id.landmark);
        stateSpinner= findViewById(R.id.state_spinner);
        saveBtn= findViewById(R.id.save_btn);

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Branches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    slist.add(String.valueOf(d.getKey()));
                }
                addSpinner();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!TextUtils.isEmpty(selectedCity)) && (!selectedState.equals("Select Your State*")))
                {
                    if (!TextUtils.isEmpty(locality.getText()))
                    {
                        if (!TextUtils.isEmpty(flatNo.getText()))
                        {
                            if (!TextUtils.isEmpty(pincode.getText()) && pincode.getText().length() == 6)
                            {
                                loadingDialog.show();
                                final Map<String, Object> addressMap= new HashMap<>();
                                addressMap.put("city", selectedCity);
                                addressMap.put("locality", locality.getText().toString());
                                addressMap.put("flatNo", flatNo.getText().toString());
                                addressMap.put("pincode", pincode.getText().toString());
                                addressMap.put("landmark", landmark.getText().toString());
                                addressMap.put("state", selectedState);

                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("address").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final String totSaveaddress= String.valueOf(dataSnapshot.child("totAddress").getValue());
                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("address").child(String.valueOf(Integer.parseInt(totSaveaddress)+1))
                                                .setValue(addressMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                FirebaseDatabase.getInstance().getReference().child("Users")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("address")
                                                        .child("totAddress").setValue(String.valueOf(Integer.parseInt(totSaveaddress)+1))
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                finish();
                                                            }
                                                        });
                                            }
                                        });
                                        loadingDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        loadingDialog.dismiss();
                                    }
                                });

                            } else
                            {
                                pincode.requestFocus();
                                Toast.makeText(InfoActivity.this, "Please provide valid pincode", Toast.LENGTH_SHORT).show();
                            }
                        } else
                        {
                            flatNo.requestFocus();
                        }
                    } else
                    {
                        locality.requestFocus();
                    }
                } else
                {
                    city.requestFocus();
                }
            }
        });

    }

    private void addSpinner() {

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, slist);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(spinnerAdapter);
        stateSpinner.setSelection(slist.indexOf(UserData.userState));
        stateSpinner.setEnabled(false);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedState = slist.get(position);
                if(cityList.size()>0){
                    cityList.clear();
                }

                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Branches")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot k: dataSnapshot.child(selectedState).getChildren()){
                            cityList.add(String.valueOf(k.getKey()));
                        }
                        addCitySpinner();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void addCitySpinner() {
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, cityList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(spinnerAdapter);
        city.setSelection(cityList.indexOf(UserData.userLoc));
        city.setEnabled(false);
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCity = cityList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cityList.clear();
        slist.clear();
        finish();
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
                        UserData.userState=  String.valueOf(dataSnapshot.child("userState").getValue());
                        UserData.userLoc=  String.valueOf(dataSnapshot.child("userLocation").getValue());

                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });
    }
}
