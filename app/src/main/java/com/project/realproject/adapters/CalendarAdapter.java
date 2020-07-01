package com.project.realproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.realproject.R;
import com.project.realproject.SpecialVacation;
import com.project.realproject.User;
import com.project.realproject.Vacation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.project.realproject.helpers.Formatter.*;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.LONG;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.YEAR;

public class CalendarAdapter extends ArrayAdapter {

    private int startPosition;
    private int dailyBaseSalary;

    private LayoutInflater inflater;
    private ArrayList<Date> days;
    private ArrayList<Vacation> vacations;
    private Context mContext;
    private Calendar cal = Calendar.getInstance();

    private User user;

    public CalendarAdapter(Context context, ArrayList<Date> days, ArrayList<Vacation> vacations,
                           int startPosition, Date searchDate) {
        super(context, R.layout.dialog_calendar_item, days);
        try {
            this.mContext = context;
            this.user = new User(context);
            this.days = days;
            this.vacations = vacations;
            this.startPosition = startPosition;
            this.dailyBaseSalary = user.getBaseSalary(searchDate) / getMonthLength(searchDate);
            inflater = LayoutInflater.from(context);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static class ViewHolder {
        TextView dateTextView;
        TextView salaryTextView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        Date current = days.get(position);
        int dailyBaseSalary = user.getBaseSalary(current) /  getMonthLength(current);
        cal.setTime(current);

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.dialog_calendar_item, parent, false);
            holder.dateTextView = view.findViewById(R.id.item_date);
            holder.salaryTextView = view.findViewById(R.id.item_salary);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        holder.dateTextView.setText(cal.get(DATE) + "");

        if (cal.get(DAY_OF_WEEK) == SATURDAY || cal.get(DAY_OF_WEEK) == SUNDAY
                || isIncludedInHoliday(current) || isIncludedInBootCamp(current)) {
            holder.salaryTextView.setText("+" + decimalFormat.format(Math.round((dailyBaseSalary) / 10.0) * 10));
        } else {
            holder.salaryTextView.setText(
                    "+" + decimalFormat.format(Math.round((dailyBaseSalary + user.getMealCost() + user.getTrafficCost()) / 10.0) * 10));
        }

        if (position >= startPosition) {

            int totalDailySalary = dailyBaseSalary;

            if (isIncludedInHoliday(current)) holder.dateTextView.setTextColor(
                    mContext.getResources().getColor(R.color.colorCalendarHoliday));

            if (user.isBootCampCalculationIncluded()) {
                if (isIncludedInBootCamp(current)) {
                    holder.dateTextView.setBackgroundColor(
                            mContext.getResources().getColor(R.color.colorCalendarBootCamp));
                }
            }

            for (final Vacation vacation : vacations) {
                if (vacation.getStartDate().compareTo(current) == 0) {
                    holder.dateTextView.setTextColor(mContext.getResources().getColor(R.color.colorCalendarVacation));
                    if (!vacation.getVacationType().isMealIncluded())
                        totalDailySalary += user.getMealCost();
                    if (!vacation.getVacationType().isTrafficIncluded())
                        totalDailySalary += user.getTrafficCost();
                    holder.salaryTextView.setText(
                            "+" + decimalFormat.format(Math.round(totalDailySalary / 10.0) * 10));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(mContext, "[" + vacation.getType() + "] "
                                    + vacation.getVacation(), Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                }
            }
//
//            if ((user.getFirstDateTime().compareTo(days.get(position)) > 0) || (user.getLastDateTime().compareTo(days.get(position)) < 0)) {
//                holder.dateTextView.setTextColor(
//                        mContext.getResources().getColor(R.color.colorCalendarOutOfRange));
//                holder.salaryTextView.setVisibility(View.GONE);
//            }
            return view;
        } else {
            holder.dateTextView.setText("");
            holder.salaryTextView.setText("");
            return view;
        }
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
        return (Arrays.asList(listOfHoliday).contains(onlyMonthAndDate) || Arrays.asList(holiday).contains(onlyMonthAndDate));
    }

    private boolean isIncludedInBootCamp(Date date) {
        return (date.compareTo(user.getBootCampStartDateTime()) >= 0 &&
                date.compareTo(user.getBootCampEndDateTime()) <= 0);
    }
}

