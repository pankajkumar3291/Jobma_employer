package com.jobma.employer.model.chat_history;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("title")
    @Expose
    private Title title;
    @SerializedName("chatdata")
    @Expose
    private List<Chatdatum> chatdata = null;
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public List<Chatdatum> getChatdata() {
        return chatdata;
    }

    public void setChatdata(List<Chatdatum> chatdata) {
        this.chatdata = chatdata;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

}