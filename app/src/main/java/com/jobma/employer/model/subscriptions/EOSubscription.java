package com.jobma.employer.model.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOSubscription {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    private EOData data;
    @SerializedName("message")
    @Expose
    private String message;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public EOData getData() {
        return data;
    }

    public void setData(EOData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}