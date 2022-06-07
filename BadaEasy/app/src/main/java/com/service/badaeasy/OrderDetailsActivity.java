package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView bid, serviceName, orderPrice, itemPrice, qty, pSmt1Text, pSmt1, pSmt2Text, pSmt2
            ,catText, cat, bookTimeText, bookTime, status, comTime, comTimeText, schTimeText, schTime, onholdReasonText, onholdReason
            ,itemSubText, itemSub, itemSubReasonText, itemSubReason, addressText, address, payModeText, payMode, payStatusText
            ,payStatus, partUseText, partUse, totPriceText, totPrice, profName, profNameText, profAboutText, profAbout
            ,itemNameList, itemPriceList, profHead, commentTitle;
    private Button commentSubmit, cancelBook;
    private CircleImageView profPic;
    String statusStr;
    String profID, catStr, commentIDStr;
    private View custReviewLayout;

    private Dialog loadingDialog;

    private List<String> list1=new ArrayList<>();
    private List<String> list2=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        /////loading Dialog
        loadingDialog = new Dialog(OrderDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
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
        commentSubmit= findViewById(R.id.comment_submit);
        commentTitle= findViewById(R.id.comment_title);
        custReviewLayout= findViewById(R.id.inclue_r);
        cancelBook= findViewById(R.id.cancel_order);

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Bookings").child(getIntent().getStringExtra("bookID"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bid.setText("BID: "+String.valueOf(dataSnapshot.child("book_id").getValue()));
                        serviceName.setText(String.valueOf(dataSnapshot.child("serviceName").getValue()));
                        orderPrice.setText(String.valueOf(dataSnapshot.child("serviceTotPrice").getValue()));
                        itemPrice.setText("Item Price: "+String.valueOf(dataSnapshot.child("servicePrice").getValue()));
                        qty.setText("Qty: "+String.valueOf(dataSnapshot.child("serviceQty").getValue()));
                        pSmt1.setText(String.valueOf(dataSnapshot.child("prob_stmt_1").getValue()));
                        pSmt2.setText(String.valueOf(dataSnapshot.child("prob_stmt_2").getValue()));
                        catStr= String.valueOf(dataSnapshot.child("cat1").getValue());
                        commentIDStr= String.valueOf(dataSnapshot.child("book_comment_ID").getValue());
                        cat.setText(catStr+" > "+
                                String.valueOf(dataSnapshot.child("cat2").getValue())+" > "+
                                String.valueOf(dataSnapshot.child("cat3").getValue()));
                        bookTime.setText(timeStampToDate(String.valueOf(dataSnapshot.child("book_time").getValue())));
                        status.setText(String.valueOf(dataSnapshot.child("book_status").getValue()));
                        comTime.setText(timeStampToDate(String.valueOf(dataSnapshot.child("book_complete_time").getValue())));
                        schTime.setText(String.valueOf(dataSnapshot.child("sch_time").getValue()));
                        onholdReason.setText(String.valueOf(dataSnapshot.child("onhold_reason").getValue()));
                        itemSub.setText(String.valueOf(dataSnapshot.child("items_submiting").getValue()));
                        itemSubReason.setText(String.valueOf(dataSnapshot.child("items_submiting_reason").getValue()));
                        address.setText(String.valueOf(dataSnapshot.child("book_name").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_Mobile").getValue())+"\n"+
                                String.valueOf(dataSnapshot.child("book_mainMobile").getValue())+"\n"+
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

                        profID=String.valueOf(dataSnapshot.child("book_professional_id").getValue());

                        loadBillSummary();

                        FirebaseDatabase.getInstance().getReference().child("Professional").child("Workers")
                                .child(profID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                profName.setText(String.valueOf(dataSnapshot.child("proName").getValue()));
                                profAbout.setText(String.valueOf(dataSnapshot.child("proAbout").getValue()));
                                Glide.with(OrderDetailsActivity.this)
                                        .load(String.valueOf(dataSnapshot.child("proPic").getValue()))
                                        .into(profPic);
                                Toast.makeText(OrderDetailsActivity.this, profID, Toast.LENGTH_SHORT).show();
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

    }

    private void loadBillSummary() {

        if (list1.size()>0){
            list1.clear();
            list2.clear();
        }
        FirebaseDatabase.getInstance().getReference().child("Bookings").child(getIntent().getStringExtra("bookID"))
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void cancelBooking(){
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Bookings")
                .child(getIntent().getStringExtra("bookID"))
                .child("book_status").setValue("Cancelled")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference().child("Bookings")
                                .child(getIntent().getStringExtra("bookID"))
                                .child("CancelledBy").setValue("User")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(OrderDetailsActivity.this, "Booking successfully cancelled!", Toast.LENGTH_SHORT).show();finish();
                                        loadingDialog.dismiss();
                                    }
                                });
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

        if ((statusStr.equals("Completed")) && (commentIDStr.equals(""))){
            custReviewLayout.setVisibility(View.VISIBLE);
        }
        else{
            custReviewLayout.setVisibility(GONE);
        }

        if((statusStr.equals("Ready"))){
            cancelBook.setVisibility(View.VISIBLE);
        }else{
            cancelBook.setVisibility(GONE);
        }

        /////////cancel booking
        cancelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(OrderDetailsActivity.this);
                builder.setMessage("Are you sure to cancel this booking?");
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelBooking(); dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog= builder.create();
                alertDialog.setTitle("Cancel Booking");
                alertDialog.show();
            }
        });
        /////////cancel booking


        ///////// commenting part

        commentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                commentSubmit.setEnabled(false);
                if (commentTitle.getText().toString().length()>3 && !TextUtils.isEmpty(commentTitle.getText().toString())) {
                    final Map<String, Object> reviewMap = new HashMap<>();
                    reviewMap.put("reviewName", UserData.userName);
                    reviewMap.put("reviewUID", getIntent().getStringExtra("bookID"));
                    reviewMap.put("reviewText", commentTitle.getText().toString());
                    reviewMap.put("reviewTime", ServerValue.TIMESTAMP);

                    final String uuid = UUID.randomUUID().toString();
                    FirebaseDatabase.getInstance().getReference().child("Comments").child(uuid)
                            .setValue(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("Services").child(catStr)
                                    .child("comments").child(uuid).setValue("COMMENT_ID").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference().child("Bookings").child(getIntent().getStringExtra("bookID"))
                                                .child("book_comment_ID").setValue(uuid)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                commentTitle.setText("");
                                                Toast.makeText(OrderDetailsActivity.this, "Review updated.", Toast.LENGTH_SHORT).show();
                                                commentSubmit.setEnabled(true);
                                                custReviewLayout.setVisibility(GONE);
                                                loadingDialog.dismiss();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(OrderDetailsActivity.this, "Error in updating review.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(OrderDetailsActivity.this, "Invalid review", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    commentSubmit.setEnabled(true);
                }
            }
        });

        //////// commenting part

    }

    private  String timeStampToDate(String time){
        if(time.equals("")){
            return "";
        }
        else{
            Long l = Long.parseLong(time);
            Timestamp timestamp = new Timestamp(l);
            Date date=new Date(timestamp.getTime());
            return String.valueOf(timestamp.toString()).substring(0, 19);
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

    @Override
    protected void onStart() {
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
