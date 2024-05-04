package com.joinflatshare.pojo.amenities;

import com.google.gson.annotations.SerializedName;
import com.joinflatshare.pojo.BaseResponse;

import java.util.ArrayList;

public class FlatAmenityResponse extends BaseResponse {
    public FlatAmenityResponse() {
    }

    @SerializedName("amenity")
    private ArrayList<AmenitiesItem> amenities = new ArrayList<>();

    @SerializedName("size")
    private ArrayList<AmenitiesItem> roomsize = new ArrayList<>();

    @SerializedName("roomtype")
    private ArrayList<AmenitiesItem> roomtype = new ArrayList<>();

    @SerializedName("furnishing")
    private ArrayList<AmenitiesItem> furnishing = new ArrayList<>();

    public ArrayList<AmenitiesItem> getFurnishing() {
        return furnishing;
    }

    public ArrayList<AmenitiesItem> getAmenities() {
        return amenities;
    }

    public ArrayList<AmenitiesItem> getRoomsize() {
        return roomsize;
    }

    public ArrayList<AmenitiesItem> getRoomtype() {
        return roomtype;
    }

    public void setAmenities(ArrayList<AmenitiesItem> amenities) {
        this.amenities = amenities;
    }

    public void setRoomsize(ArrayList<AmenitiesItem> roomsize) {
        this.roomsize = roomsize;
    }

    public void setRoomtype(ArrayList<AmenitiesItem> roomtype) {
        this.roomtype = roomtype;
    }

    public void setFurnishing(ArrayList<AmenitiesItem> furnishing) {
        this.furnishing = furnishing;
    }
}
