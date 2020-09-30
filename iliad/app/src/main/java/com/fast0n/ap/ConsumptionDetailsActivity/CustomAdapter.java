package com.fast0n.ap.ConsumptionDetailsActivity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fast0n.ap.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

class CustomAdapter extends ExpandableRecyclerViewAdapter<DataModelChildren, DataModel> {

    private final Activity activity;

    CustomAdapter(Activity activity, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.activity = activity;
    }

    @Override
    public DataModelChildren onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_consumption_details, parent, false);

        return new DataModelChildren(view);
    }

    @Override
    public DataModel onCreateChildViewHolder(ViewGroup parent, final int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_consumption_details_children, parent, false);

        return new DataModel(view);
    }

    @Override
    public void onBindChildViewHolder(DataModel holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final ModelChildren modelChildren = ((Model) group).getItems().get(childIndex);
        holder.onBind(modelChildren);
    }

    @Override
    public void onBindGroupViewHolder(DataModelChildren holder, int flatPosition, ExpandableGroup group) {
        holder.setGroupName(group);
    }
}
