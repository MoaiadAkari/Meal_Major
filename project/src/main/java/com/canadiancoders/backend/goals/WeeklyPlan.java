package com.canadiancoders.backend.goals;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "weekly_plan")
public class WeeklyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_pk")
    private Integer weeklyPk;

    @Column(name = "user_fk", nullable = false)
    private Integer userFk;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "mon")
    private Integer mon;

    @Column(name = "tue")
    private Integer tue;

    @Column(name = "wed")
    private Integer wed;

    @Column(name = "thu")
    private Integer thu;

    @Column(name = "fri")
    private Integer fri;

    @Column(name = "sat")
    private Integer sat;

    @Column(name = "sun")
    private Integer sun;

    public WeeklyPlan() {
    }

    public Integer getWeeklyPk() {
        return weeklyPk;
    }

    public void setWeeklyPk(Integer weeklyPk) {
        this.weeklyPk = weeklyPk;
    }

    public Integer getUserFk() {
        return userFk;
    }

    public void setUserFk(Integer userFk) {
        this.userFk = userFk;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getMon() {
        return mon;
    }

    public void setMon(Integer mon) {
        this.mon = mon;
    }

    public Integer getTue() {
        return tue;
    }

    public void setTue(Integer tue) {
        this.tue = tue;
    }

    public Integer getWed() {
        return wed;
    }

    public void setWed(Integer wed) {
        this.wed = wed;
    }

    public Integer getThu() {
        return thu;
    }

    public void setThu(Integer thu) {
        this.thu = thu;
    }

    public Integer getFri() {
        return fri;
    }

    public void setFri(Integer fri) {
        this.fri = fri;
    }

    public Integer getSat() {
        return sat;
    }

    public void setSat(Integer sat) {
        this.sat = sat;
    }

    public Integer getSun() {
        return sun;
    }

    public void setSun(Integer sun) {
        this.sun = sun;
    }
}