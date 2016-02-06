package com.example.sunnysummer5.umbrella;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity {

    private Button SSN, CMUpolice, police, feed, report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final String[] S = getIntent().getStringArrayExtra("array");
        SSN = (Button)findViewById(R.id.button);
        SSN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone("1234567890");
            }
        });
        CMUpolice = (Button)findViewById(R.id.button2);
        CMUpolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone("1234567890");
            }
        });
        police = (Button)findViewById(R.id.button3);
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone("1234567890");
            }
        });
        report = (Button)findViewById(R.id.button5);
        feed = (Button)findViewById(R.id.button4);
        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feed.setBackgroundColor(Color.WHITE);
                report.setBackgroundColor(Color.parseColor("#f39c12"));
                feed.setTextColor(Color.parseColor("#f39c12"));
                report.setTextColor(Color.WHITE);
                Intent editScreen = new Intent(getApplicationContext(), Main.class);
                editScreen.putExtra("array", S);
                startActivity(editScreen);

            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feed.setBackgroundColor(Color.parseColor("#f39c12"));
                report.setBackgroundColor(Color.WHITE);
                feed.setTextColor(Color.WHITE);
                report.setTextColor(Color.parseColor("#f39c12"));


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
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

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }
}
