package com.fast0n.ipersonalarea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.fast0n.ipersonalarea.java.GenerateToken;
import com.fast0n.ipersonalarea.java.myDbAdapter;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    TextView textView2;
    int i;
    private LoadingButton btn_login;
    private EditText edt_id;
    private EditText edt_password;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private CheckBox checkBox;
    myDbAdapter helper;
    String token = GenerateToken.randomString(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(toolbar.getTitle());
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // java adresses
        btn_login = findViewById(R.id.btn_login);
        edt_id = findViewById(R.id.edt_id);
        edt_password = findViewById(R.id.edt_password);
        checkBox = findViewById(R.id.checkBox);
        textView2 = findViewById(R.id.textView2);

        helper = new myDbAdapter(this);


        textView2.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgetActivity.class)));


        settings = getSharedPreferences("sharedPreferences", 0);
        String checkbox_preference = settings.getString("checkbox", null);
        editor = settings.edit();
        editor.apply();

        if (checkbox_preference != null && checkbox_preference.equals("true")) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);


        }

        settings = getSharedPreferences("sharedPreferences", 0);
        String alert = settings.getString("alert", null);
        editor = settings.edit();
        editor.apply();


        if (alert == null) {

            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

            String site_url = getString(R.string.site_url) + getString(R.string.alert);

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, site_url, null,
                    response -> {
                        try {

                            JSONObject json_raw = new JSONObject(response.toString());
                            String iliad = json_raw.getString("iliad");

                            JSONObject json = new JSONObject(iliad);
                            String string_response = json.getString("0");

                            new MaterialStyledDialog.Builder(this)
                                    .setTitle(R.string.warning)
                                    .setDescription(Html.fromHtml(string_response))
                                    .setScrollable(true)
                                    .setStyle(Style.HEADER_WITH_TITLE)
                                    .setPositiveText(R.string.read)
                                    .setCancelable(false)
                                    .onPositive((dialog, which) -> {
                                        settings = getSharedPreferences("sharedPreferences", 0);
                                        editor = settings.edit();
                                        editor.putString("alert", "1");
                                        editor.apply();
                                    }).setScrollable(true, 10)
                                    .show();

                        } catch (JSONException ignored) {
                        }

                    }, error -> {
            });
            queue.add(getRequest);

        }


        btn_login.setOnClickListener(v -> {
            View view = this.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (isOnline() && edt_id.getText().toString().length() != 0
                    && edt_password.getText().toString().length() != 0) {
                btn_login.startLoading(); //start loading

                String userid1 = edt_id.getText().toString();
                String password1 = edt_password.getText().toString();
                String token = GenerateToken.randomString(20);

                byte[] encodeValue = Base64.encode(password1.getBytes(), Base64.DEFAULT);
                String npassword = new String(encodeValue);

                String site_url = getString(R.string.site_url) + getString(R.string.login);
                String url = site_url + "?userid=" + userid1 + "&password=" + npassword.replaceAll("\\s+", "") + "&token=" + token;
                getObject(url, token, npassword.replaceAll("\\s+", ""));
                btn_login.setEnabled(false);

            } else {
                btn_login.setEnabled(true);
                Toasty.error(LoginActivity.this, getString(R.string.errorconnection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getObject(String url, String token, String password) {
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        CustomPriorityRequest customPriorityRequest = new CustomPriorityRequest(
                Request.Method.GET, url, null,
                response -> {

                    try {
                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");
                        JSONObject json = new JSONObject(iliad);

                        String stringVersion = json.getString("version");
                        if (BuildConfig.VERSION_CODE < Integer.parseInt(stringVersion)) {
                            Intent intent = new Intent(this, ErrorConnectionActivity.class);
                            intent.putExtra("errorAPI", "true");
                            startActivity(intent);
                            finish();
                        }

                        String user_id = json.getString("user_id");
                        btn_login.loadingSuccessful();

                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("userid", user_id);
                            byte[] encodeValue = Base64.encode(password.getBytes(), Base64.DEFAULT);
                            String npassword = new String(encodeValue);
                            intent.putExtra("password", npassword);
                            intent.putExtra("token", token);

                            if (checkBox.isChecked()) {

                                // Inserire i dati su DB
                                helper.insertData(edt_id.getText().toString(), password);


                                settings = getSharedPreferences("sharedPreferences", 0);
                                editor = settings.edit();
                                editor.putString("checkbox", "true");
                                editor.apply();

                            } else {
                                settings = getSharedPreferences("sharedPreferences", 0);
                                editor = settings.edit();
                                editor.putString("checkbox", "false");
                                editor.apply();
                            }
                            startActivity(intent);

                        }, 1000);


                    } catch (JSONException ignored) {
                    }

                },
                error -> {


                    try {
                        int error_code = error.networkResponse.statusCode;

                        if (error_code == 503) {
                            Toasty.warning(LoginActivity.this, getString(R.string.error_login), Toast.LENGTH_LONG, true)
                                    .show();
                            btn_login.loadingFailed();
                            btn_login.setEnabled(true);
                        }
                    } catch (Exception ignored) {
                        if (i <= 20) {
                            getObject(url, token, password);
                            i++;
                        } else {
                            Toasty.warning(LoginActivity.this, getString(R.string.error_login), Toast.LENGTH_LONG, true)
                                    .show();
                            btn_login.loadingFailed();

                        }
                    }
                });

        customPriorityRequest.setPriority(Request.Priority.IMMEDIATE);
        queue.add(customPriorityRequest);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return Objects.requireNonNull(cm).getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();

    }


}
