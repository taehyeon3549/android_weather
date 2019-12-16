package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.weather.WeatherAPI.WeatherParsing;

public class MainActivity extends AppCompatActivity {

    Button bt_setAlarm;
    //test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_setAlarm = (Button)findViewById(R.id.bt_setAlarm);

        bt_setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent set_Alarm = new Intent(MainActivity.this, AlarmActivity.class);
                set_Alarm.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(set_Alarm);
            }
        });

        try {

        }catch (Exception E){
            Log.i("TEST", E.toString());
        }
    }
}
