package com.fast0n.ap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.fast0n.ap.java.GenerateToken;
import com.fast0n.ap.java.myDbAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private final String token = GenerateToken.randomString(20);
    TextView mTitle, textView2;
    private int i;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private myDbAdapter helper;
    private String account, checkbox_preference, theme;
    private LoadingButton btn_login;
    private EditText edt_id, edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();
        theme = settings.getString("toggleTheme", null);
        try {
            if (theme.equals("0"))
                setTheme(R.style.AppTheme);
            else
                setTheme(R.style.DarkTheme);
        } catch (Exception ignored) {
            editor.putString("toggleTheme", "0");
            editor.apply();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(toolbar.getTitle());
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // java adresses
        btn_login = findViewById(R.id.btn_login);
        edt_id = findViewById(R.id.edt_id);
        edt_password = findViewById(R.id.edt_password);
        textView2 = findViewById(R.id.textView2);
        helper = new myDbAdapter(this);

        // recupero password
        textView2.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgetActivity.class)));

        // prendere le SharedPreferences
        checkbox_preference = settings.getString("checkbox", null);
        account = settings.getString("account", null);
        editor.apply();

        // se la checkbox è uguale a true va alla home activity
        if (checkbox_preference != null && checkbox_preference.equals("true"))
            startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra("token", token).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));


        // azione tasto login
        btn_login.setOnClickListener(v -> {

            // nascondere la tastiera
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if (isOnline()) {
                if (edt_id.getText().toString().length() != 0
                        && edt_password.getText().toString().length() != 0) {
                    btn_login.startLoading(); //start loading

                    String userid1 = edt_id.getText().toString();
                    String password1 = edt_password.getText().toString();

                    byte[] encodeValue = Base64.encode(password1.getBytes(), Base64.DEFAULT);
                    String npassword = new String(encodeValue);

                    String site_url = getString(R.string.site_url) + getString(R.string.login);
                    String url = site_url + "?userid=" + userid1 + "&password=" + npassword.replaceAll("\\s+", "") + "&token=" + token;


                    String getAllData = helper.getAllData();
                    String[] arrayData = getAllData.split("\n");

                    boolean found = false;


                    for (String anArrayData : arrayData) {
                        String onlyname = anArrayData.split("&")[0];
                        if (onlyname.equals(userid1)) {
                            found = true;
                            btn_login.loadingFailed();
                            Toasty.info(LoginActivity.this, getString(R.string.account_already_use), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                    if (!found) {
                        getObject(url, token, npassword.replaceAll("\\s+", ""));
                        btn_login.setEnabled(false);

                    }


                } else {
                    btn_login.setEnabled(true);
                    Toasty.error(LoginActivity.this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                }
            } else {
                btn_login.setEnabled(true);
                Toasty.error(LoginActivity.this, getString(R.string.errorconnection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getObject(String url, String token, String password) {
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);


        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");
                        JSONObject json = new JSONObject(iliad);

                        // se la versione non è supportata vai alla schermata di errore
                        String stringVersion = json.getString("version");
                        if (BuildConfig.VERSION_CODE < Integer.parseInt(stringVersion)) {
                            Intent intent = new Intent(this, ErrorConnectionActivity.class);
                            intent.putExtra("errorAPI", "true");
                            startActivity(intent);
                            finish();
                        }

                        String stringName = json.getString("user_name");
                        String stringNumber = json.getString("user_numtell").replace("Numero: ", "");
                        btn_login.loadingSuccessful();

                        // esegue il login dopo 1 sec per visualizzare l'animazione
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {


                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            byte[] encodeValue = Base64.encode(password.getBytes(), Base64.DEFAULT);
                            String npassword = new String(encodeValue);
                            intent.putExtra("password", npassword);
                            intent.putExtra("token", token);


                            editor.putString("account", edt_id.getText().toString());
                            editor.apply();

                            // Inserire i dati su DB
                            helper.insertData(edt_id.getText().toString(), password, stringName, stringNumber);

                            settings = getSharedPreferences("sharedPreferences", 0);
                            editor = settings.edit();
                            editor.putString("checkbox", "true");
                            editor.apply();
                            startActivity(intent);

                        }, 000);


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
                        if (i <= 15) {
                            getObject(url, token, password);
                            i++;
                        } else {
                            Toasty.warning(LoginActivity.this, getString(R.string.error_login), Toast.LENGTH_LONG, true)
                                    .show();
                            btn_login.loadingFailed();
                            btn_login.setEnabled(true);

                        }
                    }
                });

        queue.add(getRequest);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(cm).getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {

        if (account == null && checkbox_preference.equals("false"))
            finishAffinity();
        else {
            editor.putString("checkbox", "true");
            editor.apply();
            finish();
        }

    }


    @Override
    protected void onStop() {
        editor.putString("checkbox", "true");
        editor.apply();
        super.onStop();
    }
}
