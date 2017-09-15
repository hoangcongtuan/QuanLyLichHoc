package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
}
