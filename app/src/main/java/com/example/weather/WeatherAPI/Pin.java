package com.example.weather.WeatherAPI;

public class Pin {
    private String sx;
    private String sy;

    public Pin(String x, String y){
        sx = x;
        sy = y;
    }
    public String getSx() {
        return sx;
    }
    public void setSx(String sx) {
        this.sx = sx;
    }
    public String getSy() {
        return sy;
    }
    public void setSy(String sy) {
        this.sy = sy;
    }
}
