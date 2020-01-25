package app.rawnaq;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import app.rawnaq.classes.Constants;
import app.rawnaq.classes.GlobalFunctions;
import app.rawnaq.classes.LocaleHelper;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.webservices.RawnaqApiConfig;
import app.rawnaq.webservices.responses.general.GeneralResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private String language;
    private String regId = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(LocaleHelper.onAttach(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        GlobalFunctions.setDefaultLanguage(this);
        GlobalFunctions.setUpFont(this);
        sessionManager = new SessionManager(this);
        language = sessionManager.getUserLanguage();
        if (!language.equals("en"))
            language = "ar";
        LocaleHelper.setLocale(this, language);
        sessionManager.setUserLanguage(language);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, 3000);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("splash", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        regId = task.getResult().getToken();

                        Log.e("registerationid Splash ", "regid -> " + regId);

                        registerFirebaseTokenApi();


                    }
                });
    }

    private void registerFirebaseTokenApi() {

        RawnaqApiConfig.getCallingAPIInterface().registerFirebaseToken(sessionManager.getUserToken(), regId,
                new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {
                        if (generalResponse.status == 200) {
                            sessionManager.setRegId(regId);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }

                });

    }
}

