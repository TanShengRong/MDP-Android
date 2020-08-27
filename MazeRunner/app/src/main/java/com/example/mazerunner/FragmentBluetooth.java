package com.example.mazerunner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentBluetooth extends Fragment{
    private static final int REQUEST_ENABLE_BT = 1;
    View view;
    Log log;
    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public ArrayList<BluetoothDevice> pairedBtDevices = new ArrayList<>();
    public ArrayList<BluetoothDevice> newBtDevices = new ArrayList<>();
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
        return view;
    }

}
