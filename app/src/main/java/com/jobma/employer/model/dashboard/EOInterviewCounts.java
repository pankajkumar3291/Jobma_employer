package com.jobma.employer.model.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOInterviewCounts implements Serializable {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("data")
    @Expose
    private EOInterviewCountsData data;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public EOInterviewCountsData getData() {
        return data;
    }

    public void setData(EOInterviewCountsData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
