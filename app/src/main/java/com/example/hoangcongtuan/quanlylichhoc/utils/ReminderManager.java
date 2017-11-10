package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

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
        i.putExtra(KEY_REMINDER_ID, id);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, id, i, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
        }
        else
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
    }


    public void deleteReminder(int id) {
        Intent i = new Intent(mContext, OnAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pi);
    }

    public Calendar toCalendar(String date, String time) {
        String[] dateSplit = date.split("/");
        String[] timeSplit = time.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateSplit[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateSplit[1]) - 1);
        calendar.set(Calendar.YEAR, Integer.parseInt(dateSplit[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSplit[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeSplit[1]));

        return calendar;
    }



}
