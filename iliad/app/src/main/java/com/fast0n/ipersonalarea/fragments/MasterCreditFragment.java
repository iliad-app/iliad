package com.fast0n.ipersonalarea.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ipersonalarea.ChargeActivity;
import com.fast0n.ipersonalarea.R;
import com.fast0n.ipersonalarea.fragments.CreditFragment.CreditFragment;
import com.fast0n.ipersonalarea.fragments.CreditRoamingFragment.CreditRoamingFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MasterCreditFragment extends Fragment {

    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();


        // java adresses
        View view = inflater.inflate(R.layout.fragment_credit_master, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TextView credit = view.findViewById(R.id.creditText);
        TextView description = view.findViewById(R.id.descriptionText);
        TextView description2 = view.findViewById(R.id.descriptionText2);

        Button button = view.findViewById(R.id.button);

        SharedPreferences settings = context.getSharedPreferences("sharedPreferences", 0);
        String token = settings.getString("token", null);
        SharedPreferences.Editor editor = settings.edit();
        editor.apply();

        String site_url = getString(R.string.site_url) + getString(R.string.credit);
        String url = site_url + "?credit=true&token=" + token;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);
                        String string1 = json.getString("0");
                        JSONObject json_strings1 = new JSONObject(string1);
                        String stringCredit = json_strings1.getString("0");

                        credit.setText(stringCredit.split("&")[0]);

                        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());


                        int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), timeStamp, stringCredit.split("&")[1].replaceAll("\\s+", ""));
                        description.setText(String.valueOf(dateDifference - 1));

                        if (dateDifference - 1 > 1)
                            description2.setText(context.getString(R.string.days_renewal));
                        else
                            description2.setText(context.getString(R.string.day_renewal));


                        button.setOnClickListener(v -> {
                            Intent intent1 = new Intent(context, ChargeActivity.class);
                            intent1.putExtra("name", "Ricarica");
                            intent1.putExtra("price", "true");
                            intent1.putExtra("token", token);
                            startActivity(intent1);
                        });


                    } catch (JSONException ignored) {
                    }

                }, error -> {

        });

        queue.add(getRequest);
        setupViewPager(viewPager);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        return view;

    }

    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new CreditFragment(), getString(R.string.italy));
        adapter.addFragment(new CreditRoamingFragment(), getString(R.string.estero));
        viewPager.setAdapter(adapter);

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}