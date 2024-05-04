package com.joinflatshare.pojo.user;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelUser implements Serializable {
    public ModelUser(int id, String name, String phone, String thumbImage) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.thumbImage = thumbImage;
    }

    public ModelUser() {
    }

    @SerializedName("id")
    private int id = 0;

    @SerializedName("phone")
    private String phone = "";

    @SerializedName("name")
    private String name = "";

    @SerializedName("gender")
    private String gender = "";

    @SerializedName("dob")
    private String dob = "";

    @SerializedName("appearance")
    private String appearence = "";

    @SerializedName("isVerified")
    private int isVerified = 0;

    @SerializedName("address")
    private String address = "";

    @SerializedName("hangout")
    private String hangout = "";

    @SerializedName("work")
    private String work = "";

    @SerializedName("college")
    private String college = "";

    @SerializedName("scannedid")
    private String scannedid = "";

    @SerializedName("selfie")
    private String selfie = "";

    @SerializedName("thumbImage")
    private String thumbImage = "";

    @SerializedName("image")
    private String image = "";

    @SerializedName("status")
    private String status = "";

    public String getId() {
        return phone;
    }

    public void setId(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAppearence() {
        return appearence;
    }

    public void setAppearence(String appearence) {
        this.appearence = appearence;
    }

    public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }

    public ModelLocation getLocation() {
        if (address == null || address.isEmpty())
            return new ModelLocation();
        else return new Gson().fromJson(address, ModelLocation.class);
    }

    public ModelLocation getHangout() {
        if (hangout == null || hangout.isEmpty())
            return new ModelLocation();
        else return new Gson().fromJson(hangout, ModelLocation.class);
    }

    public void setHangout(ModelLocation hangout) {
        this.hangout = new Gson().toJson(hangout);
    }

    public ModelLocation getWork() {
        if (work == null || work.isEmpty())
            return new ModelLocation();
        else return new Gson().fromJson(work, ModelLocation.class);
    }

    public void setWork(ModelLocation work) {
        this.work = new Gson().toJson(work);
    }

    public ModelLocation getCollege() {
        if (college == null || college.isEmpty())
            return new ModelLocation();
        else return new Gson().fromJson(college, ModelLocation.class);
    }

    public void setCollege(ModelLocation college) {
        this.college = new Gson().toJson(college);
    }

    public String getScanned_id() {
        return scannedid;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public ArrayList<String> getImage() {
        if (image == null || image.isEmpty())
            return new ArrayList<>();
        else return new Gson().fromJson(image, new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = new Gson().toJson(image);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

}