package com.jobma.employer.model.get_interview;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class EOInterviewQuestionSet {

    @SerializedName("question")
    @Expose
    private List<EOInterViewQuestion> question = null;

    public List<EOInterViewQuestion> getQuestion() {
        return question;
    }

    public void setQuestion(List<EOInterViewQuestion> question){
        this.question = question;
    }

}
