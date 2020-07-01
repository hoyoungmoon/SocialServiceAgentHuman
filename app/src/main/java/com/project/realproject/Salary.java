package com.project.realproject;

import android.content.Context;

import com.project.realproject.adapters.CalendarAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.project.realproject.helpers.Formatter.*;
import static java.util.Calendar.*;

public class Salary {

    private User user;
    private SpecificPeriodVacationList vacationList;
    private ArrayList<Vacation> vacations;
    private Date startDate;
    private Date lastDate;
    private boolean isIncludedInPeriod = true;               // 소집일, 소집해제일을 포함하지 않고 넘어갔을 경우 false
    private boolean isBootCampCalculationIncluded = false;   // 이번달에 훈련소비를 넣어야할때 true
    private int searchMonthLength;
    private Calendar calendar = Calendar.getInstance();


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

    private SpecificPeriodVacationList initializeVacationList(Context context, Date today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(DATE, user.getPayDay());
        startDate = calendar.getTime();
        calendar.add(MONTH, 1); calendar.add(DATE, -1);
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

        if(user.isBootCampCalculationIncluded() && isBootCampEndDateIncluded()){
            isBootCampCalculationIncluded = true;
        }

        return new SpecificPeriodVacationList(context, formatter.format(startDate), formatter.format(lastDate));
    }

    private int countDaysToWork() {
        int total = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (calendar.getTime().compareTo(lastDate) <= 0) {
                if (!isIncludedInHoliday(calendar.getTime()) &&
                        !isIncludedInBootCampPeriod(calendar.getTime())) total++;
                calendar.add(DATE, 1);
            }
        return total;
    }

    public int calculateTotalSalary() {
        return (int) Math.round((calculateTotalBaseSalary() + calculateMealSalary() +
                calculateTrafficSalary() + calculateBootCampSalary()) / 10.0) * 10;
    }

    public double calculateTotalBaseSalary() {
        double totalBaseSalary = 0;
        calendar.setTime(startDate);
        while(calendar.getTime().compareTo(lastDate) <= 0){
            if(!isIncludedInBootCampPeriod(calendar.getTime())) {
                totalBaseSalary += (double)user.getBaseSalary(calendar.getTime()) / (double)getMonthLength(calendar.getTime());
            }
            calendar.add(DATE, 1);
        }
        return totalBaseSalary;
    }

    public String getBaseSalaryInfo() {

        int previous = 0;
        int next = 0;
        boolean isMonthChanged = false;
        calendar.setTime(startDate);
        while(calendar.getTime().compareTo(lastDate) <= 0){
            if(!isIncludedInBootCampPeriod(calendar.getTime())) {
                if (isMonthChanged) next++;
                else previous++;
            }

            if(!isMonthChanged && isMonthChanged(calendar.getTime())) isMonthChanged = true;
            calendar.add(DATE, 1);
        }
        return decimalFormat2.format(user.getBaseSalary(startDate)) + " x " + "(" +
                previous + "/" + getMonthLength(startDate) + "일)" +
                (next != 0 ? "\n + " + decimalFormat2.format(user.getBaseSalary(lastDate)) + " x " + "(" +
                next + "/" + getMonthLength(lastDate) + "일)" : "");
    }

    public int calculateMealSalary() {
        return user.getMealCost() * countNumberOfMeal();
    }

    private int countNumberOfMeal() {
        int total = countDaysToWork();
        for (Vacation vacation : vacations) {
            if (vacation.getVacationType().isMealIncluded()) {
                total--;
            }
        }
        return total;
    }

    public String getMealSalaryInfo() {
        return decimalFormat2.format(user.getMealCost()) + " x " + countNumberOfMeal() + "일";
    }

    public int calculateTrafficSalary() {
        return user.getTrafficCost() * countNumberOfTraffic();
    }

    private int countNumberOfTraffic() {
        int total = countDaysToWork();
        for (Vacation vacation : vacations) {
            if (vacation.getVacationType().isTrafficIncluded()) {
                total--;
            }
        }
        return total;
    }

    public String getTrafficSalaryInfo() {
        return decimalFormat2.format(user.getTrafficCost()) + " x " + countNumberOfTraffic() + "일";
    }


    public int calculateBootCampSalary() {

        if (user.isBootCampCalculationIncluded() && isBootCampCalculationIncluded) {
            double total = 0;
            Calendar calendar = Calendar.getInstance();
            Date searchDate = user.getBootCampStartDateTime();
            calendar.setTime(searchDate);
            while (searchDate.compareTo(user.getBootCampEndDateTime()) <= 0) {
                total += ((double) user.getBaseSalary(searchDate) / (double) getMonthLength(searchDate));
                calendar.add(DATE, 1);
                searchDate = calendar.getTime();

            }
            return (int) total;
        } else {
            return 0;
        }
    }

    public String getBootCampSalaryInfo() {
        int previousWorkDay = 0;
        int nextWorkDay = 0;
        boolean isMonthChanged = false;

        calendar.setTime(user.getBootCampStartDateTime());

        while (calendar.getTime().compareTo(user.getBootCampEndDateTime()) <= 0) {

            if (isMonthChanged) nextWorkDay++;
            else previousWorkDay++;

            if (!isMonthChanged && isMonthChanged(calendar.getTime())) isMonthChanged = true;
            calendar.add(DATE, 1);
        }

        return decimalFormat2.format(user.getBaseSalary(user.getBootCampStartDateTime())) + " x " + "(" + previousWorkDay
                + "/" + getMonthLength(user.getBootCampStartDateTime()) + "일)"
                + (nextWorkDay != 0 ? "\n + " + decimalFormat2.format(user.getBaseSalary(user.getBootCampEndDateTime()))
                + " x " + "(" + nextWorkDay + "/" + getMonthLength(user.getBootCampEndDateTime()) + "일)" : "");
    }

    private boolean isBootCampEndDateIncluded() {
        return startDate.compareTo(user.getBootCampEndDateTime()) <= 0 &&
                lastDate.compareTo(user.getBootCampEndDateTime()) >= 0;
    }

    public boolean isBootCampIncludedThisMonth() {
        return isBootCampCalculationIncluded;
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

    private boolean isIncludedInBootCampPeriod(Date date) {
        calendar.setTime(date);
        return calendar.getTime().compareTo(user.getBootCampStartDateTime()) >= 0 &&
                calendar.getTime().compareTo(user.getBootCampEndDateTime()) <= 0;
    }

    public boolean isIncludedInPeriod() {
        return isIncludedInPeriod;
    }

    private boolean isMonthChanged(Date previous) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(previous);
        int previousMonth = calendar.get(MONTH) + 1;
        calendar.add(DATE, 1);
        int nextMonth = calendar.get(MONTH) + 1;
        return previousMonth != nextMonth;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getLastDate() {
        return lastDate;
    }
}
