package com.service.badaeasy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private CharSequence item ="Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new HomeFragment());

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@androidx.annotation.NonNull MenuItem menuItem) {
            if(!item.toString().equals(menuItem.getTitle().toString())) {
                item = menuItem.getTitle();
                Fragment fragment;
//                Toast.makeText(HomeActivity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_food:
                        fragment = new FoodFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_cat:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        return true;

                }
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeFragment.homeList.clear();
        ProfileFragment.proList.clear();
    }
}
