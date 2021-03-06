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
import java.util.ArrayList;
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
    String[] Stringlocation = {"???????????????", "??????", "??????1???"};
    LocationCodeFetcher lcf;
    WeatherFetcher wf;
    private int REQUEST_SET_ALARM = 2;
    private int REQUEST_SET_LOCATE = 3;
    private int REQUEST_Modify_ALARM = 4;

    Intent ReceivedIntent;
    Adapter adapter;
    RecyclerView recycler;
    GetLocation mylocation;     //?????? ?????? ????????? ?????? class
    Boolean isGetLocation = false;          // ?????? ?????? ????????? ????????? ???????????? flag



    public static boolean TFLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /***  ????????? ???????????? ***/
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
                //Log.d("TEST","adapter.alarmDataHashMap.size()"+adapter.alarmDataHashMap.size()+1);
                Log.d("TEST","adapter.alarmDataHashMap.size()"+adapter.alarmDataArrayList.size()+1);
                //set_Alarm.putExtra("postion",adapter.alarmDataHashMap.size()); //?????? ??????

                set_Alarm.putExtra("postion",adapter.alarmDataArrayList.size()); //?????? ??????
                //????????? ?????? ????????? ??????  ??????
                //adapter.alarmDataHashMap
                //?????? ????????? ????????? ???????????? ????????????.

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

        /** ?????? ?????? ?????? ???????????? **/
        bt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mylocation.StartGetLocation(MainActivity.this);
                isGetLocation = false;
            }
        });

        /** SwipView ?????? ?????? **/
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new Adapter();
        recycler = findViewById(R.id.recyclerView);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        /***  ?????? ?????? ?????? ***/
        weather = null;
        lcf = new LocationCodeFetcher();
        wf = new WeatherFetcher();
        sdf = new SimpleDateFormat("yyyy??? MM??? dd??? HH??? ??????");
        pin = lcf.fetchLocationCode(Stringlocation);                                                                                                                        //x ,y?????? ???????????????

        try {
            weather = wf.fetchWeather(pin.getSx(), pin.getSy());
            //Log.i("TEST", "?????? ????????? x y ??????" + pin.getSx() + ", " +  pin.getSy());
        } catch (Exception E) {
            Log.i("TEST", "?????? ?????? ?????? ?????? : " + E.toString());
        }

        /***  ?????? ????????? ?????? ***/
        weather.weatherIcon(this);
        // TV ??????
        tw_weather = (TextView)findViewById(R.id.tw_weather);
        tw_weather.setText(sdf.format(weather.getBaseDate()) + "??? ???/??? ????????? " + weather.getPty() + ", ????????? " + weather.getSky() + "?????????");

        SharedPreferences alarmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = alarmPreferences.edit();
        editor.clear();
        editor.commit();

        /***  ????????? ???????????? ***/
        mylocation = new GetLocation();
        mylocation.StartGetLocation(this);
        Log.i("TEST", "create????????? ?????? ??????" + alarmPreferences.getAll().size() + "  ??????" + alarmPreferences.getAll());

        /** ?????? ?????? ????????? ????????? ???????????? Task **/
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

        /** ???????????? ?????? ?????? **/
        //startBTeService();

        //Log.i("TEST", "???????????? : " + sdf.format(weather.getBaseDate()));
        //Log.i("TEST", sdf.format(weather.getFcstDate()) + "??? ???/??? ????????? " + weather.getPty() + ", ????????? " + weather.getSky() + "?????????");

    }

    @Override
    protected void onResume() {
        super.onResume();
        /***  sharedPreferens ?????? ***/
        SharedPreferences alarmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        /***  ?????? ????????? ?????? ?????? ***/
        if (alarmPreferences.getAll().size() == 0) {
            // ????????? ????????? ??????
            Log.i("TEST", "????????? ?????? ??????");
        } else {
            /** RecycleView ????????? ????????? **/
            adapter.setAdapter(alarmPreferences);
            recycler.removeAllViews();
            recycler.setAdapter(adapter);

            Log.i("TEST", "????????? ?????? ??????" + alarmPreferences.getAll().size() + "  ??????" + alarmPreferences.getAll());

        }

        /***  ?????? ?????? ?????? ***/
        try{
            //Log.d("test",ReceivedIntent.getExtras().getString("address"));
            //Log.d("test","4321 "+ReceivedIntent.getExtras().getString("x"));
            //Log.d("test","4321 "+ReceivedIntent.getExtras().getString("y"));

            Stringlocation = ReceivedIntent.getExtras().getString("address").split("\\s");
            //pin = lcf.fetchLocationCode(Stringlocation); //????????? NULL ??? ?????? ??? ?????? ?????? ???????
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
            String getShared2 = prefs.getString(key,"0"); //??????, ????????????
            Log.d("test", "11111111111111111111111111111111111???"+getShared2);

            String[] anyweather = getShared2.split("/");
            String[] anytime = anyweather[0].split(":");

            //AlarmData tmp= adapter.alarmDataHashMap.get(posion);
            AlarmData tmp= adapter.alarmDataArrayList.get(posion);

            tmp.set_time(anytime[0],anytime[1]);
            tmp.set_weather(anyweather[1]);


        }catch (Exception E){
            Log.i("test", E.toString());
        }

        weather.weatherIcon(MainActivity.this);
        tw_weather.setText(Stringlocation[0]+" "+ Stringlocation[1]+" "+ Stringlocation[2]+"\n"+sdf.format(weather.getBaseDate()) +"??? ???/??? ????????? " + weather.getPty() + ", ????????? " + weather.getSky() + "?????????");
        //Log.i("test", "x y ????????? ??????");

    }


    /***************************************yong
     *  Intent ????????? ?????? onActivityResult Override
     * **************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddressSearchActivity.RESULT_OK){
            if(requestCode == REQUEST_SET_LOCATE) {
                Log.e("test", "?????? ??????: intent ?????? ?????? ??????");
                ReceivedIntent = data;
            }
        }

        if (resultCode == AlarmActivity.RESULT_OK) {
            if (requestCode == REQUEST_SET_ALARM) {
                Log.e("test", "?????? ??????: intent ?????? ?????? ??????");
                ReceivedIntent = data;
            }
        }
        if (resultCode == AlarmModifyActivity.RESULT_OK) {
            if (requestCode == REQUEST_Modify_ALARM) {
                Log.e("test", "?????? ?????? ??????: intent ?????? ?????? ??????");
                ReceivedIntent = data;
            }
        }
    }

    private void startBTeService(){
        //notification??? ????????? ???????????? foregrund??? ??????????????? ??????
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel("channel2", "2?????????", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("2??????????????????");
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

        private int count = 0;           //?????? ??????
        private  int[] itemsOffset = new int[count];
        //private HashMap<Integer, AlarmData> alarmDataHashMap = new HashMap<>();
        private ArrayList<AlarmData> alarmDataArrayList = new ArrayList<>();
        AlarmData alarmData;

        SharedPreferences getShared;

        public void setAdapter(SharedPreferences sharedPreferences){
            getShared = sharedPreferences;
            count = getShared.getAll().size();
            //Log.i("TEST", "?????? : " + String.valueOf(count));

            itemsOffset = new int[count];

            for(int i = 0; i<count; i++){
                String searchTag = "alarm"+(i+1);
                //Log.i("TEST", searchTag);
                String value = getShared.getString(searchTag, "");
                //Log.i("TEST", "????????? ?????? : " + value);
                try{
                    String[] anyweather = value.split("/");
                    String[] anytime = anyweather[0].split(":");
                    //Log.i("TEST", "anyweather " + anyweather[0] + " ????????? " + anyweather[1]);
                    //Log.i("TEST", "anytime " + anytime[0] + " ????????? " + anytime[1]);

                    try{
                        //alarmData = new AlarmData(null,anyweather[1],Boolean.TRUE);
                        alarmData = new AlarmData(null,anyweather[1],anyweather[2],Boolean.TRUE);
                        alarmData.setX(anyweather[3]);
                        alarmData.setY(anyweather[4]);
                    }
                    catch (Exception E){
                        Log.i("TEST", "????????? ????????????" + E.toString());
                    }

                    alarmData.set_time(anytime[0], anytime[1]);
                    //alarmDataHashMap.put(count - 1, alarmData);
                    alarmDataArrayList.add(alarmData);                                                                  // HashMap  -> ArrayList ??? ??????

                }catch (Exception E){
                    Log.i("TEST", "sharedPreferences ??? ??? ????????? " + E.toString());
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

            /** textview ?????? ( ?????? row index??? viewType?????? ??????) **/
            //viewHolder.weather.setText(alarmDataHashMap.get(index).get_weather());
            //viewHolder.time.setText(alarmDataHashMap.get(index).get_time());
            //viewHolder.location.setText(alarmDataHashMap.get(index).get_location());


            viewHolder.weather.setText(alarmDataArrayList.get(index).get_weather());                            // HashMap  -> ArrayList ??? ??????
            viewHolder.time.setText(alarmDataArrayList.get(index).get_time());                                  // HashMap  -> ArrayList ??? ??????
            viewHolder.location.setText(alarmDataArrayList.get(index).get_location());                          // HashMap  -> ArrayList ??? ??????


            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.swipeLayout.animateReset();
                }
            };

            /*
            if (viewHolder.leftView != null) {
                viewHolder.leftView.setClickable(true);
                //viewHolder.leftView.setOnClickListener(onClick);
                //????????????
                viewHolder.leftView.setOnClickListener(new View.OnClickListener() {
                   @Override
                       public void onClick(View view) {
                       Log.i("TEST", " Left : ???????????? ?????? ?????? ??????");
                       Log.i("TEST", "posion ??? : "+index);
                       Intent alarmModifyIntent = new Intent(com.example.weather.MainActivity.this,AlarmModifyActivity.class); //static class ?????? ??? ????????? ??? -> adapter static ??????
                       //Intent alarmModifyIntent = new Intent(com.example.weather.MainActivity.this,AlarmActivity.class); //static class ?????? ??? ????????? ??? -> adapter static ??????
                       alarmModifyIntent.putExtra("posion",index);

                       //AlarmData tmp = alarmDataHashMap.get(index);
                       AlarmData tmp = alarmDataArrayList.get(index);                                             // HashMap  -> ArrayList ??? ??????

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
            */


            if (viewHolder.rightView != null) {
                viewHolder.rightView.setClickable(true);
                //viewHolder.rightView.setOnClickListener(onClick);
                //????????????
                viewHolder.rightView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Log.i("TEST", " right : ???????????? ?????? ?????? ??????");
                           Log.i("TEST", "posion ??? : "+index);
                           //AlarmData tmp = alarmDataHashMap.get(index);
                           AlarmData tmp = alarmDataArrayList.get(index);                                             // HashMap  -> ArrayList ??? ??????
                           AlarmManager am = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                           Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                           PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, index, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                           if (sender != null) { am.cancel(sender); sender.cancel(); }
                           //Log.i("TEST", "?????? ??? ????????? ???????????? ????????? ?????? : "+ alarmDataHashMap.get(index));
                           Log.i("TEST", "?????? ??? ????????? ???????????? ????????? ?????? : "+ alarmDataArrayList.get(index));
                           //alarmDataHashMap.remove(index);


                           String key = "alarm"+index;
                           Log.d("test",key);
                           SharedPreferences prefs =getSharedPreferences("alarm", MODE_PRIVATE);
                           SharedPreferences.Editor editor = prefs.edit();
                           editor.remove("key"); // will delete key key_name4

                            // Save the changes in SharedPreferences
                           editor.commit(); // commit changes
                           alarmDataArrayList.remove(index);                                             // HashMap  -> ArrayList ??? ??????

//                           setHasStableIds(true);
                           adapter.notifyDataSetChanged();

                           //Intent intent2 = new Intent(MainActivity.this,MainActivity.class);
                           //intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                           //startActivity(intent2);
                           //Log.i("TEST", "?????? ??? ????????? ???????????? ????????? ?????? : "+ alarmDataHashMap.get(index));
//                           Log.i("TEST", "?????? ??? ????????? ???????????? ????????? ?????? : "+ alarmDataArrayList.get(index));
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
                    /** ???????????? ??? Toast ????????? ?????? ?????? **/
                    // ??????????????? ??????????????? ?????? ?????? ??????
                    Toast.makeText(swipeLayout.getContext(),
                            (moveToRight ? "Left" : "Right") + " ???????????????",
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
    *  ?????? ???????????? ?????? ??? ??????
    * **************************************/
    public void displayLocation(String val){
        tv_location.setText(val);
    }

    /***************************************
     *  ?????? ?????? ??? ?????????
     * **************************************/
    public class GetLocation {
        LocationManager locationManager;
        private int xPin, yPin;
        private double longitude, latitude;
        String provider;

        public void StartGetLocation(Context context) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // ??? ????????? ??????
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);
            }
        }

        /** LocationManager ?????? **/
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

        /***  LocationMagnager ????????? ***/
        private final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /***  ?????? ?????? ?????? ?????? ????????? ?????? ***/
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                provider = location.getProvider();

                /***  ?????? ????????? x,y??? ?????? ***/
                ConvertLatLon convertLatLon = new ConvertLatLon((float) longitude, (float) latitude);

                xPin = convertLatLon.getX();
                yPin = convertLatLon.getY();

                /***  ?????? ????????? ????????? ?????? ***/
                Geocoder geocoder = new Geocoder(MainActivity.this);
                List<Address> list = null;

                try {
                    Log.i("TEST", "lat" + latitude + "lon" + longitude);

                    list = geocoder.getFromLocation(latitude, longitude, 100);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("TEST", "?????? ?????? : " + e.toString());
                }
                if (list != null) {
                    if (list.size() == 0) {
                        displayLocation("???????????? ????????? ????????????.");
                    } else {
                        Log.i("TEST", "?????? ?????? \n" + list.get(0).getAddressLine(0));
                        displayLocation("???????????? : \n" + list.get(0).getAddressLine(0));
                    }
                }

                /***  ?????? ??????????????? Textview??? ?????? ?????? ***/
                //displayLocation("?????? : " + longitude + " ?????? : " + latitude + "X ??? : " + convertLatLon.getX() + " Y ??? :" + convertLatLon.getY() + " \n????????? ?????????" + provider);

                Toast.makeText(MainActivity.this, "?????? : " + longitude + " ?????? : " + latitude + " \n????????? ?????????" + provider, Toast.LENGTH_LONG).show();
                Log.i("TEST", "?????? : " + longitude + " ?????? : " + latitude + "X ??? : " + convertLatLon.getX() + " Y ??? :" + convertLatLon.getY() + " \n????????? ?????????" + provider);


                /** ????????? xy??? ?????? ??? ?????? **/
                try{
                    weather = wf.fetchWeather(pin.getSx(), pin.getSy());
                    weather.weatherIcon(MainActivity.this);

                    /**  location ?????? ??? ?????? **/
                    Stringlocation[0] = list.get(0).getAddressLine(0).split(" ")[1];
                    Stringlocation[1] = list.get(0).getAddressLine(0).split(" ")[2];
                    Stringlocation[2] = list.get(0).getAddressLine(0).split(" ")[3];

                    tw_weather.setText(list.get(0).getAddressLine(0)+"\n"+sdf.format(weather.getBaseDate()) +"??? ???/??? ????????? " + weather.getPty() + ", ????????? " + weather.getSky() + "?????????");
                }catch (Exception E){
                    Log.i("TEST", "?????? ?????? ????????? ?????? ??????");
                }

                /** ?????? ?????? ????????? ????????? flag ?????? **/
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
