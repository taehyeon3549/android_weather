package com.example.weather.cLocation;

import java.io.Serializable;

public class cLocation implements Serializable {
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
