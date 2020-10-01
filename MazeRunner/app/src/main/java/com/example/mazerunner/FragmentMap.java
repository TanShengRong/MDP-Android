package com.example.mazerunner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Context;

public class FragmentMap extends Fragment{
    View view;
    public MazeView mazeView;
    public Switch autoUpdateSwitch;
    public Switch setStartpointSwitch;
    public TextView waypointTextView;
    public TextView startpointTextView;
    public Button manualRefreshButton;
    //PassDataListener fragmapListener;


    public boolean autoUpdate = true;
    public boolean enablePlotRobotPosition = false;

    public FragmentMap(){
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_fragment,container,false);

        final MainActivity activitymain = (MainActivity) getActivity();
        mazeView = (MazeView) activitymain.getMazeView();

        //auto update switch
        autoUpdateSwitch = view.findViewById(R.id.automatic_Update_Toggle);
        autoUpdateSwitch.setChecked(true);
        autoUpdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    autoUpdate = true;
                    activitymain.autoupdate = autoUpdate;
                    mazeView.invalidate();
                    manualRefreshButton.setEnabled(false);
                    manualRefreshButton.setBackgroundResource(R.drawable.disabledbutton);
                }else {
                    autoUpdate = false;
                    activitymain.autoupdate = autoUpdate;
                    manualRefreshButton.setEnabled(true);
                    manualRefreshButton.setBackgroundResource(R.drawable.commonbutton);
                }
            }
        });

        waypointTextView = view.findViewById(R.id.waypointText);
        //show current waypoint X & Y coordinates, "--" if not set
        if (mazeView.getWaypoint()[0] < 0) {
            waypointTextView.setText("x:-- , y:--");
        } else {
            waypointTextView.setText("x:" + (mazeView.getWaypoint()[0]) + " , y:"+ (mazeView.getWaypoint()[1]));
        }


        //set startpoint switch
        setStartpointSwitch = view.findViewById(R.id.set_startpoint_Toggle);
        setStartpointSwitch.setChecked(false);
        setStartpointSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton plotRobotPositionBtn, boolean isChecked) {
                if (!isChecked) {
                    enablePlotRobotPosition = false;
                    String test = Boolean.toString(enablePlotRobotPosition);
                    Log.i("FragmentMap", test);
                    activitymain.enablestartpoint = enablePlotRobotPosition;
                } else {
                    enablePlotRobotPosition = true;
                    String test = Boolean.toString(enablePlotRobotPosition);
                    Log.i("FragmentMap", test);
                    activitymain.enablestartpoint = enablePlotRobotPosition;
                }
            }
        });

        manualRefreshButton=view.findViewById(R.id.manual_refresh);
        manualRefreshButton.setEnabled(false);
        manualRefreshButton.setBackgroundResource(R.drawable.disabledbutton);
        manualRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeView.invalidate();
            }
        });
        return view;
    }

//    public void sendData(){
//        Intent i = new Intent(getActivity().getBaseContext(),MainActivity.class);
//        i.putExtra("AUTO_KEY", autoUpdate);
//        i.putExtra("STARTPOINT_KEY", enablePlotRobotPosition);
//    }


    public void setWaypointTextView(int [] waypoint) {
        if (waypoint[0] < 0) {
            waypointTextView = view.findViewById(R.id.waypointText);
            waypointTextView.setText("x:-- , y:--");
        } else {
            waypointTextView = view.findViewById(R.id.waypointText);
            waypointTextView.setText("x:" + (waypoint[0] + 1) + " , y:" + (waypoint[1] + 1));
        }
    }

//    //text view created for robot start coordinates setting
//    public void setRobotTextView(int [] robotpoint){
//        if(robotpoint[0]<0){
//            startpointTextView.setText("x:-- , y:--");
//        }else {
//            startpointTextView.setText("x:" + (robotpoint[0]+1)+" , y:"+(robotpoint[1]+1));
//        }
//    }

    /*@Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof PassDataListener){
            fragmapListener = (PassDataListener) context;
        }
        else{
            throw new RuntimeException(context.toString() + "must implement PassDataInterface");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        fragmapListener = null;
    }

    public interface PassDataListener {
        void onDataReceived(boolean autoupdate, boolean enableplotreceive);
    }

    public void setDataListener(PassDataListener listener){
        this.fragmapListener = listener;
    }*/
}
