package com.fast0n.ap.fragments.OptionsFragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.fast0n.ap.R;
import com.fast0n.ap.java.CubeLoading;
import com.fast0n.ap.java.DialogError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OptionsFragment extends Fragment {

    public OptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_options, container, false);

        final ProgressBar loading;
        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();
        ConstraintLayout linearLayout;
        SharedPreferences settings;
        String theme;

        // java adresses
        loading = view.findViewById(R.id.progressBar);
        settings = context.getSharedPreferences("sharedPreferences", 0);
        theme = settings.getString("toggleTheme", null);
        new CubeLoading(context, loading, theme).showLoading();
        linearLayout = view.findViewById(R.id.linearLayout);

        linearLayout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        final Bundle extras = getActivity().getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token");

        final String site_url = getString(R.string.site_url) + getString(R.string.options);
        String url = site_url + "?option=true&token=" + token;

        getObject(url, context, view);

        return view;
    }

    private void getObject(String url, final Context context, View view) {

        final ProgressBar loading;
        final RecyclerView recyclerView;
        final List<DataOptionsFragments> infoList = new ArrayList<>();
        final ConstraintLayout linearLayout;
        final TextView credit;

        final Bundle extras = getActivity().getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token");

        // java adresses
        recyclerView = view.findViewById(R.id.recycler_view);
        loading = view.findViewById(R.id.progressBar);
        linearLayout = view.findViewById(R.id.linearLayout);
        credit = view.findViewById(R.id.optionsText);

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

                            String name = json_strings.getString("0"); // nome dell'opzione
                            String status = json_strings.getString("2"); // parametro per controllare se l'opzione è attiva (quindi anche il toggle)
                            String update = json_strings.getString("3"); // parametro per attivare o disattivare l'opzione
                            String info = json_strings.getString("4"); // parametro per ottenere le informazioni dell'opzione

                            infoList.add(new DataOptionsFragments(name, status, update, info));


                            CustomAdapterOptions ca = new CustomAdapterOptions(context, infoList, token);
                            recyclerView.setAdapter(ca);

                        }


                        linearLayout.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        new DialogError(this.getActivity(), String.valueOf(e)).alertbox();
                    }

                }, e -> new DialogError(this.getActivity(), String.valueOf(e)).alertbox());

        // add it to the RequestQueue
        queue.add(getRequest);


    }

}
