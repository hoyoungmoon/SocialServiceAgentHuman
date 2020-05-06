package com.project.realproject;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.project.realproject.helpers.Formatter.*;
import static java.util.Calendar.*;

public class Salary {

    private User user;
    private MonthlyVacationList vacationList;
    private ArrayList<Vacation> vacations;
    private Date startDate;
    private Date lastDate;
    private boolean isIncludedInPeriod = true;               // 소집일, 소집해제일을 포함하지 않고 넘어갔을 경우 false
    private boolean isBootCampIncludedInOneMonth = false;    // 훈련소가 두달에 걸치지 않고 한달에 다 포함될 경우 true
    private boolean isBootCampCalculationIncluded = false;   // 이번달에 훈련소비를 넣어야할때 true
    private int searchMonthLength;

    public Salary(Context context, Date today) {
        this.user = new User(context);
        this.vacationList = initializeVacationList(context, today);
        this.vacations = vacationList.getVacations();
    }

    // 직접 계산기 (임의의 시작일, 마지막일 설정하여 월급계산하도록)
    public Salary(Context context, Date startDate, Date lastDate) {
        this.user = new User(context);
        this.startDate = startDate;
        this.lastDate = lastDate;
        this.vacationList = null;
        this.vacations = null;
    }

    private MonthlyVacationList initializeVacationList(Context context, Date today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(DATE, 1);
        startDate = calendar.getTime();
        calendar.add(MONTH, 1);
        calendar.add(DATE, -1);
        lastDate = calendar.getTime();
        searchMonthLength = getDateLength(startDate, lastDate);

        if (startDate.compareTo(user.getFirstDateTime()) < 0) {
            startDate = user.getFirstDateTime();
            if (lastDate.compareTo(user.getFirstDateTime()) < 0) {
                lastDate = startDate;
                isIncludedInPeriod = false;
            }
        }

        if (lastDate.compareTo(user.getLastDateTime()) > 0) {
            lastDate = user.getLastDateTime();
            if (startDate.compareTo(user.getLastDateTime()) > 0) {
                startDate = lastDate;
                isIncludedInPeriod = false;
            }
        }

        if (user.isBootCampCalculationIncluded()) {
            if (isBootCampStartDateIncluded() && !isBootCampEndDateIncluded()) {
                calendar.setTime(user.getBootCampStartDateTime());
                calendar.add(DATE, -1);
                lastDate = calendar.getTime();
            }

            if (!isBootCampStartDateIncluded() && isBootCampEndDateIncluded()) {
                calendar.setTime(user.getBootCampEndDateTime());
                calendar.add(DATE, 1);
                startDate = calendar.getTime();
                isBootCampCalculationIncluded = true;
            }

            if (isBootCampStartDateIncluded() && isBootCampEndDateIncluded()) {
                isBootCampIncludedInOneMonth = true;
                isBootCampCalculationIncluded = true;
            }
        }

        return new MonthlyVacationList(context, formatter.format(startDate), formatter.format(lastDate));
    }


    public int calculateTotalBaseSalary(){
        return user.getBaseSalary(startDate) * getDateLength(startDate, lastDate) / searchMonthLength;
    }

    public String getBaseSalaryInfo(){
        return decimalFormat2.format(user.getBaseSalary(startDate)) + " x " + "(" + getDateLength(startDate, lastDate)
                + "/" + searchMonthLength + "일)";
    }

    public String getMealSalaryInfo(){
        return decimalFormat2.format(user.getMealCost()) + " x " + countNumberOfMeal() + "일";
    }

    public String getTrafficSalaryInfo(){
        return decimalFormat2.format(user.getTrafficCost()) + " x " + countNumberOfTraffic() + "일";
    }

    /*
    public String getSalaryToolTip() {
        int searchLength = getDateLength(startDate, lastDate);
        int sumOfMeal = user.getMealCost() * countNumberOfMeal();
        int sumOfTraffic = user.getTrafficCost() * countNumberOfTraffic();
        int sumOfBaseSalary = user.getBaseSalary(startDate) * searchLength / searchMonthLength;

        String toolTipText = ("<b>기본급여</b><br>" + decimalFormat2.format(sumOfBaseSalary) + " <b>|</b> "
                + decimalFormat2.format(user.getBaseSalary(startDate)) + " x " + "(" + searchLength
                + "/" + searchMonthLength + "일)" + "<br><br>" + "<b>식비</b><br>"
                + decimalFormat2.format(sumOfMeal) + " <b>|</b> "
                + decimalFormat2.format(user.getMealCost()) + " x " + countNumberOfMeal() + "일" + "<br><br>"
                + "<b>교통비</b><br>" + decimalFormat2.format(sumOfTraffic) + " <b>|</b> "
                + decimalFormat2.format(user.getTrafficCost()) + " x " + countNumberOfTraffic() + "일");

        if(isBootCampCalculationIncluded){
            toolTipText += getBootCampToolTip();
        }
        toolTipText += "<br><br> 공휴일 출근횟수에서 제외";

        return toolTipText;
    }

     */

    private int getDateLengthExceptBootCamp() {
        return searchMonthLength - getDateLength(user.getBootCampStartDateTime(), user.getBootCampEndDateTime());
    }

