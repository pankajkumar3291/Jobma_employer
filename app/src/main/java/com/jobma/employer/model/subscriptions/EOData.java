package com.jobma.employer.model.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOData {

    @SerializedName("expiry_date")
    @Expose
    private String expiryDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("plan")
    @Expose
    private String plan;
    @SerializedName("credit_value")
    @Expose
    private String creditValue;
    @SerializedName("live_interview")
    @Expose
    private String liveInterview;
    @SerializedName("pre_recoreded")
    @Expose
    private String preRecoreded;
    @SerializedName("cost_type")
    @Expose
    private String costType;

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public String getLiveInterview() {
        return liveInterview;
    }

    public void setLiveInterview(String liveInterview) {
        this.liveInterview = liveInterview;
    }

    public String getPreRecoreded() {
        return preRecoreded;
    }

    public void setPreRecoreded(String preRecoreded) {
        this.preRecoreded = preRecoreded;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

}