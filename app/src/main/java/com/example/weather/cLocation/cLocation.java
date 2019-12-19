package com.example.weather.cLocation;

public class cLocation {
    private String address;
    private String x;
    private String y;

    public cLocation(String address, String x, String y){
        this.address = address;
        this.x = x ;
        this.y = y ;
    };

    public String getAddress() {
        return address;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

}
