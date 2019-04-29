package com.fast0n.ap.java;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.view.ViewGroup;

import com.fast0n.ap.R;


public class SnackbarMaterial {
    public static void configSnackbar(Context context, Snackbar snack) {
        addMargins(snack);
        setRoundBordersBg(context, snack);
        ViewCompat.setElevation(snack.getView(), 6f);
    }

    private static void addMargins(Snackbar snack) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snack.getView().getLayoutParams();
        params.setMargins(12, 12, 12, 12);
        snack.getView().setLayoutParams(params);
    }

    private static void setRoundBordersBg(Context context, Snackbar snackbar) {
        SharedPreferences settings;
        String theme;

        settings = context.getSharedPreferences("sharedPreferences", 0);
        theme = settings.getString("toggleTheme", null);
        if (theme.equals("0"))
            snackbar.getView().setBackground(context.getDrawable(R.drawable.bg_snackbar));
        else
            snackbar.getView().setBackground(context.getDrawable(R.drawable.bg_snackbar_dark));

    }


}
