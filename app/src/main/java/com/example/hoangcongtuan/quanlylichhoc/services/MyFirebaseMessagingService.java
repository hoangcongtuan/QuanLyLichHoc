package com.example.hoangcongtuan.quanlylichhoc.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by hoangcongtuan on 10/24/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final static String TAG = MyFirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: From " + remoteMessage.getFrom());
        //check if contain data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "onMessageReceived: Data Payload = " + remoteMessage.getData());
        }
        //check if containt notification
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "onMessageReceived: Body = " + remoteMessage.getNotification().getBody());
        }
        sendNotification(remoteMessage);

    }


    public void sendNotification(RemoteMessage msg) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tieu_de", msg.getData().get("tieu_de"));
        intent.putExtra("thoi_gian", msg.getData().get("thoi_gian"));
        intent.putExtra("noi_dung", msg.getData().get("noi_dung"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.com_facebook_button_icon)
                .setContentTitle(msg.getNotification().getTitle())
                .setContentText(msg.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);
        NotificationManager notifcationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notifcationManager.notify(0, builder.build());
        Log.d(TAG, "sendNotification: send notification");
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

    }
}
