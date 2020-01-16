package com.jobma.employer.model.get_interview;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOGetInterviewRequest {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("data")
    @Expose
    private EOInterviewData data;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public EOInterviewData getData() {
        return data;
    }

    public void setData(EOInterviewData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}