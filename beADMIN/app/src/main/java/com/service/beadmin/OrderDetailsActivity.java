package com.service.beadmin;

import android.app.Dialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.beadmin.Adapters.ProAdapter;
import com.service.beadmin.Models.ProModel;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView bid, serviceName, orderPrice, itemPrice, qty, pSmt1Text, pSmt1, pSmt2Text, pSmt2
            ,catText, cat, bookTimeText, bookTime, status, comTime, comTimeText, schTimeText, schTime, onholdReasonText, onholdReason
            ,itemSubText, itemSub, itemSubReasonText, itemSubReason, addressText, address, payModeText, payMode, payStatusText
            ,payStatus, partUseText, partUse, totPriceText, totPrice, profName, profNameText, profAboutText, profAbout
            ,itemNameList, itemPriceList, profHead;
    private Button assignPro;
    private CircleImageView profPic;
    private String statusStr;
    private String profID, catStr1, catStr2, catStr3, cityStr, stateStr, upLocStr, bidStr;

    private Dialog loadingDialog;

    private RecyclerView rewardsRecyclerView;
    private ProAdapter myRewardsAdapter;
    private List<ProModel> rewardsModelList=new ArrayList<>();
    public static Dialog dialog;

    private String loc= "Telco Colony";
    private EditText upP1, upSch;
    private Spinner upLoc;
    private String selectedArea;
    private List<String> areaList= new ArrayList<>();
    private Button upP1Btn, upLocBtn, upSchBtn;

    private View incLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        incLay= (View) findViewById(R.id.inc_loadout);
        assignPro= findViewById(R.id.assign_pro_btn);

        if(getIntent().getIntExtra("type", -3) == 1){
            incLay.setVisibility(View.VISIBLE);
            assignPro.setVisibility(View.VISIBLE);
        }
        else if(getIntent().getIntExtra("type", -3) == 2){
            incLay.setVisibility(View.GONE);
            assignPro.setVisibility(View.GONE);
        }

        /////loading Dialog
        loadingDialog = new Dialog(OrderDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        }
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

        statusStr= getIntent().getStringExtra("status");

        Toolbar toolbar= findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My "+statusStr+" Bookings");
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
        upLoc= findViewById(R.id.txtup_loc);
        upP1= findViewById(R.id.txtup_p1);
        upSch= findViewById(R.id.txtup_sch);
        upLocBtn= findViewById(R.id.txtup_locbtn);
        upP1Btn= findViewById(R.id.txtup_p1btn);
        upSchBtn= findViewById(R.id.txtup_schBtn);

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Bookings").child(getIntent().getStringExtra("bookID"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bidStr= String.valueOf(dataSnapshot.child("book_id").getValue());
                        bid.setText("BID: "+bidStr);
                        serviceName.setText(String.valueOf(dataSnapshot.child("serviceName").getValue()));
                        orderPrice.setText(String.valueOf(dataSnapshot.child("serviceTotPrice").getValue()));
                        itemPrice.setText("Item Price: "+String.valueOf(dataSnapshot.child("servicePrice").getValue()));
                        qty.setText("Qty: "+String.valueOf(dataSnapshot.child("serviceQty").getValue()));
                        pSmt1.setText(String.valueOf(dataSnapshot.child("prob_stmt_1").getValue()));
                        pSmt2.setText(String.valueOf(dataSnapshot.child("prob_stmt_2").getValue()));
                        upP1.setText(String.valueOf(dataSnapshot.child("prob_stmt_1").getValue()));
                        catStr1= String.valueOf(dataSnapshot.child("cat1").getValue());
                        catStr2= String.valueOf(dataSnapshot.child("cat2").getValue());
                        catStr3= String.valueOf(dataSnapshot.child("cat3").getValue());
                        cat.setText(catStr1+" > "+ catStr2+" > "+ catStr3);
                        bookTime.setText(timeStampToDate(String.valueOf(dataSnapshot.child("book_time").getValue())));
                        status.setText(String.valueOf(dataSnapshot.child("book_status").getValue()));
                        comTime.setText(timeStampToDate(String.valueOf(dataSnapshot.child("book_complete_time").getValue())));
                        schTime.setText(String.valueOf(dataSnapshot.child("sch_time").getValue()));
                        upSch.setText(String.valueOf(dataSnapshot.child("sch_time").getValue()));
                        onholdReason.setText(String.valueOf(dataSnapshot.child("onhold_reason").getValue()));
                        itemSub.setText(String.valueOf(dataSnapshot.child("items_submiting").getValue()));
                        itemSubReason.setText(String.valueOf(dataSnapshot.child("items_submiting_reason").getValue()));
                        cityStr = String.valueOf(dataSnapshot.child("book_city").getValue());
                        stateStr = String.valueOf(dataSnapshot.child("book_state").getValue());
                        address.setText(String.valueOf(dataSnapshot.child("book_name").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_Mobile").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_mainMobile").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_flatNo").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_locality").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_landmark").getValue())+"\n"+
                                cityStr+" - "+
                                String.valueOf(dataSnapshot.child("book_pincode").getValue())+"\n"+
                                stateStr);
                        payMode.setText(String.valueOf(dataSnapshot.child("payment_mode").getValue()));
                        payStatus.setText(String.valueOf(dataSnapshot.child("payment_status").getValue()));
                        partUse.setText(String.valueOf(dataSnapshot.child("book_parts").getValue()));
                        totPrice.setText(String.valueOf(dataSnapshot.child("book_price").getValue()));
                        upLocStr= String.valueOf(dataSnapshot.child("book_hq").getValue());

                        profID=String.valueOf(dataSnapshot.child("book_professional_id").getValue());
                        List<String> list1=new ArrayList<>();
                        List<String> list2=new ArrayList<>();
                        for(DataSnapshot d: dataSnapshot.child("order_summary_bill_details").getChildren()){
                                list1.add(String.valueOf(d.child("itemName").getValue()));
                                list2.add(String.valueOf(d.child("itemPrice").getValue()));
                        }

                        list1.add("Total");
                        list2.add(String.valueOf(dataSnapshot.child("serviceTotPrice").getValue()));
                        Toast.makeText(OrderDetailsActivity.this, String.valueOf(list1)+"\n"+String.valueOf(list2), Toast.LENGTH_SHORT).show();
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

                        FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                .child(profID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                profName.setText(String.valueOf(dataSnapshot.child("proName").getValue()));
                                profAbout.setText(String.valueOf(dataSnapshot.child("proAbout").getValue()));
                                Glide.with(OrderDetailsActivity.this)
                                        .load(String.valueOf(dataSnapshot.child("proPic").getValue()))
                                        .into(profPic);
                                FirebaseDatabase.getInstance().getReference().child("Branches")
                                        .child(stateStr).child(cityStr)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot k: dataSnapshot.getChildren()){
                                                    areaList.add(String.valueOf(k.getKey()));
                                                }
                                                validate();
                                                loadingDialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                loadingDialog.dismiss();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                loadingDialog.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });

        ////////////////////////////////////////////////////////// dialog
        dialog= new Dialog(OrderDetailsActivity.this);
        dialog.setContentView(R.layout.dialog_reward);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rewardsRecyclerView= dialog.findViewById(R.id.re_rec);
        LinearLayoutManager layoutManager= new LinearLayoutManager(OrderDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rewardsRecyclerView.setLayoutManager(layoutManager);
        ///////////////////////////////////////////////////////// Dialog


        assignPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                if(rewardsModelList.size()>0){
                    rewardsModelList.clear();
                }
                FirebaseDatabase.getInstance().getReference().child("Professional")
                        .child("Workers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d: dataSnapshot.getChildren()){
                            String cat=String.valueOf(d.child("proCategory").getValue());
                            if(((cat).lastIndexOf(catStr1) != -1)
                                    || ((cat).lastIndexOf(catStr2) != -1)
                                    || ((cat).lastIndexOf(catStr3) != -1)){
                                if((String.valueOf(d.child("dutyStart").getValue()).equals("YES"))
                                        && (String.valueOf(d.child("proLocation").getValue()).equals(loc))){
                                    rewardsModelList.add(new ProModel(String.valueOf(d.child("proID").getValue()),
                                            String.valueOf(d.child("proName").getValue()),
                                            String.valueOf(d.child("proMobile").getValue()),
                                            "AVAILABLE",
                                            String.valueOf(d.child("dutyStart").getValue()),
                                            String.valueOf(d.child("proCategory").getValue()),
                                            String.valueOf(d.child("proLocation").getValue()),
                                            getIntent().getStringExtra("bookID")));
                                    loadingDialog.dismiss();
                                }else {
                                    Toast.makeText(OrderDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                }
                            }else {
                                Toast.makeText(OrderDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        }
                        myRewardsAdapter= new ProAdapter(OrderDetailsActivity.this, rewardsModelList);
                        rewardsRecyclerView.setAdapter(myRewardsAdapter);
                        myRewardsAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(OrderDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        upP1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Bookings")
                        .child(bidStr).child("prob_stmt_1")
                        .setValue(upP1.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismiss();
                    }
                });
            }
        });

        upLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Bookings")
                        .child(bidStr).child("book_hq")
                        .setValue(selectedArea).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismiss();
                    }
                });
            }
        });

        upSchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Bookings")
                        .child(bidStr).child("sch_time")
                        .setValue(upSch.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismiss();
                    }
                });
            }
        });

    }

    private void validate() {

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, areaList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upLoc.setAdapter(spinnerAdapter);
        upLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedArea = areaList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(!upLocStr.equals("")){
            upLoc.setSelection(areaList.indexOf(upLocStr));
        }

        if(TextUtils.isEmpty(pSmt1.getText().toString())){
            pSmt1.setVisibility(GONE);
            pSmt1Text.setVisibility(GONE);
        }
        else{
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

        if(!upLocStr.equals("")) {
            upLoc.setSelection(areaList.indexOf(upLocStr));
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
