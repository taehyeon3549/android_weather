package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity {

    Button bt_finish;
    RadioGroup rg_weather;
    RadioButton rb_fine, rb_rain, rb_cloud, rb_snow;
    TimePicker time;

    String checked_weather = "fine";
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

        //시간 설정
        time.setHour(12);
        time.setMinute(12);


        rg_weather.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_fine)
                    checked_weather = "fine";
                else if(checkedId == R.id.rb_cloud)
                    checked_weather = "cloud";
                else if(checkedId == R.id.rb_rain)
                    checked_weather = "rain";
                else if(checkedId == R.id.rb_snow)
                    checked_weather = "snow";
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
                Toast.makeText(AlarmActivity.this, "날씨는  " + checked_weather + "시간은 " + tp_hour + "시" + tp_min + "분", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
