package com.example.weather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.cLocation.cLocation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
<<<<<<< HEAD:app/src/main/java/com/example/weather/AdressSearchActivity.java
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
=======
import android.widget.AdapterView;
>>>>>>> dcc1337b3e34f94737c375451bd83671c8fd069b:app/src/main/java/com/example/weather/AddressSearchActivity.java
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
<<<<<<< HEAD:app/src/main/java/com/example/weather/AdressSearchActivity.java
import java.util.List;
import java.util.Objects;
=======
>>>>>>> dcc1337b3e34f94737c375451bd83671c8fd069b:app/src/main/java/com/example/weather/AddressSearchActivity.java

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class AddressSearchActivity extends AppCompatActivity {

    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터
    private Button searchBtn;
    ArrayList<cLocation> searchResult;
    public static final int sub = 1001; /*다른 액티비티를 띄우기 위한 요청코드(상수)*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_search);
        Log.d("test", "onCreate");

        editSearch = (EditText) findViewById(R.id.SearchText);
        listView = (ListView) findViewById(R.id.listView);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        int d =0;

        if(listView.getSelectedItemPosition() != Objects){
            d = listView.getSelectedItemPosition();
        }


        Log.d("test", searchResult.get(d).getAddress());

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "버튼 클릭");
                searchResult = settingList(editSearch.getText().toString());
                adapter = new SearchAdapter(searchResult, AddressSearchActivity.this);
                listView.setAdapter(adapter) ;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("test", "위치 값은" + position);
                Log.i("test", searchResult.get(position).getX() + searchResult.get(position).getY());
            }
        });

    }

    private ArrayList<cLocation> settingList(String districtValue) {
        ArrayList<cLocation> searchedList = new ArrayList<cLocation>(); //데이터 저장

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("location.xls"); //파일명 가져오기
            Workbook wb = Workbook.getWorkbook(is); // 파일 불러오기

            if (wb != null) {
                Sheet sheet = wb.getSheet(0);   //시트 불러오기
                if (sheet != null) {

                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getRows() - 1;

                    Log.i("test", "검색 시작");

                    for (int row = rowIndexStart; row < rowTotal; row++) {
                        if (sheet.getCell(2, row).getContents().equals(districtValue) ) {

                            String address = sheet.getCell(0, row).getContents()+" "+sheet.getCell(1, row).getContents()+" "+sheet.getCell(2, row).getContents();
                            String x = sheet.getCell(3, row).getContents();
                            String y = sheet.getCell(4, row).getContents();
                            searchedList.add(new cLocation(address,x,y));

                            Log.d("test", address);
                        }
                    }
                    return searchedList;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("test", "1번 "+e.getMessage());

        } catch (BiffException e) {
            e.printStackTrace();
            Log.d("test", "2번"+e.getMessage());
        }
        searchedList.add(new cLocation("에러","-1","-1"));
        return searchedList;
    }
}
