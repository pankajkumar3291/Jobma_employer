package com.jobma.employer.model.interviewKit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOKitList implements Serializable {

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
    private EOQuestionSet questionSet;
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
    @SerializedName("video")
    @Expose
    private Integer video;
    @SerializedName("mcq")
    @Expose
    private Integer mcq;
    @SerializedName("essay")
    @Expose
    private Integer essay;

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

    public EOQuestionSet getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(EOQuestionSet questionSet) {
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

    public Integer getVideo() {
        return video;
    }

    public void setVideo(Integer video) {
        this.video = video;
    }

    public Integer getMcq() {
        return mcq;
    }

    public void setMcq(Integer mcq) {
        this.mcq = mcq;
    }

    public Integer getEssay() {
        return essay;
    }

    public void setEssay(Integer essay) {
        this.essay = essay;
    }

}