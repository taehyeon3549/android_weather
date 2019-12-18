package com.example.weather.WeatherAPI;

import java.util.Calendar;
import java.util.Date;

public class WeatherSet {
    private int pop;
    private int sky;
    private Date baseDate = null;
    private Date fcstDate = null;

    public WeatherSet(int p, int s, Date bd) {
        pop = p;
        sky = s;
        baseDate = bd;
        Calendar calBase = Calendar.getInstance();
        calBase.setTime(baseDate);
        calBase.add(Calendar.HOUR_OF_DAY, 4);
        fcstDate = calBase.getTime();
    }

    public int getPop() {
        return pop;
    }

    public void setPop(int p) {
        pop = p;
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
}