package com.fast0n.ipersonalarea.fragments.ServicesFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ipersonalarea.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class CustomAdapterServices extends RecyclerView.Adapter<CustomAdapterServices.MyViewHolder> {

    private final Context context;
    private final String token;
    private final List<DataServicesFragments> ServicesList;


    CustomAdapterServices(Context context, List<DataServicesFragments> ServicesList, String token) {
        this.context = context;
        this.ServicesList = ServicesList;
        this.token = token;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final DataServicesFragments c = ServicesList.get(position);
        holder.textView.setText(c.textView);


        final String site_url = context.getString(R.string.site_url) + context.getString(R.string.services);


        holder.imageView.setOnClickListener(v -> {
            switch (position) {
                case 1:
                case 2:
                case 3:
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.imageView.setEnabled(false);
                    String[] name_url = {"voicemail_roaming", "block_redirect", "absent_subscriber"};

                    String URL = context.getString(R.string.site_url) + context.getString(R.string.services) + "?info=true&type=" + name_url[position - 1] + "&token=" + token;
                    RequestQueue queue = Volley.newRequestQueue(context);

                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                            response -> {
                                try {

                                    JSONObject json_raw = new JSONObject(response.toString());
                                    String iliad = json_raw.getString("iliad");

                                    JSONObject json = new JSONObject(iliad);
                                    String title = json.getString("0");
                                    String description = json.getString("1");
                                    int isEnabled = json.getInt("2");

                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());


                                    alertDialog.setTitle(title);
                                    alertDialog.setMessage(description);

                                    if (isEnabled == 1) {
                                        alertDialog.setPositiveButton(
                                                "Attiva",
                                                (dialog, which) -> {
                                                    holder.toggle.setChecked(true);
                                                }
                                        );
                                    } else {
                                        alertDialog.setNegativeButton(
                                                "Disattiva",
                                                (dialog, which) -> {
                                                    holder.toggle.setChecked(false);
                                                }
                                        );
                                    }


                                    alertDialog.show();
                                    holder.progressBar.setVisibility(View.INVISIBLE);
                                    holder.imageView.setEnabled(true);


                                } catch (JSONException ignored) {
                                }

                            }, error -> {

                    });

                    queue.add(getRequest);


                    break;
                default:
                    break;

            }
        });

        if (c.toggle.equals("false"))
            holder.toggle.setChecked(false);
        else
            holder.toggle.setChecked(true);

        holder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                String url = site_url + "?change_services=true&update=" + c.name + "&token=" + token
                        + "&activate=0";
                request_options_services(url, holder.textView.getText() + " " + "disattivato");
            } else {

                String url = site_url + "?change_services=true&update=" + c.name + "&token=" + token
                        + "&activate=1";
                request_options_services(url, holder.textView.getText() + " " + "attivo");
            }

        });
    }

    private void request_options_services(String url, final String labelOn) {

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);
                        String string_response = json.getString("0");

                        if (string_response.equals("true")) {

                            Toasty.warning(context, labelOn, Toast.LENGTH_SHORT, true).show();
                        }

                    } catch (JSONException ignored) {
                    }

                }, error -> {

        });

        queue.add(getRequest);

    }

    @Override
    public int getItemCount() {
        return ServicesList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_options_services, parent, false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        Switch toggle;
        ImageView imageView;
        ProgressBar progressBar;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView1);
            toggle = view.findViewById(R.id.toggle);
            imageView = view.findViewById(R.id.imageView);
            progressBar = view.findViewById(R.id.progressBar);

        }
    }
}