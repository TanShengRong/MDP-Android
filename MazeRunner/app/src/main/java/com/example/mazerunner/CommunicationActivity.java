/*
package com.example.mazerunner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
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
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CommunicationActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG="CommunicationActivity";
    SharedPreferences sharedPref;
    int checkedRadioButtonId = 0;
    //for msg listview
    ListView communicationLog;
    Button sendString;
    EditText outMessage;
    public ArrayList<String> messageList = new ArrayList<>();
    public CommunicationListAdapter messageListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        sharedPref = getSharedPreferences("mdp0032", MODE_PRIVATE);
        RadioGroup stringRadioGroup = (RadioGroup) findViewById(R.id.f1f2String);
        stringRadioGroup.setOnCheckedChangeListener(this);

        //for msg listview
        communicationLog = findViewById(R.id.communicationLog);
        sendString = findViewById(R.id.sendString);
        outMessage = findViewById(R.id.stringToBeSent);
        sendString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = outMessage.getText().toString().getBytes(Charset.defaultCharset());

                if(BluetoothChat.writeMsg(bytes)){

                    messageList.add("Sent : " + outMessage.getText().toString());
                    messageListAdapter = new CommunicationListAdapter(getApplicationContext(), R.layout.list_adapter_communication, messageList);
                    communicationLog.setAdapter(messageListAdapter);
                    outMessage.setText("");

                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothMessageReceiver, new IntentFilter("IncomingMsg"));

//        //add action bar
//        getSupportActionBar().setIcon(R.drawable.logo_colour);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setSubtitle("Messsaging");
    }

    BroadcastReceiver bluetoothMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivingMsg = intent.getStringExtra("receivingMsg");
            messageList.add("Received : " + receivingMsg);
            Log.d("BluetoothActivity", receivingMsg);
            messageListAdapter = new CommunicationListAdapter(getApplicationContext(), R.layout.list_adapter_communication, messageList);
            communicationLog.setAdapter(messageListAdapter);
        }
    };

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d(TAG, "onCheckedChanged running");
        checkedRadioButtonId = checkedId;
        String storedString = "";
        RadioButton selectedRadioButton = (RadioButton)findViewById(checkedRadioButtonId);
        String selectedRdbName = selectedRadioButton.getText().toString();
        System.out.println("xxx " + selectedRdbName);

        if ( selectedRdbName.equals("F1")) {
            storedString = sharedPref.getString("F1", "empty");

        }
        else if (selectedRdbName.equals("F2")) {
            storedString = sharedPref.getString("F2", "empty");
        }
        System.out.println(checkedRadioButtonId);
        TextView MessageTxtview = findViewById(R.id.stringToBeSent);
        MessageTxtview.setText(storedString);
    }

    public void onSaveClicked (View view){
        Log.d(TAG, "onSaveClicked running");
        TextView MessageTxtview =  findViewById(R.id.stringToBeSent);
        String userInput = MessageTxtview.getText().toString();
        Context context = getApplicationContext();
        CharSequence toastText = "";

        RadioButton selectedRadioButton = (RadioButton)findViewById(checkedRadioButtonId);
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
    }
}
*/
