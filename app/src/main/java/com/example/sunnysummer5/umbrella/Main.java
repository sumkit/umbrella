package com.example.sunnysummer5.umbrella;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class Main extends AppCompatActivity {

    private static final int RADIUS = 500;
    private static final String TAG = "MAIN";
    private CurrentLocation currentLocation;
    private static final String URL = "http://128.237.170.31:8000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentLocation = new CurrentLocation(Main.this);
        if(currentLocation.canGetLocation()) {
            Location current = currentLocation.getLocation();
            TextView temp = (TextView) findViewById(R.id.text);
            temp.setText(current.getLatitude() + ", " + current.getLongitude());
        }
        else {
            currentLocation.showSettingsAlert();
            TextView temp = (TextView) findViewById(R.id.text);
            temp.setText("NO");
        }

        connect();
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

    public void connect() {
        List<BasicNameValuePair> extraHeaders = Arrays.asList(
                new BasicNameValuePair("Cookie", "session=abcd"));

        WebSocketClient client = new WebSocketClient(URI.create(URL), new WebSocketClient.Listener() {
            @Override
            public void onConnect() {
                Log.d(TAG, "Connected!");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, String.format("Got string message! %s", message));
            }

            @Override
            public void onMessage(byte[] data) {
                Log.d(TAG, String.format("Got binary message! %s", data));
            }

            @Override
            public void onDisconnect(int code, String reason) {
                Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error!", error);
            }
        }, extraHeaders);

        client.connect();

        // Later…
        client.send("hello!");
        client.send(new byte[] {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF});
        client.disconnect();
    }

}
