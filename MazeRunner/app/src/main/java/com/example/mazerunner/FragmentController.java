package com.example.mazerunner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Chronometer;
import android.widget.TextView;
import android.os.SystemClock;


public class FragmentController extends Fragment{
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
    private int status;
    private TextView statusTv; //robot status

    public boolean shortest = false;

    private final String noDeviceMsg = "No device connected";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        view = inflater.inflate(R.layout.controller_fragment,container,false);

        MainActivity activitymain = (MainActivity) getContext();
        mazeView = (MazeView) activitymain.getMazeView();
        statusTv = view.findViewById(R.id.status);

        //up button onclick
        upBtn = view.findViewById(R.id.buttonUp);
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mazeView.moveUp();
            }
        });

        //down button onclick
        downBtn = view.findViewById(R.id.buttonDown);
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mazeView.moveDown();
            }
        });

        //left button onclick
        leftBtn = view.findViewById(R.id.buttonLeft);
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mazeView.moveleft();
            }
        });

        //right button onclick
        rightBtn = view.findViewById(R.id.buttonRight);
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mazeView.moveRight();
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
                shortestChr.setVisibility(View.VISIBLE); //show stopwatch
                shortestChr.setBase(SystemClock.elapsedRealtime()); //set stopwatch to 0:00
                exploreChr.stop(); //stop if it was already running
                shortestChr.setFormat("Time: %s"); //format stopwatch's text
                shortestChr.start(); //start stopwatch
            }
        });


        return view;
    }



}