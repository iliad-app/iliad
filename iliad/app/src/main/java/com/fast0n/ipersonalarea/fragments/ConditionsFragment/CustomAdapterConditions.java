package com.fast0n.ipersonalarea.fragments.ConditionsFragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fast0n.ipersonalarea.R;

import java.util.List;

public class CustomAdapterConditions extends RecyclerView.Adapter<CustomAdapterConditions.MyViewHolder> {

    private final List<DataConditionsFragments> conditionList;

    CustomAdapterConditions(List<DataConditionsFragments> conditionList) {
        this.conditionList = conditionList;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataConditionsFragments c = conditionList.get(position);

        holder.textView.setText(c.textView);
        holder.textView1.setText(c.textView1);

    }

    @Override
    public int getItemCount() {
        return conditionList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conditions, parent, false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final TextView textView1;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.title);
            textView1 = view.findViewById(R.id.date);

        }
    }
}