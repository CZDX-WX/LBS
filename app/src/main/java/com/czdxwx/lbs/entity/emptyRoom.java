package com.czdxwx.lbs.entity;


import net.tsz.afinal.annotation.sqlite.Table;

import java.io.Serializable;

@Table(name="room")
public class emptyRoom implements Serializable {
    private int id;
    private String name;
    private String school;
    private String building;
    private String week;
    private String weekday;
    private String number;

    public emptyRoom() {
    }

    public emptyRoom(int id, String name, String school, String building, String week, String weekday, String number) {
        this.id = id;
        this.name = name;
        this.school = school;
        this.building = building;
        this.week = week;
        this.weekday = weekday;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }



    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
