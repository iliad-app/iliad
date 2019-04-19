package com.fast0n.ap.fragments.AboutFragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fast0n.ap.BuildConfig;
import com.fast0n.ap.R;

import java.util.Objects;

public class AboutFragment extends Fragment {


    public AboutFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_about, container, false);

        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();

        // java adresses
        TextView tv_version = view.findViewById(R.id.tv_version);
        TextView tv_author = view.findViewById(R.id.tv_author);
        TextView tv_author2 = view.findViewById(R.id.tv_author2);
        TextView tv_author3 = view.findViewById(R.id.tv_author3);
        TextView tv_author4 = view.findViewById(R.id.tv_author4);

        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);
        LinearLayout linearLayout2 = view.findViewById(R.id.linearLayout2);
        LinearLayout linearLayout3 = view.findViewById(R.id.linearLayout3);
        LinearLayout linearLayout4 = view.findViewById(R.id.linearLayout4);
        LinearLayout linearLayout5 = view.findViewById(R.id.linearLayout5);
        LinearLayout linearLayout6 = view.findViewById(R.id.linearLayout6);
        LinearLayout linearLayout7 = view.findViewById(R.id.linearLayout7);

        LinearLayout linearLayout99 = view.findViewById(R.id.linearLayout99);


        tv_version.setText(Html.fromHtml("<b>" + getString(R.string.version) + "</b><br>" + BuildConfig.VERSION_NAME + " ("
                + BuildConfig.VERSION_CODE + ") (" + BuildConfig.APPLICATION_ID + ")"));


        SharedPreferences settings = context.getSharedPreferences("sharedPreferences", 0);
        String theme = settings.getString("toggleTheme", null);
        if (theme.equals("0")) {
            tv_author.setText(Html.fromHtml("<b><font color='#c00000'>" + getString(R.string.author) + "</fond></b><br>Massimiliano Montaleone (Fast0n)"));
            tv_author2.setText(Html.fromHtml("<b><font color='#c00000'>" + getString(R.string.author) + "</fond></b><br>Matteo Monteleone (MattVoid)"));
            tv_author3.setText(Html.fromHtml("<b><font color='#c00000'>" + getString(R.string.author) + "</fond></b><br>Domenico Majorana (Nicuz)"));
            tv_author4.setText(Html.fromHtml("<b><font color='#c00000'>" + getString(R.string.author) + "</fond></b><br>Andrea Crescentini (ElCresh)"));
        } else {
            tv_author.setText(Html.fromHtml("<b><font color='#ffffff'>" + getString(R.string.author) + "</fond></b><br>Massimiliano Montaleone (Fast0n)"));
            tv_author2.setText(Html.fromHtml("<b><font color='#ffffff'>" + getString(R.string.author) + "</fond></b><br>Matteo Monteleone (MattVoid)"));
            tv_author3.setText(Html.fromHtml("<b><font color='#ffffff'>" + getString(R.string.author) + "</fond></b><br>Domenico Majorana (Nicuz)"));
            tv_author4.setText(Html.fromHtml("<b><font color='#ffffff'>" + getString(R.string.author) + "</fond></b><br>Andrea Crescentini (ElCresh)"));
        }


        linearLayout.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Fast0n/iliad/blob/master/LICENSE")));
        });

        linearLayout2.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fast0n/iliad")));
        });

        linearLayout3.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/Fast0n/1.0")));
        });

        linearLayout4.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fast0n/")));
        });

        linearLayout5.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mattvoid/")));
        });

        linearLayout6.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Nicuz/")));
        });


        linearLayout7.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ElCresh/")));
        });

        linearLayout99.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://android12.altervista.org/privacy.html")));
        });

        return view;
    }

}
