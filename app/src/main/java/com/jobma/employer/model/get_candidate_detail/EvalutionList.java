package com.jobma.employer.model.get_candidate_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EvalutionList implements Serializable {

    @SerializedName("pitcher_data")
    @Expose
    private CandidatePitcherData pitcherData;
    @SerializedName("jobma_applied_date")
    @Expose
    private String jobmaAppliedDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("apply_mode")
    @Expose
    private String applyMode;
    @SerializedName("pre_recorded_payment_status")
    @Expose
    private String preRecordedPaymentStatus;
    @SerializedName("live_interview_payment_status")
    @Expose
    private String liveInterviewPaymentStatus;
    @SerializedName("job_id")
    @Expose
    private Integer jobId;
    @SerializedName("applied_id")
    @Expose
    private Integer appliedId;
    @SerializedName("jobma_job_title")
    @Expose
    private String jobmaJobTitle;
    @SerializedName("current_status")
    @Expose
    private String currentStatus;
    @SerializedName("interview_mode")
    @Expose
    private String interviewMode;
    @SerializedName("avg_rating")
    @Expose
    private double avgRating;

    public CandidatePitcherData getPitcherData() {
        return pitcherData;
    }

    public void setPitcherData(CandidatePitcherData pitcherData) {
        this.pitcherData = pitcherData;
    }

    public String getJobmaAppliedDate() {
        return jobmaAppliedDate;
    }

    public void setJobmaAppliedDate(String jobmaAppliedDate) {
        this.jobmaAppliedDate = jobmaAppliedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplyMode() {
        return applyMode;
    }

    public void setApplyMode(String applyMode) {
        this.applyMode = applyMode;
    }

    public String getPreRecordedPaymentStatus() {
        return preRecordedPaymentStatus;
    }

    public void setPreRecordedPaymentStatus(String preRecordedPaymentStatus) {
        this.preRecordedPaymentStatus = preRecordedPaymentStatus;
    }

    public String getLiveInterviewPaymentStatus() {
        return liveInterviewPaymentStatus;
    }

    public void setLiveInterviewPaymentStatus(String liveInterviewPaymentStatus) {
        this.liveInterviewPaymentStatus = liveInterviewPaymentStatus;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getAppliedId() {
        return appliedId;
    }

    public void setAppliedId(Integer appliedId) {
        this.appliedId = appliedId;
    }

    public String getJobmaJobTitle() {
        return jobmaJobTitle;
    }

    public void setJobmaJobTitle(String jobmaJobTitle) {
        this.jobmaJobTitle = jobmaJobTitle;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getInterviewMode() {
        return interviewMode;
    }

    public void setInterviewMode(String interviewMode) {
        this.interviewMode = interviewMode;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

}