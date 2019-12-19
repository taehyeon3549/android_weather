package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weather.WeatherAPI.LocationCodeFetcher;
import com.example.weather.WeatherAPI.Pin;
import com.example.weather.WeatherAPI.WeatherFetcher;
import com.example.weather.WeatherAPI.WeatherSet;

import org.apache.log4j.chainsaw.Main;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    Pin pin = null;
    Button bt_setAlarm, bt_search;
    ImageView iv_weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_setAlarm = (Button)findViewById(R.id.bt_setAlarm);
        bt_search = (Button)findViewById(R.id.bt_search);

        bt_setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent set_Alarm = new Intent(MainActivity.this, AlarmActivity.class);
                set_Alarm.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(set_Alarm);
            }
        });

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent set_locate = new Intent(MainActivity.this, AddressSearchActivity.class);
                set_locate.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(set_locate);
            }
        });

        String[] location = {"대구광역시", "동구", "안심1동"};
        WeatherSet weather = null;
        LocationCodeFetcher lcf = new LocationCodeFetcher();
        WeatherFetcher wf = new WeatherFetcher();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 정각");
        pin = lcf.fetchLocationCode(location);

        //날씨 정보 생성
        try {
            weather = wf.fetchWeather(pin.getSx(), pin.getSy());
        }catch (Exception E){
            Log.i("TEST", E.toString());
        }
        // 아이콘 설정
        weather.weatherIcon(this);
        // TV 설정
        TextView tw_weather = (TextView)findViewById(R.id.tw_weather);
        tw_weather.setText(sdf.format(weather.getBaseDate()) + "의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");

        SharedPreferences alarmPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = alarmPreferences.edit();
        editor.clear();
        editor.commit();

        Log.i("TEST", "발표시각 : " + sdf.format(weather.getBaseDate()));
//        Log.i("TEST", sdf.format(weather.getFcstDate()) + "의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");
//
//        //주소 변경 부분
//        Intent intent = getIntent(); /*데이터 수신*/
//        try{
//            Log.d("test",intent.getExtras().getString("address"));
//            Log.d("test",intent.getExtras().getString("x"));
//            Log.d("test",intent.getExtras().getString("y"));
//        }catch (Exception E){
//            Log.i("test", E.toString());
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // sharedPreferens 확인
        SharedPreferences alarmPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        // 알람 있는지 유무 체크
        if(alarmPreferences.getAll().size() == 0) {
            // 저장된 알람이 없음
            Log.i("TEST", "저장된 알람 없음");
        }else{
            Log.i("TEST", "저장된 알람 있음" + alarmPreferences.getAll().size() + "  시간" + alarmPreferences.getAll());
        }

        //주소 변경 부분
        Intent intent = getIntent(); /*데이터 수신*/
        try{
            Log.d("test",intent.getExtras().getString("address"));
            Log.d("test",intent.getExtras().getString("x"));
            Log.d("test",intent.getExtras().getString("y"));
        }catch (Exception E){
            Log.i("test", E.toString());
        }
//
//        if(intent.hasExtra("address")){
//
//        }else{
//            Log.i("test", "x y 변경값 없음");
//        }


    }
}
