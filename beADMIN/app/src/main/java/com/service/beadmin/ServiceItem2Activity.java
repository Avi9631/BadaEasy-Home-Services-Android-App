package com.service.beadmin;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.service.beadmin.Adapters.ServiceAdapter;
import com.service.beadmin.Models.ServicesModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceItem2Activity extends AppCompatActivity {

    private RecyclerView serviceItem2RecView;
    private ServiceAdapter service2Adapter;
    private List<ServicesModel> serviceItem2List= new ArrayList<>();

    private Button addBtn;
    private Dialog dialog;
    private EditText cat;
    private ImageView img1, img2, icon;
    private Button uploadBtn;

    private static final int PICK_IMAGE = 100;
    private Uri[] imageUri= new Uri[4];
    private List<String> downloadUrl= new ArrayList<>();
    int i=-2;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_item2);

        Toolbar toolbar= findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("key"));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////loading Dialog
        loadingDialog = new Dialog(ServiceItem2Activity.this);
        loadingDialog.setContentView(R.layout.loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        }
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        addBtn= findViewById(R.id.add_btn_2);
        serviceItem2RecView = findViewById(R.id.serviceitem2_rec);
        LinearLayoutManager l= new LinearLayoutManager(ServiceItem2Activity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        serviceItem2RecView.setLayoutManager(l);
        serviceItem2RecView.setNestedScrollingEnabled(false);
        serviceItem2RecView.setHasFixedSize(true);

        ///// Dialog
        dialog = new Dialog(ServiceItem2Activity.this);
        dialog.setContentView(R.layout.dialog_ph_2);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        cat= dialog.findViewById(R.id.d1_cat);
        img1= dialog.findViewById(R.id.d1_img_1);
        img2= dialog.findViewById(R.id.d1_img_2);
        icon= dialog.findViewById(R.id.d1_icon);
        uploadBtn= dialog.findViewById(R.id.d1_uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(0);
            }
        });
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(1);
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(2);
            }
        });
        ///// Dialog

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        if (getIntent().getStringExtra("type").equals("3")){
            loadDataforType3();
        }
        else if (getIntent().getStringExtra("type").equals("2")){
            loadDataforType2();
        }

    }

    private void loadDataforType2() {
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Services").child(getIntent().getStringExtra("key"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if (dataSnapshot1.hasChildren()  && !dataSnapshot1.getKey().toString().equals("comments")) {
                        serviceItem2List.add(new ServicesModel(String.valueOf(dataSnapshot1.child("icon").getValue()),
                                String.valueOf(dataSnapshot1.child("stitle").getValue()),
                                String.valueOf(dataSnapshot1.getKey()),
                                getIntent().getStringExtra("key"), getIntent().getStringExtra("type"),
                                getIntent().getStringExtra("title"), "", ""));
                    }
                }
                service2Adapter = new ServiceAdapter(ServiceItem2Activity.this, serviceItem2List, "Service2");
                serviceItem2RecView.setAdapter(service2Adapter);
                service2Adapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    private void loadDataforType3() {
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Services").child(getIntent().getStringExtra("previouskey"))
                .child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if (dataSnapshot1.hasChildren()) {
                        serviceItem2List.add(new ServicesModel(String.valueOf(dataSnapshot1.child("icon").getValue()),
                                String.valueOf(dataSnapshot1.child("stitle").getValue()),
                                String.valueOf(dataSnapshot1.getKey()),
                                getIntent().getStringExtra("key"), getIntent().getStringExtra("previouskey"),
                                getIntent().getStringExtra("title"), getIntent().getStringExtra("title2"),""));
                    }
                }
                service2Adapter = new ServiceAdapter(ServiceItem2Activity.this, serviceItem2List, "Service2");
                serviceItem2RecView.setAdapter(service2Adapter);
                service2Adapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    private void uploadData(){
        loadingDialog.dismiss();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference();
        for(int i=0; i<3; i++) {
            final StorageReference imageReference = storageReference.child("services3").child(imageUri[i].getLastPathSegment());
            final UploadTask uploadTask = imageReference.putFile(imageUri[i]);
            final int finalI = i;
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUrl.add(task.getResult().toString());
                                if(finalI == 2) {
                                    if (getIntent().getStringExtra("type").equals("3")){
                                        uploadPostNameIF();
                                    }
                                    else if (getIntent().getStringExtra("type").equals("2")){
                                        uploadPostNameELSE();
                                    }
                                }
                            } else {
                                Toast.makeText(ServiceItem2Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        }
                    });
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                    } else {
                        Toast.makeText(ServiceItem2Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    private void uploadPostNameIF() {
        loadingDialog.show();
        Map<String,Object> map = new HashMap<>();
        map.put("stitle", cat.getText().toString().trim());
        map.put("icon", downloadUrl.get(0));
        map.put("Img_1", downloadUrl.get(1));
        map.put("Img_2", downloadUrl.get(2));

        FirebaseDatabase.getInstance().getReference().child("Services").child(getIntent().getStringExtra("previouskey"))
                .child(getIntent().getStringExtra("key"))
                .child(cat.getText().toString())
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    finish();
                    loadingDialog.dismiss();
                } else {
                    Toast.makeText(ServiceItem2Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }
        });

    }

    private void uploadPostNameELSE() {
        loadingDialog.show();
        Map<String,Object> map = new HashMap<>();
        map.put("stitle", cat.getText().toString().trim());
        map.put("icon", downloadUrl.get(0));
        map.put("Img_1", downloadUrl.get(1));
        map.put("Img_2", downloadUrl.get(2));

        FirebaseDatabase.getInstance().getReference().child("Services")
                .child(getIntent().getStringExtra("key"))
                .child(cat.getText().toString())
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    finish();
                    loadingDialog.dismiss();
                } else {
                    Toast.makeText(ServiceItem2Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }
        });

    }


    private void openGallery(int p) {
        i= p;
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri[i] = data.getData();
            switch (i){
                case 0:icon.setImageURI(imageUri[0]);break;
                case 1:img1.setImageURI(imageUri[1]);break;
                case 2:img2.setImageURI(imageUri[2]);break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        serviceItem2List.clear();
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
