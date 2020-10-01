//package com.example.mazerunner;
//
//
//import android.app.IntentService;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import android.content.IntentFilter;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import java.io.IOException;
//import java.util.UUID;
//
//
//public class BluetoothConnectionService extends IntentService {
//
//    private static final String TAG = "BTConnectionAService";
//    private static final String appName = "Group 32 Remote Controller";
//
//    // UUID
//    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//
//    // Declarations
//    private BluetoothAdapter myBluetoothAdapter;
//    //    private AcceptThread myAcceptThread;
//    private ConnectThread myConnectThread;
//    public BluetoothDevice myDevice;
//    private UUID deviceUUID;
//    Context myContext;
//
//
//    // Constructor
//    public BluetoothConnectionService() {
//
//        super("BluetoothConnectionService");
//
//    }
//
//
//    // Handle Intent for Service
//    // Starts When Service is Created
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//
//        myContext = getApplicationContext();
//        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (intent.getStringExtra("serviceType").equals("listen")) {
//            myDevice = (BluetoothDevice) intent.getExtras().getParcelable("device");
//            Log.d(TAG, "Service Handle: startAcceptThread");
////            startAcceptThread();
//        } else {
//            myDevice = (BluetoothDevice) intent.getExtras().getParcelable("device");
//            deviceUUID = (UUID) intent.getSerializableExtra("id");
//            Log.d(TAG, "Service Handle: startClientThread");
//            startClientThread(myDevice, deviceUUID);
//        }
//
//    }
//
//    // Attempt to make outgoing connection with device
//    private class ConnectThread extends Thread {
//
//        private BluetoothSocket mySocket;
//
//        public ConnectThread(BluetoothDevice device, UUID uuid) {
//
//            Log.d(TAG, "ConnectThread: started");
//            myDevice = device;
//            deviceUUID = uuid;
//        }
//
//        public void run() {
//            BluetoothSocket temp = null;
//            Intent connectionStatusIntent;
//
//            Log.d(TAG, "Run: myConnectThread");
//
//            // BluetoothSocket for connection with given BluetoothDevice
//            try {
//                Log.d(TAG, "ConnectThread: Trying to create InsecureRFcommSocket using UUID: " + myUUID);
//                temp = myDevice.createRfcommSocketToServiceRecord(deviceUUID);
//            } catch (IOException e) {
//
//                Log.d(TAG, "ConnectThread: Could not create InsecureRFcommSocket " + e.getMessage());
//            }
//
//            mySocket = temp;
//
//            // Cancel discovery to prevent slow connection
//            myBluetoothAdapter.cancelDiscovery();
//
//            try {
//
//                Log.d(TAG, "Connecting to Device: " + myDevice);
//                // Blocking call and will only return on a successful connection / exception
//                mySocket.connect();
//
//                // Broadcast connection message
//                connectionStatusIntent = new Intent("btConnectionStatus");
//                connectionStatusIntent.putExtra("ConnectionStatus", "Connected");
//                connectionStatusIntent.putExtra("Device", myDevice);
//                LocalBroadcastManager.getInstance(myContext).sendBroadcast(connectionStatusIntent);
//
//                Log.d(TAG, "run: ConnectThread connected");
//
//                // Start BluetoothChat
//                BluetoothChat.connected(mySocket, myDevice, myContext);
//
//            } catch (IOException e) {
//
//                // Close socket on error
//                try {
//                    mySocket.close();
//
//                    connectionStatusIntent = new Intent("btConnectionStatus");
//                    connectionStatusIntent.putExtra("ConnectionStatus", "Failed Connection");
//                    connectionStatusIntent.putExtra("Device", myDevice);
//                    sendBroadcast(connectionStatusIntent);
//                    Log.d(TAG, "run: Socket Closed: Connection Failed!! " + e.getMessage());
//
//                } catch (IOException e1) {
//                    Log.d(TAG, "myConnectThread, run: Unable to close socket connection: " + e1.getMessage());
//                }
//
//            }
//
//            try {
//
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        public void cancel() {
//
//            try {
//                Log.d(TAG, "Cancel: Closing Client Socket");
//                mySocket.close();
//            } catch (IOException e) {
//                Log.d(TAG, "Cancel: Closing mySocket in ConnectThread Failed " + e.getMessage());
//            }
//        }
//    }
//
//    // Start ConnectThread and attempt to make a connection
//    public void startClientThread(BluetoothDevice device, UUID uuid) {
//
//        Log.d(TAG, "startClient: Started");
//        myConnectThread = new ConnectThread(device, uuid);
//        myConnectThread.start();
//    }
//
//
//
//
//}