package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.cLocation.cLocation;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-08-07.
 */

public class SearchAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<cLocation> searchedLocations;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;


    public SearchAdapter(ArrayList<cLocation> searchedLocations, Context context){
        this.searchedLocations = searchedLocations;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return searchedLocations.size();
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final int pos = position;
        final Context context = viewGroup.getContext();

        if(convertView == null){
            convertView = inflate.inflate(R.layout.row_listview,null);

            viewHolder = new ViewHolder();
            viewHolder.label = (TextView) convertView.findViewById(R.id.label);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), AlarmActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                Toast.makeText(context, "선택: "+searchedLocations.get(pos).getAddress(), Toast.LENGTH_SHORT).show();
                Log.i("test",  searchedLocations.get(pos).getAddress());

                intent.putExtra("address",searchedLocations.get(pos).getAddress()); /*송신*/
                intent.putExtra("x",searchedLocations.get(pos).getX());
                intent.putExtra("y",searchedLocations.get(pos).getY());
                context.startActivity(intent);

            }
        });

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.label.setText(searchedLocations.get(position).getAddress()); //여기 에러 ArrayList의 값이 가져 오지 못한다.

        return convertView;
    }

    class ViewHolder{
        public TextView label;
    }
}
