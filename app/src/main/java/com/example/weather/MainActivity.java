package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weather.WeatherAPI.LocationCodeFetcher;
import com.example.weather.WeatherAPI.Pin;
import com.example.weather.WeatherAPI.WeatherFetcher;
import com.example.weather.WeatherAPI.WeatherSet;

import org.apache.log4j.lf5.util.Resource;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    Pin pin = null;
    Button bt_setAlarm;
    ImageView iv_weather;

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


        weatherIcon(weather);
        TextView tw_weather = (TextView)findViewById(R.id.tw_weather);
        tw_weather.setText(sdf.format(weather.getFcstDate()) + "의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");

//        Log.i("TEST", "발표시각 : " + sdf.format(weather.getBaseDate()));
//        Log.i("TEST", sdf.format(weather.getFcstDate()) + "의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");
    }

    public void weatherIcon(WeatherSet weather){
        ImageView iv_weather = (ImageView)findViewById(R.id.iv_weather);

        if(weather.getSky().equals("Error"))    iv_weather.setImageDrawable(getResources().getDrawable(R.drawable.na));
        else if(weather.getSky().equals("구름 조금")) iv_weather.setImageDrawable(getResources().getDrawable(R.drawable.little_cloudy));
        else if(weather.getSky().equals("구름 많음")) iv_weather.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
        else if(weather.getSky().equals("흐림")) iv_weather.setImageDrawable(getResources().getDrawable(R.drawable.many_cloudy));
        else if(weather.getSky().equals("맑음")) iv_weather.setImageDrawable(getResources().getDrawable(R.drawable.sunny));

    }
}
