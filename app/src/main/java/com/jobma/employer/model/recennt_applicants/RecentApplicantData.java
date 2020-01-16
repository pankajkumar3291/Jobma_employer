package com.jobma.employer.model.recennt_applicants;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RecentApplicantData implements Serializable {

    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;
    @SerializedName("recent_data")
    @Expose
    private List<RecentDatum> recentData = null;

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

    public List<RecentDatum> getRecentData() {
        return recentData;
    }

    public void setRecentData(List<RecentDatum> recentData) {
        this.recentData = recentData;
    }

}
