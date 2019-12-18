package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.weather.WeatherAPI.LocationCodeFetcher;
import com.example.weather.WeatherAPI.Pin;
import com.example.weather.WeatherAPI.WeatherFetcher;
import com.example.weather.WeatherAPI.WeatherParsing;
import com.example.weather.WeatherAPI.WeatherSet;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    Pin pin = null;

    Button bt_setAlarm;

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

        String[] location = {"대구광역시", "달서구", "신당동"};

        WeatherSet weather = null;
        LocationCodeFetcher lcf = new LocationCodeFetcher();
        WeatherFetcher wf = new WeatherFetcher();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 정각");

        pin = lcf.fetchLocationCode(location);

        Log.i("TEST", "location code : " + pin.getSx() + ", " + pin.getSy());

        try {
            weather = wf.fetchWeather(pin.getSx(), pin.getSy());
        }catch (Exception E){
            Log.i("TEST", E.toString());
        }

        Log.i("TEST", "발표시각 : " + sdf.format(weather.getBaseDate()));
        Log.i("TEST", sdf.format(weather.getFcstDate()) + "의 강수확률은 " + weather.getPop() + "%, 하늘은 " + weather.getSky() + "입니당ㅎ");
    }
}
