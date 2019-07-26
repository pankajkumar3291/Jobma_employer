package com.jobma.employer.model.get_interview_question_ans;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class InterViewQuestion {

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
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("rtsp")
    @Expose
    private String rtsp;
    @SerializedName("hls")
    @Expose
    private String hls;
    @SerializedName("dash")
    @Expose
    private String dash;
    @SerializedName("fileurl")
    @Expose
    private String fileurl;
    @SerializedName("poster")
    @Expose
    private String poster;

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getRtsp() {
        return rtsp;
    }

    public void setRtsp(String rtsp) {
        this.rtsp = rtsp;
    }

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }

    public String getDash() {
        return dash;
    }

    public void setDash(String dash) {
        this.dash = dash;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
