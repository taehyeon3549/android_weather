package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

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

// TEST --

public class AdressSearchActivity extends AppCompatActivity {


    private List<String> list;          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<String> arraylist;

    //새롭게 선언
    private EditText district;
    private EditText region;
    private EditText state;
    private TextView resultLabel;
    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_search);
        Log.d("test", "onCreate");

        editSearch = (EditText) findViewById(R.id.SearchText);
        editSearch = (EditText) findViewById(R.id.SearchText);

        //listView = (ListView) findViewById(R.id.listView);

        //list = new ArrayList<String>(); //리스트 생성

        //TEST
        //TESSSS
        // branch Test

        search = (Button) findViewById(R.id.searchBtn);
        resultLabel =(TextView) findViewById(R.id.textView);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* 버튼 클릭 헀을 때 동작
                String districtValue = district.getText().toString();
                String regionValue = district.getText().toString();
                String stateValue = district.getText().toString();
                */

                StringBuilder searchResult = settingList(editSearch.getText().toString());
                Log.d("test", "111111"+searchResult.toString());
                resultLabel.setText(searchResult.toString());
            }
        });

        // 검색에 사용할 데이터을 미리 저장한다. // 굳이 필요 없음
        //settingList();

    }

    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < arraylist.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist.get(i).toLowerCase().contains(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    list.add(arraylist.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }


    // 검색에 사용될 데이터를 리스트에 추가한다.  //검색 파트
    private StringBuilder settingList(String districtValue) {

        //데이터 저장
        StringBuilder sb = new StringBuilder();

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("location.xls");
            Workbook wb = Workbook.getWorkbook(is);

            if (wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기
                if (sheet != null) {

                    int colTotal = sheet.getColumns() - 1;    // 전체 컬럼
                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getRows() - 1;


                    Log.i("test", "SearchStart");

                    for (int row = 0; row < rowTotal; row++) {
                        if ((sheet.getCell(2, row).getContents().equals(districtValue)) ) {
                            for (int col = 0; col < colTotal; col++) {
                                sb.append(sheet.getCell(col, row).getContents() + "::");
                            }
                            Log.i("test", sb.toString());
                            //break;
                        }
                    }
                    return sb;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("test", "1번");
            Log.d("test", e.getMessage());

        } catch (BiffException e) {
            e.printStackTrace();
            Log.d("test", "2번");
            Log.d("test", e.getMessage());
        }
        sb.append("error");
        return sb;
    }
}
