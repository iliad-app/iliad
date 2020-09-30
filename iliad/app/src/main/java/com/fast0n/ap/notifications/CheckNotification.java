package com.fast0n.ap.notifications;

import android.content.SharedPreferences;

import com.google.firebase.messaging.FirebaseMessaging;

public class CheckNotification {
    public CheckNotification(String toggleNotification, SharedPreferences.Editor editor) {

        try {
            if (toggleNotification.equals("0"))
                FirebaseMessaging.getInstance().subscribeToTopic("notification");
            else
                FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");
        } catch (Exception ignored) {
            FirebaseMessaging.getInstance().subscribeToTopic("notification");
            editor.putString("toggleNotification", "0");
            editor.apply();

        }

    }
}
