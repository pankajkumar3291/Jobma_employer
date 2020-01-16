package com.jobma.employer.model.chat_history;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Agent{

    @SerializedName("jobma_agent_id")
    @Expose
    private Integer jobmaAgentId;
    @SerializedName("agent_name")
    @Expose
    private String agentName;

    public Integer getJobmaAgentId() {
        return jobmaAgentId;
    }

    public void setJobmaAgentId(Integer jobmaAgentId) {
        this.jobmaAgentId = jobmaAgentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

}