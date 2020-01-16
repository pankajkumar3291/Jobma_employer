package com.jobma.employer.model.feedback;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class FeedBackData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("applied_id")
    @Expose
    private Integer appliedId;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("rated_by")
    @Expose
    private String ratedBy;
    @SerializedName("recommended")
    @Expose
    private String recommended;
    @SerializedName("feedback_params")
    @Expose
    private List<FeedbackParam> feedbackParams = null;
    @SerializedName("time_updated")
    @Expose
    private String timeUpdated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppliedId() {
        return appliedId;
    }

    public void setAppliedId(Integer appliedId) {
        this.appliedId = appliedId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(String ratedBy) {
        this.ratedBy = ratedBy;
    }

    public String getRecommended() {
        return recommended;
    }

    public void setRecommended(String recommended) {
        this.recommended = recommended;
    }

    public List<FeedbackParam> getFeedbackParams() {
        return feedbackParams;
    }

    public void setFeedbackParams(List<FeedbackParam> feedbackParams) {
        this.feedbackParams = feedbackParams;
    }

    public String getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(String timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

}
