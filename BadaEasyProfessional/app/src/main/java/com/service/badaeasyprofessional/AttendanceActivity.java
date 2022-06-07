package com.service.badaeasyprofessional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {

    public static String atten;
    private List<String> list= new ArrayList<>();
    private List<String> join= new ArrayList<>();
    private List<String> end= new ArrayList<>();
    private List<String> join2= new ArrayList<>();
    private List<Long> end2= new ArrayList<>();
    private HashMap<String, Long> map= new HashMap<>();
    String s;


    private TextView date, time, orderComplete;
    int c=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        date= findViewById(R.id.date);
        time= findViewById(R.id.time);
        orderComplete= findViewById(R.id.orderComplete);

        ////////////////////////////////////////////////present timings
        if(atten.substring(atten.length()-1).equals(";")) {
            String[] arr = atten.split(";");
            list.addAll(Arrays.asList(arr));
            for (String i : list) {
                String m[] = i.split("&");
                join.add(m[0]);
                end.add(m[1]);
            }

            for (int i = 0; i < join.size(); i++) {
                Timestamp endTime = Timestamp.valueOf(end.get(i));
                Timestamp strtTime = Timestamp.valueOf(join.get(i));

                long diff = endTime.getTime() - strtTime.getTime();
                long diffHour = diff / 1000;
                join2.add(String.valueOf(strtTime).substring(0, 10));
                end2.add(diffHour);
            }

            for (int i = 0; i < join2.size(); i++) {
                if (map.containsKey(join2.get(i))) {
                    long h = map.get(join2.get(i)) + end2.get(i);
                    map.put(join2.get(i), h);
                } else {
                    map.put(join2.get(i), end2.get(i));
                }
            }
            Toast.makeText(AttendanceActivity.this, String.valueOf(end), Toast.LENGTH_SHORT).show();
            ////////////////////////////////////////////////present timings

            String s1 = "", s2 = "";
            for (String i : map.keySet()) {
                s1 = s1 + i + "\n";
                s2 = s2 + map.get(i) + "\n";
            }
            FirebaseDatabase.getInstance().getReference().child("Bookings")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (String i : map.keySet()) {
                                int c = 0;
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    if ((String.valueOf(d.child("book_professional_id").getValue())).equals(HomeActivity.proID)) {
                                        if ((String.valueOf(d.child("book_status").getValue())).equals("Completed")) {
                                            if (timeStampToDate(String.valueOf(d.child("book_complete_time").getValue()).substring(0, 10)).equals(i)) {
                                                c++;
                                            }
                                        }
                                    }
                                }
                                s = s + c + "\n";
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            orderComplete.setText(s);
            date.setText(s1);
            time.setText(s2);
        }

    }

    private  String timeStampToDate(String time){
        if(time.equals("")){
            return "";
        }
        else{
            Long l = Long.parseLong(time);
            Timestamp timestamp = new Timestamp(l);
            Date date=new Date(timestamp.getTime());
            return String.valueOf(timestamp.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        list.clear();
        join2.clear();
        end2.clear();
        join.clear();
        end.clear();
    }
}