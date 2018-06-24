package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;

import java.util.ArrayList;

/**
 * Created by huuthangit on 2017-10-25.
 */

public class ReminderDBHelper extends SQLiteOpenHelper{
    private static final String TAG = ReminderDBHelper.class.getName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ReminderDBHelper";
    private static final String TABLE_REMINDERS = "ReminderTable";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_REPEAT = "repeat";
    private static final String KEY_TYPE = "type";

    private Context mContext;

    private static ReminderDBHelper sInstance;

    public static ReminderDBHelper getsInstance(Context context) {
        if (sInstance == null)
            sInstance = new ReminderDBHelper(context);
        return sInstance;
    }

    private ReminderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: Creating database");
        String query = "CREATE TABLE " + TABLE_REMINDERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_CONTENT + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_REPEAT + " INTEGER,"
                + KEY_TYPE + " TEXT" + ")";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(sqLiteDatabase);
    }

    public int addReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ID , reminder.getId());
        values.put(KEY_TITLE , reminder.getTitle());
        values.put(KEY_CONTENT , reminder.getContent());
        values.put(KEY_DATE , reminder.getDate());
        values.put(KEY_TIME , reminder.getTime());
        values.put(KEY_REPEAT , reminder.getRepeat());
        values.put(KEY_TYPE , reminder.getType());

        long ID = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        return (int) ID;
    }

    public Reminder getReminder(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REMINDERS, new String[] { KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE, KEY_TIME, KEY_REPEAT, KEY_TYPE}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Integer.parseInt(cursor.getString(5)), cursor.getString(6));
        cursor.close();
        db.close();
        return reminder;
    }

    public int updateReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, reminder.getId());
        values.put(KEY_TITLE , reminder.getTitle());
        values.put(KEY_CONTENT , reminder.getContent());
        values.put(KEY_DATE , reminder.getDate());
        values.put(KEY_TIME , reminder.getTime());
        values.put(KEY_REPEAT , reminder.getRepeat());
        values.put(KEY_TYPE , reminder.getType());

        return db.update(TABLE_REMINDERS, values, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getId())});
    }

    public void deleteReminder(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, KEY_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<Reminder> getAllReminders(){
        ArrayList<Reminder> reminders = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_REMINDERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Reminder reminder = new Reminder();
                reminder.setId(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setContent(cursor.getString(2));
                reminder.setDate(cursor.getString(3));
                reminder.setTime(cursor.getString(4));
                reminder.setRepeat(Integer.parseInt(cursor.getString(5)));
                reminder.setType(cursor.getString(6));

                reminders.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reminders;
    }

}
