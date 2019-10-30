package com.example.realproject;

import java.util.Date;

public class User {
    private int id;
    private String nickName;
    private Date firstDate;
    private Date lastDate;
    private int mealCost;
    private int trafficCost;

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

    public User(){};

}
