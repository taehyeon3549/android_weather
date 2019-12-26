package com.example.weather.cLocation;

import android.util.Log;

import java.util.ArrayList;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.StrictMath.pow;

public class ConvertLatLon {
    public final static int NX = 149;       // X 축 격자점 수
    public final static int NY = 253;       // Y 축 격자점 수

    private ArrayList<Float> xyPin;
    private float lon, lat;
    LamcParameter lamcParameter;

    // 파라미터 클래스
    class LamcParameter{
        float  Re;          /* 사용할 지구반경 [ km ]      */
        float  grid;        /* 격자간격        [ km ]      */
        float  slat1;       /* 표준위도        [degree]    */
        float  slat2;       /* 표준위도        [degree]    */
        float  olon;        /* 기준점의 경도   [degree]    */
        float  olat;        /* 기준점의 위도   [degree]    */
        float  xo;          /* 기준점의 X좌표  [격자거리]  */
        float  yo;          /* 기준점의 Y좌표  [격자거리]  */
        int    first;       /* 시작여부 (0 = 시작)         */

        LamcParameter(float  Re, float  grid, float  slat1, float  slat2, float  olon, float  olat, float  xo, float  yo, int first){
            this.Re = Re;
            this.grid = grid;
            this.slat1 = slat1;
            this.slat2 = slat2;
            this.olon = olon;
            this.olat = olat;
            this.xo = xo;
            this.yo = yo;
            this.first = first;
        }

        public float getGrid(){
            return this.grid;
        }
    }

    // 생성자
    public ConvertLatLon(float longitude, float latitude){
        lon = longitude;
        lat = latitude;

        //
        //  동네예보 지도 정보
        //
        float Re = (float) 6371.00877;     // 지도반경
        float grid  = (float) 5.0;            // 격자간격 (km)
        float slat1 = (float) 30.0;           // 표준위도 1
        float slat2 = (float) 60.0;           // 표준위도 2
        float olon  = (float) 126.0;          // 기준점 경도
        float olat  = (float) 38.0;           // 기준점 위도
        float xo    = 210/grid;   // 기준점 X좌표
        float yo    = 675/grid;   // 기준점 Y좌표
        int first = 0;

        // 기본 파라미터 생성
        lamcParameter = new LamcParameter(Re, grid, slat1, slat2, olon, olat, xo, yo, first);

        // 변환 시작
        xyPin = map_conv(lon, lat, lamcParameter);
    }

    public int getX(){
        int X = Math.round(xyPin.get(0));
        return X;
    }

    public int getY(){
        int Y = Math.round(xyPin.get(1));
        return Y;
    }

    public void setLonLat(String x, String y){
        this.lon = Float.parseFloat(x);
        this.lat = Float.parseFloat(y);
    }

    public void convertPin(){
        this.map_conv(this.lon, this.lat, this.lamcParameter);
    }


     //좌표변환
    ArrayList<Float> map_conv
    (
            float  lon,                    // 경도(degree)
            float  lat,                    // 위도(degree)
            LamcParameter map       // 지도정보
    ) {
        float  lon1, lat1;

        lon1 = lon;
        lat1 = lat;

        ArrayList<Float> result = lamcproj(lon1, lat1, map);

        result.set(0, (float)(result.get(0) + 1.5));
        result.set(0, (float)(result.get(1) + 1.5));

        return result;
    }

     //좌표변환 공식
    ArrayList<Float> lamcproj(float lon, float lat, LamcParameter map){
        ArrayList<Float> convert = new ArrayList<>();

        double  PI, DEGRAD;
        double  re = 0, olon, olat, sn, sf, ro = 0;
        double  slat1, slat2, alon, alat, xn, yn, ra, theta;

        if(map.first == 0) {
            PI = asin(1.0)*2.0;
            DEGRAD = PI/180.0;

            re = map.Re/map.grid;
            slat1 = map.slat1 * DEGRAD;
            slat2 = map.slat2 * DEGRAD;
            olon = map.olon * DEGRAD;
            olat = map.olat * DEGRAD;

            sn = tan(PI*0.25 + slat2*0.5)/tan(PI*0.25 + slat1*0.5);
            sn = log(cos(slat1)/cos(slat2))/log(sn);
            sf = tan(PI*0.25 + slat1*0.5);
            sf = pow(sf,sn)*cos(slat1)/sn;
            ro = tan(PI*0.25 + olat*0.5);
            ro = re*sf/pow(ro,sn);

            map.first = 1;

            ra = tan(PI*0.25+(lat)*DEGRAD*0.5);
            ra = re*sf/pow(ra,sn);
            theta = (lon)*DEGRAD - olon;
            if (theta >  PI) theta -= 2.0*PI;
            if (theta < -PI) theta += 2.0*PI;
            theta *= sn;

            convert.add(0, (float) (ra*sin(theta)) + map.xo);
            convert.add(1,(float)(ro - ra*cos(theta)) + map.yo);
        }

        return convert;
    }


}
