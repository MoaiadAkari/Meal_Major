package com.canadiancoders.backend.goals;

import jakarta.persistence.*;

@Entity
@Table(name = "join_plan_goals")
public class PlanGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_pk")
    private Integer joinPk;

    @ManyToOne
    @JoinColumn(name = "weekly_fk", nullable = false)
    private WeeklyPlan weeklyPlan;

    @ManyToOne
    @JoinColumn(name = "goal_fk", nullable = false)
    private Goal goal;

    @Column(name = "complete")
    private Boolean complete = false;

    @Column(name = "progress_count")
    private Integer progressCount = 0;

    public PlanGoal() {
    }

    public Integer getJoinPk() {
        return joinPk;
    }

    public void setJoinPk(Integer joinPk) {
        this.joinPk = joinPk;
    }

    public WeeklyPlan getWeeklyPlan() {
        return weeklyPlan;
    }

    public void setWeeklyPlan(WeeklyPlan weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
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