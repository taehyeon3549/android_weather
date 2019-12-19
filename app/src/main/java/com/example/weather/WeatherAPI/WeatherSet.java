package com.example.weather.WeatherAPI;

import android.view.View;
import android.widget.ImageView;

import com.example.weather.MainActivity;
import com.example.weather.R;

import java.util.Calendar;
import java.util.Date;

public class WeatherSet {
    private int pty;
    private int sky;
    private Date baseDate = null;
    private Date fcstDate = null;

    public WeatherSet(int p, int s, Date bd) {
        pty = p;
        sky = s;
        baseDate = bd;
        Calendar calBase = Calendar.getInstance();
        calBase.setTime(baseDate);
        calBase.add(Calendar.HOUR_OF_DAY, 4);
        fcstDate = calBase.getTime();
    }

    public String getPty() {
        String retMsg = null;

        switch (pty) {
            case 0:
                retMsg = "없음";
                break;
            case 1:
                retMsg = "비";
                break;
            case 2:
                retMsg = "눈/비";
                break;
            case 3:
                retMsg = "눈";
                break;
            default:
                retMsg = "Error";
                break;
        }
        return retMsg;
    }

    public void setPty(int p) {
        pty = p;
    }

    public int getSkyValue(){
        return sky;
    }

    public String getSky() {
        String retMsg = null;
        switch (sky) {
            case 1:
                retMsg = "맑음";
                break;
            case 2:
                retMsg = "구름 조금";
                break;
            case 3:
                retMsg = "구름 많음";
                break;
            case 4:
                retMsg = "흐림";
                break;
            default:
                retMsg = "Error";
                break;
        }
        return retMsg;
    }

    public Date getBaseDate() {
        return baseDate;
    }

    public Date getFcstDate() {
        return fcstDate;
    }

    public void setSky(int s) {
        sky = s;
    }

    public void weatherIcon(MainActivity view){
        ImageView iv_weather = (ImageView)view.findViewById(R.id.iv_weather);

        if(this.getSky().equals("Error"))    iv_weather.setImageDrawable(view.getResources().getDrawable(R.drawable.na));
        else if(this.getSky().equals("구름 조금")) iv_weather.setImageDrawable(view.getResources().getDrawable(R.drawable.little_cloudy));
        else if(this.getSky().equals("구름 많음")) iv_weather.setImageDrawable(view.getResources().getDrawable(R.drawable.cloudy));
        else if(this.getSky().equals("흐림")) iv_weather.setImageDrawable(view.getResources().getDrawable(R.drawable.many_cloudy));
        else if(this.getSky().equals("맑음")) iv_weather.setImageDrawable(view.getResources().getDrawable(R.drawable.sunny));
    }
}