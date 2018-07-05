package com.fast0n.ipersonalarea.fragments.VoicemailFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class CustomAdapterNotification extends RecyclerView.Adapter<CustomAdapterNotification.MyViewHolder> {

    private final Context context;
    private final String token;
    private final List<DataNotificationFragments> modelList;

    CustomAdapterNotification(Context context, List<DataNotificationFragments> modelList, String token) {
        this.context = context;
        this.modelList = modelList;
        this.token = token;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataNotificationFragments c = modelList.get(position);
        holder.textView.setText(c.textView);
        holder.textView1.setText(c.textView1);
        holder.textView2.setText(c.textView2);
        holder.textView3.setText(c.textView3);


        holder.button.setOnClickListener(v -> {
            final String site_url = context.getString(R.string.site_url) + context.getString(R.string.voicemail);

            String url = site_url + "?email=" + c.textView1 + "&action=delete&token=" + token;


            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {

                            JSONObject json_raw = new JSONObject(response.toString());
                            String iliad = json_raw.getString("iliad");

                            JSONObject json = new JSONObject(iliad);
                            String string_json1 = json.getString("0");


                            Toasty.success(context, Html.fromHtml(string_json1), Toast.LENGTH_LONG,
                                    true).show();

                            int newPosition = holder.getAdapterPosition();
                            modelList.remove(newPosition);
                            notifyItemRemoved(newPosition);
                            notifyItemRangeChanged(newPosition, modelList.size());


                        } catch (JSONException ignored) {
                        }

                    }, error -> {

            });

            queue.add(getRequest);
        });


    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_voicemail_notification, parent, false);
        return new MyViewHolder(v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final TextView textView1;
        final TextView textView2;
        final TextView textView3;
        final ImageButton button;

        MyViewHolder(View view) {
            super(view);
            button = view.findViewById(R.id.button);
            textView = view.findViewById(R.id.textView);
            textView1 = view.findViewById(R.id.textView1);
            textView2 = view.findViewById(R.id.textView2);
            textView3 = view.findViewById(R.id.textView3);
        }
    }
}
