package com.project.realproject.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.realproject.R;
import com.project.realproject.Salary;
import com.project.realproject.Savings;
import com.project.realproject.User;
import com.project.realproject.VacationList;
import com.project.realproject.helpers.DBHelper;

import java.util.Date;

import static com.project.realproject.helpers.Formatter.*;


public class SalaryInfoFragment extends DialogFragment {

    private LinearLayout bootCampContainer;
    private LinearLayout savingsContainer;

    private TextView currentMonthTextView;
    private TextView baseSalaryInfoTextView;
    private TextView totalBaseSalaryTextView;
    private TextView mealSalaryInfoTextView;
    private TextView totalMealSalaryTextView;
    private TextView trafficSalaryInfoTextView;
    private TextView totalTrafficSalaryTextView;
    private TextView totalSalaryTextView;
    private TextView bootCampSalaryInfoTextView;
    private TextView totalBootCampSalaryTextView;
    private TextView principalSumTextView;
    private TextView totalInterestTextView;
    private TextView totalSavingsTextView;

    private Salary salary;
    private User user;
    private Savings savings;

    private Date searchDate;


    public SalaryInfoFragment(Context context, Date searchDate) {
//         Required empty public constructor
        user = new User(context);
        salary = new Salary(context, searchDate);
        this.searchDate = searchDate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_salary_info, container, false);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        bootCampContainer = view.findViewById(R.id.bootCampContainer);
        savingsContainer = view.findViewById(R.id.savingsContainer);

        currentMonthTextView = view.findViewById(R.id.tv_currentMonth);
        baseSalaryInfoTextView = view.findViewById(R.id.tv_baseSalaryInfo);
        totalBaseSalaryTextView = view.findViewById(R.id.tv_totalBaseSalary);
        mealSalaryInfoTextView = view.findViewById(R.id.tv_mealSalaryInfo);
        totalMealSalaryTextView = view.findViewById(R.id.tv_totalMealSalary);
        trafficSalaryInfoTextView = view.findViewById(R.id.tv_trafficSalaryInfo);
        totalTrafficSalaryTextView = view.findViewById(R.id.tv_totalTrafficSalary);
        bootCampSalaryInfoTextView = view.findViewById(R.id.tv_bootCampSalaryInfo);
        totalBootCampSalaryTextView = view.findViewById(R.id.tv_totalBootCampSalary);
        totalSalaryTextView = view.findViewById(R.id.tv_totalSalary);
        principalSumTextView = view.findViewById(R.id.tv_principalSum);
        totalInterestTextView = view.findViewById(R.id.tv_totalInterest);
        totalSavingsTextView = view.findViewById(R.id.tv_totalSavings);

        setSalaryInfo();
        return view;
    }

    private void setSalaryInfo(){
        String str = dateFormat_kor.format(searchDate);
        String currentMonth = str.substring(0, str.lastIndexOf("월") + 2);
        currentMonthTextView.setText(currentMonth);
        totalBaseSalaryTextView.setText(decimalFormat2.format(salary.calculateTotalBaseSalary()));
        totalMealSalaryTextView.setText(decimalFormat2.format(salary.calculateMealSalary()));
        totalTrafficSalaryTextView.setText(decimalFormat2.format(salary.calculateTrafficSalary()));
        totalSalaryTextView.setText(decimalFormat2.format(salary.calculateTotalSalary()));

        baseSalaryInfoTextView.setText(salary.getBaseSalaryInfo());
        mealSalaryInfoTextView.setText(salary.getMealSalaryInfo());
        trafficSalaryInfoTextView.setText(salary.getTrafficSalaryInfo());

        if(user.isBootCampCalculationIncluded() && salary.isBootCampIncludedThisMonth()){
            bootCampContainer.setVisibility(View.VISIBLE);
            totalBootCampSalaryTextView.setText(decimalFormat2.format(salary.calculateBootCampSalary()));
            bootCampSalaryInfoTextView.setText(salary.getBootCampToolTip());
        }
    }
}
