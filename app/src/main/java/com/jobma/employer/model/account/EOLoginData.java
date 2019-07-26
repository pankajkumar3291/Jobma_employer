package com.jobma.employer.model.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOLoginData implements Serializable {

    @SerializedName("jobma_catcher_company")
    @Expose
    private String jobmaCatcherCompany;
    @SerializedName("jobma_catcher_parent")
    @Expose
    private Integer jobmaCatcherParent;
    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("jobma_catcher_photo")
    @Expose
    private String jobmaCatcherPhoto;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("email")
    @Expose
    private String email;

    public String getJobmaCatcherCompany() {
        return jobmaCatcherCompany;
    }

    public void setJobmaCatcherCompany(String jobmaCatcherCompany) {
        this.jobmaCatcherCompany = jobmaCatcherCompany;
    }

    public Integer getJobmaCatcherParent() {
        return jobmaCatcherParent;
    }

    public void setJobmaCatcherParent(Integer jobmaCatcherParent) {
        this.jobmaCatcherParent = jobmaCatcherParent;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getJobmaCatcherPhoto() {
        return jobmaCatcherPhoto;
    }

    public void setJobmaCatcherPhoto(String jobmaCatcherPhoto) {
        this.jobmaCatcherPhoto = jobmaCatcherPhoto;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}