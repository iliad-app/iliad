package com.fast0n.ap.java;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ap.LoginActivity;
import com.fast0n.ap.R;


public class DialogError {
    private final String token = GenerateToken.randomString(10);
    Context context;
    String descriptionError;

    public DialogError(Context con, String s) {
        this.context = con;
        this.descriptionError = s;
    }

    public void alertbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.alertbox_title)
                .setMessage(R.string.alertbox_message)
                .setCancelable(false)
                .setPositiveButton(R.string.alertbox_posbtn, (dialog, id) -> {
                    //Go home
                    context.startActivity(new Intent(context, LoginActivity.class));
                })
                .setNegativeButton(R.string.alertbox_negbtn, (dialog, id) -> {

                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(context);

                    final String site_url = context.getString(R.string.site_url) + context.getString(R.string.support);


                    String url = "?title=" + "Segnalazione App Iliad (ID: " + token.toUpperCase() +
                            ")&message=" +
                            descriptionError +
                            "&screen=" +
                            context;

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, site_url + url,
                            response -> {
                                // Display the first 500 characters of the response string.
                                context.startActivity(new Intent(context, LoginActivity.class));

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //textView.setText("That didn't work!");
                        }
                    });

                    queue.add(stringRequest);


                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}