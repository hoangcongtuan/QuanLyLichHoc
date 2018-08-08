package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by hoangcongtuan on 9/15/17.
 */

public class Utils {

    private static final String TAG = Utils.class.getName();
    private RequestQueue requestQueue;
    private static Utils sInstance;

    public final static String APP_FONT_PATH = "fonts/SourceSansPro-Regular.ttf";


    private Utils(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
    }

    public static Utils getsInstance(Context context) {
        if (sInstance == null)
            sInstance = new Utils(context);
        return sInstance;
    }

    public void unSubscribeAllTopics(ArrayList<String> lstTopic) {
        for (String s : lstTopic) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(s);
        }
    }

    public void showErrorMessage(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.error_title);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    public void subscribeTopic(ArrayList<String> lstTopic) {
        for (final String s : lstTopic) {
            Log.d(TAG, "subscribeTopic: Start subscribe: " + s);
            FirebaseMessaging.getInstance().subscribeToTopic(s).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: Finish subscribe: " + s);
                }
            });
        }
    }

    public void unSubscribeTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    public void subscribeTopic(String topic) {
        Log.d(TAG, "subscribeTopic: Start Subscribe: " + topic );
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        Log.d(TAG, "subscribeTopic: Finish subscribe topic: " + topic);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void sortLHP(ArrayList<LopHP> lstLopHP) {
        Collections.sort(lstLopHP, new Comparator<LopHP>() {
            @Override
            public int compare(LopHP lopHP, LopHP lopHP2) {
                return lopHP.getTkb().compareTo(lopHP2.getTkb());
            }
        });
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("https://www.google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
}
