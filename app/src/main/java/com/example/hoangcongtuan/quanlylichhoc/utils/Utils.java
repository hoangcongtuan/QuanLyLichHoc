package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by hoangcongtuan on 9/15/17.
 */

public class Utils {
    public static class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return null;
        }
    }

    public static class QLLHUtils {
        private final static String TAG = QLLHUtils.class.getName();
        private static QLLHUtils sInstance;

        private QLLHUtils(Context context) {

        }

        public static QLLHUtils getsInstance(Context context) {
            if (sInstance == null)
                sInstance = new QLLHUtils(context);
            return sInstance;
        }

        public void unSubscribeAllTopics(ArrayList<String> lstTopic) {
            for (String s : lstTopic) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(s);
            }
        }

        public void subscribeTopic(ArrayList<String> lstTopic) {
            for (String s : lstTopic)
                FirebaseMessaging.getInstance().subscribeToTopic(s);
        }

        public void unSubscribeTopic(String topic) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
        }

        public void subscribeTopic(String topic) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        }
    }

    public static class VolleyUtils {
        private static final String TAG = Volley.class.getName();
        private static VolleyUtils sInstance;
        private RequestQueue requestQueue;

        private VolleyUtils(Context context) {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context);
            }
        }

        public static synchronized VolleyUtils getsInstance(Context context) {
            if (sInstance == null)
                sInstance = new VolleyUtils(context);
            return  sInstance;
        }

        public RequestQueue getRequestQueue() {
            return requestQueue;
        }
    }

    public static void sortLHP(ArrayList<LopHP> lstLopHP) {
        Collections.sort(lstLopHP, new Comparator<LopHP>() {
            @Override
            public int compare(LopHP lopHP, LopHP lopHP2) {
                return lopHP.getTkb().compareTo(lopHP2.getTkb());
            }
        });
    }
}
