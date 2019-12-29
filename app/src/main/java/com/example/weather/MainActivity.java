package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.Alarm.AlarmData;
import com.example.weather.Alarm.AlarmReceiver;
import com.example.weather.WeatherAPI.LocationCodeFetcher;
import com.example.weather.WeatherAPI.Pin;
import com.example.weather.WeatherAPI.WeatherFetcher;
import com.example.weather.WeatherAPI.WeatherSet;
import com.example.weather.cLocation.ConvertLatLon;

import java.io.IOException;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.rambler.libs.swipe_layout.SwipeLayout;

public class MainActivity extends AppCompatActivity {
    Pin pin = null;
    Button bt_setAlarm, bt_search, bt_location;
    TextView tv_location, tw_weather;
    Switch AlarmSwitch;
    SimpleDateFormat sdf;
    WeatherSet weather;
    String[] Stringlocation = {"대구광역시", "동구", "안심1동"};
    LocationCodeFetcher lcf;
    WeatherFetcher wf;
    private int REQUEST_SET_ALARM = 2;
    private int REQUEST_SET_LOCATE = 3;
    private int REQUEST_Modify_ALARM = 4;

    Intent ReceivedIntent;
    Adapter adapter;
    RecyclerView recycler;
    GetLocation mylocation;     //현재 위치 가지고 오는 class
    Boolean isGetLocation = false;          // 현재 위치 가지고 왔는지 체크하는 flag



    public static boolean TFLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /***  위치값 가져오기 ***/
        mylocation = new GetLocation();
        mylocation.StartGetLocation(this);

        bt_setAlarm = (Button) findViewById(R.id.bt_setAlarm);
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_location = (Button)findViewById(R.id.bt_location);
        tv_location = (TextView)findViewById(R.id.tv_location);

        bt_setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent set_Alarm = new Intent(MainActivity.this, AlarmActivity.class);
                set_Alarm.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                set_Alarm.putExtra("x",pin.getSx());
                set_Alarm.putExtra("y",pin.getSy());
                set_Alarm.putExtra("address", Stringlocation[0]+" "+ Stringlocation[1]+" "+ Stringlocation[2]);
                Log.d("TEST","adapter.alarmDataHashMap.size()"+adapter.alarmDataHashMap.size()+1);
                set_Alarm.putExtra("postion",adapter.alarmDataHashMap.size()); //수정 완료
                //데이터 수정 삭제를 위한  구문
                //adapter.alarmDataHashMap
                //현재 알람의 갯수를 파악하여 넘어간다.

