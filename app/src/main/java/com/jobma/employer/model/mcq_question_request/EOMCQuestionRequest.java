package com.jobma.employer.model.mcq_question_request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOMCQuestionRequest {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("data")
    @Expose
    private EOMCQData data;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public EOMCQData getData() {
        return data;
    }

    public void setData(EOMCQData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}