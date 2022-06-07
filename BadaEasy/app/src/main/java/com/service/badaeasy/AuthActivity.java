package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
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
import com.service.badaeasy.Models.ServicesModel;
import static com.service.badaeasy.HomeFragment.homeList;

public class AuthActivity extends AppCompatActivity {

    private EditText authEmail, authPassword;
    private TextView authForgotPassword;
    private Button authSignInUp;
    private ImageView authSeePassword;
    private boolean passwordVisible= false;
    private String emailRegex= "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    private FirebaseAuth mAuth;

    private Dialog loadingDialog;
    private Dialog dialog;

    private EditText emailReset;
    private Button emailResetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        /////loading Dialog
        loadingDialog = new Dialog(AuthActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        mAuth= FirebaseAuth.getInstance();

        authEmail= findViewById(R.id.auth_email);
        authPassword= findViewById(R.id.auth_password);
        authSignInUp= findViewById(R.id.auth_btn);
        authSeePassword= findViewById(R.id.auth_viewpassword);
        authForgotPassword= findViewById(R.id.auth_forgot_password);

        authSignInUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        authSeePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!passwordVisible){
                    passwordVisible= true;
                    authPassword.setTransformationMethod(null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        authSeePassword.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    }
                }
                else{
                    passwordVisible= false;
                    authPassword.setTransformationMethod(new PasswordTransformationMethod());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        authSeePassword.setImageTintList(ColorStateList.valueOf(Color.parseColor("#d6d6d6")));
                    }
                }
            }
        });

        ////////////////////////////////////////////////////////// dialog
        dialog= new Dialog(AuthActivity.this);
        dialog.setContentView(R.layout.dialog_forgot_password);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        emailReset= dialog.findViewById(R.id.emailk_reset);
        emailResetBtn= dialog.findViewById(R.id.email_reset_btn);

        emailResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailReset.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    dialog.dismiss();
                                    Toast.makeText(AuthActivity.this, "Email has been sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        ///////////////////////////////////////////////////////// Dialog
        
        authForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void validate() {
        if (authEmail.getText().toString().trim().matches(emailRegex)){
            if (authPassword.getText().toString().trim().length() >= 8){
                registerUser();
            }else{
                Toast.makeText(this, "Password should contain minimum of 8 characters", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Invalid email id", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count =0;
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if (String.valueOf(dataSnapshot1.child("userEmail").getValue()).equals(authEmail.getText().toString().trim())){
                        count++;
                    }
                }
                if (count ==1){
                    signIN();
                }
                else{
                    signUP();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    private void signIN() {
        loadingDialog.show();
        mAuth.signInWithEmailAndPassword(authEmail.getText().toString().trim(), authPassword.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            loadUserdata();
                        } else {
                            Toast.makeText(AuthActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

    private void signUP() {
        Intent i= new Intent(AuthActivity.this, ProfileActivity.class);
        i.putExtra("email", authEmail.getText().toString().trim());
        i.putExtra("password", authPassword.getText().toString().trim());
        startActivity(i);
        finish();
    }

    private void loadUserdata(){
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData.userName= String.valueOf(dataSnapshot.child("userName").getValue());
                        UserData.userDOB= String.valueOf(dataSnapshot.child("userDOB").getValue());
                        UserData.userEmail= String.valueOf(dataSnapshot.child("userEmail").getValue());
                        UserData.userMobile= String.valueOf(dataSnapshot.child("userMobile").getValue());
                        loadData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });
    }

    private void loadData() {
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    homeList.add(new ServicesModel(String.valueOf(d.child("icon").getValue())
                            , String.valueOf(d.child("cattitle").getValue()),
                            String.valueOf(d.getKey()), String.valueOf(d.child("type").getValue()),
                            "", "", "", ""));
                }
                Intent i= new Intent(AuthActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

}
