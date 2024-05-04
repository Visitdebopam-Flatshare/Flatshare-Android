package com.joinflatshare.chat.metadata;

import com.google.gson.Gson;

public class MessageMetaData extends SendbirdMetadata {
    private PreviousMessageMetaData previousMessageMetaData = new PreviousMessageMetaData();
    private String messageType = "";
    private double locationLatitude = 0;
    private double locationLongitude = 0;

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public PreviousMessageMetaData getPreviousMessageMetaData() {
        return previousMessageMetaData;
    }

    public void setPreviousMessageMetaData(PreviousMessageMetaData previousMessageMetaData) {
        this.previousMessageMetaData = previousMessageMetaData;
    }

    public String getJson(MessageMetaData model) {
        if (model == null)
            model = new MessageMetaData();
        String json = "";
        json = new Gson().toJson(model);
        return json;
    }

    public MessageMetaData getModel(String json) {
        if (json.isEmpty())
            return null;
        return new Gson().fromJson(json, MessageMetaData.class);
    }
}
