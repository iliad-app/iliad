package com.fast0n.ipersonalarea.fragments.ServicesFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ipersonalarea.LoginActivity;
import com.fast0n.ipersonalarea.R;
import com.github.ybq.android.spinkit.style.CubeGrid;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServicesFragment extends Fragment {

    public ServicesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_services, container, false);

        final ProgressBar loading;
        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();
        ConstraintLayout linearLayout;

        // java adresses
        loading = view.findViewById(R.id.progressBar);
        linearLayout = view.findViewById(R.id.linearLayout);

        linearLayout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        final Bundle extras = getActivity().getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token");

        final String site_url = getString(R.string.site_url) + getString(R.string.services);
        String url = site_url + "?services=true&token=" + token;

        getObject(url, context, view);

        return view;
    }

    private void getObject(String url, final Context context, View view) {

        final ProgressBar loading;
        final RecyclerView recyclerView;
        final List<DataServicesFragments> infoList = new ArrayList<>();
        final ConstraintLayout linearLayout;
        final TextView credit;

        // java adresses
        recyclerView = view.findViewById(R.id.recycler_view);
        loading = view.findViewById(R.id.progressBar);
        CubeGrid cubeGrid = new CubeGrid();
        loading.setIndeterminateDrawable(cubeGrid);
        cubeGrid.setColor(getResources().getColor(R.color.colorPrimary));
        linearLayout = view.findViewById(R.id.linearLayout);
        credit = view.findViewById(R.id.creditText);

        final Bundle extras = getActivity().getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

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
                        credit.setText(stringCredit);

                        for (int i = 1; i < json.length(); i++) {

                            String string = json.getString(String.valueOf(i));
                            JSONObject json_strings = new JSONObject(string);

                            String a = json_strings.getString("0");
                            String b = json_strings.getString("2");
                            String c = json_strings.getString("3");

                            infoList.add(new DataServicesFragments(a, b, c));
                            CustomAdapterServices ca = new CustomAdapterServices(context, infoList, token);
                            recyclerView.setAdapter(ca);
                        }


                        linearLayout.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                }, error -> startActivity(new Intent(context, LoginActivity.class)));

        // add it to the RequestQueue
        queue.add(getRequest);

    }

}
