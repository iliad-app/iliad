package com.fast0n.ipersonalarea.fragments.OptionsFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

public class CustomAdapterOptions extends RecyclerView.Adapter<CustomAdapterOptions.MyViewHolder> {

    private final Context context;
    private final String token;
    private final List<DataOptionsFragments> optionsList;

    CustomAdapterOptions(Context context, List<DataOptionsFragments> optionsList, String token) {
        this.context = context;
        this.optionsList = optionsList;
        this.token = token;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final DataOptionsFragments c = optionsList.get(position);
        holder.textView.setText(c.textView);

        holder.toggle.setChecked(!c.toggle.equals("false"));

        if (position == 0)
            holder.toggle.setEnabled(false);

        holder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final String site_url = context.getString(R.string.site_url) + context.getString(R.string.options);
                String url = site_url + "?change_options=true&update=" + c.name + "&token=" + token
                        + "&activate=" + (isChecked ? 1 : 0);
                request_options_services(url, holder.textView.getText() + " " + (isChecked ? "attivo" : "disattivato"));
            }

            private void request_options_services(String url, String labelOn) {

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

        });


    }

    @Override
    public int getItemCount() {
        return optionsList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_options_services, parent, false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final Switch toggle;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView1);
            toggle = view.findViewById(R.id.toggle);

        }
    }
}