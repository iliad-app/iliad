package com.fast0n.ap.fragments.ServicesFragment;

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

public class ServicesFragment extends Fragment {

    public ServicesFragment() {
        // Required empty public constructor
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
        SharedPreferences settings;
        String theme;

        // java adresses
        loading = view.findViewById(R.id.progressBar);
        settings = context.getSharedPreferences("sharedPreferences", 0);
        theme = settings.getString("toggleTheme", null);
        new CubeLoading(context, loading, theme).showLoading();

        recyclerView = view.findViewById(R.id.recycler_view);
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

                            String name = json_strings.getString("0"); // nome del servizio
                            String status = json_strings.getString("2"); // parametro per controllare se il servizio è attivo (quindi anche il toggle)
                            String update = json_strings.getString("3"); // parametro per attivare o disattivare il servizio
                            String info = json_strings.getString("4"); // parametro per ottenere le informazioni del servizio

                            infoList.add(new DataServicesFragments(name, status, update, info));
                            CustomAdapterServices ca = new CustomAdapterServices(context, infoList, token);
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
