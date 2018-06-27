package com.fast0n.ipersonalarea.fragments.SettingsFragment;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fast0n.ipersonalarea.R;
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
        holder.textView.setText(c.name);


        holder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {

            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    //Toasty.success(context,"Permission Granted", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toasty.error(context, "Permission Denied", Toast.LENGTH_SHORT).show();

                }
            };

            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            switch (position) {
                case 0:
                    if (holder.toggle.isChecked())
                        TedPermission.with(context)
                                .setPermissionListener(permissionlistener)
                                .setPermissions(Manifest.permission.READ_CONTACTS)
                                .check();
                    break;

                case 1:
                    if (holder.toggle.isChecked())
                        TedPermission.with(context)
                                .setPermissionListener(permissionlistener)
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check();
                    break;

                case 2:
                    if (holder.toggle.isChecked())
                        TedPermission.with(context)
                                .setPermissionListener(permissionlistener)
                                .setPermissions(Manifest.permission.CAMERA)
                                .check();
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

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView1);
            toggle = view.findViewById(R.id.toggle);

        }
    }
}