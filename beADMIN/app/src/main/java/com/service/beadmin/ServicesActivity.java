package com.service.beadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
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

public class ServicesActivity extends AppCompatActivity {

    private RecyclerView homeRecyclerView;
    private ServiceAdapter homeAdapter;
    private List<ServicesModel> homeList = new ArrayList<>();
    private Button addBtn;
    private Dialog dialog;
    private EditText cat, type;
    private ImageView homeImg, img1, img2, icon;
    private Button uploadBtn;

    private static final int PICK_IMAGE = 100;
    private Uri[] imageUri= new Uri[4];
    private List<String> downloadUrl= new ArrayList<>();
    int i=-2;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        /////loading Dialog
        loadingDialog = new Dialog(ServicesActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        }
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        addBtn= findViewById(R.id.add_serv_btn);
        homeRecyclerView = findViewById(R.id.service_rec);
        homeRecyclerView.setLayoutManager(new GridLayoutManager(ServicesActivity.this, 3));
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setNestedScrollingEnabled(false);
        homeAdapter = new ServiceAdapter(ServicesActivity.this, homeList, "Home");
        homeRecyclerView.setAdapter(homeAdapter);

        ///// Dialog
        dialog = new Dialog(ServicesActivity.this);
        dialog.setContentView(R.layout.dialog_ph_1);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        cat= dialog.findViewById(R.id.d1_cat);
        type= dialog.findViewById(R.id.d1_type);
        homeImg= dialog.findViewById(R.id.d1_home_banner);
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
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(1);
            }
        });
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(2);
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(3);
            }
        });
        ///// Dialog

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    homeList.add(new ServicesModel(String.valueOf(d.child("icon").getValue()),
                            String.valueOf(d.child("cattitle").getValue()),
                            String.valueOf(d.getKey()), String.valueOf(d.child("type").getValue()), "", "",
                            "", ""));
                }
                homeAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void uploadData(){
        loadingDialog.show();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference();
        for(int i=0; i<4; i++) {
            final StorageReference imageReference = storageReference.child("services1").child(imageUri[i].getLastPathSegment());
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
                                if(finalI == 3) {
                                    uploadPostName();
                                }
                            } else {
                                Toast.makeText(ServicesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ServicesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    private void uploadPostName() {
        loadingDialog.show();
        Map<String,Object> map = new HashMap<>();
        map.put("cattitle", cat.getText().toString().trim());
        map.put("type", type.getText().toString().trim());
        map.put("icon", downloadUrl.get(0));
        map.put("homeIMG", downloadUrl.get(1));
        map.put("Img_1", downloadUrl.get(2));
        map.put("Img_2", downloadUrl.get(3));

        FirebaseDatabase.getInstance().getReference().child("Services").child(cat.getText().toString())
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    finish();
                    loadingDialog.dismiss();
                } else {
                    Toast.makeText(ServicesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                case 1:homeImg.setImageURI(imageUri[1]);break;
                case 2:img1.setImageURI(imageUri[2]);break;
                case 3:img2.setImageURI(imageUri[3]);break;
            }
        }
    }

}
