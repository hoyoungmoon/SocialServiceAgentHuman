package com.project.realproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.project.realproject.helpers.DBHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.project.realproject.helpers.Formatter.*;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class User {
    private DBHelper dbHelper;
    private int id;
    private String nickName;
    private String firstDate;
    private String lastDate;
    private int baseSalary;
    private String grade;
    private Date firstDateTime;
    private Date lastDateTime;
    private Date promotionDateTime;
    private int mealCost;
    private int trafficCost;
    private int totalFirstVac;
    private int totalSecondVac;
    private int totalSickVac;
    private int payDay;

    private SharedPreferences sharedPreferences;
    private SharedPreferences promotion;
    private int decimalPlaces;
    private boolean percentIsChange;
    private boolean bootCampCalculationInclude;
    private boolean isPromotionDateChanged;
    private String bootCampStartDate;
    private Date bootCampStartDateTime;
    private String bootCampEndDate;
    private Date bootCampEndDateTime;

    public User() {
    }

    public User(String nickName, String firstDate, String lastDate, int mealCost, int trafficCost,
                int totalFirstVac, int totalSecondVac, int totalSickVac, int payDay) {
        try {
            this.nickName = nickName;
            this.firstDate = firstDate;
            this.firstDateTime = formatter.parse(firstDate);
            this.lastDate = lastDate;
            this.lastDateTime = formatter.parse(lastDate);
            this.mealCost = mealCost;
            this.trafficCost = trafficCost;
            this.totalFirstVac = totalFirstVac;
            this.totalSecondVac = totalSecondVac;
            this.totalSickVac = totalSickVac;
            this.payDay = payDay;
            //setGrade();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public User(Context context) {
        try {
            dbHelper = DBHelper.getInstance(context);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            percentIsChange = sharedPreferences.getBoolean("percentIsChange", true);
            decimalPlaces = sharedPreferences.getInt("decimalPlaces", 7);
            bootCampCalculationInclude = sharedPreferences.getBoolean("bootCampInclude", false);
            bootCampStartDate = sharedPreferences.getString("bootCampStart", "2020-01-01");
            bootCampEndDate = sharedPreferences.getString("bootCampEnd", "2020-01-29");
            bootCampStartDateTime = formatter.parse(bootCampStartDate);
            bootCampEndDateTime = formatter.parse(bootCampEndDate);
            isPromotionDateChanged = sharedPreferences.getBoolean("isPromotionDateChanged", false);

            if(dbHelper.getDataCount(DBHelper.TABLE_USER) != 0) {
                Cursor c = dbHelper.query(userColumns_ver_2, DBHelper.TABLE_USER,
                        null, null, null, null, null);
                c.moveToFirst();

                nickName = c.getString(1);
                firstDate = c.getString(2);
                firstDateTime = formatter.parse(c.getString(2));
                lastDate = c.getString(3);
                lastDateTime = formatter.parse(c.getString(3));
                mealCost = c.getInt(4);
                trafficCost = c.getInt(5);
                totalFirstVac = c.getInt(6);
                totalSecondVac = c.getInt(7);
                totalSickVac = c.getInt(8);
                payDay = c.getInt(9);
                setGrade();
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getMonthsOfService(Date baseDate, Date searchDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        int startYear = calendar.get(YEAR);
        int startMonth = calendar.get(MONTH) + 1;
        calendar.setTime(searchDate);
        int year = calendar.get(YEAR);
        int month = calendar.get(MONTH) + 1;
        return ((year - startYear) * 12) + (month - startMonth);
    }

    private void setGrade() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(YEAR);
        calendar.setTime(firstDateTime);
        calendar.set(DATE, 1);
        int diff = getMonthsOfService(firstDateTime, Calendar.getInstance().getTime());

        if (isPromotionDateChanged) {
            setGradeByPeriod(diff, sharedPreferences.getInt("firstPromotion", 2),
                    sharedPreferences.getInt("secondPromotion", 8), sharedPreferences.getInt("thirdPromotion", 14));
        } else {
            if (year <= 2019) {
                setGradeByPeriod(diff, 3, 10, 17);
            } else {
                setGradeByPeriod(diff, 2, 8, 14);
            }
        }
    }

    private void setGradeByPeriod(int diff, int firstPromotion, int secondPromotion, int thirdPromotion) {
        if (diff < firstPromotion) {
            grade = "이병";
            baseSalary = payDependsOnRankAfter2020[0];
            promotionDateTime = getPromotionDate(firstDateTime, "second");
        } else if (diff < secondPromotion) {
            grade = "일병";
            baseSalary = payDependsOnRankAfter2020[1];
            promotionDateTime = getPromotionDate(firstDateTime, "third");
        } else if (diff < thirdPromotion) {
            grade = "상병";
            baseSalary = payDependsOnRankAfter2020[2];
            promotionDateTime = getPromotionDate(firstDateTime, "fourth");
        } else {
            grade = "병장";
            baseSalary = payDependsOnRankAfter2020[3];
            promotionDateTime = lastDateTime;
        }
    }

    public Date getPromotionDate(Date startDate, String grade) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(DATE, 1);

        int differenceBefore2020 = 0;
        int differenceAfter2020 = 0;
        int modifyPeriod = 0;
        int difference;
        switch (grade) {
            case "second":
                differenceBefore2020 = 3;
                differenceAfter2020 = 2;
                modifyPeriod = sharedPreferences.getInt("firstPromotion", 2);
                break;

            case "third":
                differenceBefore2020 = 10;
                differenceAfter2020 = 8;
                modifyPeriod = sharedPreferences.getInt("secondPromotion", 8);
                break;

            case "fourth":
                differenceBefore2020 = 17;
                differenceAfter2020 = 14;
                modifyPeriod = sharedPreferences.getInt("thirdPromotion", 14);
                break;
        }

        while (true) {
            if (isPromotionDateChanged) {
                difference = modifyPeriod;
            } else {
                difference = (calendar.get(YEAR) >= 2020) ? differenceAfter2020 : differenceBefore2020;
            }

            if (getMonthsOfService(startDate, calendar.getTime()) < difference) {
                calendar.add(MONTH, 1);
            } else {
                return calendar.getTime();
            }
        }
    }

    private int BaseSalary(Date searchDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        int year = calendar.get(YEAR);
        int diff = getMonthsOfService(firstDateTime, searchDate);

        if (isPromotionDateChanged) {
            return setBaseSalaryByGrade(diff, sharedPreferences.getInt("firstPromotion", 2),
                    sharedPreferences.getInt("secondPromotion", 8), sharedPreferences.getInt("thirdPromotion", 14));
        } else {
            if (year <= 2019) {
                return setBaseSalaryByGrade(diff, 3, 10, 17);
            } else {
                return setBaseSalaryByGrade(diff, 2, 8, 14);
            }
        }
    }

    private int setBaseSalaryByGrade(int diff, int firstPromotion, int secondPromotion, int thirdPromotion) {
        if (diff < firstPromotion) {
            return payDependsOnRankAfter2020[0];
        } else if (diff < secondPromotion) {
            return payDependsOnRankAfter2020[1];
        } else if (diff < thirdPromotion) {
            return payDependsOnRankAfter2020[2];
        } else {
            return payDependsOnRankAfter2020[3];
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getGrade() {
        return grade;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
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

    public Date getFirstDateTime() {
        return firstDateTime;
    }

    public Date getLastDateTime() {
        return lastDateTime;
    }

    public int getBaseSalary(Date searchDate) {
        return BaseSalary(searchDate);
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public boolean isPercentChanging() {
        return percentIsChange;
    }

    public boolean isBootCampCalculationIncluded() {
        return bootCampCalculationInclude;
    }

    public String getRankToolTip() {
        return "<b>계급</b> | " + grade + "<br>"
                + "<b>전체복무일</b> | " + getEntireServicePeriod() + "일<br>"
                + "<b>현재복무일</b> | " + getCurrentServicePeriod() + "일<br><br>"
                + "<b>현재 기본급</b> | " + decimalFormat.format(baseSalary) + "원<br><br>"
                + "<b>다음 진급일</b> | " + dateFormat_dot.format(promotionDateTime);
    }

    private long getEntireServicePeriod() {
        return (lastDateTime.getTime() - firstDateTime.getTime()) / dayIntoMilliSecond;
    }

    private long getCurrentServicePeriod() {
        return (Calendar.getInstance().getTimeInMillis() - firstDateTime.getTime()) / dayIntoMilliSecond;
    }

    public long getRemainServicePeriod() {
        return getEntireServicePeriod() - getCurrentServicePeriod();
    }

    public String getBootCampStartDate() {
        return bootCampStartDate;
    }

    public String getBootCampEndDate() {
        return bootCampEndDate;
    }

    public Date getBootCampStartDateTime() {
        return bootCampStartDateTime;
    }

    public Date getBootCampEndDateTime() {
        return bootCampEndDateTime;
    }
}
