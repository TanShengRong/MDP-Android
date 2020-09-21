package com.example.mazerunner;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class FragmentBluetooth extends Fragment{
    View view;
    Log log;
    public ArrayList<BluetoothDevice> pairedBtDevices = new ArrayList<>();
    public ArrayList<BluetoothDevice> newBtDevices = new ArrayList<>();

    private static final String TAG= "BluetoothFragment";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ListView listViewOtherDevices,listViewMyDevices;
    private Button buttonScanNow, buttonEnableDiscoverable;
    public ArrayList<BluetoothDevice> myBluetoothDevices;
    public ArrayList<BluetoothDevice> otherBluetoothDevices;
    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothListAdapter bluetoothListAdapter;
    ListView  newDevices,pairedDevices, messageLogView;

    public FragmentBluetooth(){
//        if (bluetoothAdapter == null) {
//            // Device doesn't support Bluetooth
//            log.d("debug", "no BT in device");
//        }
//        while (!bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }

    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        newBtDevices = new ArrayList<>();
//        pairedBtDevices = new ArrayList<>();
//        newDevices = getView().
////                findViewById(R.id.newDevices);
//        pairedDevices = findViewById(R.id.pairedDevices);
//        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothConnectionStatusReceiver, new IntentFilter("btConnectionStatus"));
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bluetooth_fragment,container,false);
        listViewOtherDevices = view.findViewById(R.id.otherDevices);
        listViewMyDevices = view.findViewById(R.id.myDevices);
        buttonScanNow = view.findViewById(R.id.scanNow);
        buttonEnableDiscoverable = view.findViewById(R.id.enableDiscoverable);
        otherBluetoothDevices = new ArrayList<>();
        myBluetoothDevices = new ArrayList<>();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(bluetoothConnectionStatusReceiver, new IntentFilter("btConnectionStatus"));

        // Check if Device support Bluetooth
        if (bluetoothAdapter == null) {
            showToast("Device do not have Bluetooth.");
        }
        // Scan for devices and populate listview when Bluetooth is on
        if (bluetoothAdapter.isEnabled()) {
            getDevices();
        }
        // Check if Bluetooth is on. Tries to enable if it's off
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Log.d(TAG, "created activity");

        buttonScanNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Scanning now...");
                getDevices();
            }
        });
        buttonEnableDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);
                }
                if (bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    showToast("Device is now discoverable");
                }
            }
        });


        /*//scan button
        buttonScanNow = view.findViewById(R.id.scanNow);
        buttonScanNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent();
                myIntent.setClassName("com.example.mazerunner", "com.example.mazerunner.BluetoothActivity");
                startActivity(myIntent);
            }
        });

        //discover button
        buttonEnableDiscoverable = view.findViewById(R.id.enableDiscoverable);
        buttonEnableDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myCIntent = new Intent();
                myCIntent.setClassName("com.example.mazerunner", "com.example.mazerunner.CommunicationActivity");
                startActivity(myCIntent);
            }
        });*/
        return view;
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!otherBluetoothDevices.contains(device) && !myBluetoothDevices.contains(device)){
                    otherBluetoothDevices.add(device);
                }
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC
                Log.d(TAG, "device name " + deviceName);
                Log.d(TAG, "device addr " + deviceHardwareAddress);
                bluetoothListAdapter = new BluetoothListAdapter(context, R.layout.list_adapter_bluetooth, otherBluetoothDevices);
                listViewOtherDevices.setAdapter(bluetoothListAdapter);
                listViewOtherDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String add = ((TextView)view.findViewById(R.id.deviceAddress)).getText().toString();
                        showToast(add);
                        Intent connectIntent = new Intent(getActivity(), BluetoothConnectionService.class);
                        Log.d(TAG, "id: " + otherBluetoothDevices.get((int)id).getAddress());
                        connectIntent.putExtra("serviceType", "connect");
                        connectIntent.putExtra("device", otherBluetoothDevices.get((int)id));
                        connectIntent.putExtra("id", myUUID);
                        showToast("Establishing Connection");
                        getActivity().startService(connectIntent);
                    }
                });
            }
        }
    };

    // Request enable BT
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                getDevices();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                showToast("Please enable bluetooth and location");
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    };

    private void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void checkBTPermissions() {
//        If your app targets Android 9 or lower
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = ActivityCompat.checkSelfPermission(getContext(),android.Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += ActivityCompat.checkSelfPermission(getContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
    public void getDevices(){
        if (bluetoothAdapter.isEnabled()){
            Log.d(TAG, "getDevice(): Discovering unpaired devices.");
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "getDevice(): Restarting discovery.");
                //check BT permissions in manifest
                checkBTPermissions();
                bluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                requireActivity().registerReceiver(receiver, discoverDevicesIntent);
            }
            else if (!bluetoothAdapter.isDiscovering()) {
                //check BT permissions in manifest
                checkBTPermissions();
                Log.d(TAG, "getDevice(): Starting discovery.");
                bluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                requireActivity().registerReceiver(receiver, discoverDevicesIntent);
            }
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            myBluetoothDevices.removeAll(devices);
            myBluetoothDevices.addAll(devices);
            bluetoothListAdapter = new BluetoothListAdapter(getActivity().getApplicationContext(), R.layout.list_adapter_bluetooth, myBluetoothDevices);
            listViewMyDevices.setAdapter(bluetoothListAdapter);

            listViewMyDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String add = ((TextView)view.findViewById(R.id.deviceAddress)).getText().toString();
                    showToast(add);
                    Intent connectIntent = new Intent(getActivity(), BluetoothConnectionService.class);
                    Log.d(TAG, "id: " + myBluetoothDevices.get((int)id).getAddress());
                    connectIntent.putExtra("serviceType", "connect");
                    connectIntent.putExtra("device", myBluetoothDevices.get((int)id));
                    connectIntent.putExtra("id", myUUID);
                    showToast("Establishing Connection");
                    getActivity().startService(connectIntent);
                }
            });
        }
        else {
            //bluetooth is off so can't get paired devices
            showToast("Turn on bluetooth to get paired devices");
        }

    }
    BroadcastReceiver bluetoothConnectionStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String connectionStatus = intent.getStringExtra("ConnectionStatus");
//            getSupportActionBar().setSubtitle(connectionStatus);
            Log.d(TAG, "Connection Status : " + connectionStatus);

            if (connectionStatus.equals("Disconnected")){
                BluetoothDevice temp = (BluetoothDevice) intent.getParcelableExtra("Device");
                showToast("Reconnecting...");
//                getSupportActionBar().setSubtitle("Re-Connecting...");

                Intent connectIntent = new Intent(getActivity(), BluetoothConnectionService.class);
                connectIntent.putExtra("serviceType", "connect");
                connectIntent.putExtra("device", temp);
                connectIntent.putExtra("id", myUUID);

                getActivity().startService(connectIntent);
            }

        }
    };
}
