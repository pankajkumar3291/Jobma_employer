package com.jobma.employer.model.candidate_rating;

public class CandidateRatingModel {

    private String ratingType;
    private float ratingCount;

    public CandidateRatingModel(String ratingType, int ratingCount) {
        this.ratingType = ratingType;
        this.ratingCount = ratingCount;
    }

    public void setRatingType(String ratingType) {
        this.ratingType = ratingType;
    }

    public void setRatingCount(float ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getRatingType() {
        return ratingType;
    }

    public float getRatingCount() {
        return ratingCount;
    }
}
