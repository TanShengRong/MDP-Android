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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

//assume BT is on for now...
//assume no paired BT devices
//main obj is to find devices
public class BluetoothActivity extends AppCompatActivity {
    //for debug logging
    private static final String Tag= "BluetoothActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    ListView otherDevices,myDevices;
    public ArrayList<BluetoothDevice> myBtDevices = new ArrayList<>();
    public ArrayList<BluetoothDevice> otherBtDevices = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothListAdapter bluetoothListAdapter;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        otherDevices = findViewById(R.id.otherDevices);
        myDevices = findViewById(R.id.myDevices);
        otherBtDevices = new ArrayList<>();
        myBtDevices = new ArrayList<>();
        // Check if Device support Bluetooth
        if (bluetoothAdapter == null) {
        }
        // Check if Bluetooth is on. Tries to enable
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // Query paired devices
//        Set<BluetoothDevice> myDevices = bluetoothAdapter.getBondedDevices();
//        if (myDevices.size() > 0) {
//            // There are paired devices. Get the name and address of each paired device.
//            for (BluetoothDevice device : myDevices) {
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                Log.d(Tag, "paireddeviceName "+deviceName);
//                Log.d(Tag, "paireddeviceHardwareAddress "+deviceHardwareAddress);
//            }
//        }
        // Register for broadcasts when a device is discovered.
        bluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        Log.d(Tag, "created activity");
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!otherBtDevices.contains(device) && !myBtDevices.contains(device)){
                    otherBtDevices.add(device);
                }
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC
                Log.d(Tag, "device name " + deviceName);
                Log.d(Tag, "device addr " + deviceHardwareAddress);
                bluetoothListAdapter = new BluetoothListAdapter(context, R.layout.list_adapter_bluetooth, otherBtDevices);
                otherDevices.setAdapter(bluetoothListAdapter);
//                testDevice = device.getName();
//                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, newBtDevices);
//                newDevices.setAdapter(deviceListAdapter);
//                otherBtDevices.add(device);
                otherDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String add = ((TextView)view.findViewById(R.id.deviceAddress)).getText().toString();
                        showToast(add);
                        Intent connectIntent = new Intent(BluetoothActivity.this, BluetoothConnectionService.class);
                        Log.d("bluetoothactivity", "id: " + otherBtDevices.get((int)id).getAddress());
                        connectIntent.putExtra("serviceType", "connect");
                        connectIntent.putExtra("device", otherBtDevices.get((int)id));
                        connectIntent.putExtra("id", myUUID);
                        showToast("Establishing Connection");
                        startService(connectIntent);
                    }
                });
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    };

    public void buttonClick(View v) {
        switch (v.getId()) {
            case R.id.scanNow:
                Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
                getDevices();
        }
    }
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(Tag, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
    public void getDevices(){
        if (bluetoothAdapter.isEnabled()){
            Log.d(Tag, "btnDiscover: Looking for unpaired devices.");
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
                Log.d(Tag, "btnDiscover: Canceling discovery.");
                //check BT permissions in manifest
                checkBTPermissions();
                bluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, discoverDevicesIntent);
            }
            else if (!bluetoothAdapter.isDiscovering()) {
                //check BT permissions in manifest
                checkBTPermissions();
                bluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, discoverDevicesIntent);
            }
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            myBtDevices.removeAll(devices);
            myBtDevices.addAll(devices);
            bluetoothListAdapter = new BluetoothListAdapter(getApplicationContext(), R.layout.list_adapter_bluetooth, myBtDevices);
            myDevices.setAdapter(bluetoothListAdapter);
        }
        else {
            //bluetooth is off so can't get paired devices
            showToast("Turn on bluetooth to get paired devices");
        }

    }
}