package com.project.realproject.fragments;


import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.project.realproject.R;
import com.project.realproject.Salary;
import com.project.realproject.Savings;
import com.project.realproject.SpecificPeriodVacationList;
import com.project.realproject.User;
import com.project.realproject.adapters.CalendarAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.project.realproject.helpers.Formatter.*;


public class SalaryInfoFragment extends DialogFragment {

    private AdView mAdView;
    private LinearLayout bootCampContainer;
    private LinearLayout savingsContainer;
    private GridView grid;

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
    private TextView savingsPeriodTextView;
    private ImageButton cancelButton;

    private Salary salary;
    private User user;
    private Savings savings;

    private long searchDateTime;
    private Date searchDate;
    private Date startDate;
    private Date lastDate;

    private Context mContext;

    public SalaryInfoFragment(){

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    public static SalaryInfoFragment newInstance(long searchDateTime) {
        SalaryInfoFragment fragment = new SalaryInfoFragment();
        Bundle args = new Bundle();
        args.putLong("searchDateTime", searchDateTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchDateTime = getArguments().getLong("searchDateTime");
        }
        this.searchDate = new Date(searchDateTime);

        user = new User(mContext);
        salary = new Salary(mContext, searchDate);
        savings = new Savings(mContext);
        this.startDate = salary.getStartDate();
        this.lastDate = salary.getLastDate();
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_salary_info, container, false);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        bootCampContainer = view.findViewById(R.id.bootCampContainer);
        savingsContainer = view.findViewById(R.id.savingsContainer);
        grid = view.findViewById(R.id.calendar_grid);

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
        savingsPeriodTextView = view.findViewById(R.id.tv_savingsPeriod);
        cancelButton = view.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        setSalaryInfo();
        setSavingsInfo();
        setCalendarInfo(mContext);
        return view;
    }



    private void setSalaryInfo(){
        String str = dateFormat_kor.format(searchDate);
        String currentMonth = str.substring(0, str.lastIndexOf("월") + 2);
        currentMonthTextView.setText(currentMonth);
        totalBaseSalaryTextView.setText(toMoneyUnit(salary.calculateTotalBaseSalary()));
        totalMealSalaryTextView.setText(toMoneyUnit(salary.calculateMealSalary()));
        totalTrafficSalaryTextView.setText(toMoneyUnit(salary.calculateTrafficSalary()));
        totalSalaryTextView.setText(toMoneyUnit(salary.calculateTotalSalary()));

        baseSalaryInfoTextView.setText(salary.getBaseSalaryInfo());
        mealSalaryInfoTextView.setText(salary.getMealSalaryInfo());
        trafficSalaryInfoTextView.setText(salary.getTrafficSalaryInfo());

        if(user.isBootCampCalculationIncluded() && salary.isBootCampIncludedThisMonth()){
            bootCampContainer.setVisibility(VISIBLE);
            totalBootCampSalaryTextView.setText(toMoneyUnit(salary.calculateBootCampSalary()));
            bootCampSalaryInfoTextView.setText(salary.getBootCampSalaryInfo());
        }
    }

    private void setSavingsInfo() {
        if (savings.isSavingsCalculationIncluded() && savings.isSavingsIncludedThisMonth(searchDate)) {
            savingsContainer.setVisibility(VISIBLE);
            totalInterestTextView.setText(toMoneyUnit(savings.calculateInterest(searchDate)));
            principalSumTextView.setText(toMoneyUnit(savings.calculatePrincipalSum(searchDate)));
            totalSavingsTextView.setText(toMoneyUnit(savings.calculateInterest(searchDate)
                    + savings.calculatePrincipalSum(searchDate)));
            savingsPeriodTextView.setText(savings.calculateSavingsPeriod(searchDate) + "개월차" +
                    (savings.isExpirationMonth(searchDate) ? "(만기)" : ""));
        }
    }

    private void updateCalendar(Context context) {

        SpecificPeriodVacationList vacationList = new SpecificPeriodVacationList(context,
                formatter.format(startDate), formatter.format(lastDate));

        try {
            final ArrayList<Date> cells = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();

            calendar.setTime(salary.getStartDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int monthEndingCell = monthBeginningCell + getDateLength(salary.getStartDate(), salary.getLastDate()) - 1;
            calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

            // fill cells
            while (cells.size() < (monthEndingCell + 1)) {
                cells.add(calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            resizeGridView(grid, cells.size(), 7);
            grid.setAdapter(new CalendarAdapter(getContext(), cells, vacationList.getVacations(),
                    monthBeginningCell, searchDate));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resizeGridView(GridView gridView, int items, int columns) {

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int oneRowHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
        int rows = (items / columns) + 1;
        params.height = oneRowHeight * rows;
        gridView.setLayoutParams(params);
    }

    private void setCalendarInfo(Context context) {
        updateCalendar(context);
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window;
        if (getDialog() == null) {
            return;
        } else {
            window = getDialog().getWindow();
        }
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        Display display = window.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
}
