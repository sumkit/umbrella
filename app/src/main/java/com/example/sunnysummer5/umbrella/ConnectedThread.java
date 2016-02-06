package com.example.sunnysummer5.umbrella;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ConnectedThread extends Thread {
    private BluetoothSocket mmSocket;
    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;
    private final String uuid;

    public ConnectedThread(String u, BluetoothAdapter a) {
        uuid = u;
        try {
            BluetoothDevice mmDevice = a.getRemoteDevice(a.getAddress());
            mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(u));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            try {
                mmSocket.close();}
            catch (IOException e2) {
                Log.e("ConnectThread", "IOException when trying to close socket", e2);
            }
            new AcceptThread(a, uuid).start();
            return;
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        Handler mHandler=null;
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                mHandler.obtainMessage(0, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
        // Do work to manage the connection (in a separate thread)
        synchronized (ConnectedThread.this) {
            ConnectedThread.this.cancel();
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
