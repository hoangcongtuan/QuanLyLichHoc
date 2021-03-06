package com.example.hoangcongtuan.quanlylichhoc.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHPObj;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DBLopHPHelper extends SQLiteOpenHelper {
    private static final String TAG = SQLiteOpenHelper.class.getName();
    private static final String DATABASE_NAME = "QuanLyLichHocDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String ALL_HOCPHAN_TABLE_NAME = "THONG_TIN_LOP_HOC_PHAN";
    private static final String ALL_HOCPHAN_COLUMN_MAHP = "MA_HP";
    private static final String ALL_HOCPHAN_COLUMN_GIANG_VIEN = "TEN_GIANG_VIEN";
    private static final String ALL_HOCPHAN_COLUMN_LOP_HOC_PHAN = "TEN_HOC_PHAN";
    private static final String USER_TABLE_NAME = "USER_MA_HOC_PHAN";
    private static final String USER_COLUMN_MAHP = "MA_HP";
    private static final String HOCPHAN_COLUMN_TKB = "TKB";

    private Context mContext;
    private DatabaseReference fireBaseAllHPhan;
    private static DBLopHPHelper sInstance;
    //call back khi kiem tra db
    private OnCheckDB onCheckDB;

    public static void init(Context context) {
        if(sInstance == null)
            sInstance = new DBLopHPHelper(context);
    }

    public static DBLopHPHelper getsInstance() {
        return sInstance;
    }

    private DBLopHPHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        fireBaseAllHPhan = database.child(context.getResources().getString(R.string.FIREBASE_PATH_ALL_HP));
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + ALL_HOCPHAN_TABLE_NAME + " (" +
                ALL_HOCPHAN_COLUMN_MAHP + " TEXT PRIMARY KEY, " +
                ALL_HOCPHAN_COLUMN_GIANG_VIEN + " TEXT, " +
                ALL_HOCPHAN_COLUMN_LOP_HOC_PHAN + " TEXT, " +
                HOCPHAN_COLUMN_TKB + " TEXT)"
        );
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + "(" +
                USER_COLUMN_MAHP + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ALL_HOCPHAN_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //user DB Funtions
    //them ma hoc phan vao db cua user, return true neu thanh cong
    public long insertUserMaHocPhan(String maHP) {
        long rc;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_MAHP, maHP);
        rc = db.insert(USER_TABLE_NAME, null, contentValues);
        return rc;
    }

    public long updateUserMaHocPhan(String maHP) {
        long rc;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_MAHP, maHP);
        rc = db.update(USER_TABLE_NAME, contentValues, USER_COLUMN_MAHP + " = ?", new String[] {maHP});
        return rc;
    }

    public Integer deleteUserMaHocPhan(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER_TABLE_NAME,
                USER_COLUMN_MAHP + " = ? ",
                new String[]{id});
    }

    public Integer deleteAllUserMaHocPhan() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER_TABLE_NAME, null, null);
    }

    public ArrayList<String> getListUserMaHP() {
        ArrayList<String> lstMaHP = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor  = db.rawQuery("SELECT *    FROM " + USER_TABLE_NAME, null);

        if(cursor.moveToFirst()) {
            do
                lstMaHP.add(cursor.getString(cursor.getColumnIndex(USER_COLUMN_MAHP)));
            while (cursor.moveToNext());
        }
        return lstMaHP;
    }

    public ArrayList<LopHP> getListUserLopHP() {
        ArrayList<LopHP> lstLopHP = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor  = db.rawQuery("SELECT *    FROM " + USER_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                lstLopHP.add(
                        getLopHocPhan(
                                cursor.getString(
                                        cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_MAHP)
                                )
                        )
                );
            }
            while (cursor.moveToNext());
        }
        Utils.getsInstance(mContext).sortLHP(lstLopHP);
        return lstLopHP;
    }

    //all hoc phan Functions
    public void clearDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ALL_HOCPHAN_TABLE_NAME, null, null);
        db.delete(USER_TABLE_NAME, null, null);
    }

    private int clear_all_hp_db() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(ALL_HOCPHAN_TABLE_NAME, null, null);
    }

    private long insertLopHocPhan(SQLiteDatabase db, LopHP lopHP) {
        long rc;

        ContentValues contentValues = new ContentValues();
        contentValues.put(ALL_HOCPHAN_COLUMN_MAHP, lopHP.maHP);
        contentValues.put(ALL_HOCPHAN_COLUMN_GIANG_VIEN, lopHP.tenGV);
        contentValues.put(ALL_HOCPHAN_COLUMN_LOP_HOC_PHAN, lopHP.tenHP);
        contentValues.put(HOCPHAN_COLUMN_TKB, lopHP.tkb);
        rc = db.insert(ALL_HOCPHAN_TABLE_NAME, null, contentValues);
        return rc;
    }

    public long updateLopHocPhan(LopHP lopHP) {
        long rc;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALL_HOCPHAN_COLUMN_MAHP, lopHP.maHP);
        contentValues.put(ALL_HOCPHAN_COLUMN_GIANG_VIEN, lopHP.tenGV);
        contentValues.put(ALL_HOCPHAN_COLUMN_LOP_HOC_PHAN, lopHP.tenHP);
        contentValues.put(HOCPHAN_COLUMN_TKB, lopHP.tkb);
        rc = db.update(ALL_HOCPHAN_TABLE_NAME, contentValues, ALL_HOCPHAN_COLUMN_MAHP + " = ? ", new String[]{lopHP.maHP});
        return rc;
    }

    public LopHP getLopHocPhan(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ALL_HOCPHAN_TABLE_NAME + " WHERE " +
                ALL_HOCPHAN_COLUMN_MAHP + "=?", new String[]{id});

        if (cursor == null || cursor.getCount() <= 0){
            return null;
        }

        if (cursor.moveToFirst()) {
            LopHP lopHP = new LopHP();
            lopHP.maHP = cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_MAHP));
            lopHP.tenGV = cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_GIANG_VIEN));
            lopHP.tenHP = cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_LOP_HOC_PHAN));
            lopHP.tkb = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_TKB));
            return lopHP;
        }
        else return null;
    }

    public LopHP getLopHPbyName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ALL_HOCPHAN_TABLE_NAME + " WHERE " +
                ALL_HOCPHAN_COLUMN_LOP_HOC_PHAN + "=?", new String[]{name});
        if (cursor == null || cursor.getCount() <= 0){
            return null;
        }

        if (cursor.moveToFirst()) {
            LopHP lopHP = new LopHP();
            lopHP.maHP = cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_MAHP));
            lopHP.tenGV = cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_GIANG_VIEN));
            lopHP.tenHP = cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_LOP_HOC_PHAN));
            lopHP.tkb = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_TKB));
            return lopHP;
        }
        else return null;
    }

    private Cursor getAllLopHocPhan() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ALL_HOCPHAN_TABLE_NAME, null);
        return cursor;
    }

    public ArrayList<String> getListMaHP() {
        ArrayList<String> lstMaHP = new ArrayList<>();
        Cursor cursor  = getAllLopHocPhan();
        if(cursor.moveToFirst()) {
            do {
                lstMaHP.add(cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_MAHP)));
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
                lstTenHP.add(cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_LOP_HOC_PHAN)));
            }
            while (cursor.moveToNext());
        }
        return lstTenHP;
    }

    public ArrayList<LopHP> getAllListHP() {
        ArrayList<LopHP> lstHP = new ArrayList<>();
        Cursor cursor  = getAllLopHocPhan();
        if(cursor.moveToFirst()) {
            do {
                String maHP = cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_MAHP));
                String tenHP = cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_LOP_HOC_PHAN));
                String tenGV = cursor.getString(cursor.getColumnIndex(ALL_HOCPHAN_COLUMN_GIANG_VIEN));
                String tkb = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_TKB));
                lstHP.add(new LopHP(maHP, tenHP, tenGV, tkb));
            }
            while (cursor.moveToNext());
        }
        Utils.getsInstance(mContext).sortWithName(lstHP);
        return lstHP;
    }

    public Integer deleteLopHocPhan(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ALL_HOCPHAN_TABLE_NAME,
                ALL_HOCPHAN_COLUMN_MAHP + " = ? ",
                new String[]{id});
    }

    //check functions
    public boolean isUserLocalDBAvailable() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor  = db.rawQuery("SELECT *    FROM " + USER_TABLE_NAME, null);
        return cursor.getCount() > 0;
    }

    public void checkDB() {
        int count = getAllLopHocPhan().getCount();
        if (count <= 0) {
            onCheckDB.onStartDownload();
            getAllMaHPFromFirebase();
        } else {
            onCheckDB.onDBAvailable();
        }
    }


    //retrieve data from firebase Functions
    public void download_all_hp_database() {
        //clear old db
        clear_all_hp_db();
        //download db
        onCheckDB.onStartDownload();
        getAllMaHPFromFirebase();
    }

    //DataSnapshot(Firebase) -> SQLite
    public class ASyncGetLopHP extends AsyncTask<DataSnapshot, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(DataSnapshot... dataSnapshots) {
            String maHP;
            LopHPObj lopHPObj;
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            for (DataSnapshot snapshot: dataSnapshots[0].getChildren()) {
                lopHPObj = snapshot.getValue(LopHPObj.class);
                maHP = snapshot.getKey();
                maHP = Utils.getsInstance(mContext).underLineToDot(maHP);
                insertLopHocPhan(db, new LopHP(maHP, lopHPObj));
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(String dataSnapshot) {
            super.onPostExecute(dataSnapshot);
            Log.d(TAG, "onPostExecute: finish download");
            onCheckDB.onDownloadFinish();
        }
    }

    private void getAllMaHPFromFirebase() {
        fireBaseAllHPhan.addListenerForSingleValueEvent(new ValueEventListener() {
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

    //set callback
    public void setOnCheckDB(OnCheckDB onCheckDB) {
        this.onCheckDB = onCheckDB;
    }

    public interface OnCheckDB {
        void onDBAvailable();
        void onDownloadFinish();
        void onStartDownload();
    }
}
