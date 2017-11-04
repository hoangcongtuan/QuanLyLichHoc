package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.hoangcongtuan.quanlylichhoc.services.OnAlarmReceiver;

import java.util.Calendar;

/**
 * Created by huuthangit on 2017-10-25.
 */

public class ReminderManager {
    public static final String KEY_REMINDER_ID = "Reminder_ID";

    private Context mContext;
    private AlarmManager mAlarmManager;
    private static ReminderManager sInstance;

//    public static void init(Context context) {
//        if (sInstance == null)
//            sInstance = new ReminderManager(context);
//    }

    public static ReminderManager getsInstance(Context context) {
        if (sInstance == null)
            sInstance = new ReminderManager(context);
        return sInstance;
    }

    private ReminderManager(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }


    public void setReminder(int id, Calendar when) {
        Intent i = new Intent(mContext, OnAlarmReceiver.class);
        i.putExtra(KEY_REMINDER_ID, Integer.toString(id));
        PendingIntent pi = PendingIntent.getBroadcast(mContext, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
    }


    public void deleteReminder(int id) {
        Intent i = new Intent(mContext, OnAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pi);
    }

}
