package com.project.realproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.YEAR;

public class Savings {
    private long monthlyPayment;
    private long monthlyPeriod;
    private double interestRate;
    SharedPreferences preferences;
    // 계산기용 생성자 -> 합, 원금, 이자 나타내주자   단리, 비과세로 고정하고 계산하자 일단
    public Savings(long monthlyPayment, long monthlyPeriod, double interestRate){
        this.monthlyPayment = monthlyPayment;
        this.monthlyPeriod = monthlyPeriod;
        this.interestRate = interestRate;
        //this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Savings(Context context){
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.monthlyPayment = preferences.getInt("savingsPayment", 200000);
        this.monthlyPeriod = preferences.getInt("savingsPeriod", 21);
        this.interestRate = (double)preferences.getFloat("savingsInterestRate", (float) 1.5);
    }

    public boolean isSavingsCalculationIncluded(){
        return preferences.getBoolean("savingsInclude", false);
    }

    public boolean isSavingsIncludedThisMonth(Date searchDate){
        return calculateSavingsPeriod(searchDate) != 0;
    }

    public long calculatePrincipalSum(Date searchDate){
        return monthlyPayment * calculateSavingsPeriod(searchDate);
    }

    public long calculateInterest(Date searchDate){
        int period = calculateSavingsPeriod(searchDate);
        return (long)(monthlyPayment * period * (period + 1) * interestRate / 2400);
    }

    public int calculateSavingsPeriod(Date searchDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        int period = (calendar.get(YEAR) - preferences.getInt("savingsYear", 2020)) * 12
                + ((calendar.get(Calendar.MONTH) + 1) - preferences.getInt("savingsMonth", 1)) + 1;
        return (period < 0 || period > monthlyPeriod) ? 0 : period;
    }

    public boolean isExpirationMonth(Date searchDate){
        return calculateSavingsPeriod(searchDate) == monthlyPeriod;
    }

    public long calculateTotalPrincipalSum(){
        return monthlyPayment * monthlyPeriod;
    }

    public long calculateTotalInterest(){
        return (long)(monthlyPayment * monthlyPeriod * (monthlyPeriod + 1) * interestRate / 2400);
    }

    public long calculateTotalSavings(){
        return calculateTotalInterest() + calculateTotalPrincipalSum();
    }

}
