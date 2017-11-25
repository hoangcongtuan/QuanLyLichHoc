package com.example.hoangcongtuan.quanlylichhoc.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.setup.SetupActivity;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Man hinh dang nhap
 */


public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private final static String TAG = LoginActivity.class.getName();
    private final static int RC_SIGN_IN = 1;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleApiClient mGoogleApiClient;

    private DatabaseReference firebaseDB;
    private DatabaseReference firebaseDBUserMaHP;
    private DatabaseReference firebaseUserToken;

    private Button btnLoginFb;
    private Button btnLoginGg;
    private ProgressBar progressBarLogin;
    private CoordinatorLayout coordinatorLayout;
    Toolbar toolbar;

    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        getWidgets();
        setWidgets();
        setWidgetsEvent();
    }

    private void init() {
        //init
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //init google SignIn
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //init facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                startAuthWithFirebase();
                handleFbLoginResult(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        getResources().getString(R.string.fb_login_failed), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }
        });
    }

    private void getWidgets() {
        //getwidgets
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBarLogin = (ProgressBar)findViewById(R.id.progresBar_login);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        btnLoginFb = (Button)findViewById(R.id.btnLoginFb);
        btnLoginGg = (Button)findViewById(R.id.btnLoginGg);

    }

    private void setWidgets() {
        //setwidgets
        setSupportActionBar(toolbar);
        progressBarLogin.setVisibility(View.INVISIBLE);

    }

    private void setWidgetsEvent() {
        //setwidgetsEvent
        btnLoginGg.setOnClickListener(this);
        btnLoginFb.setOnClickListener(this);
    }

    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void facebookSignIn() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    public void handleGgSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount signInAccount = result.getSignInAccount();
            startAuthWithFirebase();
            firebaseAuthWithGoogle(signInAccount);
        }
        else {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.gg_login_failed), Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            finishAuthWithFirebase();
        }

    }

    public void handleFbLoginResult(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            handleFirebaseLoginSuccess(firebaseUser);
                        }
                        else {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                    getResources().getString(R.string.email_already_login), Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            finishAuthWithFirebase();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGgSignInResult(googleSignInResult);
                break;
        }
    }

    public void startAuthWithFirebase() {
        progressBarLogin.setVisibility(View.VISIBLE);
    }

    public void finishAuthWithFirebase() {
        progressBarLogin.setVisibility(View.INVISIBLE);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            handleFirebaseLoginSuccess(firebaseUser);
                        }
                        else {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                    getResources().getString(R.string.gg_login_failed), Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();

                        }
                    }
                });
    }

    private void handleFirebaseLoginSuccess(FirebaseUser user) {
        //get local user db
        firebaseDB = FirebaseDatabase.getInstance().getReference();
        firebaseDBUserMaHP = firebaseDB.child(getResources().getString(R.string.key_firebase_user_info))
                .child(firebaseUser.getUid()).child(getResources().getString(R.string.key_firebase_list_mahp));
        firebaseUserToken = firebaseDB.child(getResources().getString(R.string.key_firebase_user_info))
                .child(firebaseUser.getUid()).child(getResources().getString(R.string.key_firebase_fcmtoken));
        firebaseDBUserMaHP.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //da thiet lap lop hoc phan
                    if (dataSnapshot.getChildrenCount() != 0) {
                        //co du lieu trong do
                        //save Firebase DB to local DB
                        DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DBLopHPHelper.getsInstance().insertUserMaHocPhan((String)snapshot.getValue());
                        }
                        //kiem tra du lieu ve tat ca cac lop hoc phan
                        DBLopHPHelper.getsInstance().setOnCheckDB(new DBLopHPHelper.OnCheckDB() {
                            @Override
                            public void onDBAvailable() {
                                finishAuthWithFirebase();
                                //subscrible topics
                                Utils.QLLHUtils.getsInstance(LoginActivity.this).subscribeTopic(
                                        DBLopHPHelper.getsInstance().getListUserMaHP()
                                );

                                Utils.QLLHUtils.getsInstance(LoginActivity.this).subscribeTopic(
                                        getResources().getString(R.string.topic_tb_chung)
                                );

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onDownloadFinish() {
                                finishAuthWithFirebase();
                                //subscrible topics
                                Utils.QLLHUtils.getsInstance(LoginActivity.this).subscribeTopic(
                                        DBLopHPHelper.getsInstance().getListUserMaHP()
                                );

                                Utils.QLLHUtils.getsInstance(LoginActivity.this).subscribeTopic(
                                        getResources().getString(R.string.topic_tb_chung)
                                );

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onStartDownload() {

                            }
                        });

                        DBLopHPHelper.getsInstance().checkDB();

                    }
                    else {
                        //di toi man hinh setup
                        //xoa du lieu cu trong may
                        DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                        finishAuthWithFirebase();
                        Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    //di toi man hinh setup
                    //xoa du lieu cu trong may
                    DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                    finishAuthWithFirebase();
                    Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.error)
                    + databaseError.getCode() + ": " + databaseError.getMessage(), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(getResources().getString(R.string.details),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle(getResources().getString(R.string.details));
                                builder.setMessage(databaseError.getDetails());
                                builder.show();
                            }
                        });
                snackbar.show();
            }
        });

        //write token FCM to firebase user
        firebaseUserToken.setValue(FirebaseInstanceId.getInstance().getToken());
    }

    private void getTopicSubcribe(String token, final String key) {
        JsonObjectRequest jsonRequest;
        jsonRequest = new JsonObjectRequest(Request.Method.GET, "https://iid.googleapis.com/iid/info/" + token + "?details=true", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: " + response.getJSONObject("rel").getJSONObject("topics").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Authorization","key=" + key);
                return headers;
            }

        };
        Utils.VolleyUtils.getsInstance(this).getRequestQueue().add(jsonRequest);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout,
                "Google Connect failed, code = " + connectionResult.getErrorCode(), Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLoginGg:
                googleSignIn();
                break;
            case R.id.btnLoginFb:
                facebookSignIn();
                break;
        }
    }
}
