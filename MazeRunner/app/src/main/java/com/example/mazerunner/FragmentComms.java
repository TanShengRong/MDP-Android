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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.comms_fragment,container,false);
        sharedPref = getActivity().getSharedPreferences("mdp0032", Context.MODE_PRIVATE);
        RadioGroup stringRadioGroup = (RadioGroup) view.findViewById(R.id.f1f2String);
        stringRadioGroup.setOnCheckedChangeListener(this);

        //for msg listview
        communicationLog = view.findViewById(R.id.communicationLog);
        sendString = view.findViewById(R.id.sendString);
        saveString = view.findViewById(R.id.saveString);
        outMessage = view.findViewById(R.id.stringToBeSent);
        sendString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = outMessage.getText().toString().getBytes(Charset.defaultCharset());

                if(BluetoothChat.writeMsg(bytes)){

                    messageList.add("Sent : " + outMessage.getText().toString());
                    messageListAdapter = new CommunicationListAdapter(getActivity().getApplicationContext(), R.layout.list_adapter_communication, messageList);
                    communicationLog.setAdapter(messageListAdapter);
                    outMessage.setText("");

                }
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
                  }
                  int duration = Toast.LENGTH_SHORT;
                  Toast.makeText(context, toastText, duration).show();
              }
          });
//        //add action bar
//        getSupportActionBar().setIcon(R.drawable.logo_colour);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setSubtitle("Messsaging");
        return view;
    }

    BroadcastReceiver bluetoothMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivingMsg = intent.getStringExtra("receivingMsg");
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
        System.out.println(checkedRadioButtonId);
        TextView MessageTxtview = view.findViewById(R.id.stringToBeSent);
        MessageTxtview.setText(storedString);
    }

    /*public void onSaveClicked(View view){
        Log.d(TAG, "onSaveClicked running");
        TextView MessageTxtview =  view.findViewById(R.id.stringToBeSent);
        String userInput = MessageTxtview.getText().toString();
        Context context = getActivity().getApplicationContext();
        CharSequence toastText = "";

        RadioButton selectedRadioButton = (RadioButton)view.findViewById(checkedRadioButtonId);
        String selectedRdbName = selectedRadioButton.getText().toString();
        if ( selectedRdbName.equals("F1")) {
            sharedPref.edit().putString("F1", userInput).commit();
            toastText = "F1 has been updated";

        }
        else if (selectedRdbName.equals("F2")) {
            sharedPref.edit().putString("F2", userInput).commit();
            toastText = "F2 has been updated";
        }
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, toastText, duration).show();
    }*/
}