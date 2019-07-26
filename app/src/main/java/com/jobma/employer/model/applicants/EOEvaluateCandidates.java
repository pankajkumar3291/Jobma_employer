package com.jobma.employer.model.applicants;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOEvaluateCandidates {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("data")
    @Expose
    private EvaluateData data;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public EvaluateData getData() {
        return data;
    }

    public void setData(EvaluateData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

