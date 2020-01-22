package com.project.realproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.project.realproject.fragments.BlankFragment;
import com.project.realproject.R;
import com.project.realproject.fragments.SettingUserInfoFragment;
import com.project.realproject.fragments.VacListFragment;
import com.project.realproject.fragments.VacSaveFragment;
import static com.project.realproject.helpers.Formatter.*;
import com.project.realproject.helpers.DBHelper;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import it.sephiroth.android.library.xtooltip.ClosePolicy;
import it.sephiroth.android.library.xtooltip.Tooltip;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.Calendar.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DBHelper DBmanager = null;

    private static String[] userColumns = new String[]{"id", "nickName", "firstDate", "lastDate",
            "mealCost", "trafficCost", "totalFirstVac", "totalSecondVac", "totalSickVac", "payDay"};
    private static String[] vacationColumns = new String[]{"id", "vacation", "startDate", "type", "count"};
    private static final int[] payDependsOnRankBefore2020 = new int[]{306100, 331300, 366200, 405700};
    private static final int[] payDependsOnRankAfter2020 = new int[]{408100, 441700, 488200, 540900};
    private static final String[] listOfHoliday = new String[]{"0101", "0301", "0505", "0606", "0815",
            "1003", "1009", "1225"};
    private static final String[] listOfHoliday2020 = new String[]{"0124", "0127", "0415", "0430", "0930",
            "1001", "1002"};
    private static final String[] listOfHoliday2021 = new String[]{"0211", "0212", "0519", "0920", "0921",
            "0922"};
    private static final String[] listOfHoliday2022 = new String[]{"0131", "0201", "0202", "0909"};
    private static final int dayIntoMilliSecond = 60 * 60 * 24 * 1000;
    public enum vacType {firstYearVac, secondYearVac, sickVac}
    public static Context mContext;
    private String firstDate;
    private String lastDate;
    private String pivotDate;
    private String pivotPlusOneDate;
    private int mealCost;
    private int trafficCost;
    private int totalFirstVac;
    private int totalSecondVac;
    private int totalSickVac;
    private int payDay;
    private String searchStartDate;
    private String toolTipText;


    private TextView firstVacRemain;
    private TextView secondVacRemain;
    private TextView sickVacRemain;
    private TextView firstVacTotal;
    private TextView secondVacTotal;
    private TextView sickVacTotal;
    private TextView nickNameTextView;
    private TextView dDayTextView;
    private TextView servicePeriodTextView;
    private TextView searchPeriodTextView;
    private ImageView goToNextPeriod;
    private ImageView goToPreviousPeriod;
    private TextView salaryTextView;
    private TextView thisMonthOuting;
    private TextView thisMonthVac;
    private TextView thisMonthSickVac;
    private ProgressBar progressBar;
    private TextView progressPercentage;
    private TextView entireService;
    private TextView currentService;
    private TextView remainService;
    private ImageView nav_button;
    private ImageView cal_button;
    private ImageView toolTipRank;
    private ImageView toolTipPay;

    // countDownTimer values
    private CountDownTimer countDownTimer;
    private boolean timerIsRunning;
    private long entire;
    private long current;

    // sharedPreference values
    private boolean percentIsChange;
    private boolean bootCampCalculationInclude;
    private String bootCampStart;
    private String bootCampEnd;
    private Date bootCampStartDate;
    private Date bootCampEndDate;
    private int decimalPlaces;

    private LinearLayout vacCard1;
    private LinearLayout vacCard2;
    private LinearLayout vacCard3;
    private LinearLayout container;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        DBmanager = new DBHelper(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        percentIsChange = preferences.getBoolean("percentIsChange", true);
        decimalPlaces = preferences.getInt("decimalPlaces", 7);

       // 관련 data 초기화
        try {
            bootCampCalculationInclude = preferences.getBoolean("bootCampInclude", false);
            bootCampStart = preferences.getString("bootCampStart", "2020-01-01");
            bootCampEnd = preferences.getString("bootCampEnd", "2020-01-29");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatter.parse(bootCampStart));
            bootCampStartDate = calendar.getTime();
            calendar.setTime(formatter.parse(bootCampEnd));
            bootCampEndDate = calendar.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        mContext = this;

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        nav_button = findViewById(R.id.navigation_drawer_button);
        cal_button = findViewById(R.id.calculator_button);
        nickNameTextView = findViewById(R.id.tv_nickName);
        dDayTextView = findViewById(R.id.tv_dDay);
        servicePeriodTextView = findViewById(R.id.tv_servicePeriod);
        searchPeriodTextView = findViewById(R.id.tv_search_period);
        goToNextPeriod = findViewById(R.id.iv_search_next_period);
        goToPreviousPeriod = findViewById(R.id.iv_search_previous_period);
        salaryTextView = findViewById(R.id.tv_salary);
        thisMonthOuting = findViewById(R.id.thisMonthOuting);
        thisMonthVac = findViewById(R.id.thisMonthVac);
        thisMonthSickVac = findViewById(R.id.thisMonthSickVac);
        progressBar = findViewById(R.id.progressBar);
        progressPercentage = findViewById(R.id.progress_percentage);
        entireService = findViewById(R.id.tv_entireService);
        currentService = findViewById(R.id.tv_currentService);
        remainService = findViewById(R.id.tv_remainService);
        vacCard1 = findViewById(R.id.vacCardView_1);
        vacCard2 = findViewById(R.id.vacCardView_2);
        vacCard3 = findViewById(R.id.vacCardView_3);
        container = findViewById(R.id.fragment_container_1);
        toolTipRank = findViewById(R.id.btn_rank_info);
        toolTipPay = findViewById(R.id.btn_pay_info);

        Button spendButton = findViewById(R.id.spendButton_1);
        Button spendButton_2 = findViewById(R.id.spendButton_2);
        Button spendButton_3 = findViewById(R.id.spendButton_3);
        firstVacRemain = findViewById(R.id.first_vacation_remain);
        secondVacRemain = findViewById(R.id.second_vacation_remain);
        sickVacRemain = findViewById(R.id.sick_vacation_remain);
        firstVacTotal = findViewById(R.id.first_vacation_total);
        secondVacTotal = findViewById(R.id.second_vacation_total);
        sickVacTotal = findViewById(R.id.sick_vacation_total);

        vacCard1.setOnClickListener(this);
        vacCard2.setOnClickListener(this);
        vacCard3.setOnClickListener(this);
        spendButton.setOnClickListener(this);
        spendButton_2.setOnClickListener(this);
        spendButton_3.setOnClickListener(this);
        goToNextPeriod.setOnClickListener(this);
        goToPreviousPeriod.setOnClickListener(this);
        cal_button.setOnClickListener(this);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        load();
    }

    public void load() {
        if (DBmanager.getDataCount(DBHelper.TABLE_USER) != 0) {
            setUserProfile();
            setRemainVac();
            setSearchStartDate();
            setThisMonthInfo(searchStartDate);
            progressBar.setProgress((int) getPercentage());
            if (percentIsChange) {
                if (!timerIsRunning && DBmanager.getDataCount(DBHelper.TABLE_USER) != 0) {
                    resetTimer();
                    startTimer();
                }
            } else {
                resetTimer();
            }

            progressPercentage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    int barWidth = progressBar.getRight() - progressBar.getLeft();
                    float barStartX = progressBar.getX();
                    float position_X = (progressBar.getX() + (barWidth * (float) getPercentage() / 100)) - 40;
                    if (position_X < barStartX) {
                        progressPercentage.setX(barStartX);
                    } else if (position_X > barWidth - barStartX - 10 - (30 * decimalPlaces)) {
                        progressPercentage.setX(barWidth - barStartX - 10 - (30 * decimalPlaces));
                    } else {
                        progressPercentage.setX((progressBar.getX() + (barWidth * (float) getPercentage() / 100)) - 40);
                    }
                }
            });

        } else {
            SettingUserInfoFragment dialog = new SettingUserInfoFragment();
            FragmentManager fg = getSupportFragmentManager();
            dialog.show(fg, "dialog");
        }
    }

    public long getCurrentSecond() {
        try {
            return (Calendar.getInstance().getTime().getTime() - formatter.parse(firstDate).getTime()) / 50;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getEntireSecond() {
        try {
            return (formatter.parse(lastDate).getTime() - formatter.parse(firstDate).getTime()) / 50;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getPercentage() {
        return (100 * getCurrentSecond() / getEntireSecond());
    }


    public void startTimer() {
        countDownTimer = new CountDownTimer(entire, 50) {

            @Override
            public void onTick(long millisUntilFinished) {
                current++; // 0.05초 지날때마다 0.05초씩 더해주는 것과 같다.
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerIsRunning = false;
            }
        }.start();
        timerIsRunning = true;
    }

    public void pauseTimer() {
        countDownTimer.cancel();
        timerIsRunning = false;
    }

    public void resetTimer() {
        entire = getEntireSecond();
        current = getCurrentSecond();
        updateCountDownText();
    }

    public void updateCountDownText() {
        double p = ((double) current / (double) entire) * 100;
        progressPercentage.setText(String.format("%." + decimalPlaces + "f", p) + "%");
    }

    @Override
    public void onClick(View view) {

        Calendar calendar = Calendar.getInstance();
        FragmentManager fg = getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        VacSaveFragment dialog;

        switch (view.getId()) {
            case R.id.vacCardView_1:
                setListView(R.id.first_vacation_image, ft, firstDate, pivotDate, vacType.firstYearVac);
                break;

            case R.id.vacCardView_2:
                setListView(R.id.second_vacation_image, ft, pivotPlusOneDate, lastDate, vacType.secondYearVac);
                break;

            case R.id.vacCardView_3:
                setListView(R.id.sick_vacation_image, ft, firstDate, lastDate, vacType.sickVac);
                break;

            case R.id.spendButton_1:
                dialog = new VacSaveFragment().newInstance(firstDate, pivotDate, vacType.firstYearVac, searchStartDate);
                dialog.show(fg, "dialog");
                break;

            case R.id.spendButton_2:
                dialog = new VacSaveFragment().newInstance(pivotPlusOneDate, lastDate, vacType.secondYearVac, searchStartDate);
                dialog.show(fg, "dialog");
                break;

            case R.id.spendButton_3:
                dialog = new VacSaveFragment().newInstance(firstDate, lastDate, vacType.sickVac, searchStartDate);
                dialog.show(fg, "dialog");
                break;

            case R.id.iv_search_next_period:
                try {
                    calendar.setTime(dateFormat.parse(searchStartDate));
                    calendar.add(MONTH, 1);
                    searchStartDate = dateFormat.format(calendar.getTime());
                    if (!setThisMonthInfo(searchStartDate)) {
                        calendar.add(MONTH, -1);
                        searchStartDate = dateFormat.format(calendar.getTime());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.iv_search_previous_period:
                try {
                    calendar.setTime(dateFormat.parse(searchStartDate));
                    calendar.add(MONTH, -1);
                    searchStartDate = dateFormat.format(calendar.getTime());
                    if (!setThisMonthInfo(searchStartDate)) {
                        calendar.add(MONTH, 1);
                        searchStartDate = dateFormat.format(calendar.getTime());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.calculator_button:
                Intent calculator = new Intent(MainActivity.this, CalculatorActivity.class);
                startActivity(calculator);
                break;
        }
    }

    public void setListView(int imageId, FragmentTransaction ft, String lowerBoundDate,
                            String upperBoundDate, vacType typeOfVac) {
        ImageView imageView = findViewById(imageId);
        Fragment fragment;

        if (container.getVisibility() == GONE) {
            switch (typeOfVac) {
                case firstYearVac:
                    vacCard1.setVisibility(VISIBLE);
                    vacCard2.setVisibility(GONE);
                    vacCard3.setVisibility(GONE);
                    break;
                case secondYearVac:
                    vacCard1.setVisibility(GONE);
                    vacCard2.setVisibility(VISIBLE);
                    vacCard3.setVisibility(GONE);
                    break;
                case sickVac:
                    vacCard1.setVisibility(GONE);
                    vacCard2.setVisibility(GONE);
                    vacCard3.setVisibility(VISIBLE);
                    break;
            }

            imageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
            fragment = new VacListFragment().newInstance(lowerBoundDate, upperBoundDate, firstDate,
                    lastDate, typeOfVac, searchStartDate);

            ft.replace(R.id.fragment_container_1, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            container.setVisibility(VISIBLE);
        } else {
            vacCard1.setVisibility(VISIBLE);
            vacCard2.setVisibility(VISIBLE);
            vacCard3.setVisibility(VISIBLE);
            container.setVisibility(GONE);
            imageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
            ft.replace(R.id.fragment_container_1, new BlankFragment(), "empty");
            ft.commit();
        }
    }

    public void toolTipRank(View view) {
        try {
            if (view == toolTipRank || view == nickNameTextView) {
                view = toolTipRank;
                Calendar calendar = Calendar.getInstance();
                Calendar today = Calendar.getInstance();
                calendar.setTime(formatter.parse(firstDate));
                calendar.set(DATE, 1);
                int currentPay = payDependsOnMonth(today.getTime());
                String rank = null;

                if (currentPay == payDependsOnRankBefore2020[0]) {
                    calendar.add(MONTH, 3);
                    rank = "이등병 (2020년 개정전 기준)";
                } else if (currentPay == payDependsOnRankAfter2020[0]) {
                    calendar.add(MONTH, 2);
                    rank = "이등병 (2020년 개정후 기준)";
                } else if (currentPay == payDependsOnRankBefore2020[1]) {
                    calendar.add(MONTH, 10);
                    rank = "일병 (2020년 개정전 기준)";
                } else if (currentPay == payDependsOnRankAfter2020[1]) {
                    calendar.add(MONTH, 8);
                    rank = "일병 (2020년 개정후 기준)";
                } else if (currentPay == payDependsOnRankBefore2020[2]) {
                    calendar.add(MONTH, 17);
                    rank = "상병 (2020년 개정전 기준)";
                } else if (currentPay == payDependsOnRankAfter2020[2]) {
                    calendar.add(MONTH, 14);
                    rank = "상병 (2020년 개정후 기준)";
                } else if (currentPay == payDependsOnRankBefore2020[3]) {
                    calendar.setTime(formatter.parse(lastDate));
                    rank = "병장 (2020년 개정전 기준)";
                } else if (currentPay == payDependsOnRankAfter2020[3]) {
                    calendar.setTime(formatter.parse(lastDate));
                    rank = "병장 (2020년 개정후 기준)";
                }
                toolTipText = ("<b>계급</b> | " + rank + "<br><br>" + "<b>현재 기본급</b> | "
                        + decimalFormat.format(currentPay) + "원<br><br>" + "<b>다음 진급일</b> | "
                        + dateFormat_dot.format(calendar.getTime()) + "");
            } else if (view == toolTipPay || view == salaryTextView) {
                view = toolTipPay;
                setThisMonthInfo(searchStartDate);
            }

            Tooltip toolTip = new Tooltip.Builder(this)
                    .styleId(R.style.ToolTipLayoutCustomStyle)
                    .text(toolTipText)
                    .anchor(view, 0, 0, false)
                    .activateDelay(0)
                    .showDuration(20000)
                    .closePolicy(new ClosePolicy.Builder()
                            .inside(true)
                            .outside(true)
                            .build())
                    .arrow(true)
                    .create();
            if (view == toolTipRank) {
                toolTip.show(view, Tooltip.Gravity.RIGHT, false);
            } else if (view == toolTipPay) {
                toolTip.show(view, Tooltip.Gravity.LEFT, false);
            }

        } catch (ParseException e) {

        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        SettingUserInfoFragment dialog = new SettingUserInfoFragment();
        FragmentManager fg = getSupportFragmentManager();
        switch (menuItem.getItemId()) {
            case R.id.profile:
                dialog.show(fg, "dialog");
                if (timerIsRunning) pauseTimer();
                break;
            case R.id.setting:
                Intent setting = new Intent(MainActivity.this, SettingExtraActivity.class);
                startActivity(setting);
                break;
            case R.id.manual:
                Intent manual = new Intent(MainActivity.this, ManualActivity.class);
                startActivity(manual);
                break;
            case R.id.calculator:
                Intent calculator = new Intent(MainActivity.this, CalculatorActivity.class);
                startActivity(calculator);
                break;
            case R.id.feedback:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/Text");
                email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"alleyoops.app@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report));
                startActivity(email);
                break;
            case R.id.rating:
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.reset:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.reset_alert)
                        .setCancelable(false)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DBmanager.deleteAll();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                new SettingUserInfoFragment().show(fragmentManager, "dialog");
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
        }
        return false;
    }

    public void setUserProfile() {
        if (DBmanager.getDataCount(DBHelper.TABLE_USER) != 0) {
            Cursor c = DBmanager.query(userColumns, DBHelper.TABLE_USER,
                    null, null, null, null, null);
            c.moveToFirst();
            firstDate = c.getString(2);
            lastDate = c.getString(3);
            mealCost = c.getInt(4);
            trafficCost = c.getInt(5);
            totalFirstVac = c.getInt(6);
            totalSecondVac = c.getInt(7);
            totalSickVac = c.getInt(8);
            payDay = c.getInt(9);

            nickNameTextView.setText(c.getString(1));
            servicePeriodTextView.setText(firstDate + " ~ " + lastDate);
            countDdayFromToday();

            firstVacTotal.setText(" / " + totalFirstVac);
            secondVacTotal.setText(" / " + totalSecondVac);
            sickVacTotal.setText(" / " + totalSickVac);

            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(formatter.parse(firstDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            cal.add(YEAR, 1);
            Date pivotTime = cal.getTime();
            cal.add(DATE, 1);
            Date pivotPlusOneTime = cal.getTime();
            pivotDate = formatter.format(pivotTime);
            pivotPlusOneDate = formatter.format(pivotPlusOneTime);
            c.close();
        }
    }

    public int payDependsOnMonth(Date searchDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        int firstYear = Integer.parseInt(firstDate.substring(0, 4));
        int firstMonth = Integer.parseInt(firstDate.substring(5, 7));
        int year = calendar.get(YEAR);
        int month = calendar.get(MONTH) + 1;
        int diff = ((year - firstYear) * 12) + (month - firstMonth);

        // 2020년을 기준으로도 알아야함
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

    public void setRemainVac() {
        Cursor c = DBmanager.query(vacationColumns, DBHelper.TABLE_VACATION,
                null, null, null, null, null);
        try {
            double firstCount = (double) totalFirstVac * 480;
            double secondCount = (double) totalSecondVac * 480;
            double thirdCount = (double) totalSickVac * 480;
            long firstTime = formatter.parse(firstDate).getTime();
            long lastTime = formatter.parse(lastDate).getTime();
            long pivotTime = formatter.parse(pivotDate).getTime();
            while (c.moveToNext()) {
                String startDate = c.getString(2);
                String type = c.getString(3);
                long startTime = formatter.parse(startDate).getTime();

                // 특별휴가(특별, 청원, 공가) count를 차감하지 않는다
                if (type.equals("병가") || type.equals("오전지참")
                        || type.equals("오후조퇴") || type.equals("병가외출")) {
                    thirdCount -= c.getDouble(4);
                } else if(type.equals("연가") || type.equals("오전반가")
                        || type.equals("오후반가") || type.equals("외출")){
                    if(startTime - firstTime >= 0 && pivotTime - startTime >= 0){
                        firstCount -= c.getDouble(4);
                    }else if(startTime - pivotTime > 0 && lastTime - startTime >= 0){
                        secondCount -= c.getDouble(4);
                    }
                }
            }
            c.close();
            firstVacRemain.setText(convertMinuteToProperUnit((int) firstCount));
            secondVacRemain.setText(convertMinuteToProperUnit((int) secondCount));
            sickVacRemain.setText(convertMinuteToProperUnit((int) thirdCount));
        } catch (ParseException e) {
        }
    }


    public boolean setThisMonthInfo(String searchDate) {
        int first, last, entireLength;
        Calendar cal = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        // 월급 검색기간 first(시작일), last(종료일) 기간을 정한다
        try {
            cal.setTime(dateFormat.parse(searchDate));
            int year = cal.get(YEAR);
            int month = cal.get(MONTH) + 1;
            int tmp = (year * 10000) + (month * 100) + (payDay);
            c.set(year, month - 1, payDay);
            Date compareDate = dateFormat.parse(tmp + "");
            long diff = cal.getTimeInMillis() - compareDate.getTime();
            // 이번달 월급날이 지난것
            if (diff >= 0) {
                c.add(MONTH, 1);
                c.add(DATE, -1);
                first = tmp;
                last = ((c.get(YEAR) * 10000) + ((c.get(MONTH) + 1) * 100)) + c.get(DATE);
                entireLength = getDateLength(first, last);
                first = checkFirstDayInclude(first);
                last = checkLastDayInclude(last);
            }
            // 이번달 월급날이 지나지 않은것
            else {
                c.add(MONTH, -1);
                first = ((c.get(YEAR) * 10000) + ((c.get(MONTH) + 1) * 100)) + c.get(DATE);
                c.add(MONTH, 1);
                c.add(DATE, -1);
                last = ((c.get(YEAR) * 10000) + ((c.get(MONTH) + 1) * 100)) + c.get(DATE);
                entireLength = getDateLength(first, last);
                first = checkFirstDayInclude(first);
                last = checkLastDayInclude(last);
            }

            // 검색기간 시작일이 종료일보다 작은지 확인
            if (last - first >= 0) {
                searchPeriodTextView.setText(intToDateFormatString(first) + " ~ " + intToDateFormatString(last));
                setThisMonthSpendVac(first, last, entireLength);
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setThisMonthSpendVac(int first_tmp, int last_tmp, int entireLength) throws ParseException {

        Calendar cal = Calendar.getInstance();
        double sum_sickVac = 0;
        double sum_Vac = 0;
        double sum_outing = 0;
        // 출근횟수 구하기
        int numberOfEntire; // 전체 날짜수
        int numberOfWork; // 출근횟수 넣기 (토, 일, 변하지않는공휴일, 변하는공휴일(추석, 설, 부처님) 빼고 카운트)
        int numberOfMeal;
        int numberOfTraffic;
        double pay = 0;
        boolean isPromoted = false;
        boolean isAmended = false;
        boolean isBootCampIncluded = false;
        int payBeforePromotion;
        int payAfterPromotion = 0;
        int numberOfAfterPromotion = 0;

        Date first_payDate = dateFormat.parse(first_tmp + "");
        Date last_payDate = dateFormat.parse(last_tmp + "");
        numberOfEntire = getDateLength(first_tmp, last_tmp);
        numberOfWork = numberOfEntire;
        Date searchDate = first_payDate;
        cal.setTime(searchDate);

        payBeforePromotion = payDependsOnMonth(searchDate);
        double checkPromotion = (double) (payDependsOnMonth(searchDate) / entireLength);
        int checkYear = cal.get(YEAR);
        String[] holiday = listOfHoliday;

        while (searchDate.compareTo(last_payDate) <= 0) {
            cal.setTime(searchDate);
            String onlyMonthAndDate = dateFormat.format(searchDate).substring(4);

            // 공휴일, 주말 일한 일수에서 차감
            if (cal.get(YEAR) == 2020) {
                holiday = listOfHoliday2020;
            } else if (cal.get(YEAR) == 2021) {
                holiday = listOfHoliday2021;
            } else if (cal.get(YEAR) == 2022) {
                holiday = listOfHoliday2022;
            }

            double payPerDay = (double) (payDependsOnMonth(searchDate) / entireLength);
            // 하루 기본급이 바뀌면 진급 또는 2020년 개정 반영된 것이므로 따로 count시작
            int year = cal.get(YEAR);
            if (checkPromotion != payPerDay) {
                if (checkYear == 2019 && year == 2020) {
                    isAmended = true;
                } else {
                    isPromoted = true;
                }
                payAfterPromotion = payDependsOnMonth(searchDate);
                numberOfAfterPromotion++;
            }

            // 토, 일, 공휴일, 훈련소가 포함되어 있으면 근무일수에서 제외
            if (cal.get(Calendar.DAY_OF_WEEK) == SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == SUNDAY ||
                    Arrays.asList(listOfHoliday).contains(onlyMonthAndDate) || Arrays.asList(holiday).contains(onlyMonthAndDate) ||
                    checkBootCampInclude(cal.getTime())){
                if(bootCampCalculationInclude && checkBootCampInclude(cal.getTime())){
                    numberOfEntire--;
                    pay -= payPerDay;  // 출근한날 아니므로 pay에서는 뺀다
                    isBootCampIncluded = true;  // tooltip에 훈련소 월급 나타낸다
                    numberOfWork--;
                } else if(cal.get(Calendar.DAY_OF_WEEK) == SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == SUNDAY ||
                        Arrays.asList(listOfHoliday).contains(onlyMonthAndDate) || Arrays.asList(holiday).contains(onlyMonthAndDate)){
                    numberOfWork--;
                }
            }

            pay += payPerDay;
            cal.add(DATE, 1);
            searchDate = cal.getTime();
        }

        numberOfMeal = numberOfWork;
        numberOfTraffic = numberOfWork;

        Cursor c = DBmanager.query(vacationColumns, DBHelper.TABLE_VACATION, null,
                null, null, null, null);
        while (c.moveToNext()) {
            String type = c.getString(3);
            double count = c.getDouble(4);
            Date startDate = formatter.parse(c.getString(2));
            long lowerDiff = startDate.getTime() - first_payDate.getTime();
            long upperDiff = last_payDate.getTime() - startDate.getTime();
            if (lowerDiff >= 0 && upperDiff >= 0) {
                if (type.equals("연가") || type.equals("청원휴가") || type.equals("특별휴가") || type.equals("공가")) {
                    numberOfMeal--;
                    numberOfTraffic--;
                    sum_Vac += count;
                } else if (type.equals("오전반가")) {
                    numberOfMeal--;
                    sum_Vac += count;
                } else if (type.equals("오후반가")) {
                    sum_Vac += count;
                } else if (type.equals("외출")) {
                    sum_outing += count;
                } else if (type.equals("병가")) {
                    numberOfMeal--;
                    numberOfTraffic--;
                    sum_sickVac += count;
                } else if (type.equals("오전지참")) {
                    numberOfMeal--;
                    sum_sickVac += count;
                } else if (type.equals("오후조퇴") || type.equals("병가외출")) {
                    sum_sickVac += count;
                }
            }
        }

        thisMonthVac.setText(convertMinuteToProperUnit((int) sum_Vac));
        thisMonthSickVac.setText(convertMinuteToProperUnit((int) sum_sickVac));
        thisMonthOuting.setText(convertMinuteToProperUnit((int) sum_outing));
        int sumOfPay = (int) Math.round((pay + (mealCost * numberOfMeal) + (trafficCost * numberOfTraffic)) / 100.0) * 100;
        int sumOfMeal = mealCost * numberOfMeal;
        int sumOfTraffic = trafficCost * numberOfTraffic;
        int sumOfBeforePromotion = payBeforePromotion * (numberOfEntire - numberOfAfterPromotion) / entireLength;
        int sumOfAfterPromotion = payAfterPromotion * numberOfAfterPromotion / entireLength;
        int sumOfNoPromotion = payBeforePromotion * numberOfEntire / entireLength;


        if (isAmended) {
            toolTipText = ("<b>2019년 개정전</b><br>" + decimalFormat2.format(sumOfBeforePromotion) + " <b>|</b> "
                    + decimalFormat2.format(payBeforePromotion) + " x " + "(" + (numberOfEntire - numberOfAfterPromotion)
                    + "/" + entireLength + "일)" + "<br><br>" + "<b>2020년 개정후</b><br>" + decimalFormat2.format(sumOfAfterPromotion)
                    + " <b>|</b> " + decimalFormat2.format(payAfterPromotion) + " x " + "(" + (numberOfAfterPromotion)
                    + "/" + entireLength + "일)");
        } else if (isPromoted) {
            toolTipText = ("<b>진급 전</b><br>" + decimalFormat2.format(sumOfBeforePromotion) + " <b>|</b> "
                    + decimalFormat2.format(payBeforePromotion) + " x " + "(" + (numberOfEntire - numberOfAfterPromotion)
                    + "/" + entireLength + "일)" + "<br><br>" + "<b>진급 후</b><br>" + decimalFormat2.format(sumOfAfterPromotion)
                    + " <b>|</b> " + decimalFormat2.format(payAfterPromotion) + " x " + "(" + (numberOfAfterPromotion)
                    + "/" + entireLength + "일)");
        } else {
            toolTipText = ("<b>기본급여</b><br>" + decimalFormat2.format(sumOfNoPromotion) + " <b>|</b> "
                    + decimalFormat2.format(payBeforePromotion) + " x " + "(" + numberOfEntire
                    + "/" + entireLength + "일)");
        }

        if(bootCampCalculationInclude && isBootCampIncluded){
            String bootCampTooltipText = setToolTipTextAboutBootCamp(bootCampStartDate, bootCampEndDate);
            if(checkBootCampEndInclude(first_payDate, last_payDate, bootCampEndDate)){
                toolTipText += ("<br><br>" + "<b>훈련소</b><br>"
                        + decimalFormat2.format(calculateBootCampPay(bootCampStartDate, bootCampEndDate)))
                        + "<br>= " + bootCampTooltipText;
                sumOfPay += calculateBootCampPay(bootCampStartDate, bootCampEndDate);
            } else{
                toolTipText += ("<br><br>" + "<b>훈련소 (다음달 급여에 포함)</b><br>"
                        + decimalFormat2.format(calculateBootCampPay(bootCampStartDate, bootCampEndDate)))
                        + "<br>= " + bootCampTooltipText;
            }
        }

        toolTipText += ("<br><br>" + "<b>식비</b><br>" + decimalFormat2.format(sumOfMeal) + " <b>|</b> "
                + decimalFormat2.format(mealCost) + " x " + numberOfMeal + "일" + "<br><br>"
                + "<b>교통비</b><br>" + decimalFormat2.format(sumOfTraffic) + " <b>|</b> "
                + decimalFormat2.format(trafficCost) + " x " + numberOfTraffic + "일" + "<br><br>"
                + "공휴일 출근횟수에서 제외");

        salaryTextView.setText(decimalFormat.format(sumOfPay) + " KRW");
    }

    public int getDateLength(int first, int last) {
        Date first_payDate = null;
        Date last_payDate = null;
        try {
            first_payDate = dateFormat.parse(first + "");
            last_payDate = dateFormat.parse(last + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) (((last_payDate.getTime() - first_payDate.getTime()) / dayIntoMilliSecond) + 1);
    }

    public int getMonthLength(Date date){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date);
        c2.setTime(date);

        c1.set(DATE, 1);
        c2.add(MONTH, 1);
        c2.set(DATE, 1);

        return (int) ((c2.getTimeInMillis() - c1.getTimeInMillis()) / dayIntoMilliSecond);
    }

    public int checkFirstDayInclude(int first) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(firstDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int firstDateToInt = ((calendar.get(YEAR) * 10000) + ((calendar.get(MONTH) + 1) * 100)) + calendar.get(DATE);
        if (first - firstDateToInt > 0) {
            return first;
        } else {
            return firstDateToInt;
        }
    }

    public int checkLastDayInclude(int last) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(lastDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int lastDateToInt = ((calendar.get(YEAR) * 10000) + ((calendar.get(MONTH) + 1) * 100)) + calendar.get(DATE);
        if (last - lastDateToInt < 0) {
            return last;
        } else {
            return lastDateToInt;
        }
    }

    public boolean checkBootCampInclude(Date date){
       return ((bootCampStartDate.compareTo(date) != 1) && (bootCampEndDate.compareTo(date) != -1));
    }

    public boolean checkBootCampEndInclude(Date firstSearchDate, Date lastSearchDate, Date date){
        return ((firstSearchDate.compareTo(date) != 1) && (lastSearchDate.compareTo(date) != -1));
    }

    public int calculateBootCampPay(Date bootCampStartDate, Date bootCampEndDate){
        int bootCampPay = 0;
        Calendar cal = Calendar.getInstance();
        Date searchDate = bootCampStartDate;
        cal.setTime(searchDate);
        while (searchDate.compareTo(bootCampEndDate) <= 0) {
            cal.setTime(searchDate);
            double payPerDay = (double) (payDependsOnMonth(searchDate) / getMonthLength(searchDate));

            bootCampPay += payPerDay;
            cal.add(DATE, 1);
            searchDate = cal.getTime();
        }
        return bootCampPay;
    }

    public String setToolTipTextAboutBootCamp(Date bootCampStartDate, Date bootCampEndDate){
        Calendar cal = Calendar.getInstance();
        Date searchDate = bootCampStartDate;
        cal.setTime(searchDate);
        int startMonth = cal.get(MONTH);
        int previousMonth = 0;
        int currentMonth = 0;
        boolean monthChange = false;
        while (searchDate.compareTo(bootCampEndDate) <= 0) {
            cal.setTime(searchDate);
            if(startMonth == cal.get(MONTH)){
                previousMonth++;
            }else{
                currentMonth++;
                monthChange = true;
            }
            cal.add(DATE, 1);
            searchDate = cal.getTime();
        }
        return monthChange ? decimalFormat2.format(payDependsOnMonth(bootCampStartDate)) + " x " + "(" + previousMonth
                + "/" + getMonthLength(bootCampStartDate) + "일)" + "<br>+ " + decimalFormat2.format(payDependsOnMonth(bootCampEndDate))
                + " x " + "(" + currentMonth + "/" + getMonthLength(bootCampEndDate) + "일)"
                : decimalFormat2.format(payDependsOnMonth(bootCampStartDate)) + " x " + "(" + previousMonth
                + "/" + getMonthLength(bootCampStartDate) + "일)";
    }

    public String intToDateFormatString(int date) {
        String tmp = Integer.toString(date);
        return tmp.substring(0, 4) + "." + tmp.substring(4, 6) + "." + tmp.substring(6);
    }

    String convertMinuteToProperUnit(int minute) {
        if (minute < 0) return "남은휴가없음";
        else {
            int day = minute / 480;
            minute %= 480;
            if (day != 0) {
                if (minute == 240) return ((double) day + 0.5) + "일";
                else {
                    int hour = minute / 60;
                    minute %= 60;
                    if (hour != 0) return minute != 0 ? day + "일 " + hour + "시간 "
                            + minute + "분" : day + "일 " + hour + "시간";
                    else return minute != 0 ? day + "일 " + minute + "분 " : day + "일";
                }
            } else {
                if (minute == 240) return "0.5일";
                else {
                    int hour = minute / 60;
                    minute %= 60;
                    if (hour != 0) return minute != 0 ? hour + "시간 "
                            + minute + "분" : hour + "시간";
                    else return minute != 0 ? minute + "분" : "0일";
                }
            }
        }
    }

    public void refreshListView(String limitStartDate, String limitLastDate, vacType numOfYear) {
        FragmentManager fg = getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();

        VacListFragment fragment = new VacListFragment().newInstance(limitStartDate, limitLastDate,
                firstDate, lastDate, numOfYear, searchStartDate);
        ft.replace(R.id.fragment_container_1, fragment);
        ft.commit();
    }

    public void setSearchStartDate() {
        try {
            Date firstDay = formatter.parse(firstDate);
            Date lastDay = formatter.parse(lastDate);
            Date today = Calendar.getInstance().getTime();

            if (today.getTime() < firstDay.getTime()) {
                searchStartDate = dateFormat.format(firstDay);
            } else if (today.getTime() > lastDay.getTime()) {
                searchStartDate = dateFormat.format(lastDay);
            } else {
                searchStartDate = dateFormat.format(today);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void countDdayFromToday() {
        Calendar today = Calendar.getInstance();
        long todayTime = today.getTime().getTime();
        long lastTime = 0;
        long firstTime = 0;
        try {
            lastTime = formatter.parse(lastDate).getTime();
            firstTime = formatter.parse(firstDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long count = (lastTime - todayTime);
        if (count > 0) {
            dDayTextView.setText("D-" + ((count / dayIntoMilliSecond) + 1));
        } else {
            dDayTextView.setText("소집해제");
        }
        entireService.setText((((lastTime - firstTime) / dayIntoMilliSecond)) + " 일");
        currentService.setText((((todayTime - firstTime) / dayIntoMilliSecond)) + " 일");
        remainService.setText(((count / dayIntoMilliSecond)) + 1 + " 일");
    }
}