package com.jobma.employer.model.interviewKit;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOQuestionData implements Serializable {

    @SerializedName("ques")
    @Expose
    private Integer ques;
    @SerializedName("ques_title")
    @Expose
    private String quesTitle;
    @SerializedName("qtype")
    @Expose
    private String qtype;
    @SerializedName("file_name")
    @Expose
    private String fileName;
    @SerializedName("thinktime")
    @Expose
    private String thinktime;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("attempts")
    @Expose
    private String attempts;
    @SerializedName("correct")
    @Expose
    private String correct;
    @SerializedName("optional")
    @Expose
    private String optional;
    @SerializedName("options")
    @Expose
    private List<String> options = null;
    public EOQuestionData(Integer ques, String quesTitle, String qtype, String fileName, String thinktime, String duration, String attempts, String correct, String optional, List<String> options) {
        this.ques = ques;
        this.quesTitle = quesTitle;
        this.qtype = qtype;
        this.fileName = fileName;
        this.thinktime = thinktime;
        this.duration = duration;
        this.attempts = attempts;
        this.correct = correct;
        this.optional = optional;
        this.options = options;
    }
    public Integer getQues() {
        return ques;
    }

    public void setQues(Integer ques) {
        this.ques = ques;
    }

    public String getQuesTitle() {
        return quesTitle;
    }

    public void setQuesTitle(String quesTitle) {
        this.quesTitle = quesTitle;
    }

    public String getQtype() {
        return qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getThinktime() {
        return thinktime;
    }

    public void setThinktime(String thinktime) {
        this.thinktime = thinktime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAttempts() {
        return attempts;
    }

    public void setAttempts(String attempts) {
        this.attempts = attempts;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

}