package com.fast0n.ipersonalarea.fragments.AboutFragment;

class DataAboutFragment {

    private final String name;
    private final int icon;

    DataAboutFragment(String name, int icon) {
        this.name = name;
        this.icon = icon;

    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

}