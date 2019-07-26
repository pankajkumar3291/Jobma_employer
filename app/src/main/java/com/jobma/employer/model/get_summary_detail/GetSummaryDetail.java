package com.jobma.employer.model.get_summary_detail;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSummaryDetail {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("data")
    @Expose
    private GetSummaryData data;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public GetSummaryData getData() {
        return data;
    }

    public void setData(GetSummaryData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}