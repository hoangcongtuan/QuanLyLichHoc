package com.example.hoangcongtuan.quanlylichhoc.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.Alarm.AlamrDetailsActivity;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderDatabase;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;
/**
 * Created by huuthangit on 2017-10-25.
 */

public class ReminderService extends IntentService {
    private final String TAG = ReminderService.class.getName();

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: push notification");

        int idReceived = intent.getIntExtra(ReminderManager.KEY_REMINDER_ID, -1);

        Intent detailIntent = new Intent(this, AlamrDetailsActivity.class);
        detailIntent.putExtra(ReminderManager.KEY_REMINDER_ID, idReceived);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //Get reminder from database
        Reminder reminder = ReminderDatabase.getsInstance(getApplicationContext()).getReminder(idReceived);


        PendingIntent pi = PendingIntent.getActivity(this, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(reminder.getTitle())
                .setContentText(reminder.getContent())
                .setLights(Color.BLUE, 1000, 1000)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(notification);

        mBuilder.setContentIntent(pi);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(reminder.getId(), mBuilder.build());
    }

}

