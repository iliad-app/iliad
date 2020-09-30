package com.fast0n.ap.fragments.ConditionsFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

public class ConditionsFragment extends Fragment {

    public ConditionsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_conditions, container, false);

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

        final String site_url = getString(R.string.site_url) + getString(R.string.document);
        String url = site_url + "?doc=true&token=" + token;

        getObject(url, context, view);

        return view;
    }

    private void getObject(String url, final Context context, View view) {

        final ProgressBar loading;
        final RecyclerView recyclerView;
        final List<DataConditionsFragments> conditionList = new ArrayList<>();
        final ConstraintLayout linearLayout;

        // java adresses
        recyclerView = view.findViewById(R.id.recycler_view);
        loading = view.findViewById(R.id.progressBar);
        linearLayout = view.findViewById(R.id.linearLayout);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {

                    JSONObject json_raw;
                    try {
                        json_raw = new JSONObject(response.toString());

                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);

                        for (int j = 0; j < json.length(); j++) {

                            String string = json.getString(String.valueOf(j));
                            JSONObject json_strings = new JSONObject(string);

                            String a = json_strings.getString("0");
                            String b = json_strings.getString("1");
                            String c = json_strings.getString("2");

                            conditionList.add(new DataConditionsFragments(a, b, c));
                            CustomAdapterConditions ca = new CustomAdapterConditions(conditionList, this.getActivity());
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
