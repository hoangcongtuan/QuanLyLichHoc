package com.example.hoangcongtuan.quanlylichhoc.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.base.BaseActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.setup.SetupActivity;
import com.example.hoangcongtuan.quanlylichhoc.customview.ProgressDialogBuilderCustom;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Man hinh dang nhap
 */

public class LoginActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private final static String TAG = LoginActivity.class.getName();
    private final static int RC_SIGN_IN = 1;

    public final static String KEY_FIRBASE_USER = "user";
    public final static String KEY_FIREBASE_LIST_MAHP = "ma_hoc_phan";
    public final static String KEY_FIREBASE_USERINFO = "info";
    public final static String KEY_FIREBASE_USERNAME = "name";
    public final static String KEY_FIREBASE_USEREMAIL = "email";
    public final static String KEY_FIREBASE_USERPHONE = "phone";
    public final static String KEY_FIREBASE_USER_LATEST_ONLINE = "latest_online";
    public final static String KEY_FIREBASE_USERPROVIDER = "provider";
    public final static String TOPIC_TBCHUNG = "TBChung";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleApiClient mGoogleApiClient;

    private Button btnLoginFb;
    private Button btnLoginGg;
    private ProgressBar progressBarLogin;
    private CoordinatorLayout coordinatorLayout;
    private ConstraintLayout viewgroup_login;
    private TextView tvUserName;

    private AlertDialog pr_login;

    private CallbackManager facebookLoginCallBack;


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
        facebookLoginCallBack = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookLoginCallBack, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                pr_login.dismiss();
                GraphRequest graphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    tvUserName.setText(object.getString("name"));
                                    startAuthWithFirebase();
                                    handleFbLoginResult(loginResult.getAccessToken());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Crashlytics.log(Log.ERROR, TAG, "Login Fb Failed : " + e.getMessage());
                                    Snackbar.make(coordinatorLayout,
                                            getResources().getString(R.string.fb_login_failed), Snackbar.LENGTH_INDEFINITE)
                                            .show();
                                }
                            }
                        }
                );

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, link");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

            @Override
            public void onCancel() {
                Crashlytics.log(Log.ERROR, TAG, "On Fb login cancel");
                pr_login.dismiss();
            }

            @Override
            public void onError(FacebookException error) {
                Crashlytics.log(Log.ERROR, TAG, "On Fb login Error");
                pr_login.dismiss();
                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        getResources().getString(R.string.fb_login_failed), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                Log.d(TAG, "onError: = " + error.toString());
                LoginManager.getInstance().logOut();
            }
        });

        //create login progress dialog
        ProgressDialogBuilderCustom pr_dialog_login_builder = new ProgressDialogBuilderCustom(this);
        pr_dialog_login_builder.setText(R.string.processing);
        pr_login = pr_dialog_login_builder.create();
    }

    private void getWidgets() {
        //getwidgets
        progressBarLogin = findViewById(R.id.progresBar_login);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        viewgroup_login = findViewById(R.id.viewgroup_login);
        btnLoginFb = findViewById(R.id.btnLoginFb);
        btnLoginGg = findViewById(R.id.btnLoginGg);
        tvUserName = findViewById(R.id.tvUserName);
    }

    private void setWidgets() {
        //setwidgets
    }

    private void setWidgetsEvent() {
        //setwidgetsEvent
        btnLoginGg.setOnClickListener(this);
        btnLoginFb.setOnClickListener(this);
    }

    /**
     * Sign in with Google
     */
    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        if (Utils.getsInstance(getApplicationContext()).isNetworkConnected(getApplicationContext())) {
            startActivityForResult(signInIntent, RC_SIGN_IN);
            //show progress dialog
            pr_login.show();
        }
        else
            showNoInternetMessage();
    }

    /**
     * SignIn whit Fb
     */
    public void facebookSignIn() {
        if (Utils.getsInstance(getApplicationContext()).isNetworkConnected(getApplicationContext())) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
            //show progress dialog
            pr_login.show();
        } else
            showNoInternetMessage();
    }

    public void handleGgSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount signInAccount = result.getSignInAccount();
            startAuthWithFirebase();
            tvUserName.setText(result.getSignInAccount().getDisplayName());
            firebaseAuthWithGoogle(signInAccount);
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
                            handleFirebaseLoginSuccess();
                        }
                        else {
                            Crashlytics.log(Log.ERROR, TAG, "Error Fb login, Email exist!");
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
        facebookLoginCallBack.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                pr_login.dismiss();
                handleGgSignInResult(googleSignInResult);
                break;
        }
    }

    public void startAuthWithFirebase() {
        progressBarLogin.setVisibility(View.VISIBLE);
        viewgroup_login.setVisibility(View.GONE);
        tvUserName.setVisibility(View.VISIBLE);
    }

    public void finishAuthWithFirebase() {
        progressBarLogin.setVisibility(View.INVISIBLE);
        viewgroup_login.setVisibility(View.VISIBLE);
        tvUserName.setVisibility(View.INVISIBLE);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            handleFirebaseLoginSuccess();
                        }
                        else {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                    getResources().getString(R.string.gg_login_failed), Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            finishAuthWithFirebase();

                        }
                    }
                });
    }

    /**
     * Login firebase success, update User Class DB and Subscribe topic
     */
    private void handleFirebaseLoginSuccess() {

        final DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference();

        DatabaseReference firebaseDBUserMaHP = firebaseDB.child(KEY_FIRBASE_USER)
                .child(firebaseUser.getUid()).child(KEY_FIREBASE_LIST_MAHP);

        firebaseDBUserMaHP.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //da thiet lap lop hoc phan
                    if (dataSnapshot.getChildrenCount() != 0) {
                        //co du lieu trong do
                        //save Firebase DB to local DB

                        //delete old db
                        DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DBLopHPHelper.getsInstance().insertUserMaHocPhan((String)snapshot.getValue());
                        }

                        ArrayList<String> list_topic = DBLopHPHelper.getsInstance().getListUserMaHP();
                        //subscribe new topic
                        Utils.getsInstance(getApplicationContext()).subscribeTopic(list_topic);
                        Utils.getsInstance(getApplicationContext()).subscribeTopic(LoginActivity.TOPIC_TBCHUNG);

                        //goto MainAct
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        //di toi man hinh setup
                        //finishAuthWithFirebase();
                        Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    //di toi man hinh setup
                    //finishAuthWithFirebase();
                    Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull final DatabaseError databaseError) {
                Crashlytics.log(Log.ERROR, TAG, "Error when get user db from firebase: " + databaseError.getMessage());
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
                finishAuthWithFirebase();
            }
        });

        DatabaseReference firebaseUserNode = firebaseDB.child(KEY_FIRBASE_USER)
                .child(firebaseUser.getUid()).child(KEY_FIREBASE_USERINFO);

        firebaseUserNode.child(KEY_FIREBASE_USEREMAIL).setValue(
                firebaseUser.getEmail()
        );
        firebaseUserNode.child(KEY_FIREBASE_USERNAME).setValue(
                firebaseUser.getDisplayName()
        );

        firebaseUserNode.child(KEY_FIREBASE_USERPHONE).setValue(
                firebaseUser.getPhoneNumber()
        );

        firebaseUserNode.child(KEY_FIREBASE_USERPROVIDER).setValue(
                firebaseUser.getProviders().get(0)
        );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout,
                "Google Connect failed, code = " + connectionResult.getErrorCode(), Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

    }

    public void showNoInternetMessage() {
        Snackbar.make(coordinatorLayout,
                getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                }).show();
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
