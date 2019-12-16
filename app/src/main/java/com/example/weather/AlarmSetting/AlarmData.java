package com.example.weather.AlarmSetting;

public class AlarmData {
    //시간, 날씨, 상태
    private String time;
    private String weather;
    private String alarm_state;

    public String get_time(){
        return time;
    }

    public String get_weather(){
        return weather;
    }

    public String alarm_state(){
        return alarm_state;
    }

    public void set_time(int hour, int min){
        time = hour + " : " + min;
    }

    public void set_weather(String weather_val){
        weather = weather_val;
    }

    public void set_alarm_state(String val){
        alarm_state = val;
    }
}
