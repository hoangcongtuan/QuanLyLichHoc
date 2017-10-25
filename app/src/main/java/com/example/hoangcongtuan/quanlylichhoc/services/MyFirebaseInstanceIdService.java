package com.example.hoangcongtuan.quanlylichhoc.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by hoangcongtuan on 10/24/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {



    private final static String TAG = MyFirebaseInstanceIdService.class.getName();

    public MyFirebaseInstanceIdService() {
        super();
        Log.d(TAG, "MyFirebaseInstanceIdService: " + FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "onTokenRefresh: " + refreshToken);
        //updateFCMTokentoFirebase(refreshToken);
    }

    public void updateFCMTokentoFirebase(String strToken) {
        DatabaseReference firebaseUserToken;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUserToken = FirebaseDatabase.getInstance().getReference().child("userInfo").child(firebaseUser.getUid()).child("FCMToken");
        firebaseUserToken.setValue(strToken);
    }
}