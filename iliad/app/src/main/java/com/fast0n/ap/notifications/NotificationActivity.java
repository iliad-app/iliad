package com.fast0n.ap.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.fast0n.ap.LoginActivity;
import com.fast0n.ap.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String url_link = extras.getString("url_link");


        if (url_link != null) {


            Handler handler = new Handler();
            handler.postDelayed(() -> {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url_link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);


            }, 500);

            startActivity(new Intent(NotificationActivity.this, LoginActivity.class));


        } else {
            startActivity(new Intent(this, LoginActivity.class));

        }
    }
}
