package com.canadiancoders.backend.goals;

import jakarta.persistence.*;

@Entity
@Table(name = "ref_goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_pk")
    private Integer goalPk;

    @Column(name = "goal")
    private String goal;

    // number of times this goal needs to be completed before it's done
    @Column(name = "required_iterations", nullable = false)
    private Integer requiredIterations;

    public Goal() {
    }

    public Integer getGoalPk() {
        return goalPk;
    }

    public void setGoalPk(Integer goalPk) {
        this.goalPk = goalPk;
    }

    public String getGoalName() {
        return goal;
    }

    public void setGoalName(String goal) {
        this.goal = goal;
    }

    // getter/setter to retrieve and set the number of iterations required for this goal
    public Integer getRequiredIterations() {
        return requiredIterations;
    }

    public void setRequiredIterations(Integer requiredIterations) {
        this.requiredIterations = requiredIterations;
    }
}