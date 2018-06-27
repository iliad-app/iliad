package com.fast0n.ipersonalarea;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.fast0n.ipersonalarea.java.GenerateToken;
import com.fast0n.ipersonalarea.java.myDbAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class Widget extends AppWidgetProvider {
    private myDbAdapter helper;
    private String password;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            RequestOptions options = new RequestOptions().override(50, 50);
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget);


            Intent iSetting = new Intent(context, LoginActivity.class);
            PendingIntent piSetting = PendingIntent.getActivity(context, 0, iSetting, 0);
            views.setOnClickPendingIntent(R.id.widget_click, piSetting);


            AppWidgetTarget img = new AppWidgetTarget(context, R.id.img, views, appWidgetId) {
            };
            Glide.with(context.getApplicationContext()).asBitmap()
                    .load("http://android12.altervista.org/res/ic_call.png").apply(options).into(img);


            AppWidgetTarget img2 = new AppWidgetTarget(context, R.id.img2, views, appWidgetId) {
            };
            Glide.with(context.getApplicationContext()).asBitmap()
                    .load("http://android12.altervista.org/res/ic_sms.png").apply(options).into(img2);


            AppWidgetTarget img3 = new AppWidgetTarget(context, R.id.img3, views, appWidgetId) {
            };
            Glide.with(context.getApplicationContext()).asBitmap()
                    .load("http://android12.altervista.org/res/ic_mms.png").apply(options).into(img3);

            helper = new myDbAdapter(context);
            // java adresses
            SharedPreferences settings = context.getSharedPreferences("sharedPreferences", 0);

            // prendere le SharedPreferences
            String account = settings.getString("account", null);

            final String site_url = context.getString(R.string.site_url);

            String getAllData = helper.getAllData();
            String[] arrayData = getAllData.split("\n");
            for (String anArrayData : arrayData) {
                String onlyname = anArrayData.split("&")[0];
                String onlypassword = anArrayData.split("&")[1];
                if (onlyname.equals(account)) {
                    password = onlypassword;
                    break;
                }
            }


            Intent intent1 = new Intent(context, Widget.class);
            intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context,
                    0, intent1, 0);

            views.setOnClickPendingIntent(R.id.button, pendingIntent1);
            views.setOnClickPendingIntent(R.id.button1, pendingIntent1);


            if (password == null) {
                views.setViewVisibility(R.id.login, VISIBLE);
                views.setViewVisibility(R.id.textView, INVISIBLE);
                views.setViewVisibility(R.id.linearLayout, INVISIBLE);

            } else {
                views.setViewVisibility(R.id.linearLayout, VISIBLE);
                views.setViewVisibility(R.id.textView, INVISIBLE);
                views.setViewVisibility(R.id.login, INVISIBLE);

                loading(site_url, account, password, context, views, appWidgetIds, appWidgetManager, appWidgetId);


            }


        }

    }

    private void loading(String site, String userid, String password, Context context, RemoteViews views, int[] appWidgetIds, AppWidgetManager appWidgetManager, int appWidgetId) {
        final String token = GenerateToken.randomString(20);
        String url = (site + "login/?userid=" + userid + "&password=" + password + "&token=" + token).replaceAll("\\s+", "");
        RequestQueue login = Volley.newRequestQueue(context);
        JsonObjectRequest getRequestLogin = new JsonObjectRequest(Request.Method.GET, url, null,
                (JSONObject response) -> {


                    String stringUser_numtell = helper.getPhoneNumber().split("\n")[0];
                    views.setTextViewText(R.id.user_numtell, stringUser_numtell);

                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm",
                            Locale.ITALIAN);
                    String var = dateFormat.format(date);

                    views.setTextViewText(R.id.update, context.getString(R.string.updatedat) + " " + var);


                    Intent intent1 = new Intent(context, Widget.class);
                    intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                    context.sendBroadcast(intent1);


                    String url1 = site + "credit/?credit=true&token=" + token;

                    RequestQueue creditqueue = Volley.newRequestQueue(context);
                    JsonObjectRequest getRequestCredit = new JsonObjectRequest(Request.Method.GET, url1, null,
                            response1 -> {

                                try {

                                    JSONObject json_raw = new JSONObject(response1.toString());
                                    String iliad = json_raw.getString("iliad");
                                    JSONObject json = new JSONObject(iliad);

                                    String stringCredit = json.getString("0");
                                    JSONObject json_Credit = new JSONObject(stringCredit);
                                    String credit = json_Credit.getString("0");
                                    views.setTextViewText(R.id.credit, credit.split("&")[0]);

                                    String stringCall = json.getString("1");
                                    JSONObject json_Call = new JSONObject(stringCall);
                                    String call = json_Call.getString("0");
                                    views.setTextViewText(R.id.call, call.replace("Chiamate: ", ""));

                                    String stringSms = json.getString("2");
                                    JSONObject json_Sms = new JSONObject(stringSms);
                                    String sms = json_Sms.getString("0");
                                    views.setTextViewText(R.id.sms, sms);

                                    String stringGb = json.getString("3");
                                    JSONObject json_Gb = new JSONObject(stringGb);
                                    String gb = json_Gb.getString("0");
                                    views.setTextViewText(R.id.gb, gb);

                                    String x = gb.split("/")[0];
                                    String y = gb.split("/")[1];

                                    Double f = null;
                                    Double e;


                                    if (x.contains("GB")) {
                                        x = x.substring(0, x.length() - 3);
                                        e = Double.parseDouble(x.replace(",", ".")) * 1000;


                                    } else {
                                        x = x.substring(0, x.length() - 3);
                                        e = Double.parseDouble(x.replace(",", "."));

                                    }
                                    if (y.contains("GB")) {
                                        y = y.substring(0, y.length() - 2);
                                        f = Double.parseDouble(y.replace(",", ".")) * 1000;

                                    }

                                    Double ef = (e / f) * 100;
                                    int result1 = ef.intValue();


                                    views.setProgressBar(R.id.progressbar, 100, result1, false);

                                    String stringMms = json.getString("4");
                                    JSONObject json_Mms = new JSONObject(stringMms);
                                    String mms = json_Mms.getString("0");
                                    views.setTextViewText(R.id.mms, mms);
                                    views.setTextViewText(R.id.percentage, String.valueOf(result1) + "%");

                                    appWidgetManager.updateAppWidget(appWidgetId, views);


                                } catch (JSONException ignored) {
                                }

                            }, error -> {
                    });
                    creditqueue.add(getRequestCredit);

                }, error1 -> {

            try {
                loading(site, userid, password, context, views, appWidgetIds, appWidgetManager, appWidgetId);
                views.setViewVisibility(R.id.linearLayout, GONE);
            } catch (Exception ignored) {


            }


        });


        Handler handler = new Handler();
        handler.postDelayed(() -> login.add(getRequestLogin), 30000);


    }
}
