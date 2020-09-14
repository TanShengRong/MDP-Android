package com.example.mazerunner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private TextView robotPosition;
    protected int[] waypoint = {1, 1};//waypoint;
    protected int[] startPoint = {1, 1}; //startpoint
    protected boolean autoupdate = true;
    protected boolean enablestartpoint = false;
    protected boolean fastest = false;
    public TextView waypointTextView;
    public TextView startpointTextView;
    private String mdfExploredString = "";
    private String mdfObstacleString = "";
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarid);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        robotPosition = (TextView) findViewById(R.id.robotPosition);


//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Adding Fragments
        FragmentMap fragment_map = new FragmentMap();
        Bundle bundle = new Bundle();
        bundle.putBoolean("STARTPOINT_KEY", enablestartpoint);
        bundle.putBoolean("AUTO_KEY", autoupdate);
        fragment_map.setArguments(bundle);

        FragmentController fragment_controller = new FragmentController();
        Bundle bundle1 = new Bundle();
//        bundle.putBoolean("TILT_KEY", enabletilt);
        fragment_controller.setArguments(bundle1);

        adapter.AddFragment(new FragmentBluetooth(), "Bluetooth"); //1
        adapter.AddFragment(new FragmentComms(), "Comms");  //2
        adapter.AddFragment(fragment_map, "Map"); //3
        adapter.AddFragment(fragment_controller, "Controller"); //4
        Log.d("Adapter_count", "" + adapter.getCount());
        Log.d("Adapter_name", "" + adapter.getPageTitle(3));

        //Adapter Setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        enablestartpoint = getIntent().getBooleanExtra("STARTPOINT_KEY", false);
        autoupdate = getIntent().getBooleanExtra("AUTO_KEY", true);
