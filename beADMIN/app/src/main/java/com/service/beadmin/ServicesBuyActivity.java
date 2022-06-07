package com.service.beadmin;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.beadmin.Adapters.ServiceBuyAdapter;
import com.service.beadmin.Models.ServiceBuyModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesBuyActivity extends AppCompatActivity {

    private RecyclerView serviceBuyRecView;
    private ServiceBuyAdapter serviceBuyAdapter;
    private List<ServiceBuyModel> serviceBuyList= new ArrayList<>();

    private Button uploadBtn;
    private EditText title, mrp, des, price;
    private Button addBtn;
    private Dialog dialog;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_buy);

        Toolbar toolbar= findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////loading Dialog
        loadingDialog = new Dialog(ServicesBuyActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        }
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        addBtn= findViewById(R.id.add_btn_buy);
        serviceBuyRecView = findViewById(R.id.buy_rec_view);
        LinearLayoutManager l= new LinearLayoutManager(ServicesBuyActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        serviceBuyRecView.setLayoutManager(l);
        serviceBuyRecView.setNestedScrollingEnabled(false);
        serviceBuyRecView.setHasFixedSize(false);

        ///// Dialog
        dialog = new Dialog(ServicesBuyActivity.this);
        dialog.setContentView(R.layout.dialog_ph_buy);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        title= dialog.findViewById(R.id.d1_cat);
        mrp= dialog.findViewById(R.id.d1_type);
        price= dialog.findViewById(R.id.d3_price);
        des= dialog.findViewById(R.id.d3_desc);
        uploadBtn= dialog.findViewById(R.id.d1_uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("verypreviouskey").equals("2")){
                    uploadPostNameIF();
                }
                else{
                    uploadPostNameELSE();
                }
            }
        });
        ///// Dialog

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        if (getIntent().getStringExtra("verypreviouskey").equals("2")){
            loadDataBuyIf();
        }
        else{
            loadDataBuyElse();
        }

    }

    private void loadDataBuyIf() {
        loadingDialog.show();
        getSupportActionBar().setTitle(getIntent().getStringExtra("key"));
        FirebaseDatabase.getInstance().getReference().child("Services")
                .child(getIntent().getStringExtra("previouskey"))
                .child(getIntent().getStringExtra("key"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot f: dataSnapshot.getChildren()){
                            if (f.hasChildren()) {
                                serviceBuyList.add(new ServiceBuyModel(String.valueOf(f.child("mtitle").getValue()),
                                        String.valueOf(f.child("mprice").getValue()),
                                        String.valueOf(f.child("mMRP").getValue()),
                                        String.valueOf(f.child("mDes").getValue()),
                                        "",
                                        getIntent().getStringExtra("title"),
                                        getIntent().getStringExtra("title2"),
                                        getIntent().getStringExtra("title3")));
                            }
                        }
                        serviceBuyAdapter = new ServiceBuyAdapter(ServicesBuyActivity.this, serviceBuyList);
                        serviceBuyRecView.setAdapter(serviceBuyAdapter);
                        serviceBuyAdapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });
    }

    private void loadDataBuyElse() {
        loadingDialog.show();
        getSupportActionBar().setTitle(getIntent().getStringExtra("key"));
        FirebaseDatabase.getInstance().getReference().child("Services").child(getIntent().getStringExtra("verypreviouskey"))
            .child(getIntent().getStringExtra("previouskey")).child(getIntent().getStringExtra("key"))
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot f: dataSnapshot.getChildren()){
                        if (f.hasChildren()) {
                            serviceBuyList.add(new ServiceBuyModel(String.valueOf(f.child("mtitle").getValue()),
                                    String.valueOf(f.child("mprice").getValue()),
                                    String.valueOf(f.child("mMRP").getValue()),
                                    String.valueOf(f.child("mDes").getValue()),
                                    "",
                                    getIntent().getStringExtra("title"),
                                    getIntent().getStringExtra("title2"),
                                    getIntent().getStringExtra("title3")));
                        }
                    }
                    serviceBuyAdapter = new ServiceBuyAdapter(ServicesBuyActivity.this, serviceBuyList);
                    serviceBuyRecView.setAdapter(serviceBuyAdapter);
                    serviceBuyAdapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingDialog.dismiss();
                }
            });
    }


    private void uploadPostNameIF() {
        loadingDialog.show();
        Map<String,Object> map = new HashMap<>();
        map.put("mtitle", title.getText().toString().trim());
        map.put("mDes", des.getText().toString().trim());
        map.put("mMRP", "Rs."+mrp.getText().toString().trim()+"/-");
        map.put("mprice", "Rs."+price.getText().toString().trim()+"/-");

        FirebaseDatabase.getInstance().getReference().child("Services")
                .child(getIntent().getStringExtra("previouskey"))
                .child(getIntent().getStringExtra("key"))
                .child(title.getText().toString().trim())
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    finish();
                    loadingDialog.dismiss();
                } else {
                    Toast.makeText(ServicesBuyActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void uploadPostNameELSE() {
        loadingDialog.show();
        Map<String,Object> map = new HashMap<>();
        map.put("mtitle", title.getText().toString().trim());
        map.put("mDes", des.getText().toString().trim());
        map.put("mMRP", mrp.getText().toString().trim());
        map.put("mprice", price.getText().toString().trim());

        FirebaseDatabase.getInstance().getReference().child("Services")
                .child(getIntent().getStringExtra("verypreviouskey"))
                .child(getIntent().getStringExtra("previouskey"))
                .child(getIntent().getStringExtra("key"))
                .child(title.getText().toString().trim())
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    finish();
                    loadingDialog.dismiss();
                } else {
                    Toast.makeText(ServicesBuyActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        serviceBuyList.clear();
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
