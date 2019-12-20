package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weather.cLocation.cLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class AddressSearchActivity extends AppCompatActivity {

    private EditText addressSearch;        // 주소 입력창
    private ListView addressListView;      // 검색을 보여줄 리스트변수
    private ListView defualtListView;      // 검색을 보여줄 리스트변수

    private SearchAdapter adapter;          // 리스트뷰에 연결할 아답터
    private ArrayList<cLocation> addressList;
    private ArrayList<cLocation> arraylist;  // 데이터를 넣은 리스트변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_search);

        addressSearch = (EditText) findViewById(R.id.addressSearch);
        addressListView = (ListView) findViewById(R.id.addressListView);
        //defualtListView = (ListView) findViewById(R.id.defaultListView);

        addressList = settingList(); //검색에 사용할 주소목록을 저장한다.

        arraylist = new ArrayList<cLocation>(); //검색된 데이터 목록 -> 이것을 출력한다.
        arraylist.addAll(addressList);

        adapter = new SearchAdapter(addressList,this); //어댑터 생성
        addressListView.setAdapter(adapter); //어댑터 연동

        addressSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = addressSearch.getText().toString();
                search(text);
            }
        });
        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("address",addressList.get(position).getAddress());
                intent.putExtra("locationX",addressList.get(position).getX());
                intent.putExtra("locationY",addressList.get(position).getY());
                Toast.makeText(getApplicationContext(),"선택 : "+addressList.get(position).getAddress(),Toast.LENGTH_SHORT).show();
                startActivity(intent);//액티비티 띄우기
                finish();
            }
        });

    }

    private ArrayList<cLocation> settingList() {
        ArrayList<cLocation> searchedList = new ArrayList<cLocation>(); //데이터 저장
        StringBuffer cLocationAddress= new StringBuffer();
        String cLocationX;
        String cLocationY;

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("location.xls"); //파일명 가져오기
            Workbook wb = Workbook.getWorkbook(is); // 파일 불러오기

            if (wb != null) {
                Sheet sheet = wb.getSheet(0);   //시트 불러오기
                if (sheet != null) {

                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getRows() - 1;

                    for (int row = rowIndexStart; row < rowTotal; row++) {
                        cLocationAddress.append(sheet.getCell(0, row).getContents()+" ");
                        cLocationAddress.append(sheet.getCell(1, row).getContents()+" ");
                        cLocationAddress.append(sheet.getCell(2, row).getContents());

                        cLocationX = sheet.getCell(3, row).getContents();
                        cLocationY = sheet.getCell(4, row).getContents();

                        searchedList.add(new cLocation( cLocationAddress.toString(),cLocationX,cLocationY));
                        cLocationAddress.setLength(0);
                    }
                }
                return searchedList;
            }

        } catch (IOException e) {
            Log.d("test", "1번 "+e.getMessage());

        } catch (BiffException e) {
            e.printStackTrace();
            Log.d("test", "2번"+e.getMessage());
        }
        searchedList.add(new cLocation(new String("에러"),"-1","-1"));
        return searchedList;
    }


    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        addressList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            addressList.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < arraylist.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist.get(i).getAddress().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    addressList.add(arraylist.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }
}
