package app.rawnaq;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.rawnaq.classes.LocaleHelper;
import app.rawnaq.classes.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private String language;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
    }
}
