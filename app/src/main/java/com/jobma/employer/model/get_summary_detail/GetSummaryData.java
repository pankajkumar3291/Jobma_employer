package com.jobma.employer.model.get_summary_detail;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
public class GetSummaryData {

    @SerializedName("rating_data")
    @Expose
    private Object ratingData;
    @SerializedName("avg_rating")
    @Expose
    private String avgRating;
    @SerializedName("recommended_yes")
    @Expose
    private String recommendedYes;
    @SerializedName("recommended_no")
    @Expose
    private String recommendedNo;

    public Object getRatingData() {
        return ratingData;
    }

    public void setRatingData(Object ratingData) {
        this.ratingData = ratingData;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getRecommendedYes() {
        return recommendedYes;
    }

    public void setRecommendedYes(String recommendedYes) {
        this.recommendedYes = recommendedYes;
    }

    public String getRecommendedNo() {
        return recommendedNo;
    }

    public void setRecommendedNo(String recommendedNo) {
        this.recommendedNo = recommendedNo;
    }
}
