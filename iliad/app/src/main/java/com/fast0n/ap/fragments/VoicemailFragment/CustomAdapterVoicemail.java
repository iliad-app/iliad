package com.fast0n.ap.fragments.VoicemailFragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ap.R;
import com.fast0n.ap.java.InputStreamVolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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


        holder.textView.setText(getContactName(c.num_tell));
        holder.textView1.setText(Html.fromHtml(c.date));

        if (c.date.equals("")) {
            holder.button.setVisibility(View.INVISIBLE);
            holder.button1.setVisibility(View.INVISIBLE);
        }

        holder.button.setOnClickListener(v -> {

            String url = site_url + "?deleteaudio=true&idaudio=" + c.id + "&token=" + c.token;


            RequestQueue queue = Volley.newRequestQueue(context);
            queue.getCache().clear();
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


        String requiredPermission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int checkVal = context.checkCallingOrSelfPermission(requiredPermission);

        if (checkVal == PackageManager.PERMISSION_GRANTED) {
            holder.button2.setVisibility(View.VISIBLE);
        }


        holder.button2.setOnClickListener(v -> {
            holder.button2.setEnabled(false);
            Toast.makeText(context, "Download in corso...", Toast.LENGTH_LONG).show();


            String mUrl = site_url + "?idaudio=" + c.id + "&token=" + c.token;


            File file = new File(Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name));


            if (!file.exists()) {
                file.mkdir();
            }


            InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, mUrl,
                    response -> {
                        try {
                            if (response != null) {


                                FileOutputStream outputStream;
                                outputStream = new FileOutputStream(file + File.separator + c.id + ".wav");
                                outputStream.write(response);
                                outputStream.close();
                                Toast.makeText(context, "Download completato", Toast.LENGTH_LONG).show();

                                holder.button2.setEnabled(true);


                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("audio/*");
                                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                share.putExtra(Intent.EXTRA_STREAM,
                                        Uri.parse(file + File.separator + c.id + ".wav"));
                                context.startActivity(Intent.createChooser(share, "Share voicemail"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
            }, null);
            RequestQueue mRequestQueue = Volley.newRequestQueue(context.getApplicationContext(), new HurlStack());
            mRequestQueue.add(request);


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

    private String getContactName(String number) {
        String name = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        ContentResolver contentResolver = context.getContentResolver();
        try {
            Cursor contactLookup = contentResolver.query(uri, null, null, null, null);
            try {
                if (contactLookup != null && contactLookup.getCount() > 0) {
                    contactLookup.moveToNext();
                    name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                } else {
                    name = number;
                }
            } finally {
                if (contactLookup != null) {
                    contactLookup.close();
                }
            }
        } catch (Exception ignored) {
            name = number;
        }

        return name;
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