package com.jobma.employer.model.applicantReports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOApplicantReportObject implements Serializable {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("data")
    @Expose
    private EOApplicantReportData data;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public EOApplicantReportData getData() {
        return data;
    }

    public void setData(EOApplicantReportData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
