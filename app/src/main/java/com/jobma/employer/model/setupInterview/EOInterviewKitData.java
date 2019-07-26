package com.jobma.employer.model.setupInterview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOInterviewKitData implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("catcher_id")
    @Expose
    private Integer catcherId;
    @SerializedName("title")
    @Expose
    private String title;

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

}
