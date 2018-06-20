package com.fast0n.ipersonalarea.fragments.CreditFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fast0n.ipersonalarea.ChargeActivity;
import com.fast0n.ipersonalarea.ConsumptionDetailsActivity.ConsumptionDetailsActivity;
import com.fast0n.ipersonalarea.CustomPriorityRequest;
import com.fast0n.ipersonalarea.LoginActivity;
import com.fast0n.ipersonalarea.R;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreditFragment extends Fragment {

    public CreditFragment() {
    }

    PullToRefreshRecyclerView recyclerView;
    ProgressBar loading;
    List<DataCreditFragments> creditList = new ArrayList<>();
    Button button, button1;
    Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_credit, container, false);

        context = Objects.requireNonNull(getActivity()).getApplicationContext();

        // java adresses
        loading = view.findViewById(R.id.progressBar);
        CubeGrid cubeGrid = new CubeGrid();
        loading.setIndeterminateDrawable(cubeGrid);
        cubeGrid.setColor(getResources().getColor(R.color.colorPrimary));
        button1 = view.findViewById(R.id.button1);
        button = view.findViewById(R.id.button);
        recyclerView = view.findViewById(R.id.recycler_view);


        button.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);


        SharedPreferences settings = context.getSharedPreferences("sharedPreferences", 0);
        String token = settings.getString("token", null);
        SharedPreferences.Editor editor = settings.edit();
        editor.apply();

        final String site_url = getString(R.string.site_url) + getString(R.string.credit);
        String url = site_url + "?credit=true&token=" + token;

        getObject(url, context, view);


        button1.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConsumptionDetailsActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });


        button.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChargeActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        recyclerView.setSwipeEnable(true);
        LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);

        recyclerView.setOnRefreshListener(() ->{
            recyclerView.setRefreshing(false);
            recyclerView.setEnabled(false);
            creditList.removeAll(creditList);
            CustomAdapterCredit ca = new CustomAdapterCredit(context, creditList);
            recyclerView.setAdapter(ca);
            getObject(url, context, view);

        });

        return view;
    }

    private void getObject(String url, final Context context, View view) {

        loading.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(context);

        CustomPriorityRequest customPriorityRequest = new CustomPriorityRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);

                        String string1 = json.getString("0");
                        JSONObject json_strings1 = new JSONObject(string1);
                        String btn = json_strings1.getString("1");
                        String btn1 = json_strings1.getString("2");

                        if (btn.equals("true"))
                            button.setVisibility(View.VISIBLE);
                        else
                            button.setVisibility(View.INVISIBLE);

                        if (btn1.equals("true"))
                            button1.setVisibility(View.VISIBLE);
                        else
                            button1.setVisibility(View.INVISIBLE);


                        for (int j = 1; j < json.length(); j++) {

                            String string = json.getString(String.valueOf(j));
                            JSONObject json_strings = new JSONObject(string);

                            String c = json_strings.getString("0");
                            String b = json_strings.getString("1");
                            String a = json_strings.getString("2");
                            String d = json_strings.getString("3");

                            creditList.add(new DataCreditFragments(a, b, c, d));
                            CustomAdapterCredit ca = new CustomAdapterCredit(context, creditList);
                            recyclerView.setAdapter(ca);

                        }

                        loading.setVisibility(View.INVISIBLE);


                    } catch (JSONException e) {
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                },
                error -> startActivity(new Intent(context, LoginActivity.class)));

        customPriorityRequest.setPriority(Request.Priority.IMMEDIATE);
        queue.add(customPriorityRequest);


    }
}