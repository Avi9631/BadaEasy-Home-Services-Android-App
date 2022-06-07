package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasy.Models.ServicesModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.service.badaeasy.HomeFragment.homeList;

public class ProfileActivity extends AppCompatActivity {

    private Button profileBtn;
    private EditText profileName, profileMobile, profileDate;
    private Spinner profileLoc;

    private Dialog loadingDialog;
    private List<String> slist= new ArrayList<>();
    private String str="";
    private String selectedState, st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /////loading Dialog
        loadingDialog = new Dialog(ProfileActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        profileName= findViewById(R.id.profile_name);
        profileMobile= findViewById(R.id.profile_mobile);
        profileDate= findViewById(R.id.profile_date);
        profileBtn= findViewById(R.id.profile_btn);
        profileLoc= findViewById(R.id.spinner);

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Branches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    String val=String.valueOf(d.getKey());
                    for(DataSnapshot l: dataSnapshot.child(val).getChildren()){
                        slist.add(String.valueOf(l.getKey())+"/"+val);
                    }
                }
                addSpinner();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });

        profileMobile.setText("+91");
        Selection.setSelection(profileMobile.getText(), profileMobile.getText().length());
        profileMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("+91")){
                    profileMobile.setText("+91");
                    Selection.setSelection(profileMobile.getText(), profileMobile.getText().length());
                }
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void addSpinner() {

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, slist);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profileLoc.setAdapter(spinnerAdapter);
        profileLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedState = slist.get(position).substring(0,slist.get(position).indexOf("/") );
                st= slist.get(position).substring(slist.get(position).indexOf("/")+1 );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void validate() {
        if((!TextUtils.isEmpty(profileName.getText().toString()))){
            if(android.util.Patterns.PHONE.matcher(profileMobile.getText().toString()).matches()){
                if(profileDate.getText().toString().matches("^(1[0-9]|0[1-9]|3[0-1]|2[1-9])/(0[1-9]|1[0-2])/[0-9]{4}$") && !TextUtils.isEmpty(profileDate.getText().toString())){
                        registerUser();
                }else{
                    Toast.makeText(this, "Invalid Date of Birth", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Invalid Mobile no.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Invalid Name", Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        FirebaseDatabase.getInstance().getReference().child("RewardRates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                str= String.valueOf(dataSnapshot.child("1stOrderReward").getValue());

                Map<String, Object> userMap= new HashMap<>();
                userMap.put("userEmail", getIntent().getStringExtra("email"));
                userMap.put("userPassword", getIntent().getStringExtra("password"));
                userMap.put("userName", profileName.getText().toString().trim());
                userMap.put("userMobile", profileMobile.getText().toString().trim());
                userMap.put("userLocation", selectedState.trim());
                userMap.put("userState", st.trim());
                userMap.put("userDOB", profileDate.getText().toString().trim());
                userMap.put("userLastSeen", ServerValue.TIMESTAMP);
                userMap.put("userCallReq", "false");
                userMap.put("rewards", str+",");

                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("address").child("totAddress").setValue("0")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loadUserdata();
                                        loadingDialog.dismiss();
                                    }
                                });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });

    }

    private void loadUserdata(){
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
                            String.valueOf(d.getKey()), String.valueOf(d.child("type").getValue()), "", "", "", ""));
                }
                Intent i= new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void registerUser(){
        loadingDialog.show();
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(getIntent().getStringExtra("email"), getIntent().getStringExtra("password"))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            register();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

}
