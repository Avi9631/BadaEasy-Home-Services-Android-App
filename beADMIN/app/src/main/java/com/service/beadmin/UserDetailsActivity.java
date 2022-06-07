package com.service.beadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.beadmin.Adapters.UserAdapter;
import com.service.beadmin.Models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsActivity extends AppCompatActivity {

    private RecyclerView userRec;
    private UserAdapter userAdapter;
    private List<UserModel> userModelList= new ArrayList<>();

    private Dialog loadingDialog;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        //////loading Dialog
        loadingDialog = new Dialog(UserDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        }
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        searchView= findViewById(R.id.searchView2);
        userRec = findViewById(R.id.userRec);
        LinearLayoutManager l= new LinearLayoutManager(UserDetailsActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        userRec.setLayoutManager(l);
        userRec.setHasFixedSize(true);
        userRec.setNestedScrollingEnabled(false);
        userAdapter = new UserAdapter(UserDetailsActivity.this, userModelList);
        userRec.setAdapter(userAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog.show();
        if(userModelList.size()>0){
            userModelList.clear();
        }
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot k: dataSnapshot.getChildren()){
                    userModelList.add(new UserModel(String.valueOf(k.child("userName").getValue()),
                            String.valueOf(k.child("userEmail").getValue()),
                            String.valueOf(k.child("userMobile").getValue()),
                            String.valueOf(k.getKey())));
                }
                userAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (userModelList.size()>0) {
            userModelList.clear();
        }
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
