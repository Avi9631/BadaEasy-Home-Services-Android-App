package com.service.badaeasy;


import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasy.Adapters.ProAdapter;
import com.service.badaeasy.Models.ProModel;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView proName, proEmail, proDOB, proMobile;

    private RecyclerView proRecyclerView;
    private ProAdapter proAdapter;
    public static List<ProModel> proList = new ArrayList<>();

    private Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        /////loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        proName= view.findViewById(R.id.pro_name);
        proEmail= view.findViewById(R.id.pro_email);
        proDOB= view.findViewById(R.id.pro_dob);
        proMobile= view.findViewById(R.id.pro_mobile);

        proName.setText(UserData.userName);
        proEmail.setText("Email: "+UserData.userEmail);
        proMobile.setText("Mobile: "+UserData.userMobile);
        proDOB.setText("DOB: "+UserData.userDOB);

        proRecyclerView = view.findViewById(R.id.pro_option_rec);
        proRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        proRecyclerView.setHasFixedSize(true);
        proRecyclerView.setNestedScrollingEnabled(false);
        proList.add(new ProModel("Rewards", R.drawable.rewardicon));
//        proList.add(new ProModel("Ready Bookings", R.drawable.ready));
//        proList.add(new ProModel("Ongoing Bookings", R.drawable.ongoing));
//        proList.add(new ProModel("Onhold Bookings", R.drawable.ongoing));
        proList.add(new ProModel("My Booking", R.drawable.completed));
        proList.add(new ProModel("Support", R.drawable.completed));
        proList.add(new ProModel("Log out", R.drawable.logout));
        proList.add(new ProModel("Saved Address", R.drawable.logout));
        proAdapter = new ProAdapter(getContext(), proList);
        proRecyclerView.setAdapter(proAdapter);
        proAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        proList.clear();
    }

    @Override
    public void onStart() {
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
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });
    }
}
