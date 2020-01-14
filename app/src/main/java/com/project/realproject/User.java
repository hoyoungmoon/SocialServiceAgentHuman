package com.project.realproject;

import java.util.Date;

public class User {
    private int id;
    private String nickName;
    private Date firstDate;
    private Date lastDate;
    private int mealCost;
    private int trafficCost;
    private int totalFirstVac;
    private int totalSecondVac;
    private int totalSickVac;
    private int payDay;

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(Date firstDate) {
        this.firstDate = firstDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public int getMealCost() {
        return mealCost;
    }

    public void setMealCost(int mealCost) {
        this.mealCost = mealCost;
    }

    public int getTrafficCost() {
        return trafficCost;
    }

    public void setTrafficCost(int trafficCost) {
        this.trafficCost = trafficCost;
    }

    public User(){}

    public int getTotalFirstVac() {
        return totalFirstVac;
    }

    public void setTotalFirstVac(int totalFirstVac) {
        this.totalFirstVac = totalFirstVac;
    }

    public int getTotalSecondVac() {
        return totalSecondVac;
    }

    public void setTotalSecondVac(int totalSecondVac) {
        this.totalSecondVac = totalSecondVac;
    }

    public int getTotalSickVac() {
        return totalSickVac;
    }

    public void setTotalSickVac(int totalSickVac) {
        this.totalSickVac = totalSickVac;
    }

    public int getPayDay() {
        return payDay;
    }

    public void setPayDay(int payDay) {
        this.payDay = payDay;
    }
}
