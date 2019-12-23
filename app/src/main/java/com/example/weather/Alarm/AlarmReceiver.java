package com.example.weather.Alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.weather.MainActivity;
import com.example.weather.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*************************************************************
         * 문제: receive를 받았을때 오늘의 날씨와 알람 설정한 날씨가
         * 같은지 확인을 하고 같다면 notiy 출력 아니면 패스
         **************************************************************/

        String getWeatherExtra = intent.getStringExtra("weather");
        Log.i("TEST", "넘겨받은 선택된 날씨" + getWeatherExtra);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");


        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            /** noti 아이콘 설정 **/
            builder.setSmallIcon(R.drawable.mom_black); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


            String channelName ="매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남


        /** 상단바 정보 **/
        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())

                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle("엄마야 's 잔소리")
                .setContentInfo("INFO")
                .setContentIntent(pendingI);

        /** Intent로 넘겨받은 값에 따른 notiy 메세지 변경 **/
        if("맑음".equals(getWeatherExtra)){
            builder.setContentText("오늘 해 쨍쨍하다!! 선크림 바르고 가라이");
        }else  if("비".equals(getWeatherExtra)){
            builder.setContentText("오늘 비온다!! 우산 챙겨 가라이");
        }else if("눈".equals(getWeatherExtra)){
            builder.setContentText("오늘 눈온다!! 우산 챙기고, 따시게 입어라이");
        }else if("흐림".equals(getWeatherExtra)){
            builder.setContentText("오늘 흐리다!! 우울해 있지말고 비타민 하나 묵어라이");
        }else{
            builder.setContentText("니 알아서 똑디 챙겨가라이");
        }

        if (notificationManager != null) {

            // 노티피케이션 동작시킴
            notificationManager.notify(1234, builder.build());

            Calendar nextNotifyTime = Calendar.getInstance();

            // 내일 같은 시간으로 알람시간 결정
            nextNotifyTime.add(Calendar.DATE, 1);

            //  Preference에 설정한 값 저장
            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
            editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
            editor.apply();
        }
    }
}
