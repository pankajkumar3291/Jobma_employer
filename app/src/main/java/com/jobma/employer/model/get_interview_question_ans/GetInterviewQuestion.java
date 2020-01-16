package com.jobma.employer.model.get_interview_question_ans;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetInterviewQuestion {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private InterviewQuestionData data;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InterviewQuestionData getData() {
        return data;
    }

    public void setData(InterviewQuestionData data) {
        this.data = data;
    }

}