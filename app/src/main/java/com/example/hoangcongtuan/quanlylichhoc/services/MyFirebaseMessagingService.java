package com.example.hoangcongtuan.quanlylichhoc.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

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
        intent.putExtra("id", msg.getData().get("id"));
        intent.putExtra("type", msg.getData().get("type"));
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String app_channel_id = getResources().getString(R.string.APP_CHANNEL_ID);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, app_channel_id)
                .setSmallIcon(R.drawable.ic_chat_white_24dp)
                .setContentTitle(msg.getData().get("title"))
                .setAutoCancel(true)
                .setContentText(msg.getData().get("body"))
                .setSound(notificationSound)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(msg.getData().get("body")))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(SplashActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(createID(), PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        Intent intentAlarm = new Intent(this, SplashActivity.class);
        intentAlarm.putExtra("screen", "add_alarm");
        intentAlarm.putExtra("tieu_de", msg.getData().get("title"));
        intentAlarm.putExtra("noi_dung", msg.getData().get("body"));

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

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
