package com.joinflatshare.customviews.bottomsheet;

public class ModelBottomSheet {
    int icon;
    String name;
    int type;
    int count = 0;

    public ModelBottomSheet(int icon, String name) {
        this.icon = icon;
        this.name = name;
        this.type = 2;
    }

    public ModelBottomSheet(int icon, int count, String name) {
        this.icon = icon;
        this.name = name;
        this.type = 2;
        this.count = count;
    }

    public ModelBottomSheet(String name, int type) {
        this.icon = 0;
        this.name = name;
        this.type = type;
    }

    public ModelBottomSheet(int icon, String name, int type) {
        this.icon = icon;
        this.name = name;
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
