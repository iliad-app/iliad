package com.fast0n.ipersonalarea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.ipersonalarea.fragments.AboutFragment.AboutFragment;
import com.fast0n.ipersonalarea.fragments.ConditionsFragment.ConditionsFragment;
import com.fast0n.ipersonalarea.fragments.InfoFragments.InfoFragments;
import com.fast0n.ipersonalarea.fragments.MasterCreditFragment;
import com.fast0n.ipersonalarea.fragments.OptionsFragment.OptionsFragment;
import com.fast0n.ipersonalarea.fragments.ServicesFragment.ServicesFragment;
import com.fast0n.ipersonalarea.fragments.SettingsFragment.SettingsFragment;
import com.fast0n.ipersonalarea.fragments.SimFragments;
import com.fast0n.ipersonalarea.fragments.VoicemailFragment.VoicemailFragment;
import com.fast0n.ipersonalarea.java.myDbAdapter;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {

    private static final int PROFILE_SETTING = 100000;
    private int pos = 0;
    private myDbAdapter helper;
    private String account;
    private String userid;
    private String password;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private ProgressBar loading;
    private SharedPreferences.Editor editor;
    private boolean backPressedToExitOnce = false;
    private AccountHeader headerResult = null;
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(toolbar.getTitle());
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // java adresses
        SharedPreferences settings = getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();
        drawer = findViewById(R.id.drawer_layout);
        loading = findViewById(R.id.progressBar);
        CubeGrid cubeGrid = new CubeGrid();
        loading.setIndeterminateDrawable(cubeGrid);
        cubeGrid.setColor(getResources().getColor(R.color.colorPrimary));
        helper = new myDbAdapter(this);


        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // prendere le SharedPreferences
        account = settings.getString("account", null);

        String getAllData = helper.getAllData();
        String[] arrayData = getAllData.split("\n");
        for (String anArrayData1 : arrayData) {
            String onlyname = anArrayData1.split("&")[0];
            String onlypassword = anArrayData1.split("&")[1];
            if (onlyname.equals(account)) {
                userid = onlyname;
                password = onlypassword;
                break;
            }
        }


        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String token = extras.getString("token", null);


        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.color.primary)
                .addProfiles(
                        new ProfileSettingDrawerItem().withName("Agg. Account").withEmail("Aggiungi account iliad").withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_add).withIdentifier(PROFILE_SETTING)

                )
                .withOnAccountHeaderListener((view, profile, current) -> {
                    if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                        if (headerResult.getProfiles() != null) {
                            editor.putString("checkbox", "false");
                            editor.apply();

                            Intent mainActivity = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(mainActivity);
                        }
                    } else {

                        for (String anArrayData : arrayData) {
                            String onlyname = anArrayData.split("&")[0];
                            String onlypassword = anArrayData.split("&")[1];
                            String phone = anArrayData.split("&")[2];
                            String getPhone = profile.getEmail().getText().toString();


                            if (getPhone.replaceAll("\\s+", "").equals(phone.replaceAll("\\s+", ""))) {
                                userid = onlyname;
                                password = onlypassword;
                                editor.putString("account", onlyname);
                                editor.apply();

                                Intent mainActivity = new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(mainActivity);
                                break;
                            }
                        }
                    }


                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .build();


        String nameDB = helper.getName();
        String phoneDB = helper.getPhoneNumber();
        String idDB = helper.getUserID();

        String[] prova = nameDB.split("\n");
        String[] prova1 = phoneDB.split("\n");
        String[] prova2 = idDB.split("\n");


        for (int z = 0; z < prova.length; z++) {

            if (prova2[z].equals(account)) {
                IProfile profile = new ProfileDrawerItem().withNameShown(true).withName(prova[z]).withEmail(prova1[z]).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_account).withIdentifier(z);
                headerResult.addProfile(profile, pos);
            }
        }

        for (int z = 0; z < prova.length; z++) {

            if (!prova2[z].equals(account)) {
                IProfile profile = new ProfileDrawerItem().withNameShown(true).withName(prova[z]).withEmail(prova1[z]).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_account).withIdentifier(z);
                headerResult.addProfile(profile, pos++);
            }
        }


        if (isOnline()) {
            String site_url = getString(R.string.site_url) + getString(R.string.login);
            String url = site_url + "?userid=" + userid + "&password=" + password.replaceAll("\\s+", "") + "&token=" + token;
            getObject(url, toolbar, savedInstanceState);
            settings = getSharedPreferences("sharedPreferences", 0);
            editor = settings.edit();
            editor.putString("token", token);
            editor.apply();

        } else {
            Intent mainActivity = new Intent(HomeActivity.this, ErrorConnectionActivity.class);
            startActivity(mainActivity);
        }


    }

    private void getObject(String url, Toolbar toolbar, Bundle savedInstanceState) {
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

        CustomPriorityRequest customPriorityRequest = new CustomPriorityRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject json_raw = new JSONObject(response.toString());
                        String iliad = json_raw.getString("iliad");
                        JSONObject json = new JSONObject(iliad);

                        String stringSim = json.getString("sim");


                        result = new DrawerBuilder()
                                .withActivity(this)
                                .withToolbar(toolbar)
                                .withHasStableIds(true)
                                .withItemAnimator(new AlphaCrossFadeAnimator())
                                .withAccountHeader(headerResult)
                                .addDrawerItems(
                                        new PrimaryDrawerItem().withName(R.string.nav_credit).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_credit).withIdentifier(1).withSelectable(true),
                                        new PrimaryDrawerItem().withName(R.string.nav_info).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_info).withIdentifier(5).withSelectable(true),
                                        new PrimaryDrawerItem().withName(R.string.nav_sim).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_sim_card).withIdentifier(6).withSelectable(true),

                                        new SectionDrawerItem().withName(R.string.split_menu),
                                        new SecondaryDrawerItem().withName(R.string.nav_contactus).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_warning).withIdentifier(20).withSelectable(false),
                                        new SecondaryDrawerItem().withName(R.string.nav_aboutus).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_info_outline).withIdentifier(21).withSelectable(true),
                                        new SecondaryDrawerItem().withName(R.string.nav_logout).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_logout).withIdentifier(23).withSelectable(false)

                                )
                                .withOnDrawerItemClickListener((view, position, drawerItem) -> {

                                    Fragment fragment = null;

                                    if (drawerItem != null) {
                                        if (drawerItem.getIdentifier() == 1) {
                                            if (isOnline())
                                                fragment = new MasterCreditFragment();
                                            else
                                                startActivity(new Intent(HomeActivity.this, ErrorConnectionActivity.class));
                                        } else if (drawerItem.getIdentifier() == 2) {
                                            if (isOnline())
                                                fragment = new OptionsFragment();
                                            else
                                                startActivity(new Intent(HomeActivity.this, ErrorConnectionActivity.class));

                                        } else if (drawerItem.getIdentifier() == 3) {
                                            if (isOnline())
                                                fragment = new ServicesFragment();
                                            else
                                                startActivity(new Intent(HomeActivity.this, ErrorConnectionActivity.class));
                                        } else if (drawerItem.getIdentifier() == 4) {
                                            if (isOnline())
                                                fragment = new VoicemailFragment();
                                            else
                                                startActivity(new Intent(HomeActivity.this, ErrorConnectionActivity.class));

                                        } else if (drawerItem.getIdentifier() == 5) {
                                            if (isOnline())
                                                fragment = new InfoFragments();
                                            else
                                                startActivity(new Intent(HomeActivity.this, ErrorConnectionActivity.class));

                                        } else if (drawerItem.getIdentifier() == 6) {
                                            if (isOnline())
                                                fragment = new SimFragments();

                                            else
                                                startActivity(new Intent(HomeActivity.this, ErrorConnectionActivity.class));

                                        } else if (drawerItem.getIdentifier() == 7) {
                                            if (isOnline())
                                                fragment = new ConditionsFragment();
                                            else
                                                startActivity(new Intent(HomeActivity.this, ErrorConnectionActivity.class));


                                        } else if (drawerItem.getIdentifier() == 20) {
                                            RequestQueue queue1 = Volley.newRequestQueue(HomeActivity.this);

                                            String url1 = getString(R.string.site_url) + getString(R.string.alert);

                                            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url1, null,
                                                    response1 -> {
                                                        try {

                                                            JSONObject json_raw1 = new JSONObject(response1.toString());
                                                            String iliad1 = json_raw1.getString("iliad");

                                                            JSONObject json1 = new JSONObject(iliad1);
                                                            String string_response = json1.getString("0");

                                                            new MaterialStyledDialog.Builder(HomeActivity.this)
                                                                    .setTitle(R.string.warning)
                                                                    .setDescription(Html.fromHtml(string_response))
                                                                    .setScrollable(true)
                                                                    .setStyle(Style.HEADER_WITH_TITLE)
                                                                    .setPositiveText(R.string.read)
                                                                    .setCancelable(false)
                                                                    .onPositive((dialog, which) -> {

                                                                    }).setScrollable(true, 10)
                                                                    .show();

                                                        } catch (JSONException ignored) {
                                                        }

                                                    }, error -> {
                                            });

                                            queue1.add(getRequest);

                                        } else if (drawerItem.getIdentifier() == 21) {
                                            if (isOnline())
                                                fragment = new AboutFragment();
                                            else
                                                startActivity(new Intent(HomeActivity.this, ErrorConnectionActivity.class));

                                        } else if (drawerItem.getIdentifier() == 22) {
                                            if (isOnline())
                                                fragment = new SettingsFragment();

                                            else
                                                startActivity(new Intent(HomeActivity.this, ErrorConnectionActivity.class));
                                        } else if (drawerItem.getIdentifier() == 23) {
                                            if (isOnline()) {

                                                new AlertDialog.Builder(HomeActivity.this).setMessage(R.string.dialog_exit).setCancelable(false)
                                                        .setPositiveButton(getString(R.string.yes), (dialog, id1) -> {


                                                            String getAllData = helper.getAllData();
                                                            String[] arrayData = getAllData.split("\n");

                                                            for (String anArrayData : arrayData) {
                                                                String onlyname = anArrayData.split("&")[0];
                                                                if (onlyname.equals(account)) {
                                                                    helper.delete(account);
                                                                    break;
                                                                }
                                                            }

                                                            if (arrayData.length > 1) {
                                                                String onlyname = arrayData[arrayData.length - 1].split("&")[0];
                                                                editor.putString("account", onlyname);
                                                                editor.apply();
                                                                Intent mainActivity = new Intent(HomeActivity.this, LoginActivity.class);
                                                                startActivity(mainActivity);
                                                            } else {

                                                                editor.putString("checkbox", "false");
                                                                editor.putString("account", null);
                                                                editor.apply();

                                                                Intent mainActivity = new Intent(HomeActivity.this, LoginActivity.class);
                                                                startActivity(mainActivity);
                                                            }


                                                        }).setNegativeButton(getString(R.string.no), null).show();

                                            }
                                        }
                                        if (fragment != null) {
                                            FragmentManager fragmentManager = getSupportFragmentManager();
                                            FragmentTransaction ft = fragmentManager.beginTransaction();
                                            ft.replace(R.id.fragment, fragment);
                                            ft.commit();
                                        }
                                    }

                                    return false;
                                })
                                .withSavedInstance(savedInstanceState)
                                .build();


                        SecondaryDrawerItem menuSetting = new SecondaryDrawerItem().withName(R.string.nav_settings).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_settings).withIdentifier(22).withSelectable(true);
                        PrimaryDrawerItem menuService = new PrimaryDrawerItem().withName(R.string.nav_options).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_options).withIdentifier(2).withSelectable(true);
                        PrimaryDrawerItem menuVoiceMail = new PrimaryDrawerItem().withName(R.string.nav_services).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_service).withIdentifier(3).withSelectable(true);
                        PrimaryDrawerItem menuInfo = new PrimaryDrawerItem().withName(R.string.nav_voicemail).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_voicemail).withIdentifier(4).withSelectable(true);
                        PrimaryDrawerItem menuConditions = new PrimaryDrawerItem().withName(R.string.nav_conditions).withTextColor(getResources().getColor(android.R.color.black)).withIcon(R.drawable.ic_conditions).withIdentifier(7).withSelectable(true);


                        if (stringSim.equals("false")) {

                            Fragment fragment;
                            fragment = new SimFragments();

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.fragment, fragment);
                            ft.commit();

                        } else {
                            Fragment fragment;
                            fragment = new MasterCreditFragment();

                            result.addItemAtPosition(menuService, 2);
                            result.addItemAtPosition(menuVoiceMail, 3);
                            result.addItemAtPosition(menuInfo, 4);
                            result.addItemAtPosition(menuConditions, 7);


                            if (android.os.Build.VERSION.SDK_INT >= 23)
                                result.addItemAtPosition(menuSetting, 11);


                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.fragment, fragment);
                            ft.commit();
                        }
                        loading.setVisibility(View.INVISIBLE);


                        toggle.syncState();
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                    } catch (JSONException ignored) {
                    }
                },
                error -> {


                    try {
                        int error_code = error.networkResponse.statusCode;

                        if (error_code == 503) {
                            Toasty.warning(HomeActivity.this, getString(R.string.error_login), Toast.LENGTH_LONG, true)
                                    .show();
                            Intent mainActivity = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(mainActivity);
                        }
                    } catch (Exception ignored) {
                        Toasty.warning(HomeActivity.this, getString(R.string.error_login), Toast.LENGTH_LONG, true)
                                .show();
                        Intent mainActivity = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(mainActivity);

                    }
                });

        customPriorityRequest.setPriority(Request.Priority.IMMEDIATE);
        queue.add(customPriorityRequest);


    }


    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backPressedToExitOnce) {
                finishAffinity();

            } else {
                this.backPressedToExitOnce = true;
                Toasty.info(HomeActivity.this, getString(R.string.press_back), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> backPressedToExitOnce = false, 1000);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

}