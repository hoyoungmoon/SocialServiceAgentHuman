package com.project.realproject;

import java.util.Date;

public class Savings {
    private long monthlyPayment;
    private long monthlyPeriod;
    private double interestRate;

    // 계산기용 생성자 -> 합, 원금, 이자 나타내주자   단리, 비과세로 고정하고 계산하자 일단
    public Savings(long monthlyPayment, long monthlyPeriod, double interestRate){
        this.monthlyPayment = monthlyPayment;
        this.monthlyPeriod = monthlyPeriod;
        this.interestRate = interestRate;
    }

    public Savings(long monthlyPayment, long interestRate, Date startDate, Date endDate){
        this.monthlyPayment = monthlyPayment;
        // 날짜로 계산해야함 this.monthlyPeriod = monthlyPeriod;
        this.interestRate = interestRate;
    }

    public long calculatePrincipalSum(){
        return monthlyPayment * monthlyPeriod;
    }

    public long calculateTotalInterest(){
        return (long)(monthlyPayment * monthlyPeriod * (monthlyPeriod + 1) * interestRate / 2400);
    }

    public long calculateTotalSavings(){
        return calculateTotalInterest() + calculatePrincipalSum();
    }

}
