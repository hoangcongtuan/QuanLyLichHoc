package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHPObj;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DBLopHPHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteOpenHelper";

    private static final String DATABASE_NAME = "SQLiteInfo.db";
    private static final int DATABASE_VERSION = 1;
    private static final String HOCPHAN_TABLE_NAME = "thong_tin_lop_hoc_phan";
    private static final String HOCPHAN_COLUMN_MAHP = "MA_HP";
    private static final String HOCPHAN_COLUMN_GIANG_VIEN = "TEN_GIANG_VIEN";
    private static final String HOCPHAN_COLUMN_LOP_HOC_PHAN = "TEN_HOC_PHAN";

    private static final String USER_TABLE_NAME = "thong_tin_ma_hoc_phan";
    private static final String USER_COMLUMN_MAHP = "MA_HP";


    private static final String PATH_INFO_ALL_LOP_HOC_PHAN = "thong_tin_lop_hoc_phan";
    private static final String HOCPHAN_COLUMN_TKB = "TKB";

    private static Context mContext;

    private DatabaseReference dbListThongTinLopHocPhan;

    private static DBLopHPHelper sInstance;

    private OnCheckDB onCheckDB;
    private OnLoadData onLoadData;

    public void setOnLoadData(OnLoadData onLoadData) {
        this.onLoadData = onLoadData;
    }

    public static void init(Context context) {
        mContext = context;
        if(sInstance == null)
            sInstance = new DBLopHPHelper(context);
    }

    public static DBLopHPHelper getsInstance() {
        //if (sInstance == null)
            //sInstance = new DBLopHPHelper(mContext);
        return sInstance;
    }

    private DBLopHPHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        dbListThongTinLopHocPhan = database.child(PATH_INFO_ALL_LOP_HOC_PHAN);
    }

    public void setOnCheckDB(OnCheckDB onCheckDB) {
        this.onCheckDB = onCheckDB;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create lop hoc phan data
        sqLiteDatabase.execSQL("CREATE TABLE " + HOCPHAN_TABLE_NAME + "(" +
                HOCPHAN_COLUMN_MAHP + " TEXT PRIMARY KEY, " +
                HOCPHAN_COLUMN_GIANG_VIEN + " TEXT, " +
                HOCPHAN_COLUMN_LOP_HOC_PHAN + " TEXT, " +
                HOCPHAN_COLUMN_TKB + " TEXT)"
        );

        //create user_data
        sqLiteDatabase.execSQL("CREATE TABLE " + USER_TABLE_NAME + "(" +
                USER_COMLUMN_MAHP + " TEXT)");
    }

    public boolean insertUserMaHocPhan(String maHP) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE " +
                USER_COMLUMN_MAHP + "=?", new String[]{maHP});
        if (cursor != null && cursor.getCount() > 0){
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COMLUMN_MAHP, maHP);
        db.insert(USER_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean updateUserMaHocPhan(String maHP) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COMLUMN_MAHP, maHP);
        db.update(USER_TABLE_NAME, contentValues, USER_COMLUMN_MAHP + " = ?", new String[] {maHP});
        return true;
    }

    private Cursor getAllUserMaHocPhan() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);
    }

    public ArrayList<String> getListUserMaHP() {
        ArrayList<String> lstMaHP = new ArrayList<>();
        Cursor cursor  = getAllUserMaHocPhan();
        if(cursor != null && cursor.getCount() == 0)
            return null;

        if(cursor.moveToFirst()) {
            do {
                lstMaHP.add(cursor.getString(cursor.getColumnIndex(USER_COMLUMN_MAHP)));

            }
            while (cursor.moveToNext());
        }
        return lstMaHP;
    }

    public ArrayList<LopHP> getListUserLopHP() {
        ArrayList<LopHP> lstLopHP = new ArrayList<>();
        Cursor cursor = getAllUserMaHocPhan();
        if (cursor.moveToFirst()) {
            do {
                lstLopHP.add(getLopHocPhan(cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_MAHP))));
            }
            while (cursor.moveToNext());
        }
        return lstLopHP;
    }

    public boolean checkDBUser() {
        return getAllUserMaHocPhan().getCount() > 0;
    }

    public Integer deleteUserMaHocPhan(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER_TABLE_NAME,
                USER_COMLUMN_MAHP + " = ? ",
                new String[]{id});
    }

    public Integer deleteAllUserMaHocPhan() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER_TABLE_NAME, null, null);
    }



    public void clearDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(HOCPHAN_TABLE_NAME, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HOCPHAN_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertLopHocPhan(LopHP lopHP) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOCPHAN_COLUMN_MAHP, lopHP.maHP);
        contentValues.put(HOCPHAN_COLUMN_GIANG_VIEN, lopHP.tenGV);
        contentValues.put(HOCPHAN_COLUMN_LOP_HOC_PHAN, lopHP.tenHP);
        contentValues.put(HOCPHAN_COLUMN_TKB, lopHP.tkb);
        db.insert(HOCPHAN_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean updateLopHocPhan(LopHP lopHP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOCPHAN_COLUMN_MAHP, lopHP.maHP);
        contentValues.put(HOCPHAN_COLUMN_GIANG_VIEN, lopHP.tenGV);
        contentValues.put(HOCPHAN_COLUMN_LOP_HOC_PHAN, lopHP.tenHP);
        contentValues.put(HOCPHAN_COLUMN_TKB, lopHP.tkb);
        db.update(HOCPHAN_TABLE_NAME, contentValues, HOCPHAN_COLUMN_MAHP + " = ? ", new String[]{lopHP.maHP});
        return true;
    }

    public LopHP getLopHocPhan(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + HOCPHAN_TABLE_NAME + " WHERE " +
                HOCPHAN_COLUMN_MAHP + "=?", new String[]{id});
        if (cursor == null || cursor.getCount() <= 0){
            return null;
        }
        LopHP lopHP = new LopHP();
        if (cursor.moveToFirst()) {
            lopHP.maHP = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_MAHP));
            lopHP.tenGV = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_GIANG_VIEN));
            lopHP.tenHP = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_LOP_HOC_PHAN));
            lopHP.tkb = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_TKB));
        }
        return lopHP;
    }

    public LopHP getLopHPbyName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + HOCPHAN_TABLE_NAME + " WHERE " +
                HOCPHAN_COLUMN_LOP_HOC_PHAN + "=?", new String[]{name});
        if (cursor == null || cursor.getCount() <= 0){
            return null;
        }
        LopHP lopHP = new LopHP();
        if (cursor.moveToFirst()) {
            lopHP.maHP = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_MAHP));
            lopHP.tenGV = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_GIANG_VIEN));
            lopHP.tenHP = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_LOP_HOC_PHAN));
            lopHP.tkb = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_TKB));
        }
        return lopHP;
    }

    private Cursor getAllLopHocPhan() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + HOCPHAN_TABLE_NAME, null);
    }

    public ArrayList<String> getListMaHP() {
        ArrayList<String> lstMaHP = new ArrayList<>();
        Cursor cursor  = getAllLopHocPhan();
        if(cursor.moveToFirst()) {
            do {
                lstMaHP.add(cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_MAHP)));

            }
            while (cursor.moveToNext());
        }
        return lstMaHP;
    }

    public ArrayList<String> getListTenHP() {
        ArrayList<String> lstTenHP = new ArrayList<>();
        Cursor cursor  = getAllLopHocPhan();
        if(cursor.moveToFirst()) {
            do {
                lstTenHP.add(cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_LOP_HOC_PHAN)));

            }
            while (cursor.moveToNext());
        }
        return lstTenHP;
    }

    public Integer deleteLopHocPhan(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(HOCPHAN_TABLE_NAME,
                HOCPHAN_COLUMN_MAHP + " = ? ",
                new String[]{id});
    }

    public void checkDB() {
        int count = getAllLopHocPhan().getCount();
        if (count <= 0) {
            //Toast.makeText(mContext, "Loading data", Toast.LENGTH_LONG).show();
            onCheckDB.onStartDownload();
            Log.d(TAG, "checkDB: start download");
            loadData();
        } else {
            //Toast.makeText(mContext, "ok", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "checkDB: db available");
            onCheckDB.onDBAvailable();
        }
    }



    public class ASyncGetLopHP extends AsyncTask<DataSnapshot, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(mContext, "Start Load", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(DataSnapshot... dataSnapshots) {
            String maHP;
            LopHPObj lopHPObj;
            for (DataSnapshot snapshot: dataSnapshots[0].getChildren()) {
                lopHPObj = snapshot.getValue(LopHPObj.class);
                //onLoadData.onLoad(lopHPObj.getTenHP());
                maHP = snapshot.getKey();
                insertLopHocPhan(new LopHP(maHP, lopHPObj));
                //Log.d(TAG, "onDataChange: " + maHP);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String dataSnapshot) {
            super.onPostExecute(dataSnapshot);
            //Toast.makeText(mContext, "Finish Load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPostExecute: finish download");
            onCheckDB.onDownloadFinish();
        }
    }


    private void loadData() {

        dbListThongTinLopHocPhan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                ASyncGetLopHP async = new ASyncGetLopHP();
                async.execute(dataSnapshot);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface OnCheckDB {
        public void onDBAvailable();
        public void onDownloadFinish();
        public void onStartDownload();
    }


    public interface OnLoadData {
        public void onLoad(String string);
    }


}