//        enabletilt = getIntent().getBooleanExtra("TILT_KEY", false);

        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothMessageReceiver, new IntentFilter("IncomingMsg"));
    }

    public MazeView getMazeView() {
        return findViewById(R.id.mazeView);
    }

    public boolean receiveAutoUpdate() {
        return autoupdate;
    }

    public boolean receiveEnableStartPoint() {
        return enablestartpoint;
    }

    public void receiveEnableTilt(Boolean updatedFastest) {
        this.fastest = updatedFastest;
    }

    public void setRobotTextView(int[] robotpoint) {
        if (robotpoint[0] < 0) {
            robotPosition.setText("x:-- , y:--");
        } else {
            robotPosition.setText("x:" + (robotpoint[0]) + " , y:" + (robotpoint[1]));
        }
    }

    public void sendWaypointTextView(int[] waypoint) {

        if (waypoint[0] < 0 || waypoint[1] < 0) {
            waypointTextView = (TextView) findViewById(R.id.waypointText);
            waypointTextView.setText("x:-- , y:--");
        } else {
            waypointTextView = (TextView) findViewById(R.id.waypointText);
            waypointTextView.setText("x:" + (waypoint[0]) + " , y:" + (waypoint[1]));
        }
    }

    public void sendStartpointTextView(int[] startpoint) {
        if (startPoint[0] < 0 || startPoint[1] < 0) {
            startpointTextView = (TextView) findViewById(R.id.startpointText);
            startpointTextView.setText("x:-- , y:--");
        } else {
            startpointTextView = (TextView) findViewById(R.id.startpointText);
            startpointTextView.setText("x:" + (startPoint[0]) + " , y:" + (startPoint[1]));
        }
    }

    //update grid when receive from bluetooth
    private BroadcastReceiver bluetoothMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FragmentController controllerFragment = (FragmentController) adapter.getItem(3);
            // Get extra data included in the Intent
            String theText = intent.getStringExtra("receivingMsg");
            MazeView mazeView = getMazeView();
            if (theText.length() > 0){
                // check for fastest path string from algo
                if(theText.length()< 15 && fastest && (theText.contains("R")||theText.contains("L") || theText.contains("F"))){
                    int forwardDistance;
                    //split string to get direction & tiles to move
                    String[] fastestCommands = theText.split("");
                    //move robot in order of command received
                    for(int i =0; i<fastestCommands.length;i++){
                        if (fastestCommands[i].equals("F")) {
                            mazeView.robotX.add(mazeView.robotCenter[0]);
                            mazeView.robotY.add(mazeView.robotCenter[1]);
                            mazeView.moveForward();
                        }
                        else if(fastestCommands[i].equals("R")){
                            mazeView.turnRight();
                        }
                        else if(fastestCommands[i].equals("L")) {
                            mazeView.turnLeft();
                        }
                        else{
                            //Exception catching in case the string format is wrong
                            try{
                                forwardDistance= Integer.parseInt(fastestCommands[i]);
                                for(int j = 0; j<(forwardDistance+1);j++) {
                                    mazeView.robotX.add(mazeView.robotCenter[0]);
                                    mazeView.robotY.add(mazeView.robotCenter[1]);
                                    mazeView.moveForward();
                                }
                            }
                            catch (Exception e){
                                Log.d("FP String", "String format wrong");
                            }

                        }
                    }
                }
                // Identifying mdf string send for real-time update of maze during exploration
                else if (theText.length() > 77&& theText.contains(":")&& !fastest) {
                    String[] stringItems = theText.split(":");
                    try {
                    } catch (Exception e) {
                        Log.d("MDF String", "AL format wrong");
                    }
                    //Getting the explored grids from MDF string
                    String[] exploredString = hexToBinary(stringItems[0]).split("");
                    // temporary storage
                    String bin = "";
                    int[] exploredGrid = new int[exploredString.length - 5]; //-5 to make it 300
                    for (int i = 0; i < exploredGrid.length; i++) {
                        exploredGrid[i] = Integer.parseInt(exploredString[i + 3]); // because first element is ""
                        bin += exploredGrid[i];
                        //Storing explored and obstacle strings
                        mdfExploredString = stringItems[0];
                        mdfObstacleString = stringItems[1];
                    }
                    String text = "";
                    //Getting the obstacle grids from MDF string
                    String[] obstacleString = hexToBinary(stringItems[1]).split("");
                    int[] obstacleGrid = new int[obstacleString.length - 1];
                    Log.d("TAG", text);
                    for (int i = 0; i < obstacleGrid.length; i++) {
                        obstacleGrid[i] = Integer.parseInt(obstacleString[i + 1]);
                    }
                    int inc = 0;
                    int inc2 = 0;
                    for (int y = 0; y < 20; y++) {
                        for (int x = 0; x < 15; x++) {
                            //For explored grids, draw obstacle if any
                            if (exploredGrid != null && exploredGrid[inc] == 1) {
                                if (obstacleGrid != null && obstacleGrid[inc2] == 1) {
                                    mazeView.setObsArray(x,y);
                                }
                                inc2++;
                            }
                            inc++;
                        }
                    }
                    //update the obstacles and explored grids
                    mazeView.updateMaze(exploredGrid, obstacleGrid);
                    //Getting the direction the robot is facing
                    if (stringItems.length >= 5) {
                        int direction= 0;
                        if(stringItems[4].equals("N"))
                            direction = 0;
                        else if(stringItems[4].equals("E"))
                            direction = 90;
                        else if(stringItems[4].equals("S"))
                            direction = 180;
                        else if(stringItems[4].equals("W"))
                            direction = 270;

                        //Updating coordinates of robot according to string receive from algorithm
                        mazeView.updateRobotCoords(Integer.parseInt(stringItems[2]),Integer.parseInt(stringItems[3])
                                ,direction);
                    }

                    //This segment of the string stores information of identified image and their coordinates
                    if(stringItems.length >= 6){
                        //changing string to int
                        int numberX = Integer.parseInt(stringItems[5]);
                        int numberY = Integer.parseInt(stringItems[6]);
                        //Checking to see if received a valid numberid
                        boolean correctId = Pattern.matches("^[1-9][0-5]?$", stringItems[7]);
                        if (correctId) {
                            ArrayList<String> tempObsArray = mazeView.getObsArray();
                            String tempPos = (numberX-1) + "," + (numberY-1);
                            boolean checkObs = false;
                            for (int i=0; i<tempObsArray.size(); i++){
                                if (tempObsArray.get(i).equals(tempPos)) {
                                    checkObs = true;
                                }
                            }
                            if (checkObs){
                                mazeView.updateNumberID(numberX,numberY,stringItems[7]);
                            }
                        }

                    }
                }
                else if(theText.equals("Explored")) {
                    //exploration completed
                    //for explore stopwatch
                    controllerFragment.stopExploreChr();
                    //for shortest stopwatch
                    controllerFragment.stopShortestChr();
//                    statusTv.setText("Exploration completed"); //update status
//                    String imageStr = ""; //create string to store infomation on images found
//                    if (mazeView.numberID != null){
//                        //if images were found, loop through X, Y, ID and add to string
//                        for (int i=0; i<mazeView.numberID.size(); i++){
//                            imageStr = imageStr + "("+(mazeView.numberIDX.get(i)-1)+", "+(mazeView.numberIDY.get(i)-1)+", "+mazeView.numberID.get(i)+")\n";
//                        }
//                    }
//                    //message that contains MDF and image information
//                    String message = "MDF String: \n"+mdfExploredString+":"+mdfObstacleString + "\n\nImage String(X, Y, ID): \n"+imageStr;
//                    BaseMessage msgOut = new BaseMessage(0, message); //id=0 because we are the one who send this
//                    messageList.add(messageList.size(), msgOut); //add message to array of messages
//                    chatAdapter.notifyDataSetChanged(); //update message array
//                    chr.stop();
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            sendCtrlToBtAct("AR,AN,C"); //send to arduino to calibrate
//                        }
//                    }, 2000); //2 seconds later
//                }
//                else {
//                    //normal message sent from device
//                    BaseMessage msgOut = new BaseMessage(1, theText); //id=1 because message sent from others
//                    messageList.add(messageList.size(), msgOut); //add message to array of messages
//                    chatAdapter.notifyDataSetChanged(); //update message array
//                }
                }
            }
        }
    };
    //method to convert hex to binary
    private String hexToBinary(String hex) {
        int pointer = 0;
        String binary = "";
        String partial;
        // 1 Hex digits each time to prevent overflow and recognize leading 0000
        while (hex.length() - pointer > 0) {
            partial = hex.substring(pointer, pointer + 1);
            String bin;
            bin = Integer.toBinaryString(Integer.parseInt(partial, 16));
            for (int i = 0; i < 4 - bin.length(); i++) {
                binary = binary.concat("0");  // padding 0 in front
            }
            binary = binary.concat(bin); // then add in the converted hextobin
            pointer += 1;
        }
        return binary;
    };
}