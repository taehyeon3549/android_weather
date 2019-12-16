package com.example.weather.WebConnection;

import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class webconnection extends AsyncTask<Void, Void, Void>{
    String json;
    String address;
    BufferedReader br;
    URL url;
    HttpURLConnection conn;
    String protocol;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(protocol);
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            json = br.readLine();

            JSONObject jsonObject_ = new JSONObject(json);
            String response_ = jsonObject_.getString("response");

            JSONObject jsonObject_1 = new JSONObject(response_);
            String body_ = jsonObject_1.getString("body");

            JSONObject jsonObject_2 = new JSONObject(body_);
            String totalCount = jsonObject_2.getString("totalCount");

            for(int k = 1; k<Integer.parseInt(totalCount)/10; k++){
                url = new URL(address+k);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(protocol);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                json = br.readLine();

                //페이지 수에 맞게 반복해서 값을 가져옴
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String response = jsonObject.getString("response");
                    //Log.i("TEST", "response" + response);

                    JSONObject jsonObject1 = new JSONObject(response);
                    String body = jsonObject1.getString("body");
                    //Log.i("TEST", body);

                    JSONObject jsonObject2 = new JSONObject(body);
                    String items = jsonObject2.getString("items");
                    //Log.i("TEST", items);

                    JSONObject jsonObject3 = new JSONObject(items);
                    String item = jsonObject3.getString("item");
                    //Log.i("TEST", item);

                    JSONArray jsonArray = new JSONArray(item);

                    for(int i=0; i<jsonArray.length(); i++){
                        String category = jsonArray.getJSONObject(i).getString("category");

                        // 날씨 형태
                        if(category.equals("SKY")){
                            String SKY_Val = jsonArray.getJSONObject(i).getString("fcstValue");
                            Log.i("TEST", "\nBase 시간 : " + jsonArray.getJSONObject(i).getString("baseTime"));
                            if(SKY_Val.equals("1")){
                                Log.i("TEST", "날씨 화창");
                            }else if(SKY_Val.equals("2")){
                                Log.i("TEST", "비옴");
                            }else if(SKY_Val.equals("3")){
                                Log.i("TEST", "구름 많음");
                            }else if(SKY_Val.equals("4")){
                                Log.i("TEST", "흐림");
                            }
                        }

                        //강수 형태
                        if(category.equals("PTY")){
                            String PTY_Val = jsonArray.getJSONObject(i).getString("fcstValue");
                            Log.i("TEST", "\nBase 시간 : " + jsonArray.getJSONObject(i).getString("baseTime"));
                            if(PTY_Val.equals("0")) {
                                Log.i("TEST", "없음");
                            }else if(PTY_Val.equals("1")) {
                                Log.i("TEST", "비");
                            }else if(PTY_Val.equals("2")) {
                                Log.i("TEST", "눈/비");
                            }else if(PTY_Val.equals("3")) {
                                Log.i("TEST", "눈");
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception E){
            Log.i("TEST", E.toString());
        }


        return null;
    }

    //백그라운드 작업이 시작 되기 전
    @Override
    protected void onPreExecute() {
        address = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?ServiceKey=ye3Vfiaa0XOU3HTyFOF9Cbn8x4X%2FLtWxwEm4DgIb6baeAHASEHo7zu49Yk2%2FqhIcpsSCl0fCV4%2FirHJ0asf2Og%3D%3D&base_date=20191216&base_time=1400&nx=87&ny=90&_type=json&pageNo=";
        protocol = "GET";

        super.onPreExecute();
    }

    //백그라운드 작업이 끝나고
    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
    }
}