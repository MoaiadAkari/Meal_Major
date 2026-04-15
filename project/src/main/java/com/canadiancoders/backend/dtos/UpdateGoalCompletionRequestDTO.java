package com.canadiancoders.backend.dtos;

public class UpdateGoalCompletionRequestDTO {
    private Integer joinPk;
    private Boolean complete;
    private Integer progressCount;

    public UpdateGoalCompletionRequestDTO() {
    }

    public Integer getJoinPk() {
        return joinPk;
    }

    public void setJoinPk(Integer joinPk) {
        this.joinPk = joinPk;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Integer getProgressCount() {
        return progressCount;
    }

    public void setProgressCount(Integer progressCount) {
        this.progressCount = progressCount;
    }
}