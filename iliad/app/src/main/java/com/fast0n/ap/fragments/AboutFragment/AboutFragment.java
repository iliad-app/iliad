package com.fast0n.ap.fragments.AboutFragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fast0n.ap.BuildConfig;
import com.fast0n.ap.R;

import java.util.Objects;

public class AboutFragment extends Fragment {

    TextView tv_version, tv_author, tv_author2, tv_author3;
    LinearLayout linearLayout, linearLayout2, linearLayout3, linearLayout4, linearLayout5, linearLayout6, linearLayout99;


    public AboutFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_about, container, false);

        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();

        // java adresses
        tv_version = view.findViewById(R.id.tv_version);
        tv_author = view.findViewById(R.id.tv_author);
        tv_author2 = view.findViewById(R.id.tv_author2);
        tv_author3 = view.findViewById(R.id.tv_author3);

        linearLayout = view.findViewById(R.id.linearLayout);
        linearLayout2 = view.findViewById(R.id.linearLayout2);
        linearLayout3 = view.findViewById(R.id.linearLayout3);
        linearLayout4 = view.findViewById(R.id.linearLayout4);
        linearLayout5 = view.findViewById(R.id.linearLayout5);
        linearLayout6 = view.findViewById(R.id.linearLayout6);

        linearLayout99 = view.findViewById(R.id.linearLayout99);

        // print version and name app
        tv_version.setText(Html.fromHtml(String.format("<b> %s </b><br>%s (%s) (%s)", getString(R.string.version), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID)));


        SharedPreferences settings = context.getSharedPreferences("sharedPreferences", 0);
        String theme = settings.getString("toggleTheme", null);
        if (theme.equals("0")) {
            tv_author.setText(Html.fromHtml("<b><font color='#c00000'>" + getString(R.string.author) + "</fond></b><br>Massimiliano Montaleone (Fast0n)"));
            tv_author2.setText(Html.fromHtml("<b><font color='#c00000'>" + getString(R.string.authorapi) + "</fond></b><br>Matteo Monteleone (MattVoid)"));
            tv_author3.setText(Html.fromHtml("<b><font color='#c00000'>" + getString(R.string.designer) + "</fond></b><br>Domenico Majorana (Nicuz)"));
        } else {
            tv_author.setText(Html.fromHtml("<b><font color='#ffffff'>" + getString(R.string.author) + "</fond></b><br>Massimiliano Montaleone (Fast0n)"));
            tv_author2.setText(Html.fromHtml("<b><font color='#ffffff'>" + getString(R.string.authorapi) + "</fond></b><br>Matteo Monteleone (MattVoid)"));
            tv_author3.setText(Html.fromHtml("<b><font color='#ffffff'>" + getString(R.string.designer) + "</fond></b><br>Domenico Majorana (Nicuz)"));
        }


        linearLayout.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://raw.githubusercontent.com/Fast0n/iliad/master/LICENSE"))));

        linearLayout2.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fast0n/iliad"))));

        linearLayout3.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/Fast0n/1.0"))));

        linearLayout4.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fast0n/"))));

        linearLayout5.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mattvoid/"))));

        linearLayout6.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Nicuz/"))));

        linearLayout99.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://raw.githubusercontent.com/Fast0n/iliad/master/PRIVACY"))));

        return view;
    }

}
