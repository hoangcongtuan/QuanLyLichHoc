package com.example.hoangcongtuan.quanlylichhoc.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.UUID;

/**
 * Created by hoangcongtuan on 10/24/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final static String TAG = MyFirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //check if contain data payload
        if (remoteMessage.getData().size() > 0) {
        }
        //check if containt notification
        if (remoteMessage.getNotification() != null) {
        }
        sendNotification(remoteMessage);

    }


    public void sendNotification(RemoteMessage msg) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("screen", "main");
        intent.putExtra("tieu_de", msg.getData().get("tieu_de"));
        intent.putExtra("thoi_gian", msg.getData().get("thoi_gian"));
        intent.putExtra("noi_dung", msg.getData().get("noi_dung"));
        intent.putExtra("id", msg.getData().get("id"));
        intent.putExtra("type", msg.getData().get("type"));
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_message_white_24dp)
                .setContentTitle(msg.getNotification().getTitle())
                .setAutoCancel(true)
                .setContentText(msg.getNotification().getBody())
                .setSound(notificationSound)
                .setColor(ContextCompat.getColor(this, R.color.colorGreen));

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(SplashActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(createID(), PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        Intent intentAlarm = new Intent(this, SplashActivity.class);
        intentAlarm.putExtra("screen", "add_alarm");
        intentAlarm.putExtra("tieu_de", msg.getData().get("tieu_de"));
        intentAlarm.putExtra("noi_dung", msg.getData().get("noi_dung"));

        PendingIntent pIAlarm = PendingIntent.getActivity(this, createID(), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(R.drawable.ic_alarm_black_24dp, "Nhắc tôi", pIAlarm);


        NotificationManager notifcationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notifcationManager.notify(createID(), builder.build());
    }

    public int createID(){
        return UUID.randomUUID().hashCode();
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

    }
}
