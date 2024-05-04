package com.joinflatshare.pojo.faq;

import com.google.gson.annotations.SerializedName;
import com.joinflatshare.pojo.BaseResponse;

import java.util.ArrayList;

public class FaqResponse extends BaseResponse {
    @SerializedName("data")
    private ArrayList<FaqItem> faqItems = new ArrayList<>();

    public ArrayList<FaqItem> getFaqItems() {
        return faqItems;
    }
}
