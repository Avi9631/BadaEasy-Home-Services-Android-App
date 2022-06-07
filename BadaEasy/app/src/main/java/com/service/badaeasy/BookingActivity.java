package com.service.badaeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.service.badaeasy.Adapters.AddressAdapter;
import com.service.badaeasy.Adapters.RewardsAdapter;
import com.service.badaeasy.Adapters.SummaryAdapter;
import com.service.badaeasy.Models.AddressModel;
import com.service.badaeasy.Models.RewardModel;
import com.service.badaeasy.Models.SummaryModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookingActivity extends AppCompatActivity {

    private TextView bookItemName, bookItemPrice, bookQty;
    private static TextView bookTotalPrice;
    private ConstraintLayout bookOffer;
    private ImageView addQty, subQty;
    private Button addNewAddress, bookNowBtn;
    private EditText bookNowMobile;
    public static TextView offerNotate;

    private RecyclerView summRecyclerView;
    public static SummaryAdapter summAdapter;
    public static List<SummaryModel> summList = new ArrayList<>();

    private RecyclerView addressRecyclerView;
    public static AddressAdapter addressAdapter;
    public static List<AddressModel> addressList = new ArrayList<>();

    private RecyclerView rewardsRecyclerView;
    private RewardsAdapter myRewardsAdapter;
    private List<RewardModel> rewardsModelList=new ArrayList<>();

    public static Dialog dialog;
    public static double appliedCoupon;
    String[] rewardArr;
    String s2;
    String fRewardStr="";

    private Dialog loadingDialog, captchaDialog;

//    //////////captcha
//    String TAG = BookingDetails.class.getSimpleName();
//    Button btnverifyCaptcha;
//    String SITE_KEY = "6LecLdcZAAAAACasMlzSGeyTiuGbqBm4J4g31sK5";
//    String SECRET_KEY = "6LecLdcZAAAAAPMLHJ-s6GPFBo4TuOcEB1UAB7os";
//    RequestQueue queue;
//    /////////captcha

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        /////loading Dialog
        loadingDialog = new Dialog(BookingActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.circle));
        loadingDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        /////loading Dialog

