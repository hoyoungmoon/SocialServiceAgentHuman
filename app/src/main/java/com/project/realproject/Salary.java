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
    private SpecificPeriodVacationList vacationList;
    private ArrayList<Vacation> vacations;
    private Date startDate;
    private Date lastDate;
    private Date maximumDate;
    private Date minimumDate;
    private boolean isIncludedInPeriod = true;               // 소집일, 소집해제일을 포함하지 않고 넘어갔을 경우 false
    private boolean isBootCampIncludedInOneMonth = false;    // 훈련소가 두달에 걸치지 않고 한달에 다 포함될 경우 true
    private boolean isEntireMonthIncludedInBootCamp = false; // 훈련소가 한달 전체를 포함하며 시작, 끝이 없을때
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

    private SpecificPeriodVacationList initializeVacationList(Context context, Date today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(DATE, 1);
        minimumDate = startDate = calendar.getTime();
        calendar.set(DATE, calendar.getActualMaximum(DAY_OF_MONTH));
        maximumDate = lastDate = calendar.getTime();
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

        // 훈련소가 포함되어 있을 때 startDate, lastDate 수정
        if (user.isBootCampCalculationIncluded()) {
            // 훈련소 시작일만 포함되어 있을 때
            if (isBootCampStartDateIncluded() && !isBootCampEndDateIncluded()) {
                calendar.setTime(user.getBootCampStartDateTime());
                calendar.add(DATE, -1);
                // 훈련소 시작일이 월초일 때 ( == 한달 전체가 훈련소에 포함)
                if (calendar.getTime().compareTo(startDate) >= 0) {
                    lastDate = calendar.getTime();
                } else {
                    lastDate = maximumDate;
                    isEntireMonthIncludedInBootCamp = true;
                }
                // 훈련소 종료일만 포함되어 있을 때
            } else if (!isBootCampStartDateIncluded() && isBootCampEndDateIncluded()) {
                calendar.setTime(user.getBootCampEndDateTime());
                calendar.add(DATE, 1);
                // 훈련소 종료일이 월말일 때 ( == 한달 전체가 훈련소에 포함)
                if (calendar.getTime().compareTo(lastDate) <= 0) {
                    startDate = calendar.getTime();
                } else {
                    startDate = minimumDate;
                    isEntireMonthIncludedInBootCamp = true;
                }
                isBootCampCalculationIncluded = true;
                // 훈련소 시작일, 종료일 모두 포함되어 있을 때
            } else if (isBootCampStartDateIncluded() && isBootCampEndDateIncluded()) {
                isBootCampIncludedInOneMonth = true;
                isBootCampCalculationIncluded = true;
                // 훈련소 시작일, 종료일 모두 포함되지 않으며 한달 전체가 훈련소에 포함될 때
            } else if (isEntireMonthIncludedInBootCamp()) {
                isEntireMonthIncludedInBootCamp = true;
            }
        }

        return new SpecificPeriodVacationList(context, formatter.format(startDate), formatter.format(lastDate));
    }

    private int countDaysInService() {
        if (isBootCampIncludedInOneMonth)
            return getDateLength(startDate, user.getBootCampStartDateTime())
                    + getDateLength(user.getBootCampEndDateTime(), lastDate) - 2;
        if (isEntireMonthIncludedInBootCamp) return 0;
        return getDateLength(startDate, lastDate);
    }

    private int countDaysToWork() {
        int total = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        if (isEntireMonthIncludedInBootCamp) return total;

        if (!isBootCampIncludedInOneMonth) {
            while (calendar.getTime().compareTo(lastDate) <= 0) {
                if (!isIncludedInHoliday(calendar.getTime())) total++;
                calendar.add(DATE, 1);
            }
        } else {
            while (calendar.getTime().compareTo(user.getBootCampStartDateTime()) < 0) {
                if (!isIncludedInHoliday(calendar.getTime())) total++;
                calendar.add(DATE, 1);
            }
            calendar.setTime(user.getBootCampEndDateTime());
            calendar.add(DATE, 1);
            while (calendar.getTime().compareTo(lastDate) <= 0) {
                if (!isIncludedInHoliday(calendar.getTime())) total++;
                calendar.add(DATE, 1);
            }
        }
        return total;
    }

    public int calculateTotalSalary() {
        return (int) Math.round((calculateTotalBaseSalary() + calculateMealSalary() +
                calculateTrafficSalary() + calculateBootCampSalary()) / 10.0) * 10;
    }

    public int calculateTotalBaseSalary() {
        return user.getBaseSalary(startDate) * countDaysInService() / searchMonthLength;
    }

    public String getBaseSalaryInfo() {
        return decimalFormat2.format(user.getBaseSalary(startDate)) + " x " + "(" +
                countDaysInService() + "/" + searchMonthLength + "일)";
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

    private boolean isEntireMonthIncludedInBootCamp() {
        return startDate.compareTo(user.getBootCampStartDateTime()) > 0 &&
                lastDate.compareTo(user.getBootCampEndDateTime()) < 0;
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

    public boolean isIncludedInPeriod() {
        return isIncludedInPeriod;
    }

    public SpecificPeriodVacationList getMonthlyVacationList() {
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

    public Date getStartDate() {
        return startDate;
    }

    public Date getLastDate() {
        return lastDate;
    }
}
