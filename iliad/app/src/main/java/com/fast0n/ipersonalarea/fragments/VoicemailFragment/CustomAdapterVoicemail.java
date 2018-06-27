package com.fast0n.ipersonalarea.fragments.VoicemailFragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

public class CustomAdapterVoicemail extends RecyclerView.Adapter<CustomAdapterVoicemail.MyViewHolder> {

    private final List<DataVoicemailFragments> conditionList;
    private final Context context;
    private MediaPlayer mediaPlayer;

    private boolean playPause;
    private boolean initialStage = true;


    CustomAdapterVoicemail(List<DataVoicemailFragments> conditionList, Context context) {
        this.conditionList = conditionList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataVoicemailFragments c = conditionList.get(position);
        final String site_url = context.getString(R.string.site_url) + context.getString(R.string.voicemail);


        holder.textView.setText(c.num_tell);
        holder.textView1.setText(Html.fromHtml(c.date));

        if (c.date.equals("")) {
            holder.button.setVisibility(View.INVISIBLE);
            holder.button1.setVisibility(View.INVISIBLE);
        }

        holder.button.setOnClickListener(v -> {

            String url = site_url + "?deleteaudio=true&idaudio=" + c.id + "&token=" + c.token;


            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {

                            JSONObject json_raw = new JSONObject(response.toString());
                            String iliad = json_raw.getString("iliad");

                            JSONObject json = new JSONObject(iliad);
                            String string_json1 = json.getString("1");


                            Toasty.success(context, string_json1, Toast.LENGTH_LONG,
                                    true).show();
                            int newPosition = holder.getAdapterPosition();
                            conditionList.remove(newPosition);
                            notifyItemRemoved(newPosition);
                            notifyItemRangeChanged(newPosition, conditionList.size());

                        } catch (JSONException ignored) {
                        }

                    }, error -> {

            });

            queue.add(getRequest);
        });


        holder.button2.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://avatars2.githubusercontent.com/u/5260133?s=88&v=4");

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            Intent chooserIntent = Intent.createChooser(shareIntent, "Pippo");
            context.startActivity(chooserIntent);
        });


        holder.button1.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                if (!playPause) {
                    holder.button1.setBackgroundResource(R.drawable.ic_pause);


                    if (initialStage) {

                        new Player().execute(site_url + "?idaudio=" + c.id + "&token=" + c.token);
                    } else {
                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }

                    playPause = true;

                } else {
                    holder.button1.setBackgroundResource(R.drawable.ic_play);

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();

                    }


                    playPause = false;
                }

            }

            class Player extends AsyncTask<String, Void, Boolean> {
                @Override
                protected Boolean doInBackground(String... strings) {
                    Boolean prepared;

                    try {
                        mediaPlayer.setDataSource(strings[0]);
                        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                            initialStage = true;
                            playPause = false;
                            holder.button1.setBackgroundResource(R.drawable.ic_play);
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                        });

                        mediaPlayer.prepare();
                        prepared = true;

                    } catch (Exception e) {
                        prepared = false;
                    }

                    return prepared;
                }


                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);


                    mediaPlayer.start();
                    initialStage = false;
                }

            }

        });


    }


    @Override
    public int getItemCount() {
        return conditionList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_voicemail, parent, false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final TextView textView1;
        final ImageButton button;
        final ImageButton button1, button2;


        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
            textView1 = view.findViewById(R.id.textView1);
            button1 = view.findViewById(R.id.button1);
            button2 = view.findViewById(R.id.button2);
            button = view.findViewById(R.id.button);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        }
    }
}