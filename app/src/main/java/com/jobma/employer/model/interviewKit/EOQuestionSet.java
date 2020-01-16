package com.jobma.employer.model.interviewKit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOQuestionSet implements Serializable {

    @SerializedName("question")
    @Expose
    private List<EOQuestionData> question = null;

    public List<EOQuestionData> getQuestion(){
        return question;
    }

    public void setQuestion(List<EOQuestionData> question){
        this.question = question;
    }

}