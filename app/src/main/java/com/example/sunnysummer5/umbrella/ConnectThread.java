package com.example.sunnysummer5.umbrella;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mBluetoothAdapter;
    private final String mUUID;
    private String input;

    public ConnectThread(BluetoothAdapter adapter, String uuid, String i) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = adapter.getRemoteDevice(adapter.getAddress());
        mBluetoothAdapter = adapter;
        mUUID = uuid;
        input = i;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid));
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        if(mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                try {
                    mmSocket.close();}
                catch (IOException e2) {
                    Log.e("ConnectThread", "IOException when trying to close socket", e2);
                }
            }
            new AcceptThread(mBluetoothAdapter, mUUID).start();
            return;
        }

        // Do work to manage the connection (in a separate thread)
        synchronized (ConnectThread.this) {
            ConnectThread.this.cancel();
        }
        new ConnectedThread(mUUID, mBluetoothAdapter, input).start();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}