//        /////////////captcha
//        captchaDialog = new Dialog(BookingActivity.this);
//        captchaDialog.setContentView(R.layout.dialog_captcha_verify);
//        captchaDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//        captchaDialog.setCancelable(false);
//        btnverifyCaptcha = captchaDialog.findViewById(R.id.button);
//        queue = Volley.newRequestQueue(getApplicationContext());
//        btnverifyCaptcha.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadingDialog.show();
//                SafetyNet.getClient(BookingActivity.this).verifyWithRecaptcha(SITE_KEY)
//                        .addOnSuccessListener(BookingActivity.this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
//                            @Override
//                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
//                                if (!response.getTokenResult().isEmpty()) {
//                                    handleSiteVerify(response.getTokenResult());
//                                }
//                            }
//                        })
//                        .addOnFailureListener(BookingActivity.this, new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                if (e instanceof ApiException) {
//                                    ApiException apiException = (ApiException) e;
//                                    Log.d(TAG, "Error message: " +
//                                            CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
//                                    loadingDialog.dismiss();
//                                } else {
//                                    Log.d(TAG, "Unknown type of error: " + e.getMessage());
//                                    loadingDialog.dismiss();
//                                }
//                            }
//                        });
//            }
//        });
//
//        /////////////captcha


        bookItemName= findViewById(R.id.book_item_name);
        bookItemPrice= findViewById(R.id.book_item_price);
        bookQty= findViewById(R.id.book_qty);
        bookTotalPrice= findViewById(R.id.book_total_price);
        bookOffer= findViewById(R.id.offer_const_layout);
        addQty= findViewById(R.id.book_qty_add);
        subQty= findViewById(R.id.book_qty_sub);
        addNewAddress= findViewById(R.id.add_new_address);
        bookNowBtn= findViewById(R.id.book_btn_final);
        bookNowMobile= findViewById(R.id.mobile_address_book);
        offerNotate= findViewById(R.id.offer_nototion);

        bookItemName.setText(getIntent().getStringExtra("buyName"));
        bookItemPrice.setText(getIntent().getStringExtra("buyPrice"));
        bookQty.setText("1");

        summRecyclerView = findViewById(R.id.summary_recView);
        LinearLayoutManager l= new LinearLayoutManager(BookingActivity.this);
        l.setOrientation(RecyclerView.VERTICAL);
        summRecyclerView.setLayoutManager(l);
        summRecyclerView.setHasFixedSize(true);
        summRecyclerView.setNestedScrollingEnabled(false);
        summAdapter = new SummaryAdapter(BookingActivity.this, summList);
        summRecyclerView.setAdapter(summAdapter);
        summList.add(new SummaryModel("Item Price", bookItemPrice.getText().toString()));
        summAdapter.notifyDataSetChanged();
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference().child("AdditionalCharges")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    String m= String.valueOf(d.getKey());
                    summList.add(new SummaryModel(m.substring(0, m.length()-3)+" fee" , "Rs."+String.valueOf(d.getValue())+"/-"));
                }
                summAdapter.notifyDataSetChanged();
                setPrice(summList);
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
        setPrice(summList);

        addressRecyclerView = findViewById(R.id.book_address_rec);
        LinearLayoutManager li= new LinearLayoutManager(BookingActivity.this);
        li.setOrientation(RecyclerView.HORIZONTAL);
        addressRecyclerView.setLayoutManager(li);
        addressRecyclerView.setHasFixedSize(true);
        addressRecyclerView.setNestedScrollingEnabled(false);

        addQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q= Integer.parseInt(bookQty.getText().toString());
                int s= q+1;
                bookQty.setText(String.valueOf(s));
                changePrice(s, Integer.parseInt(bookItemPrice.getText().toString()
                        .substring(3,bookItemPrice.length()-2)));
            }
        });

        subQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q= Integer.parseInt(bookQty.getText().toString());
                if (q>1) {
                    int s = q - 1;
                    bookQty.setText(String.valueOf(s));
                    changePrice(s, Integer.parseInt(bookItemPrice.getText().toString()
                            .substring(3,bookItemPrice.length()-2)));
                }
            }
        });

        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressList.clear();
                Intent i = new Intent(BookingActivity.this, InfoActivity.class);
                startActivity(i);
            }
        });

        bookNowMobile.setText("+91");
        Selection.setSelection(bookNowMobile.getText(), bookNowMobile.getText().length());
        bookNowMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("+91")){
                    bookNowMobile.setText("+91");
                    Selection.setSelection(bookNowMobile.getText(), bookNowMobile.getText().length());
                }
            }
        });

        bookNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookNowMobile.getText().toString().length() == 13) {

                    if ((!TextUtils.isEmpty(HomeFragment.cityfinal)) &&
                            (!TextUtils.isEmpty(HomeFragment.namefinal)) &&
                            (!TextUtils.isEmpty(HomeFragment.cityfinal)) &&
                            (!TextUtils.isEmpty(HomeFragment.cityfinal))) {
                            uploadOrder();
                    } else {
                        Toast.makeText(BookingActivity.this, "Select your address", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(BookingActivity.this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
                }


            }
        });

        ////////////////////////////////////////////////////////// dialog
        dialog= new Dialog(BookingActivity.this);
        dialog.setContentView(R.layout.dialog_reward);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rewardsRecyclerView= dialog.findViewById(R.id.re_rec);
        LinearLayoutManager layoutManager= new LinearLayoutManager(BookingActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rewardsRecyclerView.setLayoutManager(layoutManager);
        myRewardsAdapter= new RewardsAdapter(BookingActivity.this, rewardsModelList);
        rewardsRecyclerView.setAdapter(myRewardsAdapter);
        ///////////////////////////////////////////////////////// Dialog

        bookOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rewardsModelList.size()>0){
                    rewardsModelList.clear();
                }
                if(!UserData.userRewards.equals("")) {
                    if (Double.parseDouble(bookTotalPrice.getText().toString().substring(3, bookTotalPrice.getText().toString().length() - 2)) > 100) {
                        loadingDialog.show();
                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserData.userName= String.valueOf(dataSnapshot.child("userName").getValue());
                                        UserData.userDOB= String.valueOf(dataSnapshot.child("userDOB").getValue());
                                        UserData.userEmail= String.valueOf(dataSnapshot.child("userEmail").getValue());
                                        UserData.userMobile= String.valueOf(dataSnapshot.child("userMobile").getValue());
                                        UserData.userRewards=  String.valueOf(dataSnapshot.child("rewards").getValue());
                                        rewardArr= UserData.userRewards.split(",");
                                        for (String s : rewardArr) {
                                            rewardsModelList.add(new RewardModel(s));
                                        }
                                        myRewardsAdapter.notifyDataSetChanged();
                                        dialog.show();
                                        loadingDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        loadingDialog.dismiss();
                                    }
                                });
                    } else {
                        Toast.makeText(BookingActivity.this, "Coupons cannot be applied", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(BookingActivity.this, "No Coupons available", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private static void setPrice(List<SummaryModel> tempList) {
        int total=0;
        for (SummaryModel t: tempList){
            if(t.getItemName().equals("coupon applied")){
                total= total - Integer.parseInt(t.getItemPrice().substring(3,t.getItemPrice().length()-2));
            }else{
                total= total + Integer.parseInt(t.getItemPrice().substring(3,t.getItemPrice().length()-2));
            }
        }
        bookTotalPrice.setText("Rs."+String.valueOf(total)+"/-");
    }

    private void changePrice(int s, int i) {
        int newPrice= s*i;
        summList.get(0).setItemPrice("Rs."+newPrice+"/-");
        summAdapter.notifyDataSetChanged();
        setPrice(summList);
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
                for (DataSnapshot d: dataSnapshot.child("address").getChildren()){
                    if (d.hasChildren()){
                        addressList.add(new AddressModel(String.valueOf(d.child("city").getValue()),
                                String.valueOf(d.child("locality").getValue()),
                                String.valueOf(d.child("flatNo").getValue()),
                                String.valueOf(d.child("pincode").getValue()),
                                String.valueOf(d.child("landmark").getValue()),
                                UserData.userName,
                                UserData.userMobile,
                                "",
                                String.valueOf(d.child("state").getValue()),
                                false));
                    }
                }
                addressAdapter = new AddressAdapter(BookingActivity.this, addressList);
                addressRecyclerView.setAdapter(addressAdapter);
                addressAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    public static void addOfferNotation(double d){
        if(d == 0.0){
            offerNotate.setText("Offers and Coupons");
        }else{
            offerNotate.setText("Rs."+Math.round(d)+"/- coupon applied");
            if(summList.get(summList.size()-1).getItemName().equals("coupon applied")){
                summList.remove(summList.size()-1);
            }
            summList.add(new SummaryModel("coupon applied", "Rs."+String.valueOf(Math.round(d))+"/-"));
            summAdapter.notifyDataSetChanged();
            setPrice(summList);
        }
    }

//    protected  void handleSiteVerify(final String responseToken){
//        String url = "https://www.google.com/recaptcha/api/siteverify";
//        StringRequest request = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            if(jsonObject.getBoolean("success")){
//                                uploadOrder();
//                                captchaDialog.dismiss();
//                            }
//                            else{
//                                Toast.makeText(getApplicationContext(),String.valueOf(jsonObject.getString("error-codes")), Toast.LENGTH_LONG).show();
//                                captchaDialog.dismiss();
//                                loadingDialog.dismiss();
//                            }
//                        } catch (Exception ex) {
//                            Log.d(TAG, "JSON exception: " + ex.getMessage());
//                            loadingDialog.dismiss();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG, "Error message: " + error.getMessage());
//                        loadingDialog.dismiss();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("secret", SECRET_KEY);
//                params.put("response", responseToken);
//                return params;
//            }
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                50000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(request);
//    }


    private void uploadOrder(){
        loadingDialog.show();
        final String uuid = UUID.randomUUID().toString();

        HomeFragment.mainMobilefinal = bookNowMobile.getText().toString();
        final Map<String, Object> bookMap = new HashMap<>();
        bookMap.put("cat1", getIntent().getStringExtra("add1"));
        if(getIntent().getStringExtra("add2").equals("")){
            bookMap.put("cat2", getIntent().getStringExtra("add3"));
        }else {
            bookMap.put("cat2", getIntent().getStringExtra("add2"));

        }
        bookMap.put("cat3", getIntent().getStringExtra("add3"));
        bookMap.put("serviceName", getIntent().getStringExtra("buyName"));
        bookMap.put("serviceTotPrice", bookTotalPrice.getText().toString());
        bookMap.put("servicePrice", bookItemPrice.getText().toString());
        bookMap.put("serviceQty", bookQty.getText().toString());
        bookMap.put("serviceUserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        bookMap.put("book_city", HomeFragment.cityfinal);
        bookMap.put("book_locality", HomeFragment.localityfinal);
        bookMap.put("book_flatNo", HomeFragment.flatNofinal);
        bookMap.put("book_pincode", HomeFragment.pincodefinal);
        bookMap.put("book_landmark", HomeFragment.landmarkfinal);
        bookMap.put("book_name", HomeFragment.namefinal);
        bookMap.put("book_mobile", HomeFragment.mobilefinal);
        bookMap.put("book_mainMobile", HomeFragment.mainMobilefinal);
        bookMap.put("book_state", HomeFragment.statefinal);
        bookMap.put("book_time", ServerValue.TIMESTAMP);
        bookMap.put("book_status", "Ready");
        bookMap.put("book_complete_time", "");
        bookMap.put("book_id", uuid);
        bookMap.put("prob_stmt_1","");
        bookMap.put("prob_stmt_2","");
        bookMap.put("prob_stmt_3","");
        bookMap.put("book_hq","");
        bookMap.put("admin_publish","");
        bookMap.put("book_parts","");
        bookMap.put("payment_status","");
        bookMap.put("payment_mode","");
        bookMap.put("book_price","");
        bookMap.put("book_professional_id","");
        bookMap.put("sch_time", "");
        bookMap.put("items_submiting", "");
        bookMap.put("items_submiting_reason", "");
        bookMap.put("onhold_reason", "");
        bookMap.put("order_summary_bill_details", summList);
        bookMap.put("book_comment_ID", "");
        bookMap.put("CancelledBy", "");

        FirebaseDatabase.getInstance().getReference().child("Bookings")
                .child(uuid).setValue(bookMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userBooking")
                        .child(uuid).setValue("BOOKING_ID")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(summList.get(summList.size()-1).getItemName().equals("coupon applied")){
                                    String s1= summList.get(summList.size()-1).getItemPrice();
                                    s2= s1.substring(3,s1.length()-2);
                                    int m=0;
                                    rewardArr= UserData.userRewards.split(",");
                                    for(int i=0; i<rewardArr.length; i++){
                                        if(m==1){
                                            fRewardStr = fRewardStr + rewardArr[i] + ",";
                                            continue;
                                        }
                                        if (s2.equals(rewardArr[i])) {
                                            m=1;
                                        }
                                        else{
                                            fRewardStr = fRewardStr + rewardArr[i] + ",";
                                        }
                                    }
                                }


                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rewards")
                                        .setValue(fRewardStr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        UserData.userRewards= fRewardStr;
                                        Toast.makeText(BookingActivity.this, "Congo!!" + "\n" + "Booking Done!!", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(BookingActivity.this, fRewardStr, Toast.LENGTH_LONG).show();
                                        summList.clear();
                                        addressList.clear();
                                        finish();
                                    }
                                });

                            }
                        });
                loadingDialog.dismiss();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        addressList.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        addressList.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        summList.clear();
        addressList.clear();
    }

}
