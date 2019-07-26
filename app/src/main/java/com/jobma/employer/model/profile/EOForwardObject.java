package com.jobma.employer.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOForwardObject {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
