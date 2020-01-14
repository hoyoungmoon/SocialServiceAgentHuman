package com.project.realproject;

import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class PayDependsOnMonth {

    private String firstDate;
    private static PayDependsOnMonth mPayDependsOnMonth = new PayDependsOnMonth();
    private static final int[] payDependsOnRankBefore2020 = new int[]{306100, 331300, 366200, 405700};
    private static final int[] payDependsOnRankAfter2020 = new int[]{408100, 441600, 488200, 540900};

    public static PayDependsOnMonth getInstance(String firstDate){
        mPayDependsOnMonth.firstDate = firstDate;
        return mPayDependsOnMonth;
    }

    public int payDependsOnMonth(Date searchDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        int firstYear = Integer.parseInt(firstDate.substring(0, 4));
        int firstMonth = Integer.parseInt(firstDate.substring(5, 7));
        int year = calendar.get(YEAR);
        int month = calendar.get(MONTH) + 1;
        int diff = ((year - firstYear) * 12) + (month - firstMonth);

        if (year <= 2019) {
            if (diff < 3) return payDependsOnRankBefore2020[0];
            else if (diff >= 3 && diff < 10) return payDependsOnRankBefore2020[1];
            else if (diff >= 10 && diff < 17) return payDependsOnRankBefore2020[2];
            else return payDependsOnRankBefore2020[3];
        } else {
            if (diff < 2) return payDependsOnRankAfter2020[0];
            else if (diff >= 2 && diff < 8) return payDependsOnRankAfter2020[1];
            else if (diff >= 8 && diff < 14) return payDependsOnRankAfter2020[2];
            else return payDependsOnRankAfter2020[3];
        }
    }
}
