package com.fast0n.ap.fragments.SettingsFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fast0n.ap.BuildConfig;
import com.fast0n.ap.LoginActivity;
import com.fast0n.ap.R;
import com.fast0n.ap.notifications.CheckNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class CustomAdapterSettings extends RecyclerView.Adapter<CustomAdapterSettings.MyViewHolder> {

    private final Context context;
    private final List<DataSettingsFragments> SettingsList;

    CustomAdapterSettings(Context context, List<DataSettingsFragments> SettingsList) {
        this.context = context;
        this.SettingsList = SettingsList;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final DataSettingsFragments c = SettingsList.get(position);
        holder.textView.setText(Html.fromHtml(c.name));
        Glide.with(context).load(R.drawable.ic_settings).into(holder.imageView);
        Handler handler = new Handler();

        String[] permission = {"android.permission.READ_CONTACTS", "android.permission.WRITE_EXTERNAL_STORAGE"};

        if (position == 3 && holder.toggleTheme.equals("1"))
            holder.toggle.setChecked(true);
        else if (position == 2 && holder.toggleNotification.equals("0"))
            holder.toggle.setChecked(true);
        else
            holder.toggle.setChecked(false);


        for (int i = 0; i < permission.length - 1; i++) {
            int permission1 = context.checkCallingOrSelfPermission(permission[i]);
            if (position == i && permission1 == PackageManager.PERMISSION_GRANTED)
                holder.toggle.setChecked(true);
        }

        holder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    Toasty.success(context, "Permission Granted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    Toasty.error(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                    holder.toggle.setChecked(false);
                }


            };


            switch (position) {
                case 0:
                    if (holder.toggle.isChecked())
                        TedPermission.with(context)
                                .setPermissionListener(permissionlistener)
                                .setPermissions(Manifest.permission.READ_CONTACTS)
                                .check();
                    else {
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        context.startActivity(i);
                    }
                    break;

                case 1:
                    if (holder.toggle.isChecked())
                        TedPermission.with(context)
                                .setPermissionListener(permissionlistener)
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check();
                    else {
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        context.startActivity(i);
                    }
                    break;

                case 2:
                    if (holder.toggle.isChecked()) {
                        holder.editor.putString("toggleNotification", "0");
                        holder.editor.apply();
                        String toasty = context.getString(R.string.toast_notification, context.getString(R.string.toggle_enable));
                        Toasty.info(context, toasty, Toast.LENGTH_SHORT, true).show();
                        FirebaseMessaging.getInstance().subscribeToTopic("notification");

                    } else {
                        holder.editor.putString("toggleNotification", "1");
                        holder.editor.apply();
                        String toasty = context.getString(R.string.toast_notification, context.getString(R.string.toggle_disable));
                        Toasty.info(context, toasty, Toast.LENGTH_SHORT, true).show();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");

                    }
                    break;

                case 3:
                    final Runnable runnable = () -> context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK));

                    if (holder.toggle.isChecked()) {
                        holder.editor.putString("toggleTheme", "1");
                        holder.editor.apply();
                        String toasty = context.getString(R.string.toast_theme, context.getString(R.string.toggle_enable));
                        Toasty.info(context, toasty, Toast.LENGTH_SHORT, true).show();
                        handler.postDelayed(runnable, 500);

                    } else {
                        holder.editor.putString("toggleTheme", "0");
                        holder.editor.apply();
                        String toasty = context.getString(R.string.toast_theme, context.getString(R.string.toggle_disable));
                        Toasty.info(context, toasty, Toast.LENGTH_SHORT, true).show();
                        handler.postDelayed(runnable, 500);
                    }
                    break;


            }


        });

    }


    @Override
    public int getItemCount() {
        return SettingsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_options_services, parent, false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final Switch toggle;
        ImageView imageView;
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        String toggleNotification, toggleTheme;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView1);
            toggle = view.findViewById(R.id.toggle);
            imageView = view.findViewById(R.id.imageView);
            settings = context.getSharedPreferences("sharedPreferences", 0);
            editor = settings.edit();
            toggleNotification = settings.getString("toggleNotification", null);
            toggleTheme = settings.getString("toggleTheme", null);
            new CheckNotification(toggleNotification, editor);


        }
    }
}