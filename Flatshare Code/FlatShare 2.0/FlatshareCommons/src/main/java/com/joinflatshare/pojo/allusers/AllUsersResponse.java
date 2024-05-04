package com.joinflatshare.pojo.allusers;

import com.google.gson.annotations.SerializedName;
import com.joinflatshare.pojo.BaseResponse;
import com.joinflatshare.pojo.user.ModelUser;

import java.util.ArrayList;

public class AllUsersResponse extends BaseResponse {
    @SerializedName("data")
    private ArrayList<ModelUser> allUsers = new ArrayList<>();

    public ArrayList<ModelUser> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(ArrayList<ModelUser> allUsers) {
        this.allUsers = allUsers;
    }
}
