package com.example.mazerunner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class FragmentComms extends Fragment implements RadioGroup.OnCheckedChangeListener{
    View view;
    private static final String TAG="CommunicationFragment";
    SharedPreferences sharedPref;
    int checkedRadioButtonId = 0;
    //for msg listview
    ListView communicationLog;
    Button sendString;
    Button saveString;
    EditText outMessage;
    public ArrayList<String> messageList = new ArrayList<>();
    public CommunicationListAdapter messageListAdapter;
    private  MainActivity activitymain;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activitymain = (MainActivity) getActivity();
        view = inflater.inflate(R.layout.comms_fragment,container,false);
        sharedPref = getActivity().getSharedPreferences("mdp0032", Context.MODE_PRIVATE);
        RadioGroup stringRadioGroup = (RadioGroup) view.findViewById(R.id.f1f2String);
        stringRadioGroup.setOnCheckedChangeListener(this);

        //for msg listview
        communicationLog = view.findViewById(R.id.communicationLog);
        sendString = view.findViewById(R.id.sendString);
        saveString = view.findViewById(R.id.saveString);
        outMessage = view.findViewById(R.id.stringToBeSent);
        messageListAdapter = new CommunicationListAdapter(getActivity().getApplicationContext(), R.layout.list_adapter_communication, messageList);
        communicationLog.setAdapter(messageListAdapter);
//        sendString.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                byte[] bytes = outMessage.getText().toString().getBytes(Charset.defaultCharset());
//                String message = outMessage.getText().toString();
//                activitymain.sendToBtAct(message);
//                // === append to messagelist
////                if(BluetoothChat.writeMsg(message)){
////
//                    messageList.add("Sent : " + outMessage.getText().toString());
//                    messageListAdapter = new CommunicationListAdapter(getActivity().getApplicationContext(), R.layout.list_adapter_communication, messageList);
//                    communicationLog.setAdapter(messageListAdapter);
//                    outMessage.setText("");
////
////                }
//            }
//        });

//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(bluetoothMessageReceiver, new IntentFilter("IncomingMsg"));
//
//        saveString.setOnClickListener(new View.OnClickListener() {
//              @Override
//              public void onClick(View v) {
//                  Log.d(TAG, "onSaveClicked running");
//                  TextView MessageTxtview = view.findViewById(R.id.stringToBeSent);
//                  String userInput = MessageTxtview.getText().toString();
//                  Context context = getActivity().getApplicationContext();
//                  CharSequence toastText = "";
//
//                  RadioButton selectedRadioButton = (RadioButton) view.findViewById(checkedRadioButtonId);
//                  String selectedRdbName = selectedRadioButton.getText().toString();
//                  if (selectedRdbName.equals("F1")) {
//                      sharedPref.edit().putString("F1", userInput).commit();
//                      toastText = "F1 has been updated";
//
//                  } else if (selectedRdbName.equals("F2")) {
//                      sharedPref.edit().putString("F2", userInput).commit();
//                      toastText = "F2 has been updated";
//                  }
//                  int duration = Toast.LENGTH_SHORT;
//                  Toast.makeText(context, toastText, duration).show();
//              }
//          });
        return view;
    }
    //    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    @Override
    @Nullable
    @NonNull
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        sendString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = outMessage.getText().toString();
                activitymain.sendToBtAct(message);
                messageList.add("Sent : " + outMessage.getText().toString());
                messageListAdapter = new CommunicationListAdapter(getActivity().getApplicationContext(), R.layout.list_adapter_communication, messageList);
                communicationLog.setAdapter(messageListAdapter);
                outMessage.setText("");
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(bluetoothMessageReceiver, new IntentFilter("IncomingMsg"));

        saveString.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Log.d(TAG, "onSaveClicked running");
                  TextView MessageTxtview = view.findViewById(R.id.stringToBeSent);
                  String userInput = MessageTxtview.getText().toString();
                  Context context = getActivity().getApplicationContext();
                  CharSequence toastText = "";

                  RadioButton selectedRadioButton = (RadioButton) view.findViewById(checkedRadioButtonId);
                  String selectedRdbName = selectedRadioButton.getText().toString();
                  if (selectedRdbName.equals("F1")) {
                      sharedPref.edit().putString("F1", userInput).commit();
                      toastText = "F1 has been updated";

                  } else if (selectedRdbName.equals("F2")) {
                      sharedPref.edit().putString("F2", userInput).commit();
                      toastText = "F2 has been updated";
                  } else if (selectedRdbName.equals("mdfString")) {
//                      sharedPref.edit().putString("mdfString", userInput).commit();
//                      toastText = "mdfString has updated grid";
                      MazeView mazeView = activitymain.getMazeView();
                      String mdf1mdf2 = sharedPref.getString("mdfString", "empty");
                      if (mdf1mdf2.equals("empty")) {
                          Log.d("mdf1mdf2PRESSED", mdf1mdf2);
                          return;
                      };
                      String[] _mdf1mdf2 = mdf1mdf2.split(":");
                      String mdfExploredString = _mdf1mdf2[0];
//                      String mdfExploredString = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
                      String mdfObstacleString = _mdf1mdf2[1];
//                      String mdfObstacleString = "000000000400000001c800000000000700000000800000001f8000070000000002000000000";
                      // add in viewlist
                      messageList.add("mdfExploredString : " + mdfExploredString);
                      messageList.add("mdfObstacleString : " + mdfObstacleString);
                      messageListAdapter = new CommunicationListAdapter(getActivity().getApplicationContext(), R.layout.list_adapter_communication, messageList);
                      communicationLog.setAdapter(messageListAdapter);
                      outMessage.setText("");
                      //===
                      String[] exploredString = activitymain.hexToBinary(mdfExploredString).split(""); //split to get string array
                      Log.d("explored after convert", java.util.Arrays.toString(exploredString));
                      Log.d("exploredstring LEN", "" + exploredString.length);
                      int[] exploredGrid = new int[exploredString.length - 5]; //-4 to make it 300 -1 for "
                      for (int i = 0; i < exploredGrid.length; i++) {
                          exploredGrid[i] = Integer.parseInt(exploredString[i + 3]); // because first element is "
                      }
                      Log.d("explored grid", java.util.Arrays.toString(exploredGrid));

                      String[] obstacleString = activitymain.hexToBinary(mdfObstacleString).split("");
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
                  }
                  int duration = Toast.LENGTH_SHORT;
                  Toast.makeText(context, toastText, duration).show();
              }
          });
    };

    BroadcastReceiver bluetoothMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivingMsg = intent.getStringExtra("receivingMsg");
