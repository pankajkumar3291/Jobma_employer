package com.jobma.employer.model.interviewKit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOInterviewKitData implements Serializable {

    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;
    @SerializedName("kit_list")
    @Expose
    private List<EOKitList> kitList = null;

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

    public List<EOKitList> getKitList() {
        return kitList;
    }

    public void setKitList(List<EOKitList> kitList) {
        this.kitList = kitList;
    }

}