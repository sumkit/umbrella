package com.example.sunnysummer5.umbrella;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Main extends AppCompatActivity {

    private static final String TAG = "MAIN";
    private static BluetoothAdapter mBluetoothAdapter;
    private static HashSet<BluetoothDevice> devices;

    private Button feed, report;
    private ImageButton next, final3;
    private EditText hint;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<String> myItems = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layoutlist, myItems);
        ListView listt = (ListView) findViewById(R.id.listView);
        listt.setAdapter(adapter);

        if(getIntent().getStringArrayExtra("array") != null) {
            final String[] S = getIntent().getStringArrayExtra("array");
            for(int j = 0; j < S.length; j++) {
                myItems.add(S[j]);
            }
        }

        final3 = (ImageButton)findViewById(R.id.final2);
        final3.setVisibility(View.GONE);
        report = (Button)findViewById(R.id.button5);
        feed = (Button)findViewById(R.id.button4);
        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feed.setBackgroundColor(Color.WHITE);
                report.setBackgroundColor(Color.parseColor("#f39c12"));
                feed.setTextColor(Color.parseColor("#f39c12"));
                report.setTextColor(Color.WHITE);


            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feed.setBackgroundColor(Color.parseColor("#f39c12"));
                report.setBackgroundColor(Color.WHITE);
                feed.setTextColor(Color.WHITE);
                report.setTextColor(Color.parseColor("#f39c12"));
                Intent editScreen = new Intent(getApplicationContext(), Main2Activity.class);
                editScreen.putExtra("array",myItems.toArray(new String[myItems.size()]));
                startActivity(editScreen);

            }
        });
        //after message
        next = (ImageButton)findViewById(R.id.send);
        hint = (EditText)findViewById(R.id.search);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint.setHint("Type location");
                message = (hint.getText()).toString();
                next.setVisibility(View.GONE);
                final3.setVisibility(View.VISIBLE);
                hint.getText().clear();

            }

        });
        //after location
        final3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final3.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                hint.setHint("Type message");
                String location = (hint.getText()).toString();
                hint.getText().clear();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd, HH:mm a");
                String currentDateandTime = sdf.format(new Date());
                String subitem = location + ", " + currentDateandTime + "\n" + "\n" + message;
                myItems.add(0, subitem);
                adapter.notifyDataSetChanged();
            }
        });
        devices = new HashSet<BluetoothDevice>();
        initialBluetooth();
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

        boolean start = mBluetoothAdapter.startDiscovery();


        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String st = device.getName() + " - " + device.getAddress();
                    if(!(device.getAddress().equals(mBluetoothAdapter.getAddress())))
                        devices.add(device);
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uuid = deviceUuid.toString();
        Log.e(TAG, "UUID: "+uuid);

        ConnectThread connectThread = new ConnectThread(mBluetoothAdapter, uuid);
        connectThread.start();
//
//        AcceptThread acceptThread = new AcceptThread(mBluetoothAdapter, uuid);
//        acceptThread.start();
    }
}