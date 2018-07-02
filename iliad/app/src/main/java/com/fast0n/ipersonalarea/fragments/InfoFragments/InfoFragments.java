package com.fast0n.ipersonalarea.fragments.InfoFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ipersonalarea.ChangeEmailActivity;
import com.fast0n.ipersonalarea.ChangePasswordActivity;
import com.fast0n.ipersonalarea.ChargeActivity;
import com.fast0n.ipersonalarea.LoginActivity;
import com.fast0n.ipersonalarea.R;
import com.fast0n.ipersonalarea.java.RecyclerItemListener;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.github.ybq.android.spinkit.style.CubeGrid;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class InfoFragments extends Fragment {

    public InfoFragments() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_info, container, false);

        final ProgressBar loading;
        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();
        TextView offer;


        // java adresses
        loading = view.findViewById(R.id.progressBar);
        CubeGrid cubeGrid = new CubeGrid();
        loading.setIndeterminateDrawable(cubeGrid);
        cubeGrid.setColor(getResources().getColor(R.color.colorPrimary));
        offer = view.findViewById(R.id.offer);

        offer.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        final Bundle extras = getActivity().getIntent().getExtras();
        assert extras != null;
        final String password = extras.getString("password");
        final String token = extras.getString("token");

        final String site_url = getString(R.string.site_url) + getString(R.string.infomation);
        String url = site_url + "?info=true&token=" + token;

        getObject(site_url, url, context, view, token, password);

        return view;
    }

    private void getObject(String site_url, String url, final Context context, View view, final String token, final String password) {

        final ProgressBar loading;
        final RecyclerView recyclerView;
        TextView offer;
        final List<DataInfoFragments> infoList = new ArrayList<>();


        // java adresses
        offer = view.findViewById(R.id.offer);
        recyclerView = view.findViewById(R.id.recycler_view);
        loading = view.findViewById(R.id.progressBar);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemListener(context, recyclerView, new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View arg1, int position) {


                        switch (position) {
                            case 1:
                                TextView getNome = arg1.findViewById(R.id.textView3);
                                String type = getNome.getText().toString();
/*
                                if (type.equals("Manuale")){
                                    Intent intent1 = new Intent(context, ChargeActivity.class);
                                    intent1.putExtra("name", "Cambio metodo...");
                                    intent1.putExtra("price", "false");
                                    intent1.putExtra("token", token);
                                    startActivity(intent1);
                                }
                                */
                                break;
                            case 2:
                                Intent intent2 = new Intent(context, ChangeEmailActivity.class);
                                intent2.putExtra("password", password);
                                intent2.putExtra("token", token);
                                startActivity(intent2);
                                break;
                            case 3:
                                Intent intent = new Intent(context, ChangePasswordActivity.class);
                                intent.putExtra("password", password);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                break;
                            case 4:

                                RequestQueue queue = Volley.newRequestQueue(context);
                                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, site_url + "?puk=true&token=" + token, null,
                                        response -> {
                                            try {

                                                JSONObject json_raw = new JSONObject(response.toString());
                                                String iliad = json_raw.getString("iliad");

                                                JSONObject json = new JSONObject(iliad);
                                                String string = json.getString(String.valueOf(0));

                                                new MaterialStyledDialog.Builder(getContext())
                                                        .setStyle(Style.HEADER_WITH_TITLE)
                                                        .setTitle(string)
                                                        .show();


                                            } catch (JSONException e) {
                                                startActivity(new Intent(context, LoginActivity.class));
                                            }
                                        }, error -> startActivity(new Intent(context, LoginActivity.class)));

                                // add it to the RequestQueue
                                queue.add(getRequest);
                                break;
                            default:
                                Toasty.warning(context, getString(R.string.coming_soon), Toast.LENGTH_SHORT, true).show();
                        }

                    }

                    public void onLongClickItem(View v, int position) {
                    }
                }));

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);

                        for (int i = 0; i < json.length(); i++) {

                            String string = json.getString(String.valueOf(i));
                            JSONObject json_strings = new JSONObject(string);

                            try {
                                String a = json_strings.getString("0");
                                String b = json_strings.getString("1");
                                String c = json_strings.getString("2");
                                String d = json_strings.getString("3");
                                String e = json_strings.getString("4");
                                infoList.add(new DataInfoFragments(d, a, b, c, e));

                            } catch (Exception e) {
                                String a = json_strings.getString("0");
                                String b = json_strings.getString("1");
                                String c = json_strings.getString("2");
                                String d = json_strings.getString("3");

                                infoList.add(new DataInfoFragments(c, a, b, "", d));
                                CustomAdapterInfo ca = new CustomAdapterInfo(context, infoList);
                                recyclerView.setAdapter(ca);
                            }

                        }


                        offer.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                }, error -> startActivity(new Intent(context, LoginActivity.class)));

        // add it to the RequestQueue
        queue.add(getRequest);

    }

}
