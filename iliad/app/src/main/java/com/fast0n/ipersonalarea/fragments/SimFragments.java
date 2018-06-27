package com.fast0n.ipersonalarea.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ipersonalarea.LoginActivity;
import com.fast0n.ipersonalarea.R;
import com.github.ybq.android.spinkit.style.CubeGrid;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SimFragments extends Fragment {

    public SimFragments() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sim, container, false);

        final ProgressBar loading;
        final CardView cardView1, cardView2, cardView3;
        final Button btn_activatesim;
        final EditText edt_iccid;
        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        // java adresses
        loading = view.findViewById(R.id.progressBar);
        CubeGrid cubeGrid = new CubeGrid();
        loading.setIndeterminateDrawable(cubeGrid);
        cubeGrid.setColor(getResources().getColor(R.color.colorPrimary));
        btn_activatesim = view.findViewById(R.id.btn_activatesim);
        edt_iccid = view.findViewById(R.id.edt_iccid);
        cardView1 = view.findViewById(R.id.cardView1);
        cardView2 = view.findViewById(R.id.cardView2);
        cardView3 = view.findViewById(R.id.cardView3);

        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        final Bundle extras = getActivity().getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token");

        final String site_url = getString(R.string.site_url) + getString(R.string.sim);
        String url = site_url + "?activation_sim=true&token=" + token;

        getObject(url, context, view);

        settings = context.getApplicationContext().getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();
        editor.apply();

        btn_activatesim.setOnClickListener(v -> {
            if (edt_iccid.getText().toString().length() == 19)
                activateSim(site_url + "?iccid=" + edt_iccid.getText().toString() + "&token=" + token, context);
            else
                Toasty.error(context, getString(R.string.error_iccid), Toast.LENGTH_SHORT).show();
        });

        edt_iccid.setOnClickListener(v -> updateKeyboardStatusText(KeyboardVisibilityEvent.isKeyboardVisible(getActivity()), view));

        return view;
    }

    private void activateSim(String url, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);
                        String stringSim = json.getString("sim");

                        JSONObject json_sim = new JSONObject(stringSim);
                        String toast = json_sim.getString("0");
                        String sim_state = json_sim.getString("1");

                        if (sim_state.equals("false"))
                            Toasty.warning(context, toast, Toast.LENGTH_LONG, true).show();

                        else {
                            Toasty.success(context, toast, Toast.LENGTH_LONG, true).show();
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);

                        }
                    } catch (JSONException e) {
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                }, error -> startActivity(new Intent(context, LoginActivity.class)));

        // add it to the RequestQueue
        queue.add(getRequest);

    }

    private void getObject(String url, final Context context, View view) {

        final ProgressBar loading;
        final CardView cardView1, cardView2, cardView3;
        final TextView tvvalidation, tvorder_date, tvdate, tvtracking, tvshipping, tvorder_shipped, tvactivation,
                tvtitle_activation, tvoffer;
        final EditText edt_iccid;
        final Button btn_activatesim;

        // java adresses
        tvvalidation = view.findViewById(R.id.validation);
        tvorder_date = view.findViewById(R.id.order_date);
        tvdate = view.findViewById(R.id.date);
        tvshipping = view.findViewById(R.id.shipping);
        tvtracking = view.findViewById(R.id.tracking);
        tvorder_shipped = view.findViewById(R.id.order_shipped);
        tvactivation = view.findViewById(R.id.activation);
        tvtitle_activation = view.findViewById(R.id.title_activation);
        tvoffer = view.findViewById(R.id.offer);
        edt_iccid = view.findViewById(R.id.edt_iccid);
        btn_activatesim = view.findViewById(R.id.btn_activatesim);
        cardView1 = view.findViewById(R.id.cardView1);
        cardView2 = view.findViewById(R.id.cardView2);
        cardView3 = view.findViewById(R.id.cardView3);

        loading = view.findViewById(R.id.progressBar);
        loading.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);
                        String stringValidation = json.getString("validation");
                        String stringShipping = json.getString("shipping");
                        String stringSim = json.getString("sim");

                        JSONObject json_validation = new JSONObject(stringValidation);
                        String validation = json_validation.getString("0");
                        String order_date = json_validation.getString("1");
                        String date = json_validation.getString("2");

                        tvvalidation.setText(validation);
                        tvorder_date.setText(order_date);
                        tvdate.setText(date);

                        JSONObject json_shipping = new JSONObject(stringShipping);
                        String shipping = json_shipping.getString("0");
                        String order_shipped = json_shipping.getString("1");
                        String tracking = json_shipping.getString("2");


                        tvshipping.setText(shipping);
                        tvorder_shipped.setText(order_shipped);
                        tvtracking.setText(tracking);

                        JSONObject json_sim = new JSONObject(stringSim);
                        String activation = json_sim.getString("0");
                        String title_activation = json_sim.getString("1");
                        String response_sim = json_sim.getString("2");
                        String offer = json_sim.getString("3");

                        tvoffer.setText(offer);
                        tvactivation.setText(activation);
                        tvtitle_activation.setText(title_activation);


                        if (response_sim.equals("true")) {
                            tvtitle_activation.setTextColor(getResources().getColor(R.color.colorPrimary));
                            edt_iccid.setVisibility(View.GONE);
                            btn_activatesim.setVisibility(View.GONE);
                        }


                        try {
                            String tracking_url = json_shipping.getString("3");
                            cardView3.setVisibility(View.VISIBLE);
                            cardView3.setOnClickListener(v -> {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(tracking_url));
                                startActivity(i);
                            });
                        } catch (Exception ignored) {
                            cardView3.setVisibility(View.GONE);
                        }


                        cardView1.setVisibility(View.VISIBLE);
                        cardView2.setVisibility(View.VISIBLE);

                        loading.setVisibility(View.INVISIBLE);

                    } catch (JSONException ignored) {
                    }
                }, error -> startActivity(new Intent(context, LoginActivity.class)));

        // add it to the RequestQueue
        queue.add(getRequest);

    }

    private void updateKeyboardStatusText(boolean isOpen, View view) {

        final CardView cardView3;

        // java adresses
        cardView3 = view.findViewById(R.id.cardView3);

        if (isOpen) {
            cardView3.setVisibility(View.INVISIBLE);

        } else {
            cardView3.setVisibility(View.VISIBLE);
        }
    }

}
