package com.jobma.employer.model.companyProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOCompanyVideo {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("path")
    @Expose
    private EOVideoPath path;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public EOVideoPath getPath() {
        return path;
    }

    public void setPath(EOVideoPath path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}