package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHPObj;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DBLopHPHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteOpenHelper";

    private static final String DATABASE_NAME = "SQLiteInfo.db";
    private static final int DATABASE_VERSION = 1;
    private static final String HOCPHAN_TABLE_NAME = "thong_tin_lop_hoc_phan";
    private static final String HOCPHAN_COLUMN_MAHP = "MA_HP";
    private static final String HOCPHAN_COLUMN_GIANG_VIEN = "TEN_GIANG_VIEN";
    private static final String HOCPHAN_COLUMN_LOP_HOC_PHAN = "TEN_HOC_PHAN";

    private static final String PATH_INFO_ALL_LOP_HOC_PHAN = "thong_tin_lop_hoc_phan";
    private static final String HOCPHAN_COLUMN_TKB = "TKB";

    private Context mContext;

    private DatabaseReference dbListThongTinLopHocPhan;

    private static DBLopHPHelper sInstance;
    public static DBLopHPHelper getsInstance(Context context) {
        if (sInstance == null)
            sInstance = new DBLopHPHelper(context);
        return sInstance;
    }

    private DBLopHPHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        dbListThongTinLopHocPhan = database.child(PATH_INFO_ALL_LOP_HOC_PHAN);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + HOCPHAN_TABLE_NAME + "(" +
                HOCPHAN_COLUMN_MAHP + " TEXT PRIMARY KEY, " +
                HOCPHAN_COLUMN_GIANG_VIEN + " TEXT, " +
                HOCPHAN_COLUMN_LOP_HOC_PHAN + " TEXT, " +
                HOCPHAN_COLUMN_TKB + " TEXT)"
        );
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
        contentValues.put(HOCPHAN_COLUMN_MAHP, lopHP.ma_hoc_phan);
        contentValues.put(HOCPHAN_COLUMN_GIANG_VIEN, lopHP.ten_giang_vien);
        contentValues.put(HOCPHAN_COLUMN_LOP_HOC_PHAN, lopHP.ten_hoc_phan);
        contentValues.put(HOCPHAN_COLUMN_TKB, lopHP.tkb);
        db.insert(HOCPHAN_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean updateLopHocPhan(LopHP lopHP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOCPHAN_COLUMN_MAHP, lopHP.ma_hoc_phan);
        contentValues.put(HOCPHAN_COLUMN_GIANG_VIEN, lopHP.ten_giang_vien);
        contentValues.put(HOCPHAN_COLUMN_LOP_HOC_PHAN, lopHP.ten_hoc_phan);
        contentValues.put(HOCPHAN_COLUMN_TKB, lopHP.tkb);
        db.update(HOCPHAN_TABLE_NAME, contentValues, HOCPHAN_COLUMN_MAHP + " = ? ", new String[]{lopHP.ma_hoc_phan});
        return true;
    }

    public LopHP getLopHocPhan(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + HOCPHAN_TABLE_NAME + " WHERE " +
                HOCPHAN_COLUMN_MAHP + "=" + id, null);
        LopHP lopHP = new LopHP();
        if (cursor.moveToFirst()) {
            lopHP.ma_hoc_phan = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_MAHP));
            lopHP.ten_giang_vien = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_GIANG_VIEN));
            lopHP.ten_hoc_phan = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_LOP_HOC_PHAN));
            lopHP.tkb = cursor.getString(cursor.getColumnIndex(HOCPHAN_COLUMN_TKB));
        }
        return lopHP;
    }

      private Cursor getAllLopHocPhan() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + HOCPHAN_TABLE_NAME, null);
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
            loadData();
        } else {
            Toast.makeText(mContext, "ok", Toast.LENGTH_SHORT).show();
        }
    }


    public class ASyncGetLopHP extends AsyncTask<DataSnapshot, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(mContext, "Start Load", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(DataSnapshot... dataSnapshots) {
            String maHP;
            LopHPObj lopHPObj;
            for (DataSnapshot snapshot: dataSnapshots[0].getChildren()) {
                lopHPObj = snapshot.getValue(LopHPObj.class);
                maHP = snapshot.getKey();
                insertLopHocPhan(new LopHP(maHP, lopHPObj));
                //Log.d(TAG, "onDataChange: " + maHP);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String dataSnapshot) {
            super.onPostExecute(dataSnapshot);
            Toast.makeText(mContext, "Finish Load", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadData() {


        dbListThongTinLopHocPhan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ASyncGetLopHP async = new ASyncGetLopHP();
                async.execute(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        dbListThongTinLopHocPhan.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                LopHPObj itemLopHP;
//                try {
//                    String key = dataSnapshot.getKey();
//                    itemLopHP = dataSnapshot.getValue(LopHPObj.class);
//                    insertLopHocPhan(new LopHP(key, itemLopHP));
//                }catch (Exception e){
//                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
