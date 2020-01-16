package com.jobma.employer.model.candidateTrack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvitedDatum {

    //pitcher
    @SerializedName(value = "pitcher", alternate = "pitcher_data")
    @Expose
    private Pitcher pitcher;
    @SerializedName("jobma_pitcher_id")
    @Expose
    private Integer jobmaPitcherId;
    @SerializedName("acceptation")
    @Expose
    private String acceptation;
    @SerializedName("applied_id")
    @Expose
    private Integer appliedId;

    public Integer getAppliedId() {
        return appliedId;
    }

    public void setAppliedId(Integer appliedId) {
        this.appliedId = appliedId;
    }

    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("current_status")
    @Expose
    private String currentStatus;

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    @SerializedName("live_interview_payment_status")
    @Expose
    private String liveInterviewStatus;

    public String getLiveInterviewStatus() {
        return liveInterviewStatus;
    }

    public void setLiveInterviewStatus(String liveInterviewStatus) {
        this.liveInterviewStatus = liveInterviewStatus;
    }

    @SerializedName("pre_recorded_payment_status")
    @Expose
    private String preRecordedPaymentStatus;

    public String getPreRecordedPaymentStatus() {
        return preRecordedPaymentStatus;
    }

    public void setPreRecordedPaymentStatus(String preRecordedPaymentStatus) {
        this.preRecordedPaymentStatus = preRecordedPaymentStatus;
    }

    //jobma_invitation_date
    @SerializedName("jobma_interview_mode")
    @Expose
    private String jobmaInterviewMode;
    @SerializedName(value = "jobma_invitation_date", alternate = "jobma_applied_date")
    @Expose
    private String jobmaInvitationDate;
    @SerializedName("job_id")
    @Expose
    private Integer jobId;
    @SerializedName("apply_mode")
    @Expose
    private String applyMode;

    public String getApplyMode() {
        return applyMode;
    }

    public void setApplyMode(String applyMode) {
        this.applyMode = applyMode;
    }

    @SerializedName("interview_mode")
    @Expose
    private String interviewMode;
    @SerializedName("acceptation_data")
    @Expose
    private String acceptationData;

    public Pitcher getPitcher() {
        return pitcher;
    }

    public void setPitcher(Pitcher pitcher) {
        this.pitcher = pitcher;
    }

    public Integer getJobmaPitcherId() {
        return jobmaPitcherId;
    }

    public void setJobmaPitcherId(Integer jobmaPitcherId) {
        this.jobmaPitcherId = jobmaPitcherId;
    }

    public String getAcceptation() {
        return acceptation;
    }

    public void setAcceptation(String acceptation) {
        this.acceptation = acceptation;
    }

    public String getJobmaInterviewMode() {
        return jobmaInterviewMode;
    }

    public void setJobmaInterviewMode(String jobmaInterviewMode) {
        this.jobmaInterviewMode = jobmaInterviewMode;
    }

    public String getJobmaInvitationDate() {
        return jobmaInvitationDate;
    }

    public void setJobmaInvitationDate(String jobmaInvitationDate) {
        this.jobmaInvitationDate = jobmaInvitationDate;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getInterviewMode() {
        return interviewMode;
    }

    public void setInterviewMode(String interviewMode) {
        this.interviewMode = interviewMode;
    }

    public String getAcceptationData() {
        return acceptationData;
    }

    public void setAcceptationData(String acceptationData) {
        this.acceptationData = acceptationData;
    }

}
