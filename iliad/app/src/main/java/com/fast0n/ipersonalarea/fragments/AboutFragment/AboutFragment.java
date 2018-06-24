package com.fast0n.ipersonalarea.fragments.AboutFragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fast0n.ipersonalarea.BuildConfig;
import com.fast0n.ipersonalarea.R;

import java.util.ArrayList;
import java.util.Objects;

public class AboutFragment extends Fragment {


    public AboutFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_about, container, false);

        ArrayList<DataAboutFragment> DataAboutFragments;
        ListView listView;
        CustomAdapterAboutFragment adapter;
        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();

        // java adresses
        listView = view.findViewById(R.id.list_info);
        DataAboutFragments = new ArrayList<>();


        // add data element in listview
        DataAboutFragments
                .add(new DataAboutFragment(
                        getString(R.string.version) + "<br><small>" + BuildConfig.VERSION_NAME + " ("
                                + BuildConfig.VERSION_CODE + ") (" + BuildConfig.APPLICATION_ID + ")</small>",
                        R.drawable.ic_info_outline));
        DataAboutFragments.add(new DataAboutFragment(getString(R.string.source_code), R.drawable.ic_github));
        DataAboutFragments.add(new DataAboutFragment(getString(R.string.donate), R.drawable.ic_credit));
        DataAboutFragments.add(new DataAboutFragment(getString(R.string.author) + "<br><small>Massimiliano Montaleone (Fast0n)</small>",
                R.drawable.ic_user));
        DataAboutFragments.add(new DataAboutFragment(getString(R.string.author) + "<br><small>Matteo Monteleone (MattVoid)</small>",
                R.drawable.ic_user));
        DataAboutFragments.add(new DataAboutFragment(getString(R.string.author) + "<br><small>Domenico Majorana (Nicuz)</small>",
                R.drawable.ic_user));
        DataAboutFragments.add(new DataAboutFragment(getString(R.string.author) + "<br><small>Luca Stefani (luca020400)</small>",
                R.drawable.ic_user));
        DataAboutFragments.add(new DataAboutFragment(getString(R.string.author) + "<br><small>Andrea Crescentini (ElCresh)</small>",
                R.drawable.ic_user));

        DataAboutFragments.add(new DataAboutFragment(getString(R.string.content), R.drawable.ic_warning));

        // set data to Adapter
        adapter = new CustomAdapterAboutFragment(DataAboutFragments, context);
        listView.setAdapter(adapter);


        // setOnItemClickListener listview
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 1:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fast0n/iliad")));
                    break;
                case 2:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/Fast0n/1.0")));
                    break;
                case 3:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fast0n/")));
                    break;
                case 4:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mattvoid/")));
                    break;
                case 5:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Nicuz/")));
                    break;
                case 6:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/luca020400/")));
                    break;
                case 7:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ElCresh/")));
                    break;
                case 8:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Fast0n/iliad/blob/master/LICENSE")));
                    break;

            }

        });


        return view;
    }

}
