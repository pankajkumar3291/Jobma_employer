package com.jobma.employer.model.companyProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOCompanyProfilePic implements Serializable {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("message")
    @Expose
    private String message;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
