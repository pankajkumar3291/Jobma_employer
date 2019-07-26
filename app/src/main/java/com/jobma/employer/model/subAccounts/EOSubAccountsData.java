package com.jobma.employer.model.subAccounts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobma.employer.model.reportIssue.GetUserList;

import java.io.Serializable;
import java.util.List;

public class EOSubAccountsData implements Serializable {

    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;
    @SerializedName("getUserList")
    @Expose
    private List<GetUserList> getUserList = null;

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

    public List<GetUserList> getGetUserList() {
        return getUserList;
    }

    public void setGetUserList(List<GetUserList> getUserList) {
        this.getUserList = getUserList;
    }

}