    public int calculateTotalSalary() {
        int searchLength;
        if (isBootCampIncludedInOneMonth) {
            searchLength = getDateLengthExceptBootCamp();
        } else {
            searchLength = getDateLength(startDate, lastDate);
        }

        int sumOfBaseSalary = user.getBaseSalary(startDate) * searchLength / searchMonthLength;

        return (int) Math.round((sumOfBaseSalary + calculateMealSalary() +
                calculateTrafficSalary() + calculateBootCampSalary()) / 100.0) * 100;
    }

    public int calculateBootCampSalary() {

        if (user.isBootCampCalculationIncluded() && isBootCampCalculationIncluded) {
            double total = 0;
            Calendar calendar = Calendar.getInstance();
            Date searchDate = user.getBootCampStartDateTime();
            calendar.setTime(searchDate);
            while (searchDate.compareTo(user.getBootCampEndDateTime()) <= 0) {
                total += ((double)user.getBaseSalary(searchDate) / (double)getMonthLength(searchDate));
                calendar.add(DATE, 1);
                searchDate = calendar.getTime();
            }
            return (int) total;
        } else {
            return 0;
        }
    }

    public String getBootCampToolTip() {
        String returnString = "";
        int previousBaseSalary;
        int previousWorkDay = 0;
        int nextBaseSalary;
        int nextWorkDay = 0;
        boolean isMonthChanged = false;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getBootCampStartDateTime());
        Date searchDate = calendar.getTime();

        previousBaseSalary = user.getBaseSalary(user.getBootCampStartDateTime());
        nextBaseSalary = user.getBaseSalary(user.getBootCampEndDateTime());

        while (searchDate.compareTo(user.getBootCampEndDateTime()) <= 0) {
            if (!isMonthChanged) {
                if (isMonthChanged(searchDate)) isMonthChanged = true;
                previousWorkDay++;
            } else {
                nextWorkDay++;
            }
            calendar.add(DATE, 1);
            searchDate = calendar.getTime();
        }

        if (isMonthChanged) {
            returnString += decimalFormat2.format(previousBaseSalary) + " x " + "(" + previousWorkDay
                    + "/" + getMonthLength(user.getBootCampStartDateTime()) + "일) +\n" + decimalFormat2.format(nextBaseSalary)
                    + " x " + "(" + nextWorkDay + "/" + getMonthLength(user.getBootCampEndDateTime()) + "일)";
        } else {
            returnString +=
                    decimalFormat2.format(previousBaseSalary) + " x " + "(" + previousWorkDay
                            + "/" + getMonthLength(user.getBootCampStartDateTime()) + "일)";
        }
        return returnString;
    }

    private boolean isBootCampStartDateIncluded() {
        return startDate.compareTo(user.getBootCampStartDateTime()) <= 0 &&
                lastDate.compareTo(user.getBootCampStartDateTime()) >= 0;
    }

    private boolean isBootCampEndDateIncluded() {
        return startDate.compareTo(user.getBootCampEndDateTime()) <= 0 &&
                lastDate.compareTo(user.getBootCampEndDateTime()) >= 0;
    }

    public boolean isBootCampIncludedThisMonth(){
        return isBootCampCalculationIncluded;
    }

    public int calculateMealSalary() {
        return user.getMealCost() * countNumberOfMeal();
    }

    public int calculateTrafficSalary() {
        return user.getTrafficCost() * countNumberOfTraffic();
    }

    public int countNumberOfMeal() {
        int total = countWorkDayExceptVacation();
        for (Vacation vacation : vacations) {
            if (vacation.getVacationType().isMealIncluded()) {
                total--;
            }
        }
        return total;
    }

    public int countNumberOfTraffic() {
        int total = countWorkDayExceptVacation();
        for (Vacation vacation : vacations) {
            if (vacation.getVacationType().isTrafficIncluded()) {
                total--;
            }
        }
        return total;
    }

    public double countNumberOfSickVacation() {
        return vacationList.getSickVacCount();
    }

    public double countNumberOfGeneralVacation() {
        return vacationList.getGeneralVacCount();
    }

    public double countNumberOfOutingVacation() {
        return vacationList.getOutingVacCount();
    }

    private int countWorkDayExceptVacation() {
        int total = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (calendar.getTime().compareTo(lastDate) <= 0) {
            if (!isIncludedInHoliday(calendar.getTime())) {
                total++;
            }
            calendar.add(DATE, 1);
        }
        return total;
    }

    private boolean isIncludedInHoliday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String[] holiday = listOfHoliday;
        if (calendar.get(YEAR) == 2020) {
            holiday = listOfHoliday2020;
        } else if (calendar.get(YEAR) == 2021) {
            holiday = listOfHoliday2021;
        } else if (calendar.get(YEAR) == 2022) {
            holiday = listOfHoliday2022;
        }

        String onlyMonthAndDate = dateFormat.format(calendar.getTime()).substring(4);
        return (calendar.get(DAY_OF_WEEK) == SATURDAY || calendar.get(DAY_OF_WEEK) == SUNDAY ||
                Arrays.asList(listOfHoliday).contains(onlyMonthAndDate) || Arrays.asList(holiday).contains(onlyMonthAndDate));
    }

    public boolean isIncludedInPeriod() {
        return isIncludedInPeriod;
    }

    public MonthlyVacationList getMonthlyVacationList() {
        return vacationList;
    }

    private boolean isMonthChanged(Date previous) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(previous);
        int previousMonth = calendar.get(MONTH) + 1;
        calendar.add(DATE, 1);
        int nextMonth = calendar.get(MONTH) + 1;
        return previousMonth != nextMonth;
    }
}