                startActivityForResult(set_Alarm, REQUEST_SET_ALARM);
            }
        });

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent set_locate = new Intent(MainActivity.this, AddressSearchActivity.class);
                set_locate.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(set_locate, REQUEST_SET_LOCATE);
            }
        });

        /** 현재 위치 다시 가져오기 **/
        bt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mylocation.StartGetLocation(MainActivity.this);
                isGetLocation = false;
            }
        });

        /** SwipView 알람 기록 **/
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new Adapter();
        recycler = findViewById(R.id.recyclerView);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        /***  지역 위치 설정 ***/
        weather = null;
        lcf = new LocationCodeFetcher();
        wf = new WeatherFetcher();
        sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 정각");
        pin = lcf.fetchLocationCode(Stringlocation);                                                                                                                        //x ,y값이 생성되는곳

        try {
            weather = wf.fetchWeather(pin.getSx(), pin.getSy());
            //Log.i("TEST", "청운 효자동 x y 출력" + pin.getSx() + ", " +  pin.getSy());
        } catch (Exception E) {
            Log.i("TEST", "날씨 정보 파싱 에러 : " + E.toString());
        }

        /***  날씨 아이콘 설정 ***/
        weather.weatherIcon(this);
        // TV 설정
        tw_weather = (TextView)findViewById(R.id.tw_weather);
        tw_weather.setText(sdf.format(weather.getBaseDate()) + "의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");

        SharedPreferences alarmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = alarmPreferences.edit();
        editor.clear();
        editor.commit();

        /***  위치값 가져오기 ***/
        mylocation = new GetLocation();
        mylocation.StartGetLocation(this);
        Log.i("TEST", "create저장된 알람 있음" + alarmPreferences.getAll().size() + "  시간" + alarmPreferences.getAll());

        /** 현재 위치 가지고 왔는지 체크하는 Task **/
        TimerTask getLocation = new TimerTask() {
            @Override
            public void run() {
                if(isGetLocation == true){
                    mylocation.StopGetLocation();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(getLocation, 0, 3000);

        /** 블루투스 스캔 실행 **/
        //startBTeService();

        //Log.i("TEST", "발표시각 : " + sdf.format(weather.getBaseDate()));
        //Log.i("TEST", sdf.format(weather.getFcstDate()) + "의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");

    }

    @Override
    protected void onResume() {
        super.onResume();
        /***  sharedPreferens 확인 ***/
        SharedPreferences alarmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        /***  알람 있는지 유무 체크 ***/
        if (alarmPreferences.getAll().size() == 0) {
            // 저장된 알람이 없음
            Log.i("TEST", "저장된 알람 없음");
        } else {
            /** RecycleView 어뎁터 재설정 **/
            adapter.setAdapter(alarmPreferences);
            recycler.removeAllViews();
            recycler.setAdapter(adapter);

            Log.i("TEST", "저장된 알람 있음" + alarmPreferences.getAll().size() + "  시간" + alarmPreferences.getAll());

        }

        /***  주소 변경 부분 ***/
        try{
            //Log.d("test",ReceivedIntent.getExtras().getString("address"));
            //Log.d("test","4321 "+ReceivedIntent.getExtras().getString("x"));
            //Log.d("test","4321 "+ReceivedIntent.getExtras().getString("y"));

            Stringlocation = ReceivedIntent.getExtras().getString("address").split("\\s");
            //pin = lcf.fetchLocationCode(Stringlocation); //아마도 NULL 로 인한 앱 종료 오류 해결?
            pin.setSx(ReceivedIntent.getExtras().getString("x"));
            pin.setSy(ReceivedIntent.getExtras().getString("y"));

            Log.d("test", "1234"+pin.getSx());
            Log.d("test", "1234"+pin.getSy());
            weather = wf.fetchWeather(pin.getSx(), pin.getSy());
        }catch (Exception E){
            Log.i("test", E.toString());
        }

        try{
            SharedPreferences getShared;
            int posion = ReceivedIntent.getExtras().getInt("posion");

            String key = "alarm"+ReceivedIntent.getExtras().getInt("posion");
            Log.d("test",key);
            SharedPreferences prefs =getSharedPreferences("alarm", MODE_PRIVATE);
            String getShared2 = prefs.getString(key,"0"); //키값, 디폴트값
            Log.d("test", "11111111111111111111111111111111111전"+getShared2);

            String[] anyweather = getShared2.split("/");
            String[] anytime = anyweather[0].split(":");

            AlarmData tmp= adapter.alarmDataHashMap.get(posion);

            tmp.set_time(anytime[0],anytime[1]);
            tmp.set_weather(anyweather[1]);


        }catch (Exception E){
            Log.i("test", E.toString());
        }

        weather.weatherIcon(MainActivity.this);
        tw_weather.setText(Stringlocation[0]+" "+ Stringlocation[1]+" "+ Stringlocation[2]+"\n"+sdf.format(weather.getBaseDate()) +"의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");
        //Log.i("test", "x y 변경값 없음");

    }


    /***************************************yong
     *  Intent 수신을 위한 onActivityResult Override
     * **************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddressSearchActivity.RESULT_OK){
            if(requestCode == REQUEST_SET_LOCATE) {
                Log.e("test", "주소 검색: intent 결과 받기 성공");
                ReceivedIntent = data;
            }
        }

        if (resultCode == AlarmActivity.RESULT_OK) {
            if (requestCode == REQUEST_SET_ALARM) {
                Log.e("test", "알람 설정: intent 결과 받기 성공");
                ReceivedIntent = data;
            }
        }
        if (resultCode == AlarmModifyActivity.RESULT_OK) {
            if (requestCode == REQUEST_Modify_ALARM) {
                Log.e("test", "알람 수정 설정: intent 결과 받기 성공");
                ReceivedIntent = data;
            }
        }
    }

    private void startBTeService(){
        //notification을 만들고 서비스를 foregrund로 실행시키는 부분
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel("channel2", "2번채널", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("2번채널입니다");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.GREEN);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(notificationChannel);

        TFLAG = true;
        Intent backStartIntent = new Intent(getApplicationContext() , BLEService.class);
        backStartIntent.setAction("Action1");
        ContextCompat.startForegroundService(getApplicationContext(), backStartIntent);
    }

    /***************************************
     *  SwipView Adapter
     * **************************************/
    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private int count = 0;           //세팅 갯수
        private  int[] itemsOffset = new int[count];
        private HashMap<Integer, AlarmData> alarmDataHashMap = new HashMap<>();
        AlarmData alarmData;

        SharedPreferences getShared;

        public void setAdapter(SharedPreferences sharedPreferences){
            getShared = sharedPreferences;
            count = getShared.getAll().size();
            //Log.i("TEST", "갯수 : " + String.valueOf(count));

            itemsOffset = new int[count];

            for(int i = 0; i<count; i++){
                String searchTag = "alarm"+(i+1);
                //Log.i("TEST", searchTag);
                String value = getShared.getString(searchTag, "");
                //Log.i("TEST", "꺼내온 값은 : " + value);
                try{
                    String[] anyweather = value.split("/");
                    String[] anytime = anyweather[0].split(":");
                    //Log.i("TEST", "anyweather " + anyweather[0] + " 그리고 " + anyweather[1]);
                    //Log.i("TEST", "anytime " + anytime[0] + " 그리고 " + anytime[1]);

                    try{
                        //alarmData = new AlarmData(null,anyweather[1],Boolean.TRUE);
                        alarmData = new AlarmData(null,anyweather[1],anyweather[2],Boolean.TRUE);
                        alarmData.setX(anyweather[3]);
                        alarmData.setY(anyweather[4]);
                    }
                    catch (Exception E){
                        Log.i("TEST", "생성이 글러머금" + E.toString());
                    }

                    alarmData.set_time(anytime[0], anytime[1]);
                    alarmDataHashMap.put(count - 1, alarmData);

                }catch (Exception E){
                    Log.i("TEST", "sharedPreferences 값 못 가져옴 " + E.toString());
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, final int index) {
            int layoutId;
            layoutId = R.layout.list_item_left_right;
            /*
            switch (viewType) {
                case 0:
                    layoutId = R.layout.list_item_left_right;
                    break;

                case 1:
                    layoutId = R.layout.list_item_left;
                    break;

                case 2:
                    layoutId = R.layout.list_item_right;
                    break;

                default:
                    throw new IllegalArgumentException();
            }
            */

            final View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            final ViewHolder viewHolder = new ViewHolder(itemView);

            /** textview 삽입 ( 해당 row index는 viewType으로 구분) **/
            viewHolder.weather.setText(alarmDataHashMap.get(index).get_weather());
            viewHolder.time.setText(alarmDataHashMap.get(index).get_time());
            viewHolder.location.setText(alarmDataHashMap.get(index).get_location());


            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.swipeLayout.animateReset();
                }
            };

            if (viewHolder.leftView != null) {
                viewHolder.leftView.setClickable(true);
                //viewHolder.leftView.setOnClickListener(onClick);
                //수정파트
                viewHolder.leftView.setOnClickListener(new View.OnClickListener() {
                   @Override
                       public void onClick(View view) {
                       Log.i("TEST", " Left : 수정하는 공간 버튼 형식");
                       Log.i("TEST", "posion 값 : "+index);
                       Intent alarmModifyIntent = new Intent(com.example.weather.MainActivity.this,AlarmModifyActivity.class); //static class 에서 는 불가능 함 -> adapter static 제거
                       //Intent alarmModifyIntent = new Intent(com.example.weather.MainActivity.this,AlarmActivity.class); //static class 에서 는 불가능 함 -> adapter static 제거
                       alarmModifyIntent.putExtra("posion",index);
                       AlarmData tmp = alarmDataHashMap.get(index);
                       alarmModifyIntent.putExtra("address",tmp.get_location());
                       alarmModifyIntent.putExtra("x",tmp.getX());
                       alarmModifyIntent.putExtra("y",tmp.getY());
                       alarmModifyIntent.putExtra("postion",index);

                       alarmModifyIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                       startActivityForResult(alarmModifyIntent, REQUEST_Modify_ALARM);
                       }
                   }
                );
            }

            if (viewHolder.rightView != null) {
                viewHolder.rightView.setClickable(true);
                //viewHolder.rightView.setOnClickListener(onClick);
                //수정파트
                viewHolder.rightView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Log.i("TEST", " right : 삭제하는 공간 버튼 형식");
                           Log.i("TEST", "posion 값 : "+index);
                           AlarmData tmp = alarmDataHashMap.get(index);
                           AlarmManager am = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                           Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                           PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, index, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                           if (sender != null) { am.cancel(sender); sender.cancel(); }
                           alarmDataHashMap.remove(index);

                       }
                   }
                );
            }

            viewHolder.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                @Override
                public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {
                }

                @Override
                public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
                    /** 슬라이드 후 Toast 텍스트 출력 부분 **/
                    // 오른쪽으로 움직였는지 확인 또는 아님
                    Toast.makeText(swipeLayout.getContext(),
                            (moveToRight ? "Left" : "Right") + " 움직였네유",
                            Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                }

                @Override
                public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                }
            });


            return new ViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.swipeLayout.setOffset(itemsOffset[position]);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                itemsOffset[holder.getAdapterPosition()] = holder.swipeLayout.getOffset();
            }
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {
            return count;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final SwipeLayout swipeLayout;
            private final View rightView;
            private final View leftView;

            TextView time;
            TextView weather;
            TextView location;
            Switch alarmSwitch;

            ViewHolder(View itemView) {
                super(itemView);
                swipeLayout = itemView.findViewById(R.id.swipe_layout);
                rightView = itemView.findViewById(R.id.right_view);
                leftView = itemView.findViewById(R.id.left_view);

                time = itemView.findViewById(R.id.tvTime);
                weather = itemView.findViewById(R.id.tvWeather);
                location = itemView.findViewById(R.id.tvLocation);
            }
        }
    }

    /***************************************
    *  메인 엑티비티 위치 값 찍음
    * **************************************/
    public void displayLocation(String val){
        tv_location.setText(val);
    }

    /***************************************
     *  현재 위치 값 가져옴
     * **************************************/
    public class GetLocation {
        LocationManager locationManager;
        private int xPin, yPin;
        private double longitude, latitude;
        String provider;

        public void StartGetLocation(Context context) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // 앱 퍼미션 확인
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);
            }
        }

        /** LocationManager 멈춤 **/
        public void StopGetLocation(){
            locationManager.removeUpdates(mLocationListener);
        }

        public int getxPin() {
            return xPin;
        }

        public int getyPin() {
            return yPin;
        }

        public double getLat() {
            return latitude;
        }

        public double getLon() {
            return longitude;
        }

        public String getProvider() {
            return provider;
        }

        /***  LocationMagnager 리스너 ***/
        private final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /***  위치 값이 갱신 되면 이벤트 발생 ***/
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                provider = location.getProvider();

                /***  위도 경도를 x,y로 변환 ***/
                ConvertLatLon convertLatLon = new ConvertLatLon((float) longitude, (float) latitude);

                xPin = convertLatLon.getX();
                yPin = convertLatLon.getY();

                /***  위도 경도를 주소로 변환 ***/
                Geocoder geocoder = new Geocoder(MainActivity.this);
                List<Address> list = null;

                try {
                    Log.i("TEST", "lat" + latitude + "lon" + longitude);

                    list = geocoder.getFromLocation(latitude, longitude, 100);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("TEST", "변환 실패 : " + e.toString());
                }
                if (list != null) {
                    if (list.size() == 0) {
                        displayLocation("해당되는 주소가 없습니다.");
                    } else {
                        Log.i("TEST", "위치 값은 \n" + list.get(0).getAddressLine(0));
                        displayLocation("현재위치 : \n" + list.get(0).getAddressLine(0));
                    }
                }

                /***  메인 액티비티의 Textview의 값을 변경 ***/
                //displayLocation("위도 : " + longitude + " 경도 : " + latitude + "X 값 : " + convertLatLon.getX() + " Y 값 :" + convertLatLon.getY() + " \n서비스 제공자" + provider);

                Toast.makeText(MainActivity.this, "위도 : " + longitude + " 경도 : " + latitude + " \n서비스 제공자" + provider, Toast.LENGTH_LONG).show();
                Log.i("TEST", "위도 : " + longitude + " 경도 : " + latitude + "X 값 : " + convertLatLon.getX() + " Y 값 :" + convertLatLon.getY() + " \n서비스 제공자" + provider);


                /** 변환된 xy로 날씨 재 검색 **/
                try{
                    weather = wf.fetchWeather(pin.getSx(), pin.getSy());
                    weather.weatherIcon(MainActivity.this);

                    /**  location 배열 재 설정 **/
                    Stringlocation[0] = list.get(0).getAddressLine(0).split(" ")[1];
                    Stringlocation[1] = list.get(0).getAddressLine(0).split(" ")[2];
                    Stringlocation[2] = list.get(0).getAddressLine(0).split(" ")[3];

                    tw_weather.setText(list.get(0).getAddressLine(0)+"\n"+sdf.format(weather.getBaseDate()) +"의 비/눈 상황은 " + weather.getPty() + ", 하늘은 " + weather.getSky() + "입니다");
                }catch (Exception E){
                    Log.i("TEST", "현재 위치 값으로 변환 실패");
                }

                /** 현재 위치 가지고 왔다고 flag 변경 **/
                isGetLocation = true;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }
}
