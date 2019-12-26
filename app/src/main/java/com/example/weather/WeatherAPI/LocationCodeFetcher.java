package com.example.weather.WeatherAPI;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONArray;

import java.util.Map;

public class LocationCodeFetcher extends AsyncTask<String[], String[], Pin> {
    private final String sUrlDef = "http://www.kma.go.kr/DFSROOT/POINT/DATA/";
    private final String sUrlMdlHead = "mdl.";
    private final String sUrlLeafHead = "leaf.";
    private final String sUrlTail = ".json.txt";
    private Map<String, String> mapTop;
    private Map<String, String> mapMdl;
    Map<String, Pin> mapLeaf;

    private WeatherParser jjp;

    public LocationCodeFetcher() {
        jjp = WeatherParser.getInstance();
    }

    @Override
    protected Pin doInBackground(String[]... saLocation) {
        JSONArray jsonArrTop = null;
        JSONArray jsonArrMdl = null;
        JSONArray jsonArrLeaf = null;

        try{
            jsonArrTop = jjp.getRemoteJSONArray(getStrUrl("top"));
            mapTop = jjp.getJsonSubMap(jsonArrTop);
            jsonArrMdl = jjp.getRemoteJSONArray(getStrUrl("mdl", mapTop.get(saLocation[0])));
            mapMdl = jjp.getJsonSubMap(jsonArrMdl);
            jsonArrLeaf = jjp.getRemoteJSONArray(getStrUrl("leaf", mapMdl.get(saLocation[1])));
            mapLeaf = jjp.getJsonLeafMap(jsonArrLeaf);
        }catch (Exception E){
            Log.i("TEST", E.toString());
        }

        return mapLeaf.get(saLocation[saLocation.length - 1]);
    }

    private String getStrUrl(String s) {
        if (s.equals("top"))
            return sUrlDef + "top" + sUrlTail;
        else
            return sUrlDef;
    }

    private String getStrUrl(String s, String code) {
        String tmp = null;
        if (s.equals("mdl"))
            tmp = sUrlMdlHead;
        else if (s.equals("leaf"))
            tmp = sUrlLeafHead;
        return sUrlDef + tmp + code + sUrlTail;
    }

    public Pin fetchLocationCode(String[] saLocation) { // 인자: {시군구, 시도, 동면읍}
        JSONArray jsonArrTop = null;
        JSONArray jsonArrMdl = null;
        JSONArray jsonArrLeaf = null;

        try{
            jsonArrTop = jjp.getRemoteJSONArray(getStrUrl("top"));
            mapTop = jjp.getJsonSubMap(jsonArrTop);
            jsonArrMdl = jjp.getRemoteJSONArray(getStrUrl("mdl", mapTop.get(saLocation[0])));
            mapMdl = jjp.getJsonSubMap(jsonArrMdl);
            jsonArrLeaf = jjp.getRemoteJSONArray(getStrUrl("leaf", mapMdl.get(saLocation[1])));
            mapLeaf = jjp.getJsonLeafMap(jsonArrLeaf);
        }catch (Exception E){
            Log.i("TEST", "시도군 검색 에러(LocationCodeFetcher.java) >>" + E.toString());
        }

        return mapLeaf.get(saLocation[saLocation.length - 1]);
    }

    public Map<String, String> getMapTop() {
        return mapTop;
    }

    public Map<String, String> getMapMdl() {
        return mapMdl;
    }

    public Map<String, Pin> getMapLeaf() {
        return mapLeaf;
    }
}
