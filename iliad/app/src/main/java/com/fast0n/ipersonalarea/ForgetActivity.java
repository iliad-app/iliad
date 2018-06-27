package com.fast0n.ipersonalarea;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.fast0n.ipersonalarea.java.GenerateToken;
import com.github.ybq.android.spinkit.style.CubeGrid;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class ForgetActivity extends AppCompatActivity {

    private TextInputLayout id;
    private TextInputLayout surname;
    private TextInputLayout name;
    private EditText edt_id;
    private EditText edt_email;
    private EditText edt_surname;
    private EditText edt_name;

    private static boolean isEmail(String email) {
        String expression = "^[\\w.]+@([\\w]+\\.)+[A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.tv_title_foget);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // set row icon in the toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // java adresses
        id = findViewById(R.id.id);
        surname = findViewById(R.id.surname);
        name = findViewById(R.id.name);
        TextView tv_forget_id = findViewById(R.id.tv_forget_id);
        Button done_forget = findViewById(R.id.done_forget);

        edt_id = findViewById(R.id.edt_id);
        edt_email = findViewById(R.id.edt_email);
        edt_surname = findViewById(R.id.edt_surname);
        edt_name = findViewById(R.id.edt_name);


        String token = GenerateToken.randomString(20);

        done_forget.setOnClickListener(v -> {

            if (id.getVisibility() == View.VISIBLE) {

                if (edt_id.getText().toString().length() == 8 && isEmail(edt_email.getText().toString())) {

                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    final ProgressBar loading;
                    final ConstraintLayout layout;

                    layout = findViewById(R.id.constraint);

                    loading = findViewById(R.id.progressBar);
                    CubeGrid cubeGrid = new CubeGrid();
                    loading.setIndeterminateDrawable(cubeGrid);
                    cubeGrid.setColor(getResources().getColor(android.R.color.white));

                    layout.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);

                    String site_url = getString(R.string.site_url) + getString(R.string.recover) + "?userid=" + edt_id.getText().toString() + "&email=" + edt_email.getText().toString() + "&token=" + token;

                    requests(site_url, layout, loading);


                }

            } else {

                if (edt_surname.getText().toString().length() != 0 && edt_name.getText().toString().length() != 0 && isEmail(edt_email.getText().toString())) {

                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    final ProgressBar loading;
                    final ConstraintLayout layout;

                    layout = findViewById(R.id.constraint);

                    loading = findViewById(R.id.progressBar);
                    CubeGrid cubeGrid = new CubeGrid();
                    loading.setIndeterminateDrawable(cubeGrid);
                    cubeGrid.setColor(getResources().getColor(android.R.color.white));

                    layout.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);

                    String site_url = getString(R.string.site_url) + getString(R.string.recover) + "?surname=" + edt_surname.getText().toString() + "&name=" + edt_name.getText().toString() + "&email=" + edt_email.getText().toString() + "&token=" + token;


                    requests(site_url, layout, loading);

                }


            }

        });


        tv_forget_id.setOnClickListener(v -> {
            if (id.getVisibility() == View.VISIBLE) {
                id.setVisibility(View.GONE);
                surname.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
            } else {
                id.setVisibility(View.VISIBLE);
                surname.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
            }
        });


    }

    private void requests(String site_url, ConstraintLayout layout, ProgressBar loading) {
        RequestQueue queue = Volley.newRequestQueue(ForgetActivity.this);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, site_url, null,
                response -> {
                    try {

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");

                        JSONObject json = new JSONObject(iliad);
                        String string_response = json.getString("0");

                        if (string_response.equals("true")) {
                            Toasty.success(ForgetActivity.this, "Fatto", Toast.LENGTH_LONG,
                                    true).show();
                            finish();
                        } else {
                            Toasty.warning(ForgetActivity.this, getString(R.string.error_forget1), Toast.LENGTH_LONG,
                                    true).show();
                        }


                    } catch (JSONException ignored) {
                    }

                }, error -> {


            try {

                int error_code = error.networkResponse.statusCode;

                if (error_code == 400) {
                    layout.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    Toasty.warning(ForgetActivity.this, getString(R.string.email_wrong), Toast.LENGTH_LONG,
                            true).show();
                }
            } catch (Exception ignored) {
            }


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
