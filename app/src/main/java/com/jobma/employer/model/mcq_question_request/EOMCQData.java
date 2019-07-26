package com.jobma.employer.model.mcq_question_request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOMCQData {

    @SerializedName("qid")
    @Expose
    private Integer qid;
    @SerializedName("qtype")
    @Expose
    private Integer qtype;
    @SerializedName("qcontent")
    @Expose
    private String qcontent;

    public Integer getQid() {
        return qid;
    }

    public void setQid(Integer qid) {
        this.qid = qid;
    }

    public Integer getQtype() {
        return qtype;
    }

    public void setQtype(Integer qtype) {
        this.qtype = qtype;
    }

    public String getQcontent() {
        return qcontent;
    }

    public void setQcontent(String qcontent) {
        this.qcontent = qcontent;
    }

}
