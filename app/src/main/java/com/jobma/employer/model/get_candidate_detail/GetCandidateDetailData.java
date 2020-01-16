package com.jobma.employer.model.get_candidate_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetCandidateDetailData implements Serializable {

    @SerializedName("evalution_list")
    @Expose
    private EvalutionList evalutionList;
    @SerializedName("job_name")
    @Expose
    private JobName jobName;
    @SerializedName("avg_rating")
    @Expose
    private double avgRating;
    @SerializedName("total_rated_person_count")
    @Expose
    private Integer totalRatedPersonCount;

    public EvalutionList getEvalutionList() {
        return evalutionList;
    }

    public void setEvalutionList(EvalutionList evalutionList) {
        this.evalutionList = evalutionList;
    }

    public JobName getJobName() {
        return jobName;
    }

    public void setJobName(JobName jobName) {
        this.jobName = jobName;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getTotalRatedPersonCount() {
        return totalRatedPersonCount;
    }

    public void setTotalRatedPersonCount(Integer totalRatedPersonCount) {
        this.totalRatedPersonCount = totalRatedPersonCount;
    }

}
