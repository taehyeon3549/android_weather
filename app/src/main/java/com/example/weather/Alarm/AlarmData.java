package com.example.weather.Alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmData {
    //시간, 날씨, 상태
    private String time;
    private String weather;
    private Boolean alarm_state;

    public AlarmData(Calendar calendar, String weather, Boolean alarm_state){
        this.weather = weather;
        this.alarm_state = alarm_state;
        this.time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());
    }

    public String get_time(){
        return time;
    }

    public String get_weather(){
        return weather;
    }

    public Boolean alarm_state(){
        return alarm_state;
    }

    public void set_time(int hour, int min){
        time = hour + " : " + min;
    }

    public void set_weather(String weather_val){
        weather = weather_val;
    }

    public void set_alarm_state(Boolean val){
        alarm_state = val;
    }
}
