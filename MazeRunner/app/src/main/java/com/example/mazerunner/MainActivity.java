package com.example.mazerunner;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements FragmentBluetooth.OnMessageChangedListener{

    private TabLayout tabLayout;
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
    private Chronometer shortestChr;
    private TextView statusTv;
    private Chronometer exploreChr;
    protected String receivemsg;
    private static FragmentComms fragment_comms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        robotPosition = (TextView) findViewById(R.id.robotPosition);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Adding Fragments
        FragmentMap fragment_map = new FragmentMap();
        Bundle bundle = new Bundle();
        bundle.putBoolean("STARTPOINT_KEY", enablestartpoint);
        bundle.putBoolean("AUTO_KEY", autoupdate);
        fragment_map.setArguments(bundle);

        FragmentController fragment_controller = new FragmentController();
        Bundle bundle1 = new Bundle();
        fragment_controller.setArguments(bundle1);

        FragmentBluetooth fragment_bluetooth = new FragmentBluetooth();
        Bundle bundle2 = new Bundle();
//        bundle.putString("receiveMsg", receivemsg);
        fragment_bluetooth.setArguments(bundle2);

//        FragmentComms fragment_comms = new FragmentComms();
        fragment_comms = new FragmentComms();
        Bundle bundle3 = new Bundle();
        bundle.putString("receiveMsg", receivemsg);
        fragment_bluetooth.setArguments(bundle3);

        adapter.AddFragment(fragment_bluetooth, "Bluetooth"); //0
        adapter.AddFragment(fragment_comms, "Comms");  //1
        adapter.AddFragment(fragment_map, "Map"); //2
        adapter.AddFragment(fragment_controller, "Controller"); //3
        Log.d("Adapter_count", "" + adapter.getCount());
        Log.d("Adapter_name", "" + adapter.getPageTitle(2));

        //Adapter Setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        enablestartpoint = getIntent().getBooleanExtra("STARTPOINT_KEY", false);
        autoupdate = getIntent().getBooleanExtra("AUTO_KEY", true);
        receivemsg = getIntent().getStringExtra("receiveMsg");
//        enabletilt = getIntent().getBooleanExtra("TILT_KEY", false);
        shortestChr = (Chronometer) findViewById(R.id.shortestTimer);
//        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothMessageReceiver, new IntentFilter("IncomingMsg"));
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

    public void receiveShortestPath(Boolean updatedFastest) {
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
        int pos = viewPager.getCurrentItem();
        String spos = Integer.toString(pos);
        Log.d("sendwaypoint", spos);
        if (viewPager.getCurrentItem()!=0){

            if (waypoint[0] < 0 || waypoint[1] < 0) {
                waypointTextView = (TextView) findViewById(R.id.waypointText);
                waypointTextView.setText("x:-- , y:--");
            } else {

                waypointTextView = (TextView) findViewById(R.id.waypointText);
                waypointTextView.setText("x:" + (waypoint[0]) + " , y:" + (waypoint[1]));
                String message = "waypoint x" + waypoint[0] + "y" + waypoint[1];
                byte [] bytes = message.getBytes(Charset.defaultCharset());
                sendCtrlToBtAct(bytes);
            }
        }

    }

    public void sendStartpointTextView(int[] startPoint) {
        if (viewPager.getCurrentItem()!=0) {
            if (startPoint[0] < 0 || startPoint[1] < 0) {
                startpointTextView = (TextView) findViewById(R.id.startpointText);
                startpointTextView.setText("x:-- , y:--");
            } else {
                startpointTextView = (TextView) findViewById(R.id.startpointText);
                startpointTextView.setText("x:" + (startPoint[0]) + " , y:" + (startPoint[1]));
                String message = "start point x" + startPoint[0] + "y" + startPoint[1];
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                sendCtrlToBtAct(bytes);
            }
        }
    }
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
    // send control to bluetooth socket
    public void sendCtrlToBtAct(String msg) {
        // write out
//        BluetoothChat.writeMsg(bytes);

        Intent intent = new Intent("getCtrlToSend");
        // You can also include some extra data.
        intent.putExtra("control", msg);
        android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    //to send bluetoothactivity for bluetooth chat
    public void sendToBtAct(String msg) {
        Intent intent = new Intent("getTextToSend");
        // You can also include some extra data.
        intent.putExtra("tts", msg);
        android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    //to send bluetoothactivity for bluetooth chat
    public void sendToCommsFrag(String msg) {
        Intent intent = new Intent("IncomingMsg");
        // You can also include some extra data.
        intent.putExtra("receivingMsg", msg);
        android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void stopFastestWatch(){
        shortestChr = (Chronometer) findViewById(R.id.shortestTimer);
        shortestChr.stop();
    }
    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof FragmentBluetooth) {
            FragmentBluetooth bluetoothFragment = (FragmentBluetooth) fragment;
            bluetoothFragment.setOnMessageChangedListener(this);
        }
    }
    //update grid when receive from bluetooth
    @Override
    public void onMessageChanged(String msg) {
        MazeView mazeView = getMazeView();
        if (msg.length() > 0) {
            Log.d("string received", "" + msg.length());
            // Identifying mdf string sent for real-time update of maze during exploration
            // ROBOT:{"map": "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff","obstacle": "000000000400000001c800000000000700000000800000001f8000070000000002000000000","robotCenter": "(1, 1)","robotHead": "(1, 2)","heading": "N"}
            if (msg.contains("ROBOT")) {
                // Identifying mdf string send for real-time update of maze during exploration
                Log.d("algo ROBOT parsing", msg);
                try {
                    msg = msg.substring(6);
                    JSONObject jObject = new JSONObject(msg);

                    // Update grid
                    mdfExploredString = jObject.getString("map");
                    mdfObstacleString = jObject.getString("obstacle");
                    //Getting the explored grids from MDF string by making it into str array and changing to binary for accessing
                    String[] exploredString = hexToBinary(mdfExploredString).split(""); //split to get string array
                    Log.d("explored after convert", java.util.Arrays.toString(exploredString));
                    Log.d("exploredstring LEN", "" + exploredString.length);
                    int[] exploredGrid = new int[exploredString.length - 5]; //-4 to make it 300 -1 for "
                    for (int i = 0; i < exploredGrid.length; i++) {
                        exploredGrid[i] = Integer.parseInt(exploredString[i + 3]); // because first element is "
                    }
                    Log.d("explored grid", java.util.Arrays.toString(exploredGrid));

                    String[] obstacleString = hexToBinary(mdfObstacleString).split("");
                    Log.d("obstacle after convert", java.util.Arrays.toString(obstacleString));

                    int[] obstacleGrid = new int[obstacleString.length - 1]; //-1 for "
                    for (int i = 0; i < obstacleGrid.length; i++) {
                        obstacleGrid[i] = Integer.parseInt(obstacleString[i + 1]); // because first element is ""
                    }
                    Log.d("obstacleGrid", java.util.Arrays.toString(obstacleGrid));
                    int inc = 0;
                    int inc2 = 0;
                    for (int y = 0; y < 20; y++) {
                        for (int x = 0; x < 15; x++) {
                            //For explored grids, draw obstacle if any
                            if (exploredGrid != null && exploredGrid[inc] == 1) {
                                if (obstacleGrid != null && obstacleGrid[inc2] == 1) {
                                    mazeView.setObsArray(x, y);
                                }
                                inc2++;
                            }
                            inc++;
                        }
                    }
                    mazeView.updateMaze(exploredGrid, obstacleGrid);

                    // Update robot position
                    String robotCenter = jObject.getString("robotCenter");
                    robotCenter = robotCenter.substring(1, robotCenter.length() - 1); //remove curly brackets
                    String[] robotCenterCoord = robotCenter.split(", ");
                    int robotCenterX = Integer.parseInt(robotCenterCoord[0]);
                    int robotCenterY = Integer.parseInt(robotCenterCoord[1]);
                    int robotHeading;
                    switch (jObject.getString("heading")) {
                        case "N"://^
                            robotHeading = 0;
                            break;
                        case "E"://>
                            robotHeading = 90;
                            break;
                        case "S"://v
                            robotHeading = 180;
                            break;
                        case "W"://<
                            robotHeading = 270;
                            break;
                        default://gg
                            Log.d("robotHeading", "invalid");
                            robotHeading = 0;
                    }
                    mazeView.updateRobotCoords(robotCenterX, robotCenterY, robotHeading);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
                // Plot image recognised on obstacle
                // e.g. NumberIDABXY where AB is 01 to 15; X is 01-15, Y is 01-20
                // AB is number id; X is x-axis; Y is y-axis
            else if (msg.contains("NumberID")) {
                Log.d("NumberID", msg);
                boolean isNumberIDStr = Pattern.matches("^[a-zA-z]{8}[0-9]{6}$", msg);
                if (isNumberIDStr) {
                    Log.d("NumberID", "isNumberIDStr");
                    int numberId = Integer.parseInt(msg.substring(8, 10)); // index 8-9
                    int xCoord = Integer.parseInt(msg.substring(10, 12)); // index 10-11
                    int yCoord = Integer.parseInt(msg.substring(12, 14)); // index 12-13
                    boolean isValidNumbers = (numberId > 0 && numberId <= 15) &&
                            (xCoord > 0 && xCoord <= 15)  &&
                            (yCoord > 0 && yCoord <= 20);
                    if (isValidNumbers) {
                        Log.d("NumberID", "isValidNumbers");
                        ArrayList<String> tempObsArray = mazeView.getObsArray();
                        String tempPos = (xCoord-1) + "," + (yCoord-1);
                        boolean isOnObstacle;
                        for (int i=0; i<tempObsArray.size(); i++){
                            isOnObstacle = tempObsArray.get(i).equals(tempPos);
                            if (isOnObstacle) {
                                mazeView.updateNumberID(xCoord, yCoord, ""+numberId);
                                break;
                            }
                        }
                    }
                }
            } else if (msg.equals("EX_DONE")) { // exploration finished
                //exploration completed
                //for explore stopwatch
                stopExploreWatch();
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
            } else {
                //normal message sent from device
//                FragmentComms commsFrag = (FragmentComms)
//                        getSupportFragmentManager().getFragments().get(1);
                FragmentComms commsFrag = (FragmentComms)
                        adapter.getItem(1);
                commsFrag.updateCommsList(msg);
//                try {
//                    fragment_comms.updateCommsList(msg);
//                } catch (Exception e) {
//
//                }
//                Log.d("IncomingMsg", msg);
//                Intent intent = new Intent("IncomingMsg");
//                // You can also include some extra data.
//                intent.putExtra("receivingMsg", msg);
//                android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }


    public void stopExploreWatch(){
        exploreChr = (Chronometer) findViewById(R.id.exploreTimer);
        exploreChr.stop();

    }
}
