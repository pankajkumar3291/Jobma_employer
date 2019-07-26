package com.jobma.employer.model.applicantReports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApplicantsData implements Serializable {

    @SerializedName("jobma_pitcher_id")
    @Expose
    private Integer jobmaPitcherId;
    @SerializedName("jobma_pitcher_fname")
    @Expose
    private String jobmaPitcherFname;
    @SerializedName("jobma_pitcher_lname")
    @Expose
    private String jobmaPitcherLname;
    @SerializedName("jobma_pitcher_email")
    @Expose
    private String jobmaPitcherEmail;
    @SerializedName("jobma_pitcher_phone")
    @Expose
    private String jobmaPitcherPhone;
    @SerializedName("jobma_pitcher_photo")
    @Expose
    private String jobmaPitcherPhoto;
    @SerializedName("jobma_job_title")
    @Expose
    private String jobmaJobTitle;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("jobma_applied_date")
    @Expose
    private String jobmaAppliedDate;
    @SerializedName("jobma_job_company_name")
    @Expose
    private String jobmaJobCompanyName;
    @SerializedName("pre_recorded_payment_status")
    @Expose
    private String preRecordedPaymentStatus;
    @SerializedName("apply_mode")
    @Expose
    private String applyMode;
    @SerializedName("jobma_applied_id")
    @Expose
    private Integer jobmaAppliedId;
    @SerializedName("jobma_job_post_id")
    @Expose
    private Integer jobmaJobPostId;

    public Integer getJobmaPitcherId() {
        return jobmaPitcherId;
    }

    public void setJobmaPitcherId(Integer jobmaPitcherId) {
        this.jobmaPitcherId = jobmaPitcherId;
    }

    public String getJobmaPitcherFname() {
        return jobmaPitcherFname;
    }

    public void setJobmaPitcherFname(String jobmaPitcherFname) {
        this.jobmaPitcherFname = jobmaPitcherFname;
    }

    public String getJobmaPitcherLname() {
        return jobmaPitcherLname;
    }

    public void setJobmaPitcherLname(String jobmaPitcherLname) {
        this.jobmaPitcherLname = jobmaPitcherLname;
    }

    public String getJobmaPitcherEmail() {
        return jobmaPitcherEmail;
    }

    public void setJobmaPitcherEmail(String jobmaPitcherEmail) {
        this.jobmaPitcherEmail = jobmaPitcherEmail;
    }

    public String getJobmaPitcherPhone() {
        return jobmaPitcherPhone;
    }

    public void setJobmaPitcherPhone(String jobmaPitcherPhone) {
        this.jobmaPitcherPhone = jobmaPitcherPhone;
    }

    public String getJobmaPitcherPhoto() {
        return jobmaPitcherPhoto;
    }

    public void setJobmaPitcherPhoto(String jobmaPitcherPhoto) {
        this.jobmaPitcherPhoto = jobmaPitcherPhoto;
    }

    public String getJobmaJobTitle() {
        return jobmaJobTitle;
    }

    public void setJobmaJobTitle(String jobmaJobTitle) {
        this.jobmaJobTitle = jobmaJobTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJobmaAppliedDate() {
        return jobmaAppliedDate;
    }

    public void setJobmaAppliedDate(String jobmaAppliedDate) {
        this.jobmaAppliedDate = jobmaAppliedDate;
    }

    public String getJobmaJobCompanyName() {
        return jobmaJobCompanyName;
    }

    public void setJobmaJobCompanyName(String jobmaJobCompanyName) {
        this.jobmaJobCompanyName = jobmaJobCompanyName;
    }

    public String getPreRecordedPaymentStatus() {
        return preRecordedPaymentStatus;
    }

    public void setPreRecordedPaymentStatus(String preRecordedPaymentStatus) {
        this.preRecordedPaymentStatus = preRecordedPaymentStatus;
    }

    public String getApplyMode() {
        return applyMode;
    }

    public void setApplyMode(String applyMode) {
        this.applyMode = applyMode;
    }

    public Integer getJobmaAppliedId() {
        return jobmaAppliedId;
    }

    public void setJobmaAppliedId(Integer jobmaAppliedId) {
        this.jobmaAppliedId = jobmaAppliedId;
    }

    public Integer getJobmaJobPostId() {
        return jobmaJobPostId;
    }

    public void setJobmaJobPostId(Integer jobmaJobPostId) {
        this.jobmaJobPostId = jobmaJobPostId;
    }

}
