package com.example.mazerunner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

//assume BT is on for now...
//assume no paired BT devices
//main obj is to find devices
public class BluetoothActivity extends AppCompatActivity {
    //for debug logging
    private static final String Tag= "BluetoothActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    ListView availableDevices,pairedDevices;
    public ArrayList<BluetoothDevice> pairedBtDevices = new ArrayList<>();
    public ArrayList<BluetoothDevice> availableBtDevices = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        availableDevices = findViewById(R.id.availableDevices);
        pairedDevices = findViewById(R.id.pairedDevices);
        availableBtDevices = new ArrayList<>();
        pairedBtDevices = new ArrayList<>();
        // Check if Device support Bluetooth
        if (bluetoothAdapter == null) {
        }
        // Check if Bluetooth is on. Tries to enable
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        bluetoothAdapter.startDiscovery();
        // Query paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(Tag, "paireddeviceName"+deviceName);
                Log.d(Tag, "paireddeviceHardwareAddress"+deviceHardwareAddress);
            }
        }
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC
                Log.d(Tag, "device name" + deviceName);
                Log.d(Tag, "device name" + deviceHardwareAddress);
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

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.scanNow:
//                bluetoothAdapter.startDiscovery();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

}