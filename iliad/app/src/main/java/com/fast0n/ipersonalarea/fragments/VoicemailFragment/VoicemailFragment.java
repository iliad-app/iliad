package com.fast0n.ipersonalarea.fragments.VoicemailFragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class VoicemailFragment extends Fragment {


    public VoicemailFragment() {
    }

    private boolean isEmail(String email) {
        String expression = "^[\\w.]+@([\\w]+\\.)+[A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_voicemail, container, false);

        final ProgressBar loading;
        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();
        CardView cardView1;


        // java adresses
        loading = view.findViewById(R.id.progressBar);
        CubeGrid cubeGrid = new CubeGrid();
        cubeGrid.setColor(getResources().getColor(R.color.colorPrimary));
        loading.setIndeterminateDrawable(cubeGrid);
        cardView1 = view.findViewById(R.id.cardView1);

        cardView1.setVisibility(View.INVISIBLE);


        loading.setVisibility(View.VISIBLE);

        final Bundle extras = getActivity().getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token");

        final String site_url = getString(R.string.site_url) + getString(R.string.voicemail);
        String url = site_url + "?voicemail=true&token=" + token;
        String url1 = site_url + "?voicemailoptions=true&token=" + token;
        String url2 = site_url + "?voicemailreport=true&token=" + token;


        getObject(url, url1, url2, context, view, token);

        return view;
    }

    private void getObject(String url, String url1, String url2, Context context, View view, String token) {

        final ProgressBar loading;
        final RecyclerView recyclerView, recyclerView1, recyclerView2, recyclerView3;
        final List<DataVoicemailFragments> infomail = new ArrayList<>();
        final List<DataCustomizationFragments> infoList = new ArrayList<>();
        final List<DataNotificationFragments> infoList1 = new ArrayList<>();
        CardView cardView1;
        EditText editText;
        Spinner spinner;
        ImageButton button;

        TextView textvoicemail, customization, notification;

        // java adresses
        button = view.findViewById(R.id.button);
        cardView1 = view.findViewById(R.id.cardView1);
        editText = view.findViewById(R.id.editText);
        textvoicemail = view.findViewById(R.id.textView);
        customization = view.findViewById(R.id.textView1);
        notification = view.findViewById(R.id.textView2);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView1 = view.findViewById(R.id.recycler_view1);
        recyclerView2 = view.findViewById(R.id.recycler_view2);
        recyclerView3 = view.findViewById(R.id.recycler_view3);
        spinner = view.findViewById(R.id.spinner);


        String list[] = {"Notifica inviata via email", "File audio inviato in allegato"};


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);


        button.setOnClickListener(v -> {
            if (isEmail(editText.getText().toString())) {
                String montant = spinner.getSelectedItem().toString();

                RequestQueue queue = Volley.newRequestQueue(context);
                final String site_url = getString(R.string.site_url) + getString(R.string.voicemail);

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, site_url + "?email=" + editText.getText().toString() + "&action=add&type=" + montant.replace("Notifica inviata via email", "report").replace("File audio inviato in allegato", "attachment") + "&token=" + token, null,
                        response -> {
                            try {

                                JSONObject json_raw = new JSONObject(response.toString());
                                String iliad = json_raw.getString("iliad");

                                JSONObject json = new JSONObject(iliad);

                            } catch (JSONException ignored) {
                            }

                        }, error -> {

                });

                queue.add(getRequest);

            } else {
                Toasty.warning(context, getString(R.string.email_wrong), Toast.LENGTH_LONG,
                        true).show();
            }
        });

        loading = view.findViewById(R.id.progressBar);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);

        LinearLayoutManager llm1 = new LinearLayoutManager(context);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(llm1);

        LinearLayoutManager llm2 = new LinearLayoutManager(context);
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(llm2);

        LinearLayoutManager llm3 = new LinearLayoutManager(context);
        llm3.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(llm3);


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
                        textvoicemail.setText(stringCredit);

                        for (int i = 1; i < json.length(); i++) {

                            String string = json.getString(String.valueOf(i));
                            JSONObject json_strings = new JSONObject(string);

                            String a = json_strings.getString("0");
                            try {
                                String b = json_strings.getString("1");
                                String c = json_strings.getString("2");
                                infomail.add(new DataVoicemailFragments(a, b, c, token));
                            } catch (Exception ignored) {
                                infomail.add(new DataVoicemailFragments(a, "", "0", token));
                            }

                            CustomAdapterVoicemail ca = new CustomAdapterVoicemail(infomail, context);
                            recyclerView.setAdapter(ca);
                        }

                        textvoicemail.setVisibility(View.VISIBLE);


                    } catch (JSONException e) {
                        startActivity(new Intent(context, LoginActivity.class));
                    }

                }, error -> {

        });

        queue.add(getRequest);


        RequestQueue queue1 = Volley.newRequestQueue(context);
        JsonObjectRequest getRequest1 = new JsonObjectRequest(Request.Method.GET, url1, null,
                response -> {
                    try {
                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);

                        String string1 = json.getString("0");
                        JSONObject json_strings1 = new JSONObject(string1);
                        String stringCredit = json_strings1.getString("0");
                        customization.setText(stringCredit);

                        for (int i = 1; i < json.length(); i++) {

                            String string = json.getString(String.valueOf(i));
                            JSONObject json_strings = new JSONObject(string);

                            String a = json_strings.getString("0");
                            String b = json_strings.getString("2");
                            String c = json_strings.getString("3");
                            infoList.add(new DataCustomizationFragments(a, b, c));
                            customization.setVisibility(View.VISIBLE);

                        }

                        CustomAdapterCustomization ca = new CustomAdapterCustomization(context, infoList, token);
                        recyclerView1.setAdapter(ca);
                        notification.setVisibility(View.VISIBLE);


                    } catch (JSONException e) {
                        startActivity(new Intent(context, LoginActivity.class));
                    }

                }, error -> {

        });

        queue1.add(getRequest1);


        RequestQueue queue2 = Volley.newRequestQueue(context);
        JsonObjectRequest getRequest2 = new JsonObjectRequest(Request.Method.GET, url2, null,
                response -> {
                    try {
                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");


                        try {
                            JSONObject json = new JSONObject(iliad);

                            String string1 = json.getString("0");
                            JSONObject json_strings1 = new JSONObject(string1);
                            String stringCredit = json_strings1.getString("0");
                            notification.setText(stringCredit);


                            for (int i = 1; i < json.length(); i++) {

                                String string = json.getString(String.valueOf(i));
                                JSONObject json_strings = new JSONObject(string);


                                String a = json_strings.getString("0");
                                String d = json_strings.getString("3");

                                try {
                                    String b = json_strings.getString("1");
                                    String c = json_strings.getString("2");
                                    infoList1.add(new DataNotificationFragments(a, b, c, d));
                                } catch (Exception ignored) {
                                    infoList1.add(new DataNotificationFragments(a, "", "0", d));
                                }


                            }

                            CustomAdapterNotification ca = new CustomAdapterNotification(context, infoList1, token);
                            recyclerView2.setAdapter(ca);
                            loading.setVisibility(View.INVISIBLE);
                            cardView1.setVisibility(View.VISIBLE);
                        } catch (Exception ignored) {
                        }

                    } catch (JSONException e) {
                        startActivity(new Intent(context, LoginActivity.class));
                    }

                }, error -> {

        });

        queue2.add(getRequest2);


    }

}
