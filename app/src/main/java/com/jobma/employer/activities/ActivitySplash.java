package com.jobma.employer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.util.Constants;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;

public class ActivitySplash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.initView();
    }

    private void initView() {
        SessionSecuredPreferences loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);

        //TODO first time to check api key exist or not, on the basis of that screen will open
        if (loginPreferences.contains(Constants.SELECTED_API_KEY)) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent dashboardIntent = new Intent(ActivitySplash.this, ActivityDashboard.class);
                    startActivity(dashboardIntent);
                    ActivitySplash.this.finish();
                }
            }, SPLASH_TIME_OUT);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                    ActivitySplash.this.finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }
}
