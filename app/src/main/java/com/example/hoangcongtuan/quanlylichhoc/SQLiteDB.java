package com.example.hoangcongtuan.quanlylichhoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.hoangcongtuan.quanlylichhoc.DataBase.MyDBHelper;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;

import java.util.ArrayList;

public class SQLiteDB extends AppCompatActivity implements View.OnClickListener{
    Button btnCreate, btnRead, btnClear;
    ListView lvHP;
    ArrayAdapter<String> adapter;
    ArrayList<String> lstHP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_db);
        btnCreate = (Button)findViewById(R.id.btnCreate);
        btnRead = (Button)findViewById(R.id.btnReadDB);
        btnClear = (Button)findViewById(R.id.btnClear);


        lvHP = (ListView)findViewById(R.id.lstHP);
        lstHP = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstHP);
        lvHP.setAdapter(adapter);

        btnRead.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        btnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreate:
                createDB();
                break;
            case R.id.btnReadDB:
                readDB();
                break;
            case R.id.btnClear:
                clearDB();
                break;
        }
    }

    private void clearDB() {
        MyDBHelper.getInstace(this).deleteDB();
    }

    public void createDB() {
        MyDBHelper.getInstace(this).addHP(new LopHP(
                "4130403.1710.15.11"
                , "Anh văn CN CNTT"
                , "Nguyễn Thế Xuân Ly"
                , "Thứ 4: 4-5,H208"
        ));

        MyDBHelper.getInstace(this).addHP(new LopHP(
                "1020102.1710.15.11", "CS dữ liệu", "Trương Ngọc Châu",	"Thứ 7: 1-2,H206"
        ));

        MyDBHelper.getInstace(this).addHP(new LopHP(
                "1021463.1710.15.11", "Công nghệ di động", "Trần Thế Vũ", "Thứ 5: 9-10,F408"
        ));
    }

    public void readDB() {
        ArrayList<LopHP> arrayList = MyDBHelper.getInstace(this).getAlls();
        lstHP.clear();
        for(LopHP lopHP : arrayList) {
            lstHP.add(lopHP.toString());
        }

        adapter.notifyDataSetChanged();
    }
}
