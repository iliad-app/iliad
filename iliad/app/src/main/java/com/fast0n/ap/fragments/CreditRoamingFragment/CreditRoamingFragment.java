package com.fast0n.ap.fragments.CreditRoamingFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ap.ConsumptionDetailsActivity.ConsumptionRoamingDetailActivity;
import com.fast0n.ap.R;
import com.fast0n.ap.java.CubeLoading;
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


    public CreditRoamingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_credit_roaming, container, false);

        context = Objects.requireNonNull(getActivity()).getApplicationContext();

        // java adresses
        loading = view.findViewById(R.id.progressBar);
        settings = context.getSharedPreferences("sharedPreferences", 0);
        theme = settings.getString("toggleTheme", null);
        new CubeLoading(context, loading, theme).showLoading();
        recyclerView = view.findViewById(R.id.recycler_view);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        button = view.findViewById(R.id.button);

        final Bundle extras = getActivity().getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token");

        final String site_url = getString(R.string.site_url) + getString(R.string.credit);
        String url = site_url + "?estero=true&token=" + token;

        if (isOnline())
            getObject(url, context);
        else
            getOfflineObject(context);

        pullToRefresh.setEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        pullToRefresh.setOnRefreshListener(() -> {
            recyclerView.setEnabled(false);
            creditEsteroList.clear();
            mRecyclerViewAdapter = new CustomAdapterCreditRoaming(context, creditEsteroList);
            recyclerView.setAdapter(mRecyclerViewAdapter);

            if (isOnline())
                getObject(url, context);
            else
                getOfflineObject(context);
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

    private void getObject(String url, final Context context) {

        // java adresses
        loading.setVisibility(View.VISIBLE);
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
                        loading.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        new DialogError(this.getActivity(), String.valueOf(e)).alertbox();
                    }
                }, e -> new DialogError(this.getActivity(), String.valueOf(e)).alertbox());


        queue.add(getRequest);

    }

    private void getOfflineObject(final Context context) {
        loading.setVisibility(View.VISIBLE);

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
            loading.setVisibility(View.INVISIBLE);


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
