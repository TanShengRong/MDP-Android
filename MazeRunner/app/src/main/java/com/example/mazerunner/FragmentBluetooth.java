package com.example.mazerunner;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class FragmentBluetooth extends Fragment{
    View view;
    Button btnScanNow;
    Button btnEnableDiscoverable;
    ListView listViewOtherDevices;
    ListView listViewMyDevices;
    private BluetoothAdapter btAdapter = null;
    private ProgressDialog progress;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address = "";
    String name = "";
    private String device;

    public static BluetoothService btService = null;
    public static StringBuffer mOutStringBuffer;

    private ArrayAdapter<String> newDevicesArrayAdapter;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;

    private MainActivity activitymain;
    OnMessageChangedListener callback;

    public FragmentBluetooth(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activitymain = (MainActivity) getActivity();
        view = inflater.inflate(R.layout.bluetooth_fragment,container,false);
        listViewOtherDevices = view.findViewById(R.id.otherDevices);
        listViewMyDevices = view.findViewById(R.id.myDevices);
        btnScanNow = view.findViewById(R.id.scanNow);
        btnEnableDiscoverable = view.findViewById(R.id.enableDiscoverable);

        pairedDevicesArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.device_name);
        listViewMyDevices.setAdapter(pairedDevicesArrayAdapter);
        listViewMyDevices.setOnItemClickListener(myListClickListener);

        newDevicesArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.device_name);
        listViewOtherDevices.setAdapter(newDevicesArrayAdapter);
        listViewOtherDevices.setOnItemClickListener(myListClickListener);

        //get intent
        Bundle bundle = activitymain.getIntent().getExtras();
        if (bundle != null){
            if (bundle.containsKey("device")){
                device = bundle.getString("device");
            } else {
                device = "";
            }
        } else {
            device = "";
        }

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
        {
            //no bluetooth adapter
            Toast.makeText(activitymain.getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            activitymain.finish();
        }
        else
        {
            if (!btAdapter.isEnabled()) {
                //bluetooth not enabled
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            } else {
                //use android's .getBondedDevices() to retrieve a list of paired devices attached to the phone
                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                ArrayList list = new ArrayList();

                if (pairedDevices.size()>0) {
                    //found at least one paired devices
                    for(BluetoothDevice bt : pairedDevices)
                    {
                        //add all paired devices to list
                        list.add(bt.getName() + "\n MAC Address: " + bt.getAddress()); //Get the device's name and the address
                    }
                }
                else {
                    //no paired devices found on device
                    list.add("No Paired Bluetooth Devices Found");
                }
            }
        }
        btnScanNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getContext(), "Refreshing list of devices", Toast.LENGTH_SHORT).show();
                pairedDevicesList();
                newDevicesList();
            }
        });
        btnEnableDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                enableDiscovery();
            }
        });
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop discovery
        if (btAdapter != null) {
            btAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        getActivity().unregisterReceiver(bReceiver);
    }
    //method for enabling discovery of the bluetooth device to other devices
    private void enableDiscovery() {

        if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
            Toast.makeText(getContext(), "Device is now Discoverable", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "Device is already Discoverable", Toast.LENGTH_SHORT).show();
        }
    }
    //method called when user manually finds paired devices
    private void pairedDevicesList()
    {
        //check if user enabled required permissions
        checkBTPermissions();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
        {
            //no bluetooth adapter
            Toast.makeText(getContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        } else {
            if (!btAdapter.isEnabled()) {
                //bluetooth not enabled
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            //found at least one paired devices
            pairedDevicesArrayAdapter.clear(); //clear arrayadapter to refresh
            for (BluetoothDevice bt : pairedDevices) {
                //add all paired devices to adapter
                String device_str = bt.getName() + "\n MAC Address: " + bt.getAddress();
                pairedDevicesArrayAdapter.add(device_str); //Get the device's name and the address
            }
        } else {
            //no paired devices found
            pairedDevicesArrayAdapter.clear(); //clear arrayadapter since no paired devices found
            pairedDevicesArrayAdapter.add("No Paired Bluetooth Devices Found"); //set first item to this message to let user know the result
        }
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);
            name = info.substring(0, info.length() - 30);
            final BluetoothDevice device = btAdapter.getRemoteDevice(address);
            btService = new BluetoothService(getContext(), mHandler);

            //create ProgressDialog to let user know that application is trying to connect to device
            progress = ProgressDialog.show(getContext(), "Connecting...", "Please wait");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btService.connect(device, false); //connect to the device that was clicked
                    progress.dismiss(); //close ProgressDialog
                    registerReceivers(); //register receivers to allow current activity to continue receiving & executing messages
//                    Intent i = new Intent(getContext(), MainActivity.class);
//                    startActivity(i); //go to MainActivity page
                }
            }, 1000); //delay of 1s

        }
    };

    //scan available nearby bluetooth devices
    private void newDevicesList(){
        //if device is already discovering, cancel it
        if(btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        //check if user has enabled required permissions
        checkBTPermissions();

        //start discovering
        btAdapter.startDiscovery();
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(bReceiver, discoverDevicesIntent);
    }

    //broadcast receiver for bluetooth
    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //when a bluetooth device is found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String newDevice = device.getName()+ "\n MAC Address: "+device.getAddress();
                // adds device only if does not exist
                if (newDevicesArrayAdapter.getPosition(newDevice) == -1 ) {
                    newDevicesArrayAdapter.add(newDevice);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //when bluetooth has completed scanning
                if (newDevicesArrayAdapter.getCount() == 0) {
                    //if no devices found
                    String noDevices = "No devices found"; //set first item to let user know the results
                    newDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
    /**
     * check if user has enabled permissions for access_fine_location and access_coarse_location
     * required only if user's device's SDK is after LOLLIPOP's version
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 200);
            // Otherwise, setup the chat session
        } else if (btService == null) {
            setupChat();
        }
    }
    // Initialize the BluetoothService to perform bluetooth connections
    private void setupChat(){
        btService = new BluetoothService(getContext(), mHandler);
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * The Handler that gets information back from the BluetoothService
     */
    public final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            //when notified that bluetooth service has connected to a device
                            sendToMain(name); //send name of device to MainActivity
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            //when notified that bluetooth service is connecting to a device
                            sendToMain(""); //send empty string to MainActivity to notify that no devices connected currently
                            name = ""; //set name to empty string since no devices is currently connected
                            break;
                        case BluetoothService.STATE_LISTEN:
                            //when notified that bluetooth service is listening for devices
                            sendToMain(""); //send empty string to MainActivity to notify that no devices connected currently
                            name = ""; //set name to empty string since no devices is currently connected
                        case BluetoothService.STATE_NONE:
                            sendToMain(""); //send empty string to MainActivity to notify that no devices connected currently
                            name = ""; //set name to empty string since no devices is currently connected
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("MESSAGE_READ", "handleMessage: received input "+readMessage);
                    sendTextToMain(readMessage); //send message to MainActivity that was received in buffer
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    name = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != getContext()) {
                        if (name == null){

                        } else {
                            Toast.makeText(getContext(), "Connected to "+ name, Toast.LENGTH_SHORT).show();
                            //send to mainactivity
                            sendToMain(name); //name of device currently connected
                        }
                    }

                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != getContext()) {
                        String theMsg = msg.getData().getString(Constants.TOAST) ;
                        if (theMsg.equalsIgnoreCase("device connection was lost")){
                            Toast.makeText(getContext(), theMsg, Toast.LENGTH_SHORT).show();
                            name = ""; //set name to empty string since connection was lost
                            sendToMain(""); //send empty string to mainactivity to notify no device currently connected
                        }
                    }
                    break;
            }
        }
    };
    //method to pass data to MainActivity
    private void sendToMain(String msg) {
        Intent intent = new Intent("getConnectedDevice");
        // You can also include some extra data.
        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
    // method to send text received from bluetooth connection to activity main
    private void sendTextToMain(String msg) {
        callback.onMessageChanged(msg);
    }
    public void setOnMessageChangedListener(OnMessageChangedListener callback) {
        this.callback = callback;
    }
    public interface OnMessageChangedListener {
        public void onMessageChanged(String msg);
    }
    //get sent text from MainActivity
    private BroadcastReceiver mTextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String theText = intent.getStringExtra("tts");
            if (theText != null){
                if (btService.getState() != btService.STATE_CONNECTED) {
                    Toast.makeText(getContext(), "Connection Lost. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("sendToBtAct", theText);
                //send out message
                byte[] send = theText.getBytes();
                btService.write(send);

                // Reset out string buffer to zero
                mOutStringBuffer.setLength(0);

            }
        }
    };
    //get robot movements from MainActivity
    private BroadcastReceiver mCtrlReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String control = intent.getStringExtra("control");
            if (control != null){
                if (btService.getState() != btService.STATE_CONNECTED) {
                    Toast.makeText(getContext(), "Connection Lost. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("mCtrlReceiver", control);
                //send out message
                byte[] send = control.getBytes();
                btService.write(send);
                mOutStringBuffer.setLength(0);

            }
        }
    };
    //listen for disconnection from MainActivity
    private BroadcastReceiver mDcReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String control = intent.getStringExtra("disconnect");
            if (control != null){
                if (btService.getState() != btService.STATE_CONNECTED) {
                    Toast.makeText(getContext(), "Connection Lost. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                destroyReceivers();
                btService.stop();
                btService.start();
            }
        }
    };
    //register receivers needed
    private void registerReceivers(){
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTextReceiver, new IntentFilter("getTextToSend"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mCtrlReceiver, new IntentFilter("getCtrlToSend"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDcReceiver, new IntentFilter("initiateDc"));

        // Register for broadcasts when discovery has finished
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(bReceiver, filter);
    }
    //destroy all receivers
    private void destroyReceivers(){
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mTextReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mCtrlReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDcReceiver);
        getActivity().unregisterReceiver(bReceiver);
    }
}
