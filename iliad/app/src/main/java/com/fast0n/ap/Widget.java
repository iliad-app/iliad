package com.fast0n.ap;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.fast0n.ap.java.GenerateToken;
import com.fast0n.ap.java.myDbAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Widget extends AppWidgetProvider {
    public static String CLOCK_WIDGET_UPDATE = "com.fast0n.ap.widget.8BITCLOCK_WIDGET_UPDATE", stringUser_numtell;
    private static myDbAdapter helper;
    private static String password, account;

    static void updateAppWidget(Context context,
                                AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        RequestOptions options = new RequestOptions().override(50, 50);


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
        account = settings.getString("account", null);


        try {
            String getAllData = helper.getAllData();
            String[] arrayData = getAllData.split("\n");
            for (String anArrayData : arrayData) {
                String onlyname = anArrayData.split("&")[0];
                String onlypassword = anArrayData.split("&")[1];

                if (onlyname.equals(account)) {
                    password = onlypassword;
                    stringUser_numtell = anArrayData.split("&")[2];
                    break;
                }
            }
            views.setViewVisibility(R.id.linearLayout, VISIBLE);
            views.setViewVisibility(R.id.textView, INVISIBLE);
        } catch (Exception ignored) {
            views.setTextViewText(R.id.textView, "Esegui il login...");
        }


        Intent iSetting = new Intent(context, LoginActivity.class);
        PendingIntent piSetting = PendingIntent.getActivity(context, 0, iSetting, 0);
        views.setOnClickPendingIntent(R.id.widget_click, piSetting);


        Intent intentUpdate = new Intent(context, Widget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);


        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context, appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.refresh, pendingUpdate);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID : ids) {
                updateAppWidget(context, appWidgetManager, appWidgetID);

            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;


        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            updateAppWidget(context, appWidgetManager, appWidgetId);
            final String site_url = context.getString(R.string.site_url);
            // String site_url = PreferenceManager.getDefaultSharedPreferences(context).getString("credit", null);

            loading(site_url, account, context, appWidgetManager, appWidgetId);


        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
    }


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000, createClockTickIntent(context));
    }

    private PendingIntent createClockTickIntent(Context context) {
        Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    private void loading(String site, String userid, Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Toast.makeText(context, "Widget aggiornato...", Toast.LENGTH_SHORT).show();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        final String token = GenerateToken.randomString(20);
        String url = (site + "login/?userid=" + userid + "&password=" + password + "&token=" + token).replaceAll("\\s+", "");
        RequestQueue login = Volley.newRequestQueue(context);
        JsonObjectRequest getRequestLogin = new JsonObjectRequest(Request.Method.GET, url, null,
                (JSONObject response) -> {

                    views.setTextViewText(R.id.user_numtell, stringUser_numtell);
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm",
                            Locale.ITALIAN);
                    String var = dateFormat.format(date);

                    views.setTextViewText(R.id.update, context.getString(R.string.updatedat) + " " + var);


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
                                        x = x.substring(0, x.length() - 2);
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
                                    views.setTextViewText(R.id.percentage, result1 + "%");

                                    appWidgetManager.updateAppWidget(appWidgetId, views);


                                } catch (JSONException ignored) {
                                }

                            }, error -> {
                    });
                    creditqueue.add(getRequestCredit);

                }, error1 -> {

            try {
                views.setViewVisibility(R.id.linearLayout, GONE);
            } catch (Exception ignored) {


            }


        });


        login.add(getRequestLogin);
    }
}