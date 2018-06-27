package com.fast0n.ipersonalarea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.fast0n.ipersonalarea.java.myDbAdapter;
import com.github.ybq.android.spinkit.style.CubeGrid;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class ChangeEmailActivity extends AppCompatActivity {

    private myDbAdapter helper;
    private String account;
    private String pwd;
    private EditText edt_email;
    private EditText edt_password;
    private Button btn_change_email;

    private static boolean isEmail(String email) {
        String expression = "^[\\w.]+@([\\w]+\\.)+[A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.change_email_title);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // set row icon in the toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        helper = new myDbAdapter(this);

        final Bundle extras = getIntent().getExtras();
        assert extras != null;
        final String token = extras.getString("token");
        final String site_url = getString(R.string.site_url) + getString(R.string.infomation);

        // java adresses
        SharedPreferences settings = getSharedPreferences("sharedPreferences", 0);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        btn_change_email = findViewById(R.id.btn_change_email);

        // prendere le SharedPreferences
        account = settings.getString("account", null);

        btn_change_email.setOnClickListener(v -> {

            if (edt_password.getText().toString().length() != 0
                    || edt_email.getText().toString().length() != 0) {

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

                if (isEmail(edt_email.getText().toString()) && edt_password.getText().toString().equals(ppassword.replaceAll("\\s+", ""))) {
                    View view = this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    String url = site_url + "?email=" + edt_email.getText().toString() + "&email_confirm="
                            + edt_email.getText().toString() + "&password=" + pwd.replaceAll("\\s+", "") + "&token=" + token;
                    changeMail(url);


                    btn_change_email.setEnabled(false);
                } else {
                    btn_change_email.setEnabled(true);
                    Toasty.warning(ChangeEmailActivity.this, getString(R.string.email_wrong), Toast.LENGTH_LONG,
                            true).show();
                }
            } else
                btn_change_email.setEnabled(true);
            Toasty.warning(ChangeEmailActivity.this, getString(R.string.error_forget1), Toast.LENGTH_LONG,
                    true).show();

        });

    }

    private void changeMail(String url) {

        final ProgressBar loading;
        final ConstraintLayout layout;

        layout = findViewById(R.id.constraint);

        loading = findViewById(R.id.progressBar);
        CubeGrid cubeGrid = new CubeGrid();
        loading.setIndeterminateDrawable(cubeGrid);
        cubeGrid.setColor(getResources().getColor(android.R.color.white));


        layout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(ChangeEmailActivity.this);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);
                        String string_response = json.getString("0");

                        if (string_response.equals("true")) {

                            edt_password.setInputType(0);
                            Intent intent = new Intent(ChangeEmailActivity.this, LoginActivity.class);
                            startActivity(intent);

                            Toasty.success(ChangeEmailActivity.this, getString(R.string.email_change_success),
                                    Toast.LENGTH_LONG, true).show();
                        }

                    } catch (JSONException ignored) {
                    }

                }, error -> {

        });

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