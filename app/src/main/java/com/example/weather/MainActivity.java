package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.WeatherAPI.LocationCodeFetcher;
import com.example.weather.WeatherAPI.Pin;
import com.example.weather.WeatherAPI.WeatherFetcher;
import com.example.weather.WeatherAPI.WeatherSet;
import com.example.weather.cLocation.ConvertLatLon;

import org.apache.log4j.chainsaw.Main;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    Pin pin = null;
    Button bt_setAlarm, bt_search;
    ImageView iv_weather;
    TextView tv_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_setAlarm = (Button) findViewById(R.id.bt_setAlarm);
        bt_search = (Button) findViewById(R.id.bt_search);
        tv_location = (TextView)findViewById(R.id.tv_location);

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

        /***  지역 위치 설정 ***/
        String[] location = {"대구광역시", "동구", "안심1동"};
        WeatherSet weather = null;
        LocationCodeFetcher lcf = new LocationCodeFetcher();
        WeatherFetcher wf = new WeatherFetcher();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 정각");
        pin = lcf.fetchLocationCode(location);

        /***  날씨 정보 생성 ***/
        try {
            weather = wf.fetchWeather(pin.getSx(), pin.getSy());
        } catch (Exception E) {
            Log.i("TEST", "날씨 정보 파싱 에러 : " + E.toString());
        }

        /***  날씨 아이콘 설정 ***/
        weather.weatherIcon(this);
        // TV 설정
        TextView tw_weather = (TextView) findViewById(R.id.tw_weather);
        tw_weather.setText(sdf.format(weather.getBaseDate()) + "의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");

        SharedPreferences alarmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = alarmPreferences.edit();
        editor.clear();
        editor.commit();

        /***  위치값 가져오기 ***/
        GetLocation mylocation = new GetLocation();
        mylocation.StartGetLocation(this);

        //Log.i("TEST", "발표시각 : " + sdf.format(weather.getBaseDate()));
//        Log.i("TEST", sdf.format(weather.getFcstDate()) + "의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");
//


    }

    @Override
    protected void onResume() {
        super.onResume();


        /***  sharedPreferens 확인 ***/
        SharedPreferences alarmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        /***  알람 있는지 유무 체크 ***/
        if (alarmPreferences.getAll().size() == 0) {
            // 저장된 알람이 없음
            Log.i("TEST", "저장된 알람 없음");
        } else {
            Log.i("TEST", "저장된 알람 있음" + alarmPreferences.getAll().size() + "  시간" + alarmPreferences.getAll());
        }

        /***  주소 변경 부분 ***/
        Intent intent = getIntent();
        if(intent.hasExtra("address")){
            Log.d("test", intent.getExtras().getString("address"));
            Log.d("test", intent.getExtras().getString("x"));
            Log.d("test", intent.getExtras().getString("y"));
        }else{
            Log.i("test", "x y 변경값 없음");
        }

    }

    /***************************************
    *  메인 엑티비티 위치 값 찍음
    * **************************************/
    public void displayLocation(String val){
        tv_location.setText(val);
    }

    /***************************************
     *  현재 위치 값 가져옴
     * **************************************/
    public class GetLocation {
        LocationManager locationManager;
        private int xPin, yPin;
        private double longitude, latitude;
        String provider;

        public void StartGetLocation(Context context) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // 앱 퍼미션 확인
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1, mLocationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1, mLocationListener);
            }
        }

        public int getxPin() {
            return xPin;
        }

        public int getyPin() {
            return yPin;
        }

        public double getLat() {
            return latitude;
        }

        public double getLon() {
            return longitude;
        }

        public String getProvider() {
            return provider;
        }

        private final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /***  위치 값이 갱신 되면 이벤트 발생 ***/
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                provider = location.getProvider();

                /***  위도 경도를 x,y로 변환 ***/
                ConvertLatLon convertLatLon = new ConvertLatLon((float)longitude, (float)latitude);

                xPin = convertLatLon.getX();
                yPin = convertLatLon.getY();

                /***  메인 액티비티의 Textview의 값을 변경 ***/
                displayLocation("위도 : " + longitude + " 경도 : " + latitude + "X 값 : " + convertLatLon.getX() + " Y 값 :" + convertLatLon.getY() + " \n서비스 제공자" + provider);

                Toast.makeText(MainActivity.this, "위도 : " + longitude + " 경도 : " + latitude + " \n서비스 제공자" + provider, Toast.LENGTH_LONG).show();
                Log.i("TEST", "위도 : " + longitude + " 경도 : " + latitude + "X 값 : " + convertLatLon.getX() + " Y 값 :" + convertLatLon.getY() + " \n서비스 제공자" + provider);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }


}
