package com.example.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.Alarm.AlarmData;
import com.example.weather.Alarm.AlarmReceiver;
import com.example.weather.cLocation.cLocation;

import java.util.ArrayList;
import java.util.HashMap;

import ru.rambler.libs.swipe_layout.SwipeLayout;

//private class Adapter extends RecyclerView.Adapter<MainActivity.Adapter.ViewHolder> {
private class MainAdapter extends BaseAdapter {

    private int count = 0;           //세팅 갯수
    private int[] itemsOffset = new int[count];

    AlarmData alarmData;

    //새로 변경중
    private Context context;
    //private HashMap<Integer, AlarmData> alarmDataHashMap = new HashMap<>();
    private HashMap<Integer, AlarmData> alarmDataHashMap; //new HashMap<>(); 이건 메인으로 빼줘야함
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    //새로 변경중 끝
    SharedPreferences getShared;

    public MainAdapter(HashMap<Integer, AlarmData> alarmDataHashMap, Context context) {
        this.alarmDataHashMap = alarmDataHashMap;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return alarmDataHashMap.size();
    }

    public int getItemCount() { // 카운트로 대체 되었음
        return count;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public int getItemViewType(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final int pos = position;
        final Context context = viewGroup.getContext();

        if (convertView == null) {
            convertView = inflate.inflate(R.layout.row_listview, null);

            viewHolder = new ViewHolder(getView(  R.layout.list_item_left_right,ViewGroup,viewGroup));

            viewHolder.swipeLayout = convertView.findViewById(R.id.swipe_layout);
            viewHolder.rightView = convertView.findViewById(R.id.right_view);
            viewHolder.leftView = convertView.findViewById(R.id.left_view);
            viewHolder.time = convertView.findViewById(R.id.tvTime);
            viewHolder.weather = convertView.findViewById(R.id.tvWeather);
            viewHolder.location = convertView.findViewById(R.id.tvLocation);
            viewHolder.alarmSwitch = convertView.findViewById(R.id.AlarmSwitch);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MainAdapter.ViewHolder) convertView.getTag();
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        //viewHolder.label.setText(alarmDataHashMap.get(position).getAddress()); //여기 에러 ArrayList의 값이 가져 오지 못한다.
        viewHolder.time.setText(        alarmDataHashMap.get(position).get_time());
        viewHolder.location.setText(        alarmDataHashMap.get(position).get_location());
        viewHolder.weather.setText(        alarmDataHashMap.get(position).get_weather());


        //해줄 필요없음 -> 리스너 작업 필요
        //viewHolder.swipeLayout
        //viewHolder.rightView
        //viewHolder.leftView
        //viewHolder.alarmSwitch



        return convertView;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public  SwipeLayout swipeLayout;
        public  View rightView;
        public  View leftView;

        public TextView time;
        public TextView weather;
        public TextView location;
        public Switch alarmSwitch;

        ViewHolder(View itemView) {
            super(itemView);
    }
}

//메인에서 작업 해줘야 하는 부분

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
                    alarmData = new AlarmData(null,anyweather[1],Boolean.TRUE);
                    alarmData = new AlarmData(null,anyweather[1],anyweather[2],Boolean.TRUE);
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
//


    @Override
    public MainActivity.Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int index) {
        int layoutId;
        layoutId = R.layout.list_item_left_right;

        final View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        final MainActivity.Adapter.ViewHolder viewHolder = new MainActivity.Adapter.ViewHolder(itemView);

        /* 작업 완료
        ///** textview 삽입 ( 해당 row index는 viewType으로 구분)
        viewHolder.weather.setText(alarmDataHashMap.get(index).get_weather());
        viewHolder.time.setText(alarmDataHashMap.get(index).get_time());
        viewHolder.location.setText(alarmDataHashMap.get(index).get_location());
        */

        //
        item listener part
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
                       Intent alarmModifyIntent = new Intent(com.example.weather.MainActivity.this,AlarmActivity.class); //static class 에서 는 불가능 함 -> adapter static 제거
                       alarmModifyIntent.putExtra("posion",i);
                       AlarmData tmp = alarmDataHashMap.get(i);

                       alarmModifyIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                       startActivity(alarmModifyIntent);



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
                                                            AlarmData tmp = alarmDataHashMap.get(i);
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
                //슬라이드 후 Toast 텍스트 출력 부분
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

        //스위치 Onchanged 이벤트
        viewHolder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    Log.i("TEST", "switch 버튼 TRUE 로 했을때 바인딩 된 DATA에 접근하는 방법 모르겠음");
                    //index값으로 위치 및 hash 위치를 알수 있다. //수정파트
                }else{
                    Log.i("TEST", "switch 버튼 FALSE 로 했을때 바인딩 된 DATA에 접근하는 방법 모르겠음");
                    //수정파트
                }
            }
        });

        return new MainActivity.Adapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainActivity.Adapter.ViewHolder holder, int position) {
        holder.swipeLayout.setOffset(itemsOffset[position]);
    }

    @Override
    public void onViewDetachedFromWindow(MainActivity.Adapter.ViewHolder holder) {
        if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
            itemsOffset[holder.getAdapterPosition()] = holder.swipeLayout.getOffset();
        }
    }

    @Override
    public void onViewRecycled(MainActivity.Adapter.ViewHolder holder) {
        super.onViewRecycled(holder);
    }*/



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
            alarmSwitch = itemView.findViewById(R.id.AlarmSwitch);
        }
    }
}