//            messageList.add("Received : " + receivingMsg);

//            String receivingMsg = activitymain.receivemsg;
            messageList.add("Received : " + receivingMsg);
            Log.d("BluetoothActivity", receivingMsg);
            messageListAdapter = new CommunicationListAdapter(getActivity().getApplicationContext(), R.layout.list_adapter_communication, messageList);
            communicationLog.setAdapter(messageListAdapter);
        }
    };

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d(TAG, "onCheckedChanged running");
        checkedRadioButtonId = checkedId;
        String storedString = "";
        RadioButton selectedRadioButton = (RadioButton)view.findViewById(checkedRadioButtonId);
        String selectedRdbName = selectedRadioButton.getText().toString();
        System.out.println("xxx " + selectedRdbName);

        if ( selectedRdbName.equals("F1")) {
            storedString = sharedPref.getString("F1", "empty");

        }
        else if (selectedRdbName.equals("F2")) {
            storedString = sharedPref.getString("F2", "empty");
        }
        else if (selectedRdbName.equals("mdfString")) {
            storedString = sharedPref.getString("mdfString", "empty");
        }
        System.out.println(checkedRadioButtonId);
        TextView MessageTxtview = view.findViewById(R.id.stringToBeSent);
        MessageTxtview.setText(storedString);
    }

    public void updateCommsList(String message) {
        messageList.add("Received : " + message);
        messageListAdapter = new CommunicationListAdapter(getActivity().getApplicationContext(), R.layout.list_adapter_communication, messageList);
        communicationLog.setAdapter(messageListAdapter);
        outMessage.setText("");
    }

    public void updateMdfString(String mdf1mdf2) {
        sharedPref.edit().putString("mdfString", mdf1mdf2).commit();
        Toast.makeText(getActivity().getApplicationContext(), "mdfString has been updated", Toast.LENGTH_SHORT).show();
    }
}