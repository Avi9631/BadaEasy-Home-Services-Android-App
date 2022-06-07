package com.service.badaeasyprofessional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.badaeasyprofessional.Models.UpcomingModel;

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

public class ViewDetailsActivity extends AppCompatActivity {

    private TextView bid, serviceName, orderPrice, itemPrice, qty, pSmt1Text, pSmt1, pSmt2Text, pSmt2
            ,catText, cat, bookTimeText, bookTime, status, comTime, comTimeText, schTimeText, schTime, onholdReasonText, onholdReason
            ,itemSubText, itemSub, itemSubReasonText, itemSubReason, addressText, address, payModeText, payMode, payStatusText
            ,payStatus, partUseText, partUse, totPriceText, totPrice, profName, profNameText, profAboutText, profAbout
            ,itemNameList, itemPriceList, profHead, cusHead;
    private CircleImageView profPic;
    String statusStr;
    String profID, catStr;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        /////loading Dialog
        loadingDialog = new Dialog(ViewDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading);
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
        cusHead= findViewById(R.id.cus_heading);

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
                        if(getIntent().getIntExtra("source", -2) ==  2){
                            cusHead.setVisibility(View.VISIBLE);
                            addressText.setVisibility(View.VISIBLE);
                            address.setVisibility(View.VISIBLE);
                            address.setText(String.valueOf(dataSnapshot.child("book_name").getValue())+"\n"+
                                    String.valueOf(dataSnapshot.child("book_Mobile").getValue())+"\n"+
                                    String.valueOf(dataSnapshot.child("book_mainMobile").getValue())+"\n"+
                                    String.valueOf(dataSnapshot.child("book_flatNo").getValue())+"\n"+
                                    String.valueOf(dataSnapshot.child("book_locality").getValue())+"\n"+
                                    String.valueOf(dataSnapshot.child("book_landmark").getValue())+"\n"+
                                    String.valueOf(dataSnapshot.child("book_city").getValue())+" - "+
                                    String.valueOf(dataSnapshot.child("book_pincode").getValue())+"\n"+
                                    String.valueOf(dataSnapshot.child("book_state").getValue()));
                        }else {
                            address.setVisibility(GONE);
                            addressText.setVisibility(GONE);
                            cusHead.setVisibility(GONE);
                        }

                        payMode.setText(String.valueOf(dataSnapshot.child("payment_mode").getValue()));
                        payStatus.setText(String.valueOf(dataSnapshot.child("payment_status").getValue()));
                        partUse.setText(String.valueOf(dataSnapshot.child("book_parts").getValue()));
                        totPrice.setText(String.valueOf(dataSnapshot.child("book_price").getValue()));

                        profID=String.valueOf(dataSnapshot.child("book_professional_id").getValue());
                        List<String> list1=new ArrayList<>();
                        List<String> list2=new ArrayList<>();
                        for(DataSnapshot d: dataSnapshot.child("order_summary_bill_details").getChildren()){
                            list1.add(String.valueOf(d.child("itemName").getValue()));
                            list2.add(String.valueOf(d.child("itemPrice").getValue()));
                        }

                        list1.add("Total");
                        list2.add(String.valueOf(dataSnapshot.child("serviceTotPrice").getValue()));
                        Toast.makeText(ViewDetailsActivity.this, String.valueOf(list1)+"\n"+String.valueOf(list2), Toast.LENGTH_SHORT).show();
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
                                Glide.with(ViewDetailsActivity.this)
                                        .load(String.valueOf(dataSnapshot.child("proPic").getValue()))
                                        .into(profPic);
                                Toast.makeText(ViewDetailsActivity.this, profID, Toast.LENGTH_SHORT).show();
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
