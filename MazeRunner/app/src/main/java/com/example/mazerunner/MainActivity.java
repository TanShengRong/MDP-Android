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
    protected int[] waypoint = {1,1};//waypoint;
    protected int[] startPoint = {1,1}; //startpoint
    protected boolean autoupdate = true;
    protected boolean enablestartpoint = false;
    public TextView waypointTextView;
    public TextView startpointTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarid);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        robotPosition = (TextView) findViewById(R.id.robotPosition);


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Adding Fragments
        FragmentMap fragment_map = new FragmentMap();
        Bundle bundle = new Bundle();
        bundle.putBoolean("STARTPOINT_KEY",enablestartpoint);
        bundle.putBoolean("AUTO_KEY", autoupdate);
        fragment_map.setArguments(bundle);

        adapter.AddFragment(new FragmentBluetooth(),"Bluetooth");
        adapter.AddFragment(new FragmentComms(), "Comms");
        adapter.AddFragment(fragment_map,"Map");
        adapter.AddFragment(new FragmentController(),"Controller");

        //Adapter Setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        enablestartpoint = getIntent().getBooleanExtra("STARTPOINT_KEY", false);
        autoupdate = getIntent().getBooleanExtra("AUTO_KEY", true);


    }

    public MazeView getMazeView(){
        return findViewById(R.id.mazeView);
    }

    public boolean receiveAutoUpdate(){
        return autoupdate;
    }

    public boolean receiveEnableStartPoint(){
        return enablestartpoint;
    }

    public void setRobotTextView(int [] robotpoint){
        if(robotpoint[0]<0){
            robotPosition.setText("x:-- , y:--");
        }else {
            robotPosition.setText("x:" + (robotpoint[0])+" , y:"+(robotpoint[1]));
        }
    }

    public void sendWaypointTextView(int[] waypoint){

        if (waypoint[0] < 0 ||waypoint[1]<0) {
            waypointTextView = (TextView) findViewById(R.id.waypointText);
            waypointTextView.setText("x:-- , y:--");
        } else {
            waypointTextView = (TextView) findViewById(R.id.waypointText);
            waypointTextView.setText("x:" + (waypoint[0]) + " , y:" + (waypoint[1]));
        }
    }

    public void sendStartpointTextView(int[] startpoint){
        if (startPoint[0] < 0 ||startPoint[1]<0) {
            startpointTextView = (TextView) findViewById(R.id.startpointText);
            startpointTextView.setText("x:-- , y:--");
        } else {
            startpointTextView = (TextView) findViewById(R.id.startpointText);
            startpointTextView.setText("x:" + (startPoint[0]) + " , y:" + (startPoint[1]));
        }
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
    }*/


}