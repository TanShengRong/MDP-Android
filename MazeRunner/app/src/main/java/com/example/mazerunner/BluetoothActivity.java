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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG= "BluetoothActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ListView listViewOtherDevices,listViewMyDevices;
    Button buttonScanNow, buttonEnableDiscoverable;
    public ArrayList<BluetoothDevice> myBluetoothDevices;
    public ArrayList<BluetoothDevice> otherBluetoothDevices;
    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothListAdapter bluetoothListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        listViewOtherDevices = findViewById(R.id.otherDevices);
        listViewMyDevices = findViewById(R.id.myDevices);
        buttonScanNow = findViewById(R.id.scanNow);
        buttonEnableDiscoverable = findViewById(R.id.enableDiscoverable);
        otherBluetoothDevices = new ArrayList<>();
        myBluetoothDevices = new ArrayList<>();

        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothConnectionStatusReceiver, new IntentFilter("btConnectionStatus"));

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
                        Intent connectIntent = new Intent(BluetoothActivity.this, BluetoothConnectionService.class);
                        Log.d(TAG, "id: " + otherBluetoothDevices.get((int)id).getAddress());
                        connectIntent.putExtra("serviceType", "connect");
                        connectIntent.putExtra("device", otherBluetoothDevices.get((int)id));
                        connectIntent.putExtra("id", myUUID);
                        showToast("Establishing Connection");
                        startService(connectIntent);
                    }
                });
            }
        }
    };

    // Request enable BT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                getDevices();
            } else if (resultCode == RESULT_CANCELED) {
                showToast("Please enable bluetooth and location");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    };

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void checkBTPermissions() {
//        If your app targets Android 9 or lower
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
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
                registerReceiver(receiver, discoverDevicesIntent);
            }
            else if (!bluetoothAdapter.isDiscovering()) {
                //check BT permissions in manifest
                checkBTPermissions();
                Log.d(TAG, "getDevice(): Starting discovery.");
                bluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, discoverDevicesIntent);
            }
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            myBluetoothDevices.removeAll(devices);
            myBluetoothDevices.addAll(devices);
            bluetoothListAdapter = new BluetoothListAdapter(getApplicationContext(), R.layout.list_adapter_bluetooth, myBluetoothDevices);
            listViewMyDevices.setAdapter(bluetoothListAdapter);

            listViewMyDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String add = ((TextView)view.findViewById(R.id.deviceAddress)).getText().toString();
                    showToast(add);
                    Intent connectIntent = new Intent(BluetoothActivity.this, BluetoothConnectionService.class);
                    Log.d(TAG, "id: " + myBluetoothDevices.get((int)id).getAddress());
                    connectIntent.putExtra("serviceType", "connect");
                    connectIntent.putExtra("device", myBluetoothDevices.get((int)id));
                    connectIntent.putExtra("id", myUUID);
                    showToast("Establishing Connection");
                    startService(connectIntent);
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

                Intent connectIntent = new Intent(BluetoothActivity.this, BluetoothConnectionService.class);
                connectIntent.putExtra("serviceType", "connect");
                connectIntent.putExtra("device", temp);
                connectIntent.putExtra("id", myUUID);

                startService(connectIntent);
            }

        }
    };
}