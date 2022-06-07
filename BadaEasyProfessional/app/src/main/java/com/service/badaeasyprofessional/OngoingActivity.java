package com.service.badaeasyprofessional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class OngoingActivity extends AppCompatActivity {

    private TextView bid, serviceName, orderPrice, itemPrice, qty, pSmt1Text, pSmt1, pSmt2Text, pSmt2
            ,catText, cat, bookTimeText, bookTime, status, comTime, comTimeText, schTimeText, schTime, onholdReasonText, onholdReason
            ,itemSubText, itemSub, itemSubReasonText, itemSubReason, addressText, address, payModeText, payMode, payStatusText
            ,payStatus, partUseText, partUse, totPriceText, totPrice, profName, profNameText, profAboutText, profAbout
            ,itemNameList, itemPriceList, profHead;
    private CircleImageView profPic;
    private String profID;

    private Spinner payModeSpin, payStatusSpin;
    private Button callBtn, reqParts;
    private Button completeBtn, pStmt2Btn, onholdBTN, subFHoldBtn;
    private String uidStr, totPriceStr, cat1Str, cat2Str, cat3Str;
    private EditText pStmt2Edit, partEdit;

    private String[] paymodeList;
    private String selectedMode;
    private String[] paystatusList;
    private String selectedStatus;
    private String no;
    private String bidStr;

    private Dialog dialog;
    private TextView rAccept, iAccept, rHolding;
    private Spinner acceptSpinner;
    private String[] aSpin;
    private String aSpinStatus;
    private LinearLayout linlay1;

    private List<String> list1=new ArrayList<>();
    private List<String> list2=new ArrayList<>();

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);

        /////loading Dialog
        loadingDialog = new Dialog(OngoingActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        Toolbar toolbar= findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Ongoing Bookings");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bid=findViewById(R.id.d_bid);
        serviceName=findViewById(R.id.d_service_name);
        orderPrice=findViewById(R.id.d_order_price);
        itemPrice=findViewById(R.id.d_item_price);
        qty=findViewById(R.id.d_qty);
        pSmt1Text=findViewById(R.id.d_prbstmt_1_text);
        pSmt1=findViewById(R.id.d_prbstmt_1);
        pSmt2Text=findViewById(R.id.d_prbstmt_2_text);
        pSmt2=findViewById(R.id.d_prbstmt_2);
        catText=findViewById(R.id.d_category_text);
        cat=findViewById(R.id.d_category);
        bookTimeText=findViewById(R.id.d_book_time_text);
        bookTime=findViewById(R.id.d_book_time);
        status=findViewById(R.id.d_status);
        comTime=findViewById(R.id.d_complete_time);
        comTimeText=findViewById(R.id.d_complete_time_text);
        schTimeText=findViewById(R.id.d_sch_time_text);
        schTime=findViewById(R.id.d_sch_time);
        onholdReasonText=findViewById(R.id.d_ohold_reason_text);
        onholdReason=findViewById(R.id.d_ohold_reason);
        itemSubText=findViewById(R.id.d_item_submit_text);
        itemSub=findViewById(R.id.d_item_submit);
        itemSubReasonText=findViewById(R.id.d_item_submit_reason_text);
        itemSubReason=findViewById(R.id.d_item_submit_reason);
        addressText=findViewById(R.id.d_address_text);
        address=findViewById(R.id.d_address);
        payModeText=findViewById(R.id.d_pay_mode_text);
        payMode=findViewById(R.id.d_pay_mode);
        payStatusText=findViewById(R.id.d_pay_status_text);
        payStatus=findViewById(R.id.d_pay_status);
        partUseText=findViewById(R.id.d_part_use_text);
        partUse=findViewById(R.id.d_part_used);
        totPriceText=findViewById(R.id.d_tot_price_text);
        totPrice=findViewById(R.id.d_tot_price);
        profNameText=findViewById(R.id.d_prof_name_text);
        profName=findViewById(R.id.d_prof_name);
        profAboutText=findViewById(R.id.d_prof_about_text);
        profAbout=findViewById(R.id.d_prof_about);
        profPic=findViewById(R.id.d_prof_pic);
        itemNameList= findViewById(R.id.d_itemName_list);
        itemPriceList= findViewById(R.id.d_itemPrice_list);
        profHead= findViewById(R.id.prof_heading);
        completeBtn= findViewById(R.id.txtup_comp_order);
        pStmt2Btn= findViewById(R.id.txtup_p1btn);
        onholdBTN= findViewById(R.id.txtup_onhold);
        callBtn= findViewById(R.id.txtup_call);
        pStmt2Edit= findViewById(R.id.txtup_p1);
        reqParts= findViewById(R.id.txtup_parts);
        partEdit= findViewById(R.id.req_parts_txt);

        if(HomeActivity.proSwitch.equals("YES")){
            callBtn.setEnabled(true);
            pStmt2Btn.setEnabled(true);
            pStmt2Edit.setEnabled(true);
            onholdBTN.setEnabled(true);
            completeBtn.setEnabled(true);
            reqParts.setEnabled(true);
            partEdit.setEnabled(true);
        }
        else{
            callBtn.setEnabled(false);
            pStmt2Btn.setEnabled(false);
            pStmt2Edit.setEnabled(false);
            onholdBTN.setEnabled(false);
            completeBtn.setEnabled(false);
            reqParts.setEnabled(false);
            partEdit.setEnabled(false);
        }

        payModeSpin= findViewById(R.id.txtup_paymode_spin);
        paymodeList = getResources().getStringArray(R.array.pay_mode);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paymodeList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payModeSpin.setAdapter(spinnerAdapter);
        payModeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedMode = paymodeList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        payStatusSpin= findViewById(R.id.txtup_paystatus_spin);
        paystatusList = getResources().getStringArray(R.array.pay_status);
        ArrayAdapter spinnerAdapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paystatusList);
        spinnerAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payStatusSpin.setAdapter(spinnerAdapter1);
        payStatusSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedStatus = paystatusList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Bookings").child(HomeActivity.proCurrOrderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bidStr= String.valueOf(dataSnapshot.child("book_id").getValue());
                        bid.setText("BID: "+bidStr);
                        uidStr= String.valueOf(dataSnapshot.child("serviceUserID").getValue());
                        serviceName.setText(String.valueOf(dataSnapshot.child("serviceName").getValue()));
                        totPriceStr=String.valueOf(dataSnapshot.child("serviceTotPrice").getValue());
                        orderPrice.setText(totPriceStr);
                        itemPrice.setText("Item Price: "+String.valueOf(dataSnapshot.child("servicePrice").getValue()));
                        qty.setText("Qty: "+String.valueOf(dataSnapshot.child("serviceQty").getValue()));
                        pSmt1.setText(String.valueOf(dataSnapshot.child("prob_stmt_1").getValue()));
                        pSmt2.setText(String.valueOf(dataSnapshot.child("prob_stmt_2").getValue()));
                        cat1Str= String.valueOf(dataSnapshot.child("cat1").getValue());
                        cat2Str= String.valueOf(dataSnapshot.child("cat2").getValue());
                        cat3Str= String.valueOf(dataSnapshot.child("cat3").getValue());
                        cat.setText(cat1Str+" > "+ cat2Str+" > "+cat3Str);
                        bookTime.setText(timeStampToDate(String.valueOf(dataSnapshot.child("book_time").getValue())));
                        status.setText(String.valueOf(dataSnapshot.child("book_status").getValue()));
                        comTime.setText(timeStampToDate(String.valueOf(dataSnapshot.child("book_complete_time").getValue())));
                        schTime.setText(String.valueOf(dataSnapshot.child("sch_time").getValue()));
                        onholdReason.setText(String.valueOf(dataSnapshot.child("onhold_reason").getValue()));
                        itemSub.setText(String.valueOf(dataSnapshot.child("items_submiting").getValue()));
                        itemSubReason.setText(String.valueOf(dataSnapshot.child("items_submiting_reason").getValue()));
                        no= String.valueOf(dataSnapshot.child("book_mainMobile").getValue());
                        address.setText(String.valueOf(dataSnapshot.child("book_name").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_Mobile").getValue())+"\n"+
                                no+"\n"+
                                String.valueOf(dataSnapshot.child("book_flatNo").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_locality").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_landmark").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_city").getValue())+" - "+
                                String.valueOf(dataSnapshot.child("book_pincode").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_state").getValue()));
                        payMode.setText(String.valueOf(dataSnapshot.child("payment_mode").getValue()));
                        payStatus.setText(String.valueOf(dataSnapshot.child("payment_status").getValue()));
                        partUse.setText(String.valueOf(dataSnapshot.child("book_parts").getValue()));
                        totPrice.setText(String.valueOf(dataSnapshot.child("book_price").getValue()));

                        if(!TextUtils.isEmpty(pSmt2.getText().toString())){
                            pStmt2Btn.setEnabled(false);
                        }
                        else {
                            pStmt2Btn.setEnabled(true);
                        }

                        profID=String.valueOf(dataSnapshot.child("book_professional_id").getValue());
                        loadBillSummary();

                        FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                .child(profID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                profName.setText(String.valueOf(dataSnapshot.child("proName").getValue()));
                                profAbout.setText(String.valueOf(dataSnapshot.child("proAbout").getValue()));
                                Glide.with(OngoingActivity.this)
                                        .load(String.valueOf(dataSnapshot.child("proPic").getValue()))
                                        .into(profPic);
                                Toast.makeText(OngoingActivity.this, profID, Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                loadingDialog.dismiss();
                            }
                        });

                        validate();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });


        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+no));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(OngoingActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                        return;
                    }
                    startActivity(i);
                }
                else{
                    startActivity(i);
                }
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Bookings").child(bidStr)
                        .child("payment_mode").setValue(selectedMode)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        FirebaseDatabase.getInstance().getReference().child("Bookings").child(bidStr)
                                .child("payment_status").setValue(selectedStatus)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        FirebaseDatabase.getInstance().getReference().child("Bookings").child(bidStr)
                                                .child("book_status").setValue("Completed")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                FirebaseDatabase.getInstance().getReference().child("Bookings").child(bidStr)
                                                        .child("book_complete_time").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                                                .child(HomeActivity.proID)
                                                                .child("curr_order_id").setValue("")
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        FirebaseDatabase.getInstance().getReference().child("RewardRates")
                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                        final String rRate= String.valueOf(dataSnapshot.child(cat2Str).getValue());
                                                                                        final String maxReward= String.valueOf(dataSnapshot.child("MaxReward").getValue());

                                                                                        ////////Reward
                                                                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                                                                .child(uidStr).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                                                String rewards= String.valueOf(dataSnapshot.child("rewards").getValue());
                                                                                                double p=Double.parseDouble(totPriceStr.substring(3, totPriceStr.length()-2)) * Double.parseDouble(rRate);
                                                                                                if(p >= Double.parseDouble(maxReward)) {
                                                                                                    p= Double.parseDouble(maxReward);
                                                                                                }
                                                                                                String z;
                                                                                                if((p==0)||(p==0.0)){
                                                                                                    z= rewards;
                                                                                                }else {
                                                                                                    z = rewards + String.valueOf(Math.round(p)) + ",";
                                                                                                }

                                                                                                FirebaseDatabase.getInstance().getReference().child("Users")
                                                                                                        .child(uidStr).child("rewards").setValue(z)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                HomeActivity.proCurrOrderId = "";
                                                                                                                Toast.makeText(OngoingActivity.this, "Order Completed!!", Toast.LENGTH_LONG).show();
                                                                                                                finish();
                                                                                                                loadingDialog.dismiss();
                                                                                                            }
                                                                                                        });
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                loadingDialog.dismiss();
                                                                                            }
                                                                                        });
                                                                                        /////////Reward
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                        loadingDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                    }
                });
            }
        });

        pStmt2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Bookings").child(bidStr).child("prob_stmt_2")
                        .setValue(pStmt2Edit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pSmt2.setText(pStmt2Edit.getText().toString());
                        loadBillSummary();
                        validate();
                        pStmt2Edit.setEnabled(false);
                        pStmt2Btn.setEnabled(false);
                        loadingDialog.dismiss();
                    }
                });
            }
        });

        //////////////////////////////////////////////////////////Dialog onhold
        dialog= new Dialog(OngoingActivity.this);
        dialog.setContentView(R.layout.dialog_onhold_submit);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rAccept= dialog.findViewById(R.id.item_accepting_reason);
        rHolding= dialog.findViewById(R.id.holding_reason);
        iAccept= dialog.findViewById(R.id.item_accepting);
        acceptSpinner= dialog.findViewById(R.id.accepting_spinner);
        linlay1= dialog.findViewById(R.id.hold_submit_linlay);
        subFHoldBtn= dialog.findViewById(R.id.submit_final_hold_btn);


        aSpin = getResources().getStringArray(R.array.ac_spin);
        ArrayAdapter spinnerAdapter3 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, aSpin);
        spinnerAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acceptSpinner.setAdapter(spinnerAdapter3);
        acceptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                aSpinStatus = aSpin[position];
                if(aSpinStatus.equals("YES")){
                    linlay1.setVisibility(View.VISIBLE);
                }
                else{
                    linlay1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //////////////////////////////////////////////////Dialog onhold

        onholdBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        subFHoldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Bookings").child(bidStr)
                        .child("book_status").setValue("Onhold")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference().child("Bookings")
                                        .child(bidStr).child("onhold_reason").setValue(rHolding.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                if(aSpinStatus.equals("YES")){
                                                    FirebaseDatabase.getInstance().getReference().child("Bookings").child(bidStr)
                                                            .child("items_submiting").setValue(iAccept.getText().toString())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    FirebaseDatabase.getInstance().getReference().child("Bookings").child(bidStr)
                                                                            .child("items_submiting_reason").setValue(rAccept.getText().toString())
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    FirebaseDatabase.getInstance().getReference().child("Professional")
                                                                                            .child("Workers").child(HomeActivity.proID)
                                                                                            .child("curr_order_id").setValue("")
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    HomeActivity.proCurrOrderId="";
                                                                                                    dialog.dismiss();
                                                                                                    finish();
                                                                                                    loadingDialog.dismiss();
                                                                                                }
                                                                                            });
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                                else{
                                                    FirebaseDatabase.getInstance().getReference().child("Professional")
                                                            .child("Workers").child(HomeActivity.proID)
                                                            .child("curr_order_id").setValue("")
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    HomeActivity.proCurrOrderId="";
                                                                    dialog.dismiss();
                                                                    finish();
                                                                    loadingDialog.dismiss();
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        });
            }
        });

        reqParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Bookings")
                        .child(bidStr).child("book_parts").setValue(partEdit.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(list1.contains("Parts Price")){
                                    for(int i=0; i<list1.size();i++){
                                        if(list1.get(i).equals("Parts Price")){
                                            FirebaseDatabase.getInstance().getReference().child("Bookings")
                                                    .child(bidStr).child("order_summary_bill_details")
                                                    .child(String.valueOf(i)).child("itemPrice")
                                                    .setValue("Rs."+partEdit.getText().toString()+"/-")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            partUse.setText(partEdit.getText().toString());
                                                            loadBillSummary();
                                                            validate();
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                        }
                                    }


                                }else{
                                    Map<String,String> map=new HashMap<>();
                                    map.put("itemName", "Parts Price");
                                    map.put("itemPrice", "Rs."+partEdit.getText().toString()+"/-");
                                    FirebaseDatabase.getInstance().getReference().child("Bookings")
                                            .child(bidStr).child("order_summary_bill_details")
                                            .child(String.valueOf(list1.size()-1))
                                            .setValue(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    partUse.setText(partEdit.getText().toString());
                                                    loadBillSummary();
                                                    validate();
                                                    loadingDialog.dismiss();
                                                }
                                            });
                                }
                            }
                        });
            }
        });

    }

    private void loadBillSummary() {
        if (list1.size()>0){
            list1.clear();
            list2.clear();
        }
        FirebaseDatabase.getInstance().getReference().child("Bookings").child(HomeActivity.proCurrOrderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot d: dataSnapshot.child("order_summary_bill_details").getChildren()){
                            list1.add(String.valueOf(d.child("itemName").getValue()));
                            list2.add(String.valueOf(d.child("itemPrice").getValue()));
                        }
                        if(list1.contains("Parts Price")){
                            String totRate=String.valueOf(dataSnapshot.child("serviceTotPrice").getValue());
                            for(int i=0; i<list1.size();i++){
                                if(list1.get(i).equals("Parts Price")) {
                                    String temp=String.valueOf(Integer.parseInt(totRate.substring(3,totRate.length()-2))+
                                            Integer.parseInt(list2.get(i).substring(3,list2.get(i).length()-2)));
                                    list1.add("Total");
                                    list2.add("Rs."+temp+"/-");
                                }
                            }

                        }else{
                            list1.add("Total");
                            list2.add(String.valueOf(dataSnapshot.child("serviceTotPrice").getValue()));
                        }
                        Toast.makeText(OngoingActivity.this, String.valueOf(list1)+"\n"+String.valueOf(list2), Toast.LENGTH_SHORT).show();
                        String s1="", s2="";
                        for(int i=0; i<list1.size(); i++){
                            if(i==list1.size()-1){
                                s1= s1+"\n"+list1.get(i);
                                s2= s2+"\n"+list2.get(i);
                            }
                            else{
                                s1= s1+list1.get(i) + "\n";
                                s2= s2+list2.get(i) + "\n";
                            }

                        }
                        itemNameList.setText(s1);
                        itemPriceList.setText(s2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void validate() {

        if(TextUtils.isEmpty(pSmt1.getText().toString())){
            pSmt1.setVisibility(GONE);
            pSmt1Text.setVisibility(GONE);
        }
        else{
            Toast.makeText(this, pSmt1.getText().toString(), Toast.LENGTH_SHORT).show();
            pSmt1.setVisibility(View.VISIBLE);
            pSmt1Text.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(pSmt2.getText().toString())){
            pSmt2.setVisibility(GONE);
            pSmt2Text.setVisibility(GONE);
        }
        else {
            pSmt2.setVisibility(View.VISIBLE);
            pSmt2Text.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(comTime.getText().toString())){
            comTimeText.setVisibility(GONE);
            comTime.setVisibility(GONE);
        }
        else{
            comTimeText.setVisibility(View.VISIBLE);
            comTime.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(schTime.getText().toString())){
            schTime.setVisibility(GONE);
            schTimeText.setVisibility(GONE);
        }
        else{
            schTime.setVisibility(View.VISIBLE);
            schTimeText.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(onholdReason.getText().toString())){
            onholdReason.setVisibility(GONE);
            onholdReasonText.setVisibility(GONE);
        }
        else {
            onholdReason.setVisibility(View.VISIBLE);
            onholdReasonText.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(itemSub.getText().toString())){
            itemSub.setVisibility(GONE);
            itemSubText.setVisibility(GONE);
        }
        else {
            itemSub.setVisibility(View.VISIBLE);
            itemSubText.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(itemSubReason.getText().toString())){
            itemSubReasonText.setVisibility(GONE);
            itemSubReason.setVisibility(GONE);
        }
        else {
            itemSubReasonText.setVisibility(View.VISIBLE);
            itemSubReason.setVisibility(View.VISIBLE);
        }

        if(profID.equals("")){
            profName.setVisibility(GONE);
            profNameText.setVisibility(GONE);
            profAbout.setVisibility(GONE);
            profAboutText.setVisibility(GONE);
            profPic.setVisibility(GONE);
            profHead.setVisibility(GONE);
        }
        else {
            profName.setVisibility(View.VISIBLE);
            profNameText.setVisibility(View.VISIBLE);
            profAbout.setVisibility(View.VISIBLE);
            profAboutText.setVisibility(View.VISIBLE);
            profPic.setVisibility(View.VISIBLE);
            profHead.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(payStatus.getText().toString())){
            payStatus.setText("UNPAID");
        }

        if(TextUtils.isEmpty(payMode.getText().toString())){
            payMode.setVisibility(GONE);
            payModeText.setVisibility(GONE);
        }
        else{
            payMode.setVisibility(View.VISIBLE);
            payModeText.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(partUse.getText().toString())){
            partUse.setVisibility(GONE);
            partUseText.setVisibility(GONE);
        }
        else {
            partUse.setVisibility(View.VISIBLE);
            partUseText.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(totPrice.getText().toString())){
            totPriceText.setVisibility(GONE);
            totPrice.setVisibility(GONE);
        }
        else {
            totPriceText.setVisibility(View.VISIBLE);
            totPrice.setVisibility(View.VISIBLE);
        }

    }

    private  String timeStampToDate(String time){
        if(time.equals("")){
            return "";
        }
        else{
            Instant ins1=Instant.ofEpochMilli(Long.parseLong(time));
            ZoneId zd= ZoneId.of("Asia/Kolkata");
            ZonedDateTime zdt1= ZonedDateTime.ofInstant(ins1, zd);
            DateTimeFormatter dt= DateTimeFormatter.ofPattern("dd/MM/yyyy , hh:mm:ss")  ;
            return dt.format(zdt1);
        }
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
        finish();
    }
}
