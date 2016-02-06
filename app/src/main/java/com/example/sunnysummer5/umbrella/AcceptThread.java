package com.example.sunnysummer5.umbrella;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class AcceptThread extends Thread {
   private final BluetoothServerSocket mmServerSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private static final String TAG = "AcceptThread";

    public AcceptThread(BluetoothAdapter adapter, String uuid) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        mBluetoothAdapter = adapter;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Umbrella", UUID.fromString(uuid));
            Log.e(TAG, "FINISHED");
        } catch (IOException e) {
            Log.e(TAG, "IOException when making BluetoothServerSocket"); }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        synchronized (AcceptThread.this) {
            AcceptThread.this.cancel();
        }
    }

    // Will cancel the listening socket, and cause the thread to finish
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }

    public void manageConnectedSocket(BluetoothSocket socket) {
        //setup input stream
        InputStream tmpIn = null;
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) { Log.e(TAG, "IOException trying to read input stream"); }

        //read input stream
        BufferedReader r = new BufferedReader(new InputStreamReader(tmpIn));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, total.toString());
    }
}