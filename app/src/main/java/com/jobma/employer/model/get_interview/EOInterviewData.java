package com.jobma.employer.model.get_interview;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class EOInterviewData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("catcher_id")
    @Expose
    private Integer catcherId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("question_set")
    @Expose
    private EOInterviewQuestionSet questionSet;
    @SerializedName("ques_ids")
    @Expose
    private String quesIds;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("random_ques")
    @Expose
    private String randomQues;
    @SerializedName("random_options")
    @Expose
    private String randomOptions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCatcherId() {
        return catcherId;
    }

    public void setCatcherId(Integer catcherId) {
        this.catcherId = catcherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EOInterviewQuestionSet getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(EOInterviewQuestionSet questionSet) {
        this.questionSet = questionSet;
    }

    public String getQuesIds() {
        return quesIds;
    }

    public void setQuesIds(String quesIds) {
        this.quesIds = quesIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRandomQues() {
        return randomQues;
    }

    public void setRandomQues(String randomQues) {
        this.randomQues = randomQues;
    }

    public String getRandomOptions() {
        return randomOptions;
    }

    public void setRandomOptions(String randomOptions) {
        this.randomOptions = randomOptions;
    }
}
