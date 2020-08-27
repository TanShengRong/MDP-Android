package com.example.mazerunner;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarid);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Adding Fragments
        adapter.AddFragment(new FragmentBluetooth(),"Bluetooth");
        adapter.AddFragment(new FragmentComms(), "Comms");
        adapter.AddFragment(new FragmentMap(),"Map");
        adapter.AddFragment(new FragmentController(),"Controller");
        //Adapter Setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    public void buttonClick(View v) {
        switch(v.getId()) {
            case R.id.btnGoToBtActivity:
                Intent myIntent = new Intent();
                myIntent.setClassName("com.example.mazerunner", "com.example.mazerunner.BluetoothActivity");
                startActivity(myIntent);
                break;
            case R.id.btnGoToCommunicationActivity:
                Intent myCIntent = new Intent();
                myCIntent.setClassName("com.example.mazerunner", "com.example.mazerunner.CommunicationActivity");
                startActivity(myCIntent);
                break;
        }
    }
}