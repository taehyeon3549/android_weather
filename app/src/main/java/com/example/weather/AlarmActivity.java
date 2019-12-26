package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.weather.Alarm.AlarmData;
import com.example.weather.Alarm.AlarmReceiver;
import com.example.weather.Alarm.DeviceBootReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {

    Button bt_finish;
    RadioGroup rg_weather;
    RadioButton rb_fine, rb_rain, rb_cloud, rb_snow;
    TimePicker time;
    Intent ReceivedIntent;
    String checked_weather = "맑음";
    String getLocationExtras = "주소";
    String getxExtras = "-1";
    String getyExtras = "-1";
    int tp_hour = 12;
    int tp_min = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        bt_finish = (Button)findViewById(R.id.bt_finish);
        rg_weather = (RadioGroup)findViewById(R.id.rg_weather);
        rb_fine = (RadioButton)findViewById(R.id.rb_fine);
        rb_rain = (RadioButton)findViewById(R.id.rb_rain);
        rb_cloud = (RadioButton)findViewById(R.id.rb_cloud);
        rb_snow = (RadioButton)findViewById(R.id.rb_snow);
        time = (TimePicker)findViewById(R.id.tp_time);

        ReceivedIntent = getIntent();
        getLocationExtras =ReceivedIntent.getExtras().getString("address");
        getxExtras =ReceivedIntent.getExtras().getString("x");
        getyExtras=ReceivedIntent.getExtras().getString("y");

        //Log.d("test",ReceivedIntent.getExtras().getString("address"));

        /** 시간 설정 **/
        time.setIs24HourView(true);     //24시간 설정

        /** sharedpreference 설정 **/
        SharedPreferences sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);

        long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());

        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);

        /** 이전 설정값으로 TimePicker 초기화 **/
        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));
        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));


        if (Build.VERSION.SDK_INT >= 23 ){
            time.setHour(pre_hour);
            time.setMinute(pre_minute);
        }
        else{
            time.setCurrentHour(pre_hour);
            time.setCurrentMinute(pre_minute);
        }

        rg_weather.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_fine)
                    checked_weather = "맑음";
                else if(checkedId == R.id.rb_cloud)
                    checked_weather = "흐림";
                else if(checkedId == R.id.rb_rain)
                    checked_weather = "비";
                else if(checkedId == R.id.rb_snow)
                    checked_weather = "눈";
            }
        });

        time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                tp_hour = hourOfDay;
                tp_min = minute;
            }
        });

        bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour_24, minute;

                if (Build.VERSION.SDK_INT >= 23 ){
                    hour_24 = time.getHour();
                    minute = time.getMinute();
                }
                else{
                    hour_24 = time.getCurrentHour();
                    minute = time.getCurrentMinute();
                }

                /** 현재 지정된 시간으로 알람 시간 설정 **/
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour_24);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                Date currentDateTime = calendar.getTime();
                String date_text = new SimpleDateFormat("HH시 mm분 ", Locale.getDefault()).format(currentDateTime);
                Toast.makeText(getApplicationContext(),checked_weather + "날씨 알람을 " + date_text + "에 울리게 설정되었습니다!", Toast.LENGTH_SHORT).show();

                //  Preference에 설정한 값 저장
                SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                editor.putLong("nextNotifyTime", (long)calendar.getTimeInMillis());
                editor.apply();

                /** 알람 세팅 **/
                //setAlarm(calendar, checked_weather);
                setAlarm(calendar, checked_weather);

                //Toast.makeText(AlarmActivity.this, "날씨는  " + checked_weather + "시간은 " + tp_hour + "시" + tp_min + "분", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    void setAlarm(Calendar calendar, String checked_weather)
    {
        /** AlarmData 생성 **/
        //AlarmData myData = new AlarmData(calendar, checked_weather, true);
        AlarmData myData = new AlarmData(calendar, checked_weather,getLocationExtras, true);
        Log.i("TEST", myData.get_time());

        /** sharedPreferencs 값 넣음 **/
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences alarmPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = alarmPreferences.edit();

        /*******************************
         * 알람History 설정
         * Format
         * Format
         * Tag : alarm01
         * Value : 설정 시간/설정 날씨/지역정보
         *******************************/
        //editor.putString("alarm"+(alarmPreferences.getAll().size()+1), myData.get_time()+"/"+checked_weather);
        editor.putString("alarm"+(alarmPreferences.getAll().size()+1), myData.get_time()+"/"+checked_weather+"/"+getLocationExtras);
        editor.commit();

        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);

        /** 선택한 날씨 정보 Intent로 넘김 **/
        /** PendingIntent 의 4번째 파라미터는 Flag로 이미 실행 중인 알람이 있다면 extra Data만 교체함 **/
        alarmIntent.putExtra("weather", checked_weather);
        alarmIntent.putExtra("x", getxExtras);
        alarmIntent.putExtra("y", getyExtras);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /** 사용자가 매일 알람을 허용했다면 **/
        if (dailyNotify) {
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            /**부팅 후 실행되는 리시버 사용가능하게 설정**/
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

        /** 메인 화면으로 이동 **/
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //startActivity(intent);
        setResult(RESULT_OK, intent);

//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }


}
