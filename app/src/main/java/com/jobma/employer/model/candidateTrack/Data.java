package com.jobma.employer.model.candidateTrack;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("remaining")
    @Expose
    //invited_data
    private Integer remaining;
    @SerializedName(value="invited_data", alternate= {"applied_data","evaluation_list"})
    @Expose
    private List<InvitedDatum> invitedData = null;

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

    public List<InvitedDatum> getInvitedData() {
        return invitedData;
    }

    public void setInvitedData(List<InvitedDatum> invitedData) {
        this.invitedData = invitedData;
    }

}