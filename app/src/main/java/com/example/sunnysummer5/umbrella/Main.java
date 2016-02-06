package com.example.sunnysummer5.umbrella;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.UUID;

public class Main extends AppCompatActivity {

    private static final int RADIUS = 500;
    private static final String TAG = "MAIN";
    private CurrentLocation currentLocation;
    private static BluetoothAdapter mBluetoothAdapter;
    private static TextView temp;
    private Button button;
    private static HashSet<BluetoothDevice> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devices = new HashSet<BluetoothDevice>();
        temp = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialBluetooth();
            }
        });

        currentLocation = new CurrentLocation(Main.this);
        if(currentLocation.canGetLocation()) {
            Location current = currentLocation.getLocation();
            temp.setText(current.getLatitude() + ", " + current.getLongitude());
        }
        else {
            currentLocation.showSettingsAlert();
            temp.setText("NO");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        // Check device has Bluetooth and that it is turned on
        super.onResume();
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBluetoothAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void initialBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        if (!mBluetoothAdapter.isEnabled()) {
            //enable Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);

            //become discoverable by other devices
            Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,3600);
            startActivity(discoverable);
            Toast.makeText(this, "Bluetooth not enabled.", Toast.LENGTH_LONG).show();
        }
        else {
            temp.setText("blue");
        }

        boolean start = mBluetoothAdapter.startDiscovery();
        if(start)
            temp.setText("true");
        else
            temp.setText("false");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String st = device.getName() + " - " + device.getAddress();
                    Main.temp.setText(Main.temp.getText() + "\n" + st);
                    if(!(device.getAddress().equals(mBluetoothAdapter.getAddress())))
                        devices.add(device);
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        /*TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();*/
        //String uuid = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uuid = deviceUuid.toString();
        Log.e(TAG, "UUID: "+uuid);

        BluetoothDevice me = mBluetoothAdapter.getRemoteDevice(mBluetoothAdapter.getAddress());
        ConnectThread connectThread = new ConnectThread(uuid, me);
        connectThread.start();

        AcceptThread acceptThread = new AcceptThread(mBluetoothAdapter, uuid, this);
        acceptThread.start();
    }
}