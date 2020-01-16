package com.jobma.employer.model.get_interview_question_ans;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class InterviewQuestionData {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("jobma_post_id")
    @Expose
    private Integer jobmaPostId;
    @SerializedName("jobma_pitcher_id")
    @Expose
    private Integer jobmaPitcherId;
    @SerializedName("jobma_answers")
    @Expose
    private JobmaAnswers jobmaAnswers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJobmaPostId() {
        return jobmaPostId;
    }

    public void setJobmaPostId(Integer jobmaPostId) {
        this.jobmaPostId = jobmaPostId;
    }

    public Integer getJobmaPitcherId() {
        return jobmaPitcherId;
    }

    public void setJobmaPitcherId(Integer jobmaPitcherId) {
        this.jobmaPitcherId = jobmaPitcherId;
    }

    public JobmaAnswers getJobmaAnswers() {
        return jobmaAnswers;
    }

    public void setJobmaAnswers(JobmaAnswers jobmaAnswers) {
        this.jobmaAnswers = jobmaAnswers;
    }
}
