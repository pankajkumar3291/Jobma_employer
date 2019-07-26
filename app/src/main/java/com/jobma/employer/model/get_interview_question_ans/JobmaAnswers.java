package com.jobma.employer.model.get_interview_question_ans;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JobmaAnswers {
    @SerializedName("question")
    @Expose
    private List<InterViewQuestion> question = null;
    public List<InterViewQuestion> getQuestion() {
        return question;
    }
    public void setQuestion(List<InterViewQuestion> question) {
        this.question = question;
    }
}
