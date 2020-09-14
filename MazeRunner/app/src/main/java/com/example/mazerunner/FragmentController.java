package com.example.mazerunner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Chronometer;
import android.widget.Switch;
import android.widget.TextView;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.content.Context.SENSOR_SERVICE;


public class FragmentController extends Fragment implements SensorEventListener {
    View view;
    MazeView mazeView;

    private ImageButton upBtn;
    private ImageButton downBtn;
    private ImageButton leftBtn;
    private ImageButton rightBtn;
    private Chronometer exploreChr;
    private Chronometer shortestChr;
    private ImageButton refreshBtn;
    private ImageButton explorationBtn ;
    private ImageButton shortestBtn;
    public Switch tiltSwitch;
    private int status;
    private TextView statusTv; //robot status

    public boolean shortest = false;

    public boolean enabletilt = true;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private final String noDeviceMsg = "No device connected";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        view = inflater.inflate(R.layout.controller_fragment,container,false);

        final MainActivity activitymain = (MainActivity) getActivity();
//        MainActivity activitymain = (MainActivity) getContext();
        mazeView = (MazeView) activitymain.getMazeView();
        statusTv = view.findViewById(R.id.status);

        //up button onclick
        upBtn = view.findViewById(R.id.buttonUp);
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeView.moveUp();
                statusTv.setText("Robot Moving Up");
            }
        });

        //down button onclick
        downBtn = view.findViewById(R.id.buttonDown);
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeView.moveDown();
                statusTv.setText("Robot Moving Down");
            }
        });

        //left button onclick
        leftBtn = view.findViewById(R.id.buttonLeft);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeView.moveLeft();
                statusTv.setText("Robot Moving Left");
            }
        });

        //right button onclick
        rightBtn = view.findViewById(R.id.buttonRight);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeView.moveRight();
                statusTv.setText("Robot Moving Right");
            }
        });

        //for explore stopwatch
        exploreChr = (Chronometer) view.findViewById(R.id.exploreTimer);

        //for shortest stopwatch
        shortestChr = (Chronometer) view.findViewById(R.id.shortestTimer);

        //restart maze, buttons, status textview and chronometer
        refreshBtn = view.findViewById(R.id.buttonReset);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeView.clearExploredGrid();
                mazeView.clearNumID();
                mazeView.clearObstacleGrid();
                mazeView.updateRobotCoords(1,1,0);
                mazeView.clearRobot();
                explorationBtn.setEnabled(true);
                shortestBtn.setEnabled(true);
                mazeView.clearObsArray();
                statusTv.setText("Waiting for instructions");
                shortest = false;
                exploreChr.setBase(SystemClock.elapsedRealtime()); //set stopwatch to 0:00
                exploreChr.stop();
                shortestChr.setBase(SystemClock.elapsedRealtime()); //set stopwatch to 0:00
                shortestChr.stop();
            }
        });

        //start exploration
        explorationBtn = view.findViewById(R.id.buttonExplore);
        explorationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear maze for new exploration
                mazeView.clearExploredGrid();
                mazeView.clearNumID();
                mazeView.clearObstacleGrid();
                mazeView.clearObsArray();

                final int tempX = mazeView.getWaypoint()[0] + 1;
                final int tempY = mazeView.getWaypoint()[1] + 1;

                //sendCtrlToBtAct("AR,AN,E"); //send exploration message to arduino
                explorationBtn.setEnabled(false); //disable exploration button
                shortestBtn.setEnabled(true); //enable fastest button
                activitymain.receiveEnableTilt(shortestBtn.isEnabled());
                statusTv.setText("Exploration in progress..."); //update status
                exploreChr.setBase(SystemClock.elapsedRealtime()); //set stopwatch to 0:00
                shortestChr.stop(); //stop in case there is currently stopwatch running
                exploreChr.setFormat("Time: %s"); //format stopwatch's text
                exploreChr.start(); //start stopwatch
            }
        });

        //start fastest path
        shortestBtn = view.findViewById(R.id.buttonShortest);
        shortestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sendCtrlToBtAct("PC,AN,FP"); //send fastest path message to algorithm
                shortest = true;
                explorationBtn.setEnabled(true); //enable exploration button
                shortestBtn.setEnabled(false); //disable fastest button
                statusTv.setText("Fastest path in progress..."); //update status
//                shortestChr.setVisibility(View.VISIBLE); //show stopwatch
                shortestChr.setBase(SystemClock.elapsedRealtime()); //set stopwatch to 0:00
                exploreChr.stop(); //stop if it was already running
                shortestChr.setFormat("Time: %s"); //format stopwatch's text
                shortestChr.start(); //start stopwatch
            }
        });

        //auto update switch
        //tilting
        sensorManager = (SensorManager) this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 200000);
        tiltSwitch = view.findViewById(R.id.switchTilt);
        tiltSwitch.setChecked(false);
        tiltSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    enabletilt = true;
                    registerSensorListener();
                    Toast.makeText(getContext(), "Tilt activated", Toast.LENGTH_SHORT).show();
                }else {
                    enabletilt = false;
                    unregisterSensorListener();
                    Toast.makeText(getContext(), "Tilt deactivated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    private void registerSensorListener() {
        sensorManager.registerListener(this, sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_FASTEST);
    }
    private void unregisterSensorListener() {
        sensorManager.unregisterListener(this);
    }
        @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        if (enabletilt){
            //move robot only if tilt has been enabled
            if (y < -5){
                //device has been tilted forward
                Toast.makeText(getContext(), "Tilt forward", Toast.LENGTH_SHORT).show();
                mazeView.moveUp();
                statusTv.setText("Robot Moving Up");
            } else if (x < -5){
                //device has been tilted to the right
                Toast.makeText(getContext(), "Tilt right", Toast.LENGTH_SHORT).show();
                mazeView.moveRight();
                statusTv.setText("Robot Moving Right");
            } else if (x > 5){
                //device has been tilted to the left
                Toast.makeText(getContext(), "Tilt left", Toast.LENGTH_SHORT).show();
                mazeView.moveLeft();
                statusTv.setText("Robot Moving Left");
            } else if (y > 5){
                //device tilted to the bottom
                Toast.makeText(getContext(), "Tilt downwards(bottom)", Toast.LENGTH_SHORT).show();
                mazeView.moveDown();
                statusTv.setText("Robot Moving Down");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    public void stopShortestChr(){
//        this.shortestChr.stop(); //start stopwatch
        Log.d("Btn access", "");
    }
    public void stopExploreChr(){
        this.exploreChr.stop(); //start stopwatch
    }
}