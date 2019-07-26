package com.jobma.employer.model.jobList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOMessageObject implements Serializable {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("message")
    @Expose
    private String message;

    public String  getError() {
        return error;
    }

    public void setError(String  error) {
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
