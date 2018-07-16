package com.fast0n.ipersonalarea.fragments.SettingsFragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fast0n.ipersonalarea.BuildConfig;
import com.fast0n.ipersonalarea.HomeActivity;
import com.fast0n.ipersonalarea.R;
import com.fast0n.ipersonalarea.fragments.CreditFragment.CreditFragment;
import com.fast0n.ipersonalarea.fragments.MasterCreditFragment;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

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

        String [] permission = {"android.permission.READ_CONTACTS", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"};


        if (position == 3 && holder.toggleState.equals("0"))
            holder.toggle.setChecked(true);
        else
            holder.toggle.setChecked(false);


        for (int i = 0; i< permission.length-1; i++){
            int permission1 = context.checkCallingOrSelfPermission(permission[i]);
            if (position == i && permission1==PackageManager.PERMISSION_GRANTED)
                holder.toggle.setChecked(true);
        }

        holder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    Toasty.success(context,"Permission Granted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
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
                    else{
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
                    else{
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        context.startActivity(i);
                    }
                    break;

                case 2:
                    if (holder.toggle.isChecked())
                        TedPermission.with(context)
                                .setPermissionListener(permissionlistener)
                                .setPermissions(Manifest.permission.CAMERA)
                                .check();
                    else{
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        context.startActivity(i);
                    }
                    break;

                case 3:
                    if (holder.toggle.isChecked()) {
                        holder.editor.putString("toggleState", "0");
                        holder.editor.apply();
                        String toasty = context.getString(R.string.toast_notification, String.valueOf(context.getString(R.string.toggle_enable)));
                        Toasty.info(context, toasty, Toast.LENGTH_SHORT, true).show();
                    }else{
                        holder.editor.putString("toggleState", "1");
                        holder.editor.apply();
                        String toasty = context.getString(R.string.toast_notification, String.valueOf(context.getString(R.string.toggle_disable)));
                        Toasty.info(context, toasty, Toast.LENGTH_SHORT, true).show();
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
        ImageView imageView;
        final Switch toggle;
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        String toggleState;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView1);
            toggle = view.findViewById(R.id.toggle);
            imageView = view.findViewById(R.id.imageView);
            settings = context.getSharedPreferences("sharedPreferences", 0);
            editor = settings.edit();
            toggleState = settings.getString("toggleState", null);


        }
    }
}