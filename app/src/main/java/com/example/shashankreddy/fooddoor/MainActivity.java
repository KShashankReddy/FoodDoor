package com.example.shashankreddy.fooddoor;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.shashankreddy.fooddoor.fragments.CheckOutFragment;
import com.example.shashankreddy.fooddoor.fragments.HomeFragment;
import com.example.shashankreddy.fooddoor.fragments.OrdersFragment;
import com.example.shashankreddy.fooddoor.fragments.PersonalInfoFragment;
import com.example.shashankreddy.fooddoor.fragments.ReviewFragment;
import com.example.shashankreddy.fooddoor.utils.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bootomNavigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        fragment = HomeFragment.newInstance("delhi");
                        break;
                    case R.id.bottom_orders:
                        fragment = new OrdersFragment();
                        break;
                    case R.id.bottom_personalInfo:
                        fragment = new PersonalInfoFragment();
                        break;
                    case R.id.bottom_checkout:
                        fragment = new CheckOutFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_replaceable, fragment).commit();
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_replaceable,HomeFragment.newInstance("delhi")).commit();
    }

    //back
    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_replaceable);
        if(f instanceof ReviewFragment)
        {
            OrdersFragment fragment = new OrdersFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment_replaceable,fragment).commit();
        }
        else
        {
            super.onBackPressed();
        }
    }


}
