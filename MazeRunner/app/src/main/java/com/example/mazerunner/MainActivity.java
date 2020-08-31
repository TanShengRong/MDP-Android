package com.example.mazerunner;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private TextView robotPosition;
    protected int[] waypoint = {1,1};
    protected boolean enablePlotReceived;

    private FragmentMap fragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarid);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        robotPosition = (TextView) findViewById(R.id.robotPosition);


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

    public MazeView getMazeView(){
        return findViewById(R.id.mazeView);
    }

    public boolean receiveAutoUpdate(){
        Intent i = getIntent();
        boolean autoupdate = i.getBooleanExtra("AUTO_KEY", true);
        return autoupdate;
    }

    public boolean receiveEnableStartPoint(){
        Intent i = getIntent();
        boolean enableStartPoint = i.getBooleanExtra("STARTPOINT_KEY", true);
        return enableStartPoint;
    }

    public void setRobotTextView(int [] robotpoint){
        if(robotpoint[0]<0){
            robotPosition.setText("x:-- , y:--");
        }else {
            robotPosition.setText("x:" + (robotpoint[0])+" , y:"+(robotpoint[1]));
        }
    }

    public void sendWaypointTextView(int[] waypoint){
        this.waypoint = waypoint;
    }
    /*public void buttonClick(View v) {
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

    @Override
    public void onEnablePlotReceivedBoolean(boolean data) {
        this.enablePlotReceived = data;
    }*/


}