package com.service.beadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.service.beadmin.Adapters.GridAdapter;
import com.service.beadmin.Models.HomeModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView homeRecyclerView;
    private GridAdapter homeAdapter;
    private List<HomeModel> homeList = new ArrayList<>();

    public static String adminCity = "Jamshedpur";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeRecyclerView = findViewById(R.id.home_rec);
        homeRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setNestedScrollingEnabled(false);
        homeAdapter = new GridAdapter(MainActivity.this, homeList);
        homeRecyclerView.setAdapter(homeAdapter);

        homeList.add(new HomeModel("Manage Services"));
        homeList.add(new HomeModel("Admit Professional"));
        homeList.add(new HomeModel("Upcoming Bookings"));
        homeList.add(new HomeModel("Completed Bookings"));
        homeList.add(new HomeModel("Onhold Bookings"));
        homeList.add(new HomeModel("Ongoing Bookings"));
        homeList.add(new HomeModel("Cancelled Orders"));
        homeList.add(new HomeModel("Ready Orders"));
        homeList.add(new HomeModel("Users"));
        homeList.add(new HomeModel("Customer Service"));

        homeAdapter.notifyDataSetChanged();

    }
}
