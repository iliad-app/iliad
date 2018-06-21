package com.fast0n.ipersonalarea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ipersonalarea.java.GenerateToken;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private EditText edt_id;
    private EditText edt_password;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private CheckBox checkBox;
    TextView textView2;

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

        textView2.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgetActivity.class)));


        settings = getSharedPreferences("sharedPreferences", 0);
        String userid = settings.getString("userid", null);
        String alert = settings.getString("alert", null);
        String password = settings.getString("password", null);
        editor = settings.edit();
        editor.apply();

        if (alert == null) {

            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

            String site_url = getString(R.string.site_url) +  getString(R.string.alert);

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
                                    .setPositiveText(R.string.accept)
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


        if (password != null && password.trim().length() > 7) {
            String token = GenerateToken.randomString(20);
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

            intent.putExtra("userid", userid);
            intent.putExtra("password", password);
            intent.putExtra("token", token);

            startActivity(intent);
        }

        btn_login.setOnClickListener(v -> {
            if (isOnline() && edt_id.getText().toString().length() != 0
                    && edt_password.getText().toString().length() != 0) {

                String userid1 = edt_id.getText().toString();
                String password1 = edt_password.getText().toString();
                String token = GenerateToken.randomString(20);

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("userid", userid1);


                byte[] encodeValue = Base64.encode(password1.getBytes(), Base64.DEFAULT);
                String npassword = new String(encodeValue);
                intent.putExtra("password", npassword);
                intent.putExtra("token", token);

                if (checkBox.isChecked()) {
                    intent.putExtra("checkbox", "true");
                } else {
                    intent.putExtra("checkbox", "false");
                }
                startActivity(intent);
                btn_login.setEnabled(false);

            } else {
                btn_login.setEnabled(true);
                Toasty.error(LoginActivity.this, getString(R.string.errorconnection), Toast.LENGTH_SHORT).show();
            }
        });
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
