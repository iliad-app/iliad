package com.fast0n.ipersonalarea.fragments.ConditionsFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fast0n.ipersonalarea.R;

import java.util.List;

public class CustomAdapterConditions extends RecyclerView.Adapter<CustomAdapterConditions.MyViewHolder> {

    private final List<DataConditionsFragments> conditionList;
    Context context;

    CustomAdapterConditions(List<DataConditionsFragments> conditionList, Context context) {
        this.conditionList = conditionList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataConditionsFragments c = conditionList.get(position);

        holder.textView.setText(c.textView);
        holder.textView1.setText(c.textView1);
        Glide.with(context).load(R.drawable.ic_documents).into(holder.icon_info);
        Glide.with(context).load(R.drawable.ic_open_in_browser).into(holder.icon);

        holder.icon.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(c.url));
            context.startActivity(i);
        });


    }

    @Override
    public int getItemCount() {
        return conditionList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_info, parent, false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView, textView1;
        ImageView icon;
        ImageView icon_info;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView2);
            textView1 = view.findViewById(R.id.textView3);
            icon = view.findViewById(R.id.icon);
            icon_info = view.findViewById(R.id.icon_info);


        }
    }
}