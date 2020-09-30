package com.fast0n.ap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ap.java.CubeLoading;
import com.fast0n.ap.java.DialogError;
import com.fast0n.ap.java.GenerateToken;
import com.fast0n.ap.java.myDbAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ChangePasswordActivity extends AppCompatActivity {

    String theme;
    SharedPreferences settings;
    private myDbAdapter helper;
    private String pwd, account;
    private EditText edt_newpassword, edt_password;
    private Button btn_change_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences("sharedPreferences", 0);
        theme = settings.getString("toggleTheme", null);
        if (theme.equals("0"))
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.DarkTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.change_password_title);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // set row icon in the toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // java adresses
        edt_newpassword = findViewById(R.id.edt_newpassword);
        edt_password = findViewById(R.id.edt_oldpassword);
        btn_change_password = findViewById(R.id.btn_password);
        helper = new myDbAdapter(this);

        // prendere le SharedPreferences
        account = settings.getString("account", null);

        final Bundle extras = getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token", null);
        final String site_url = getString(R.string.site_url) + getString(R.string.infomation);

        btn_change_password.setOnClickListener(v -> {


            if (edt_password.getText().toString().length() != 0
                    || edt_newpassword.getText().toString().length() != 0) {


                String getAllData = helper.getAllData();
                String[] arrayData = getAllData.split("\n");
                for (String anArrayData : arrayData) {
                    String onlyname = anArrayData.split("&")[0];
                    String onlypassword = anArrayData.split("&")[1];
                    if (onlyname.equals(account)) {
                        pwd = onlypassword;
                        break;
                    }
                }

                byte[] decodeValue1 = Base64.decode(pwd, Base64.DEFAULT);
                String ppassword = new String(decodeValue1);


                if (edt_password.getText().toString().equals(ppassword.replaceAll("\\s+", ""))) {
                    View view = this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    byte[] decodeValue = Base64.encode(edt_password.getText().toString().getBytes(), Base64.DEFAULT);
                    String oldpassword = new String(decodeValue);


                    byte[] encodeValue1 = Base64.encode(edt_newpassword.getText().toString().getBytes(), Base64.DEFAULT);
                    String newpassword = new String(encodeValue1);


                    String url = site_url + "?new_password=" + newpassword.replaceAll("\\s+", "")
                            + "&new_password_confirm=" + newpassword.replaceAll("\\s+", "") + "&password=" + oldpassword.replaceAll("\\s+", "")
                            + "&token=" + token;


                    changePassword(url, oldpassword.replaceAll("\\s+", ""), newpassword.replaceAll("\\s+", ""));
                    btn_change_password.setEnabled(false);
                } else {
                    btn_change_password.setEnabled(true);
                    Toasty.warning(ChangePasswordActivity.this, getString(R.string.wrong_password), Toast.LENGTH_LONG,
                            true).show();
                }
            } else {
                btn_change_password.setEnabled(true);
                Toasty.warning(ChangePasswordActivity.this, getString(R.string.wrong_password), Toast.LENGTH_LONG,
                        true).show();
            }
        });

    }

    private void changePassword(String url, String oldPassword, final String newpassword) {

        final ProgressBar loading;
        final ConstraintLayout layout;

        layout = findViewById(R.id.constraint);

        loading = findViewById(R.id.progressBar);
        settings = getSharedPreferences("sharedPreferences", 0);
        theme = settings.getString("toggleTheme", null);
        new CubeLoading(this, loading, theme).showLoading();

        layout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(ChangePasswordActivity.this);
        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);
                        String string_response = json.getString("0");

                        if (string_response.equals("true")) {
                            String token = GenerateToken.randomString(20);
                            Intent intent = new Intent(ChangePasswordActivity.this, HomeActivity.class);
                            helper.updatePassword(oldPassword, newpassword);
                            intent.putExtra("token", token);
                            intent.putExtra("checkbox", "true");
                            startActivity(intent);
                            Toasty.success(ChangePasswordActivity.this, getString(R.string.password_change_success),
                                    Toast.LENGTH_LONG, true).show();


                        }

                    } catch (JSONException e) {
                        new DialogError(this, String.valueOf(e)).alertbox();
                    }

                }, e -> new DialogError(this, String.valueOf(e)).alertbox());

        queue.add(getRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                super.onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
