package com.example.weather.WeatherAPI;

import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.util.Log;


import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WeatherParser {
    private WeatherParser() {
    }

    private static class parser {
        private static final WeatherParser instance = new WeatherParser();
    }

    public static WeatherParser getInstance() {
        return parser.instance;
    }

    public JSONArray getRemoteJSONArray(String url) throws ExecutionException, InterruptedException {
        final StringBuffer jsonHtml = new StringBuffer();

            AsyncTask<String, Void, JSONArray> asyncTask = new AsyncTask<String, Void, JSONArray>() {
                @Override
                protected JSONArray doInBackground(String... url) {
                    try {
                    URL u = new URL(url[0]);
                    InputStream uis = u.openStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(uis, "UTF-8"));

                    String line = null;
                    while ((line = br.readLine()) != null) {
                        jsonHtml.append(line + "\r\n");
                    }
                    br.close();
                    uis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                    JSONArray jsonArr = (JSONArray) JSONValue.parse(jsonHtml.toString());
                    //Log.i("TEST", "출력" + jsonArr.toString());
                    return jsonArr;
                }
            };

        JSONArray jsonArray = asyncTask.execute(url).get();

        return jsonArray;
    }

    public JSONArray getWeatherJSONArray(final String url) throws ExecutionException, InterruptedException {

        AsyncTask<String, Void, JSONArray> asyncTask = new AsyncTask<String, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... url) {
                final StringBuffer jsonHtml = new StringBuffer();
                JSONArray jsonArr = null;
                JSONObject jsonObj = null;
                final String[] saAttribName = {"response", "body", "items"};

                try {
                    URL u = new URL(url[0]);
                    InputStream uis = u.openStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(uis, "UTF-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        jsonHtml.append(line + "\r\n");
                    }
                    br.close();
                    uis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(jsonHtml.toString());

                jsonObj = (JSONObject) JSONValue.parse(jsonHtml.toString());
                try {
                    for (int i = 0; i < saAttribName.length; i++)
                        jsonObj = (JSONObject) jsonObj.get(saAttribName[i]);
                    jsonArr = (JSONArray) jsonObj.get("item");

                }catch (Exception E){
                    Log.i("TEST", E.toString());
                }

                return jsonArr;
            }
        };

        JSONArray jsonArray = asyncTask.execute(url).get();

        return jsonArray;
    }

    public Map<String, String> getJsonSubMap(JSONArray jsonArrSource) {
        Map<String, String> jsonMap = new LinkedHashMap<String, String>();

        // 기상청 API 에서 받아오는 JSON은 code:value형식만 갖추므로
        // 이 Algorithm으로 일관되게 mapping할 수 있음
        // (leaf단에서는 x,y값이 추가로 들어가므로 다른 method사용해야함)
        for (int i = 0; i < jsonArrSource.size(); i++) {
            try{
                JSONObject jsonObjItem = (JSONObject) jsonArrSource.get(i); // JSONArray에서 JSONObject하나씩 가져옴
                String code = (String) jsonObjItem.get("code"); // JSONObject에서 key, value 가져옴
                String value = (String) jsonObjItem.get("value");
                jsonMap.put(value, code); // 지역이름으로 code를 알아내길 원하므로 K,V를 바꿔서 mapping
            }catch (Exception E){
                Log.i("TEST", E.toString());
            }
        }
        return jsonMap;
    }

    public Map<String, Pin> getJsonLeafMap(JSONArray jsonArrSource) {
        Map<String, Pin> jsonMap = new LinkedHashMap<String, Pin>();
        for (int i = 0; i < jsonArrSource.size(); i++) {
            try{
                JSONObject jsonObjItem = (JSONObject) jsonArrSource.get(i);
                String value = (String) jsonObjItem.get("value");
                String x = (String) jsonObjItem.get("x");
                String y = (String) jsonObjItem.get("y");
                jsonMap.put(value, new Pin(x, y));
            }catch (Exception E){
                Log.i("TEST", E.toString());
            }
        }
        return jsonMap;
    }
}
