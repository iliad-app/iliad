package com.fast0n.ap.ConsumptionDetailsActivity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ap.R;
import com.fast0n.ap.java.CubeLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ConsumptionDetailsActivity extends AppCompatActivity {

    private final ArrayList<ModelChildren> iphones = new ArrayList<>();
    private final ArrayList<ModelChildren> nexus = new ArrayList<>();
    private final ArrayList<ModelChildren> windows = new ArrayList<>();
    SharedPreferences settings;
    String theme;
    private RecyclerView recyclerView;
    private ArrayList<Model> model;
    private CustomAdapter adapter;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences("sharedPreferences", 0);
        theme = settings.getString("toggleTheme", null);
        if (theme.equals("0"))
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.DarkTheme);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.app_name);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // set row icon in the toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        final Bundle extras = getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token", null);
        final String site_url = getString(R.string.site_url) + getString(R.string.credit);
        TextView offer;


        // java adresses
        offer = findViewById(R.id.offer);
        recyclerView = findViewById(R.id.recycler_view);
        model = new ArrayList<>();
        loading = findViewById(R.id.progressBar);
        new CubeLoading(this, loading, theme).showLoading();
        loading.setVisibility(View.VISIBLE);

        offer.setText(getString(R.string.consumptiondetail));

        String url = site_url + "?details=true&token=" + token;
        getConsumption(url);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    private void getConsumption(String url) {

        RequestQueue queue = Volley.newRequestQueue(ConsumptionDetailsActivity.this);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        ArrayList[] ConsumptionDetails = new ArrayList[]{iphones, nexus, windows};

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");


                        JSONObject json = new JSONObject(iliad);

                        String title = json.getString("title");
                        JSONObject json_title = new JSONObject(title);

                        for (int z = 0; z < json_title.length(); z++) {
                            String x = json_title.getString(String.valueOf(z));

                            try {
                                String string = json.getString(String.valueOf(z));
                                JSONObject json_strings = new JSONObject(string);

                                for (int j = 0; j < json_strings.length(); j++) {

                                    String string_one = json_strings.getString(String.valueOf(j));
                                    JSONObject json_strings_one = new JSONObject(string_one);

                                    String a = json_strings_one.getString(String.valueOf(0));
                                    String b = json_strings_one.getString(String.valueOf(1));
                                    String c = json_strings_one.getString(String.valueOf(2));
                                    String d = json_strings_one.getString(String.valueOf(3));
                                    String e = json_strings_one.getString(String.valueOf(4));
                                    String f = json_strings_one.getString(String.valueOf(5));

                                    ConsumptionDetails[z].add(new ModelChildren(a, b, c, d, e, f));
                                }
                                model.add(new Model(x, ConsumptionDetails[z]));
                                loading.setVisibility(View.INVISIBLE);
                            } catch (JSONException e) {

                            }
                        }

                        adapter = new CustomAdapter(ConsumptionDetailsActivity.this, model);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException ignored) {

                    }


                }, error -> {
            int error_code = error.networkResponse.statusCode;
            if (error_code == 503) {
                ArrayList[] ConsumptionDetails = new ArrayList[]{iphones, nexus, windows};
                String a = "";
                String b = "";
                String c = "";
                String d = "";
                String e = "";
                String f = "";
                ConsumptionDetails[0].add(new ModelChildren(a, b, c, d, e, f));
                model.add(new Model("Nessun consumo", ConsumptionDetails[0]));
                loading.setVisibility(View.INVISIBLE);
                adapter = new CustomAdapter(ConsumptionDetailsActivity.this, model);
                recyclerView.setAdapter(adapter);

            }

        });

        queue.add(getRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                super.onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
