package com.service.badaeasyprofessional;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class AuthActivity extends AppCompatActivity {

    private EditText authEmail, authPassword;
    private Button authSignInUp;
    private ImageView authSeePassword;
    private boolean passwordVisible = false;
    private String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    private FirebaseAuth mAuth;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

        /////loading Dialog
        loadingDialog = new Dialog(AuthActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        authEmail = findViewById(R.id.auth_email);
        authPassword = findViewById(R.id.auth_password);
        authSignInUp = findViewById(R.id.auth_btn);
        authSeePassword = findViewById(R.id.auth_viewpassword);

        authSignInUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                validate();
            }
        });

        authSeePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passwordVisible) {
                    passwordVisible = true;
                    authPassword.setTransformationMethod(null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        authSeePassword.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    }
                } else {
                    passwordVisible = false;
                    authPassword.setTransformationMethod(new PasswordTransformationMethod());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        authSeePassword.setImageTintList(ColorStateList.valueOf(Color.parseColor("#d6d6d6")));
                    }
                }
            }
        });

    }

    private void validate() {
        if (authEmail.getText().toString().trim().matches(emailRegex)) {
            if (authPassword.getText().toString().trim().length() >= 8) {
                loginUser("proEmail");
            } else {
                Toast.makeText(this, "Password should contain minimum of 8 characters", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        } else if (!authEmail.getText().toString().trim().equals("")) {
            if (authPassword.getText().toString().trim().length() >= 8) {
                loginUser("proID");
            } else {
                Toast.makeText(this, "Password should contain minimum of 8 characters", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        } else {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }

    private void loginUser(final String id) {
        FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if ((authEmail.getText().toString().equals(String.valueOf(dataSnapshot1.child(id).getValue())) &&
                                    (authPassword.getText().toString().equals(String.valueOf(dataSnapshot1.child("proPass").getValue()))))) {
                                HomeActivity.proID = String.valueOf(dataSnapshot1.getKey());
                                HomeActivity.proEmail = String.valueOf(dataSnapshot1.child("proEmail").getValue());
                                HomeActivity.proName = String.valueOf(dataSnapshot1.child("proName").getValue());
                                HomeActivity.proCurrOrderId = String.valueOf(dataSnapshot1.child("curr_order_id").getValue());
                                HomeActivity.proDeviceDetail = String.valueOf(dataSnapshot1.child("deviceDetails").getValue());

                                Intent i = new Intent(AuthActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();
                                loadingDialog.dismiss();
                    }
                    else {
                        loadingDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }


    public static void loadUserData(){

        FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                .child(HomeActivity.proID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HomeActivity.proID= String.valueOf(dataSnapshot.getKey());
                HomeActivity.proEmail= String.valueOf(dataSnapshot.child("proEmail").getValue());
                HomeActivity.proName= String.valueOf(dataSnapshot.child("proName").getValue());
                HomeActivity.proCurrOrderId= String.valueOf(dataSnapshot.child("curr_order_id").getValue());
                HomeActivity.proDeviceDetail= String.valueOf(dataSnapshot.child("deviceDetails").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
