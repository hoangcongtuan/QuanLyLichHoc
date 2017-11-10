package com.example.hoangcongtuan.quanlylichhoc.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

/**
 * Created by huuthangit on 2017-10-25.
 */

public class OnAlarmReceiver extends BroadcastReceiver{
    private final String TAG = OnAlarmReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: received from alarm manager");

        //nhận dữ liệu từ intent gửi tới và put dữ liệu qua cho ReminderService
        Intent i = new Intent(context, ReminderService.class);
        i.putExtra(ReminderManager.KEY_REMINDER_ID, intent.getIntExtra(ReminderManager.KEY_REMINDER_ID, -1));
        context.startService(i);
    }
}
