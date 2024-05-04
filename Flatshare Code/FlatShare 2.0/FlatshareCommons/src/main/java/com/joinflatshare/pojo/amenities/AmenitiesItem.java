package com.joinflatshare.pojo.amenities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AmenitiesItem implements Serializable {

    @SerializedName("name")
    private String name = "";

    @SerializedName("id")
    private int id = 0;

    boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }
}