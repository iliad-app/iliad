package com.fast0n.ap.fragments.CreditRoamingFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.fast0n.ap.ConsumptionDetailsActivity.ConsumptionRoamingDetailActivity;
import com.fast0n.ap.R;
import com.fast0n.ap.java.DialogError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CreditRoamingFragment extends Fragment {

    private final List<DataCreditRoamingFragments> creditEsteroList = new ArrayList<>();
    SharedPreferences settings;
    String theme;
    CustomAdapterCreditRoaming mRecyclerViewAdapter;
    SwipeRefreshLayout pullToRefresh;
    private ProgressBar loading;
    private Context context;
    private Button button;
    private RecyclerView recyclerView;
    private SkeletonScreen skeletonScreen;


    public CreditRoamingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_credit_roaming, container, false);
        int mColor;

        context = Objects.requireNonNull(getActivity()).getApplicationContext();

        // java adresses
        loading = view.findViewById(R.id.progressBar);
        settings = context.getSharedPreferences("sharedPreferences", 0);
        theme = settings.getString("toggleTheme", null);
        if (theme.equals("0"))
            mColor = android.R.color.black;
        else
            mColor = android.R.color.white;
        recyclerView = view.findViewById(R.id.recycler_view);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        button = view.findViewById(R.id.button);

        final Bundle extras = getActivity().getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token");

        final String site_url = getString(R.string.site_url) + getString(R.string.credit);
        String url = site_url + "?estero=true&token=" + token;

        if (isOnline())
            getObject(url, context, mColor);
        else
            getOfflineObject(context, mColor);


        pullToRefresh.setRefreshing(false);

        LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        pullToRefresh.setOnRefreshListener(() -> {
            skeletonScreen = Skeleton.bind(recyclerView)
                    .load(R.layout.row_credit_roaming_loading)
                    .duration(2000)
                    .count(4)
                    .angle(0)
                    .frozen(false)
                    .show();
            recyclerView.setEnabled(false);
            creditEsteroList.clear();
            mRecyclerViewAdapter = new CustomAdapterCreditRoaming(context, creditEsteroList);
            recyclerView.setAdapter(mRecyclerViewAdapter);

            if (isOnline())
                getObject(url, context, mColor);
            else
                getOfflineObject(context, mColor);

            pullToRefresh.setRefreshing(false);

        });

        if (!isOnline())
            button.setVisibility(View.GONE);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConsumptionRoamingDetailActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        return view;
    }

    private void getObject(String url, final Context context, int mColor) {

        skeletonScreen = Skeleton.bind(recyclerView)
                .load(R.layout.row_credit_roaming_loading)
                .duration(2000)
                .count(4)
                .angle(0)
                .color(mColor)
                .frozen(false)
                .show();

        // java adresses
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        PreferenceManager.getDefaultSharedPreferences(context).edit()
                                .putString("creditEstero", response.toString()).apply();


                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);

                        for (int j = 1; j < json.length(); j++) {

                            String string = json.getString(String.valueOf(j));
                            JSONObject json_strings = new JSONObject(string);

                            String c = json_strings.getString("0");
                            try {
                                String b = json_strings.getString("1");
                                String d = json_strings.getString("3");
                                creditEsteroList.add(new DataCreditRoamingFragments(b, c, d));
                                mRecyclerViewAdapter = new CustomAdapterCreditRoaming(context, creditEsteroList);
                                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                                recyclerView.setAdapter(mRecyclerViewAdapter);
                            } catch (Exception ignored) {
                            }


                        }

                    } catch (JSONException e) {
                        new DialogError(this.getActivity(), String.valueOf(e)).alertbox();
                    }
                }, e -> new DialogError(this.getActivity(), String.valueOf(e)).alertbox());


        queue.add(getRequest);

    }

    private void getOfflineObject(final Context context, int mColor) {
        skeletonScreen = Skeleton.bind(recyclerView)
                .load(R.layout.row_credit_roaming_loading)
                .duration(2000)
                .count(4)
                .angle(0)
                .color(mColor)
                .frozen(false)
                .show();


        String jsonCredit = PreferenceManager.
                getDefaultSharedPreferences(context).getString("creditEstero", null);

        try {

            JSONObject json_raw = new JSONObject(jsonCredit);
            String iliad = json_raw.getString("iliad");

            JSONObject json = new JSONObject(iliad);

            for (int j = 1; j < json.length(); j++) {

                String string = json.getString(String.valueOf(j));
                JSONObject json_strings = new JSONObject(string);

                String c = json_strings.getString("0");
                try {
                    String b = json_strings.getString("1");
                    String d = json_strings.getString("3");
                    creditEsteroList.add(new DataCreditRoamingFragments(b, c, d));
                } catch (Exception ignored) {
                }
            }


        } catch (JSONException e) {
            new DialogError(this.getActivity(), String.valueOf(e)).alertbox();
        }
        mRecyclerViewAdapter = new CustomAdapterCreditRoaming(context, creditEsteroList);
        recyclerView.setAdapter(mRecyclerViewAdapter);
    }


    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
