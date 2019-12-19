package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import com.example.weather.cLocation.cLocation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class AdressSearchActivity extends AppCompatActivity {



    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터

    private TextView resultLabel;
    private Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_search);
        Log.d("test", "onCreate");

        editSearch = (EditText) findViewById(R.id.SearchText);
        listView = (ListView) findViewById(R.id.ListView);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        resultLabel =(TextView) findViewById(R.id.textView);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<cLocation> searchResult = settingList(editSearch.getText().toString());
                Log.d("test", "버튼 클릭");
                resultLabel.setText(searchResult.get(1).getAddress());
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
