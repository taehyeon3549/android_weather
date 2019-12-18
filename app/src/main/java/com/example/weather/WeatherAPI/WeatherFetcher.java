package com.example.weather.WeatherAPI;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class WeatherFetcher {
    private final int BASE_DATE = 3;
    private final int BASE_TIME = 5;
    private final int NX = 7;
    private final int NY = 9;
    private final String[] uri = {
            "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?ServiceKey=",
            "ye3Vfiaa0XOU3HTyFOF9Cbn8x4X%2FLtWxwEm4DgIb6baeAHASEHo7zu49Yk2%2FqhIcpsSCl0fCV4%2FirHJ0asf2Og%3D%3D", //service key
            "&base_date=", "", "&base_time=", "", "&nx=", "", "&ny=", "", "&_type=json"
    };

    private Calendar calBase = null;
    private int hour;
    private int lastBaseTime;

    private WeatherParser weatherParser = null;

    public WeatherFetcher() {
        weatherParser = WeatherParser.getInstance();
        calBase = Calendar.getInstance(); // 현재시간 가져옴
        calBase.set(Calendar.MINUTE, 0); // 분, 초 필요없음
        calBase.set(Calendar.SECOND, 0);
        hour = calBase.get(Calendar.HOUR_OF_DAY);
        lastBaseTime = getLastBaseTime(hour);
    }

    private int getLastBaseTime(int t) {
        if (t >= 0) {
            if (t < 2) {
                calBase.add(Calendar.DATE, -1);
                calBase.set(Calendar.HOUR_OF_DAY, 23);
                return 23;
            } else {
                calBase.set(Calendar.HOUR_OF_DAY, t - (t + 1) % 3);
                return t - (t + 1) % 3;
            }
        } else
            return -1;
    }

    private String getBaseTime() {
        if (lastBaseTime / 10 > 0) // 두자리수이면
            return lastBaseTime + "00";
        else // 한자리수이면
            return "0" + lastBaseTime + "00";
    }

    public WeatherSet fetchWeather(String nx, String ny) throws ExecutionException, InterruptedException {
        WeatherSet ws = null;
        String sUrl = new String();
        int pop = -1, sky = -1;

        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat( "HHmm");

        Calendar now = Calendar.getInstance();

        String BaseDate = dateFormat.format(now.getTime());
        String BaseTime = timeFormat.format(now.getTime());

        Log.i("TEST", BaseDate);
        Log.i("TEST", now.getTime().toString());

        uri[BASE_DATE] = new SimpleDateFormat("yyyyMMdd").format(calBase.getTime());
        uri[BASE_TIME] = getBaseTime();
        uri[NX] = nx;
        uri[NY] = ny;

        for (int i = 0; i < uri.length; i++)
            sUrl += uri[i];

        JSONArray jsonArr = weatherParser.getWeatherJSONArray(sUrl);

        for (int i = 0; i < jsonArr.size(); i++) {
            try {
                JSONObject jobj = (JSONObject) jsonArr.get(i);
                if (((String) jobj.get("category")).equals("PTY"))
                    pop = (int) (long) jobj.get("fcstValue"); // JSON에서 ""로 감싸지지않은 값은 long 형이므로 casting 필수!
                else if (((String) jobj.get("category")).equals("SKY"))
                    sky = (int) (long) jobj.get("fcstValue");
            }catch (Exception E){
                Log.i("TEST", E.toString());
            }
        }
        if (pop != -1 && sky != -1){
            Date bd = calBase.getTime();
            ws = new WeatherSet(pop, sky, bd);
        }
        return ws;
    }
}