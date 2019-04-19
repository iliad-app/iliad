package com.fast0n.ap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ErrorConnectionActivity extends AppCompatActivity {

    TextView title, desc;
    SharedPreferences settings;
    String theme;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();
        theme = settings.getString("toggleTheme", null);
        try {
            if (theme.equals("0"))
                setTheme(R.style.AppTheme);
            else
                setTheme(R.style.DarkTheme);
        } catch (Exception ignored) {
            editor.putString("toggleTheme", "0");
            editor.apply();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_connection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        Button button = findViewById(R.id.button);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);

        try {
            Bundle extras = getIntent().getExtras();
            assert extras != null;
            String errorAPI = extras.getString("errorAPI", null);

            if (errorAPI.equals("true")) {
                title.setText(R.string.old_version);
                desc.setText(R.string.old_version_title);
                button.setText(R.string.update);

                button.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))));
            }
        } catch (Exception ignored) {
            button.setOnClickListener(v -> {

                if (isOnline()) {
                    Intent mStartActivity = new Intent(ErrorConnectionActivity.this, LoginActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(ErrorConnectionActivity.this,
                            mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) ErrorConnectionActivity.this
                            .getSystemService(Context.ALARM_SERVICE);
                    assert mgr != null;
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);

                } else {
                    Toasty.info(ErrorConnectionActivity.this, desc.getText(), Toast.LENGTH_SHORT, true)
                            .show();
                }

            });
        }

    }

    @Override
    public void onBackPressed() {
        finishAffinity();

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
