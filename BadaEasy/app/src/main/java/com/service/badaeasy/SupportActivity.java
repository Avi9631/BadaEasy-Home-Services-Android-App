package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SupportActivity extends AppCompatActivity {

    private EditText mobile;
    private Button callNow, cancelCallReq;
    private TextView callReqNoti;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        Toolbar toolbar= findViewById(R.id.toolbarSupport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Customer Support");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////loading Dialog
        loadingDialog = new Dialog(SupportActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        mobile= findViewById(R.id.noTxt);
        callNow= findViewById(R.id.callmenowBtn);
        callReqNoti= findViewById(R.id.callReqNotif);
        cancelCallReq= findViewById(R.id.cancelcallReq);

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String val= String.valueOf(dataSnapshot.child("userCallReq").getValue());
                        validate(val);
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });

        mobile.setText("+91");
        Selection.setSelection(mobile.getText(), mobile.getText().length());

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("+91")){
                    mobile.setText("+91");
                    Selection.setSelection(mobile.getText(), mobile.getText().length());
                }
            }
        });

        callNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("userCallReq").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String uuid= UUID.randomUUID().toString();
                        Map<String, Object> map=new HashMap<>();
                        map.put("userID", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        map.put("phone", mobile.getText().toString());
                        map.put("timeReq", ServerValue.TIMESTAMP);
                        map.put("exeID","");
                        map.put("timeRes", "");
                        map.put("resStatus", "Pending");
                        FirebaseDatabase.getInstance().getReference().child("CallCustReq").child(uuid)
                                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(SupportActivity.this, "Request Placed Successfully", Toast.LENGTH_SHORT).show();
                                validate("true");
                                loadingDialog.dismiss();
                            }
                        });
                    }
                });

            }
        });

        cancelCallReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();

                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("userCallReq").setValue("false")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference().child("CallCustReq").orderByChild("userID")
                                        .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot d: dataSnapshot.getChildren()){
                                                    if(String.valueOf(d.child("resStatus").getValue()).equals("Pending")){
                                                        FirebaseDatabase.getInstance().getReference().child("CallCustReq")
                                                                .child(String.valueOf(d.getKey())).removeValue()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        validate("false");
                                                                        loadingDialog.dismiss();
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        });
            }
        });


    }

    private void validate(String val){
        if(val.equals("true")){
            callReqNoti.setVisibility(View.VISIBLE);
            cancelCallReq.setVisibility(View.VISIBLE);
            mobile.setVisibility(View.GONE);
            callNow.setVisibility(View.GONE);
        } else {
            callReqNoti.setVisibility(View.GONE);
            cancelCallReq.setVisibility(View.GONE);
            mobile.setVisibility(View.VISIBLE);
            callNow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
