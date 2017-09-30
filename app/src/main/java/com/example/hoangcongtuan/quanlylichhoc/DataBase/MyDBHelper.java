package com.example.hoangcongtuan.quanlylichhoc.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/28/17.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "LopHocPhan";
    private final static String TABLE_NAME = "LopHocPhan";
    private final static int DB_VERSION = 1;

    private final static String MA_HOC_PHAN = "MaHP";
    private final static String TEN_HOC_PHAN = "TenHP";
    private final static String GIANG_VIEN  = "GV";
    private final static String THOI_KHOA_BIEU = "TKB";

    private static MyDBHelper sInstance;

    private Context context;


    public static MyDBHelper getInstace(Context context) {
        if (sInstance == null)
            sInstance = new MyDBHelper(context);
        return sInstance;
    }

    private MyDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;

    }

    public void deleteDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlCreate = "CREATE TABLE " + DB_NAME +"("
                + MA_HOC_PHAN + " TEXT," + TEN_HOC_PHAN + " TEXT," + GIANG_VIEN + " TEXT,"
                + THOI_KHOA_BIEU + " TEXT);";
        sqLiteDatabase.execSQL(sqlCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addHP(LopHP lopHP) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MA_HOC_PHAN, lopHP.getMa_hoc_phan());
        values.put(TEN_HOC_PHAN, lopHP.getTen_hoc_phan());
        values.put(GIANG_VIEN, lopHP.getTen_giang_vien());
        values.put(THOI_KHOA_BIEU, lopHP.getTkb());

        db.insert(TABLE_NAME, null, values);

        db.close();
    }


    public LopHP getHP(String maHP) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + MA_HOC_PHAN + " = " + maHP;
        Cursor cursor = db.rawQuery(query, null);
        LopHP lopHP = new LopHP(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        db.close();
        return lopHP;
    }

    public ArrayList<LopHP> getAlls() {
        ArrayList<LopHP> lstHP = new ArrayList<LopHP>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                LopHP lopHP = new LopHP(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3));
                lstHP.add(lopHP);
            }
            while (cursor.moveToNext());
        }
        return lstHP;
    }
}
