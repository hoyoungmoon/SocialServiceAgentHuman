package com.project.realproject.helpers;

import android.animation.LayoutTransition;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.*;

public class Formatter {
    public static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyyMMdd", Locale.ENGLISH);
    public static final SimpleDateFormat dateFormat_dot = new SimpleDateFormat(
            "yyyy.MM.dd", Locale.ENGLISH);
    public static final SimpleDateFormat dateFormat_kor = new SimpleDateFormat(
            "yyyy년 MM월 dd일", Locale.ENGLISH);
    public static final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    public static final DecimalFormat decimalFormat2 = new DecimalFormat("###,###,###원");
    public static final String[] listOfSickVac = new String[]{"병가", "오전지참", "오후조퇴", "병가외출"};
    public static final String[] listOfVacExceptSpecialVac = new String[]{"연가", "오전반가", "오후반가", "외출"};
    public static final String[] listOfVac = new String[]{"연가", "오전반가", "오후반가", "외출", "특별휴가", "청원휴가", "공가"};

    public static final int dayIntoMilliSecond = 60 * 60 * 24 * 1000;
    public static String[] userColumns = new String[]{"id", "nickName", "firstDate", "lastDate",
            "mealCost", "trafficCost", "totalFirstVac", "totalSecondVac", "totalSickVac"};
    public static String[] vacationColumns = new String[]{"id", "vacation", "startDate", "type", "count"};
    public static final int[] payDependsOnRankBefore2020 = new int[]{306100, 331300, 366200, 405700};
    public static final int[] payDependsOnRankAfter2020 = new int[]{408100, 441700, 488200, 540900};
    public static final String[] listOfHoliday = new String[]{"0101", "0301", "0505", "0606", "0815",
            "1003", "1009", "1225"};
    public static final String[] listOfHoliday2020 = new String[]{"0124", "0127", "0415", "0430", "0930",
            "1001", "1002"};
    public static final String[] listOfHoliday2021 = new String[]{"0211", "0212", "0519", "0920", "0921",
            "0922"};
    public static final String[] listOfHoliday2022 = new String[]{"0131", "0201", "0202", "0909"};

    public static final String toMoneyUnit(double money) {
        return decimalFormat2.format(Math.round((money) / 10.0) * 10);
    }

    public static void setLayoutTransition(LinearLayout viewGroup) {
        LayoutTransition lt = new LayoutTransition();
        lt.setDuration(200);
        lt.enableTransitionType(LayoutTransition.APPEARING);
        lt.disableTransitionType(LayoutTransition.DISAPPEARING);
        lt.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
        lt.disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);

        viewGroup.setLayoutTransition(lt);
    }

    public static int getDateLength(Date firstDate, Date lastDate) {
        int returnValue = (int) (((lastDate.getTime() - firstDate.getTime()) / dayIntoMilliSecond) + 1);
        return returnValue > 0 ? returnValue : 0;
    }

    public static int getDateLengthExceptSaturdayAndSunday(Date firstDate, Date lastDate){
        int count = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDate);
        while(calendar.getTime().compareTo(lastDate) <= 0){
            if(calendar.get(Calendar.DAY_OF_WEEK) == SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == SUNDAY) {
                count++;
            }
            calendar.add(DATE, 1);
        }
        return getDateLength(firstDate, lastDate) - count;
    }

    public static int getMonthLength(Date searchDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);

        return calendar.getActualMaximum(DAY_OF_MONTH) - calendar.getActualMinimum(DAY_OF_MONTH) + 1;
    }


    public static int getTagOnlyInt(String tag) {
        if(tag.equals("")) return 0;
        String reTag = tag.replaceAll("[^0-9]", "");
        return Integer.parseInt(reTag);
    }
}
