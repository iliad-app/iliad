package com.fast0n.ap.ConsumptionDetailsActivity;

import android.view.View;
import android.widget.TextView;

import com.fast0n.ap.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class DataModelChildren extends GroupViewHolder {

    private final TextView osName;

    public DataModelChildren(View itemView) {
        super(itemView);

        osName = itemView.findViewById(R.id.mobile_os);
    }

    @Override
    public void expand() {
        osName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arrow, 0);
    }

    @Override
    public void collapse() {
        osName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
    }

    public void setGroupName(ExpandableGroup group) {
        osName.setText(group.getTitle());
    }
}
