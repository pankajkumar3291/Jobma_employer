package com.jobma.employer.model.reportIssue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetUserList implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("jobma_user_id")
    @Expose
    private Integer jobmaUserId;
    @SerializedName("jobma_user_name")
    @Expose
    private String jobmaUserName;
    @SerializedName("jobma_last_login")
    @Expose
    private String jobmaLastLogin;
    @SerializedName("confirmed")
    @Expose
    private String confirmed;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("catcher_photo")
    @Expose
    private String catcherPhoto;
    @SerializedName("balance")
    @Expose
    private Integer balance;
    @SerializedName("approval")
    @Expose
    private String approval;
    @SerializedName("prerecord")
    @Expose
    private Integer prerecord;
    @SerializedName("liveinterivew")
    @Expose
    private Integer liveinterivew;
    @SerializedName("subuer_last_login")
    @Expose
    private String subuerLastLogin;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJobmaUserId() {
        return jobmaUserId;
    }

    public void setJobmaUserId(Integer jobmaUserId) {
        this.jobmaUserId = jobmaUserId;
    }

    public String getJobmaUserName() {
        return jobmaUserName;
    }

    public void setJobmaUserName(String jobmaUserName) {
        this.jobmaUserName = jobmaUserName;
    }

    public String getJobmaLastLogin() {
        return jobmaLastLogin;
    }

    public void setJobmaLastLogin(String jobmaLastLogin) {
        this.jobmaLastLogin = jobmaLastLogin;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCatcherPhoto() {
        return catcherPhoto;
    }

    public void setCatcherPhoto(String catcherPhoto) {
        this.catcherPhoto = catcherPhoto;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public Integer getPrerecord() {
        return prerecord;
    }

    public void setPrerecord(Integer prerecord) {
        this.prerecord = prerecord;
    }

    public Integer getLiveinterivew() {
        return liveinterivew;
    }

    public void setLiveinterivew(Integer liveinterivew) {
        this.liveinterivew = liveinterivew;
    }

    public String getSubuerLastLogin() {
        return subuerLastLogin;
    }

    public void setSubuerLastLogin(String subuerLastLogin) {
        this.subuerLastLogin = subuerLastLogin;
    }

}