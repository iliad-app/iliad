package com.fast0n.ipersonalarea.ConsumptionDetailsActivity;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

class Model extends ExpandableGroup<ModelChildren> {

    Model(String title, List<ModelChildren> items) {
        super(title, items);
    }
}

