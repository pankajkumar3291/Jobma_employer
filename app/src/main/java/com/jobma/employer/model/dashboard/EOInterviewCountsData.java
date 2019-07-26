package com.jobma.employer.model.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOInterviewCountsData implements Serializable {

    @SerializedName("On-Hold")
    @Expose
    private Integer onHold;
    @SerializedName("Selected")
    @Expose
    private Integer selected;
    @SerializedName("Rejected")
    @Expose
    private Integer rejected;
    @SerializedName("Application")
    @Expose
    private Integer application;
    @SerializedName("Pending")
    @Expose
    private Integer pending;

    public Integer getOnHold() {
        return onHold;
    }

    public void setOnHold(Integer onHold) {
        this.onHold = onHold;
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }

    public Integer getRejected() {
        return rejected;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }

    public Integer getApplication() {
        return application;
    }

    public void setApplication(Integer application) {
        this.application = application;
    }

    public Integer getPending() {
        return pending;
    }

    public void setPending(Integer pending) {
        this.pending = pending;
    }
}
