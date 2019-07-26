package com.jobma.employer.model.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOOverviewData implements Serializable {

    @SerializedName("jobma_post")
    @Expose
    private Integer jobmaPost;
    @SerializedName("jobma_invitation_date")
    @Expose
    private String jobmaInvitationDate;
    @SerializedName("invitation_timezone")
    @Expose
    private String invitationTimezone;
    @SerializedName("jobma_interview_mode")
    @Expose
    private String jobmaInterviewMode;
    @SerializedName("jobma_interview_token")
    @Expose
    private String jobmaInterviewToken;
    @SerializedName("acceptation")
    @Expose
    private String acceptation;
    @SerializedName("jobma_interview_status")
    @Expose
    private String jobmaInterviewStatus;
    @SerializedName("interview_mode")
    @Expose
    private String interviewMode;
    @SerializedName("job_name")
    @Expose
    private String jobName;
    @SerializedName("pitcher_name")
    @Expose
    private String pitcherName;
    @SerializedName("pitcher_photo")
    @Expose
    private String pitcherPhoto;

    public Integer getJobmaPost() {
        return jobmaPost;
    }

    public void setJobmaPost(Integer jobmaPost) {
        this.jobmaPost = jobmaPost;
    }

    public String getJobmaInvitationDate() {
        return jobmaInvitationDate;
    }

    public void setJobmaInvitationDate(String jobmaInvitationDate) {
        this.jobmaInvitationDate = jobmaInvitationDate;
    }

    public String getInvitationTimezone() {
        return invitationTimezone;
    }

    public void setInvitationTimezone(String invitationTimezone) {
        this.invitationTimezone = invitationTimezone;
    }

    public String getJobmaInterviewMode() {
        return jobmaInterviewMode;
    }

    public void setJobmaInterviewMode(String jobmaInterviewMode) {
        this.jobmaInterviewMode = jobmaInterviewMode;
    }

    public String getJobmaInterviewToken() {
        return jobmaInterviewToken;
    }

    public void setJobmaInterviewToken(String jobmaInterviewToken) {
        this.jobmaInterviewToken = jobmaInterviewToken;
    }

    public String getAcceptation() {
        return acceptation;
    }

    public void setAcceptation(String acceptation) {
        this.acceptation = acceptation;
    }

    public String getJobmaInterviewStatus() {
        return jobmaInterviewStatus;
    }

    public void setJobmaInterviewStatus(String jobmaInterviewStatus) {
        this.jobmaInterviewStatus = jobmaInterviewStatus;
    }

    public String getInterviewMode() {
        return interviewMode;
    }

    public void setInterviewMode(String interviewMode) {
        this.interviewMode = interviewMode;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getPitcherName() {
        return pitcherName;
    }

    public void setPitcherName(String pitcherName) {
        this.pitcherName = pitcherName;
    }

    public String getPitcherPhoto() {
        return pitcherPhoto;
    }

    public void setPitcherPhoto(String pitcherPhoto) {
        this.pitcherPhoto = pitcherPhoto;
    }
}
