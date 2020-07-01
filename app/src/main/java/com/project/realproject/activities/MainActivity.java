package com.project.realproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.project.realproject.FirstYearVacationList;
import com.project.realproject.SpecificPeriodVacationList;
import com.project.realproject.Salary;
import com.project.realproject.SecondYearVacationList;
import com.project.realproject.SickVacationList;
import com.project.realproject.User;
import com.project.realproject.VacationList;
import com.project.realproject.fragments.BlankFragment;
import com.project.realproject.R;
import com.project.realproject.fragments.SalaryInfoFragment;
import com.project.realproject.fragments.SettingPromotionFragment;
import com.project.realproject.fragments.SettingUserInfoFragment;
import com.project.realproject.fragments.VacListFragment;
import com.project.realproject.fragments.VacSaveFragment;

import static android.app.AlertDialog.*;
import static com.project.realproject.helpers.Formatter.*;

import com.project.realproject.helpers.DBHelper;
import com.transitionseverywhere.Rotate;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import it.sephiroth.android.library.xtooltip.ClosePolicy;
import it.sephiroth.android.library.xtooltip.Tooltip;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.Calendar.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnDismissListener {

    private TextView firstVacRemain;
    private TextView secondVacRemain;
    private TextView sickVacRemain;
    private TextView firstVacTotal;
    private TextView secondVacTotal;
    private TextView sickVacTotal;
    private TextView nickNameTextView;
    private TextView gradeTextView;
    private TextView dDayTextView;
    private TextView servicePeriodTextView;
    private TextView searchPeriodTextView;
    private ImageView goToNextPeriod;
    private ImageView goToPreviousPeriod;

    private TextSwitcher salaryTextSwitcher;
    private TextView thisMonthOuting;
    private TextView thisMonthVac;
    private TextView thisMonthSickVac;
    private ProgressBar progressBar;
    private TextView progressPercentage;

    private ImageView nav_button;
    private ImageView cal_button;
    private ImageView toolTipRank;
    private ImageView toolTipPay;

    private LinearLayout vacCard1;
    private LinearLayout vacCard2;
    private LinearLayout vacCard3;

    private LinearLayout slideContainer;
    private LinearLayout expandableListViewContainer;
    private RelativeLayout rotateContainer1;
    private RelativeLayout rotateContainer2;
    private RelativeLayout rotateContainer3;

    DBHelper dbHelper = null;

    public enum vacType {firstYearVac, secondYearVac, sickVac}

    public static Context mContext;
    private AdView mAdView;
    private Calendar calendar;
    private static final String FRAGMENT_VACATION_LIST = "FRAGMENT_VACATION_LIST";
    private String firstDate;
    private String lastDate;
    private String pivotDate;
    private String pivotPlusOneDate;
    private int totalFirstVac;
    private int totalSecondVac;
    private int totalSickVac;

    private FirebaseAnalytics mFirebaseAnalytics;

    // private String searchStartDate;
    private Date searchDate;
    private String toolTipText;

    // countDownTimer values
    private CountDownTimer countDownTimer;
    private boolean timerIsRunning;
    private long entire;
    private long current;

    // sharedPreference values
    private boolean isPercentChanging;
    private int decimalPlaces;

    private Vibrator mVibe;
    private static Handler mHandler;
    private User user;
    private Salary salary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        dbHelper = DBHelper.getInstance(getApplicationContext());
        mContext = this;
        mVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        nav_button = findViewById(R.id.navigation_drawer_button);
        cal_button = findViewById(R.id.calculator_button);
        nickNameTextView = findViewById(R.id.tv_nickName);
        nickNameTextView.setSelected(true);
        gradeTextView = findViewById(R.id.tv_grade);
        dDayTextView = findViewById(R.id.tv_dDay);
        servicePeriodTextView = findViewById(R.id.tv_servicePeriod);
        searchPeriodTextView = findViewById(R.id.tv_search_period);
        goToNextPeriod = findViewById(R.id.iv_search_next_period);
        goToPreviousPeriod = findViewById(R.id.iv_search_previous_period);
        salaryTextSwitcher = findViewById(R.id.ts_salary);
        thisMonthOuting = findViewById(R.id.thisMonthOuting);
        thisMonthVac = findViewById(R.id.thisMonthVac);
        thisMonthSickVac = findViewById(R.id.thisMonthSickVac);
        progressBar = findViewById(R.id.progressBar);
        progressPercentage = findViewById(R.id.progress_percentage);

        vacCard1 = findViewById(R.id.vacCardView_1);
        vacCard2 = findViewById(R.id.vacCardView_2);
        vacCard3 = findViewById(R.id.vacCardView_3);

        slideContainer = findViewById(R.id.slide_container);
        expandableListViewContainer = findViewById(R.id.expandableListViewContainer);
        rotateContainer1 = findViewById(R.id.rotate_container1);
        rotateContainer2 = findViewById(R.id.rotate_container2);
        rotateContainer3 = findViewById(R.id.rotate_container3);
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


        salaryTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                return inflater.inflate(R.layout.item_text_switcher, null);
            }
        });

        setLayoutTransition(slideContainer);

        salaryTextSwitcher.setOnClickListener(this);
        toolTipPay.setOnClickListener(this);
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

        initialLoad();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        setRemainVac();
        setMonthlyInfo(searchDate);
        VacListFragment fragment = (VacListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_VACATION_LIST);
        if (fragment != null) {
            fragment.reloadListView();
        }
    }

    public void initialLoad() {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (dbHelper.getDataCount(DBHelper.TABLE_USER) != 0) {
            searchDate = calendar.getTime();
            setUserProfile();
            setRemainVac();
            setMonthlyInfo(searchDate);

            progressBar.setProgress((int) getPercentage());
            if (isPercentChanging) {
                if (!timerIsRunning && dbHelper.getDataCount(DBHelper.TABLE_USER) != 0) {
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


    @Override
    public void onClick(View view) {

        FragmentManager fg = getSupportFragmentManager();
        VacSaveFragment dialog;

        switch (view.getId()) {
            case R.id.btn_pay_info:
                calendar.setTime(searchDate);
                SalaryInfoFragment infoFragment1 = SalaryInfoFragment.newInstance(calendar.getTimeInMillis());
                infoFragment1.show(fg, "dialog");
                break;

            case R.id.ts_salary:
                mVibe.vibrate(1);
                calendar.setTime(searchDate);
                SalaryInfoFragment infoFragment2 = SalaryInfoFragment.newInstance(calendar.getTimeInMillis());
                infoFragment2.show(fg, "dialog");
                Bundle bundle = new Bundle();

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "salaryInfoClick");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;

            case R.id.vacCardView_1:
                salaryTextSwitcher.setInAnimation(this, R.anim.stop);
                salaryTextSwitcher.setOutAnimation(this, R.anim.stop);
                setListView(new FirstYearVacationList(this, firstDate, pivotDate),
                        R.id.first_vacation_image, rotateContainer1, vacType.firstYearVac);
                break;

            case R.id.vacCardView_2:
                salaryTextSwitcher.setInAnimation(this, R.anim.stop);
                salaryTextSwitcher.setOutAnimation(this, R.anim.stop);
                setListView(new SecondYearVacationList(this, pivotPlusOneDate, lastDate),
                        R.id.second_vacation_image, rotateContainer2, vacType.secondYearVac);
                break;

            case R.id.vacCardView_3:
                salaryTextSwitcher.setInAnimation(this, R.anim.stop);
                salaryTextSwitcher.setOutAnimation(this, R.anim.stop);
                setListView(new SickVacationList(this, firstDate, lastDate),
                        R.id.sick_vacation_image, rotateContainer3, vacType.sickVac);
                break;


            case R.id.spendButton_1:
                mVibe.vibrate(1);
                // 1년 이하의 복무기간일 경우 1년차 연가사용 기간 조정 (pivotDate 대신 lastDate)
                if (lastDate.compareTo(pivotDate) > 0) {
                    dialog = new VacSaveFragment().newInstance(new FirstYearVacationList(this, firstDate, pivotDate));
                } else {
                    dialog = new VacSaveFragment().newInstance(new FirstYearVacationList(this, firstDate, lastDate));
                }
                dialog.show(fg, "dialog");
                break;

            case R.id.spendButton_2:
                mVibe.vibrate(1);
                if (lastDate.compareTo(pivotDate) < 0) {
                    Toast.makeText(this, "복무기간이 1년 이하인 경우 2년차 연가를 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = new VacSaveFragment().newInstance(new SecondYearVacationList(this, pivotPlusOneDate, lastDate));
                    dialog.show(fg, "dialog");
                }
                break;

            case R.id.spendButton_3:
                mVibe.vibrate(1);
                dialog = new VacSaveFragment().newInstance(new SickVacationList(this, firstDate, lastDate));
                dialog.show(fg, "dialog");
                break;

            case R.id.iv_search_next_period:
                mVibe.vibrate(1);
                calendar.setTime(searchDate);
                calendar.add(MONTH, 1);
                searchDate = calendar.getTime();
                Salary nextSalary = new Salary(mContext, searchDate);
                if (nextSalary.isIncludedInPeriod()) {
                    salaryTextSwitcher.setInAnimation(this, R.anim.slide_in_right);
                    salaryTextSwitcher.setOutAnimation(this, R.anim.slide_out_left);
                    setMonthlyInfo(searchDate);
                } else {
                    salaryTextSwitcher.setInAnimation(this, R.anim.stop);
                    salaryTextSwitcher.setOutAnimation(this, R.anim.stop);
                    calendar.add(MONTH, -1);
                    searchDate = calendar.getTime();
                    setMonthlyInfo(searchDate);
                }
                break;

            case R.id.iv_search_previous_period:
                mVibe.vibrate(1);
                calendar.setTime(searchDate);
                calendar.add(MONTH, -1);
                searchDate = calendar.getTime();
                Salary previousSalary = new Salary(mContext, searchDate);
                if (previousSalary.isIncludedInPeriod()) {
                    salaryTextSwitcher.setInAnimation(this, R.anim.slide_in_left);
                    salaryTextSwitcher.setOutAnimation(this, R.anim.slide_out_right);
                    setMonthlyInfo(searchDate);
                } else {
                    salaryTextSwitcher.setInAnimation(this, R.anim.stop);
                    salaryTextSwitcher.setOutAnimation(this, R.anim.stop);
                    calendar.add(MONTH, 1);
                    searchDate = calendar.getTime();
                    setMonthlyInfo(searchDate);
                }
                break;

            case R.id.calculator_button:
                mVibe.vibrate(1);
                Intent calculator = new Intent(MainActivity.this, CalculatorActivity.class);
                startActivity(calculator);
                break;
        }
    }


    // runnable을 이용한 vacListView Thread 관리
    private static class mHandler extends Handler {
        final vacType typeOfVac;
        final FragmentTransaction ft;
        final VacationList vacationList;

        private mHandler(final VacationList vacationList, final vacType typeOfVac, final FragmentTransaction ft) {
            this.vacationList = vacationList;
            this.typeOfVac = typeOfVac;
            this.ft = ft;
        }

        @Override
        public void handleMessage(Message msg) {
            Fragment fragment;
            fragment = new VacListFragment().newInstance(vacationList, typeOfVac);

            ft.replace(R.id.expandableListViewContainer, fragment, FRAGMENT_VACATION_LIST);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }

    class NewRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(0);
        }
    }

    public void setListView(VacationList vacationList, int imageId, ViewGroup rotateContainer, final vacType typeOfVac) {

        FragmentManager fg1 = getSupportFragmentManager();
        FragmentManager fg2 = getSupportFragmentManager();
        final FragmentTransaction ft1 = fg1.beginTransaction();
        final FragmentTransaction ft2 = fg2.beginTransaction();

        mHandler = new mHandler(vacationList, typeOfVac, ft1);

        ImageView imageView = findViewById(imageId);
        TransitionManager.beginDelayedTransition(rotateContainer, new Rotate().setDuration(200));
        if (expandableListViewContainer.getVisibility() == GONE) {
            NewRunnable nr = new NewRunnable();
            Thread t = new Thread(nr);
            t.start();
            expandableListViewContainer.setVisibility(VISIBLE);
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
            imageView.setRotation(180);

        } else {
            vacCard1.setVisibility(VISIBLE);
            vacCard2.setVisibility(VISIBLE);
            vacCard3.setVisibility(VISIBLE);
            expandableListViewContainer.setVisibility(GONE);
            imageView.setRotation(360);
            ft2.replace(R.id.expandableListViewContainer, new BlankFragment(), "empty");
            ft2.commit();
        }
    }


    public void onClickToolTip(View view) {

        toolTipText = user.getRankToolTip();
        Tooltip toolTip = new Tooltip.Builder(this)
                .styleId(R.style.ToolTipLayoutCustomStyle)
                .text(toolTipText)
                .anchor(toolTipRank, 0, 0, false)
                .activateDelay(0)
                .showDuration(20000)
                .closePolicy(new ClosePolicy.Builder()
                        .inside(true)
                        .outside(true)
                        .build())
                .arrow(true)
                .create();

        if (nickNameTextView.getText().length() <= 2) {
            toolTip.show(view, Tooltip.Gravity.RIGHT, false);
        } else {
            toolTip.show(view, Tooltip.Gravity.BOTTOM, false);
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentManager fg = getSupportFragmentManager();
        switch (menuItem.getItemId()) {
            case R.id.profile:
                SettingUserInfoFragment.newInstance().show(fg, "dialog");
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
                                dbHelper.deleteAll();
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
        if (dbHelper.getDataCount(DBHelper.TABLE_USER) != 0) {

            user = new User(mContext);
            firstDate = user.getFirstDate();
            lastDate = user.getLastDate();
            totalFirstVac = user.getTotalFirstVac();
            totalSecondVac = user.getTotalSecondVac();
            totalSickVac = user.getTotalSickVac();

            decimalPlaces = user.getDecimalPlaces();
            isPercentChanging = user.isPercentChanging();

            nickNameTextView.setText(user.getNickName());
            gradeTextView.setText(user.getGrade());
            servicePeriodTextView.setText(dateFormat_kor.format(user.getFirstDateTime())
                    + "  ~  " + dateFormat_kor.format(user.getLastDateTime()));
            countDdayFromToday();

            firstVacTotal.setText(" / " + totalFirstVac);
            secondVacTotal.setText(" / " + totalSecondVac);
            sickVacTotal.setText(" / " + totalSickVac);

            Calendar cal = (Calendar) calendar.clone();
            cal.setTime(user.getFirstDateTime());
            cal.add(YEAR, 1);
            pivotDate = formatter.format(cal.getTime());
            cal.add(DATE, 1);
            pivotPlusOneDate = formatter.format(cal.getTime());

        }
    }

    public void setMonthlyInfo(Date today) {
        salary = new Salary(mContext, today);

        searchPeriodTextView.setText(dateFormat_dot.format(salary.getStartDate())
                + " ~ " + dateFormat_dot.format(salary.getLastDate()));
        thisMonthVac.setText(convertMinuteToProperUnit((int) salary.countNumberOfGeneralVacation()));
        thisMonthSickVac.setText(convertMinuteToProperUnit((int) salary.countNumberOfSickVacation()));
        thisMonthOuting.setText(convertMinuteToProperUnit((int) salary.countNumberOfOutingVacation()));
        salaryTextSwitcher.setText(decimalFormat.format(salary.calculateTotalSalary()) + " KRW");
    }

    public void setRemainVac() {
        double firstCount = (double) totalFirstVac * 480;
        double secondCount = (double) totalSecondVac * 480;
        double thirdCount = (double) totalSickVac * 480;

        firstCount -= new FirstYearVacationList(this, firstDate, pivotDate).getTotalCount();
        secondCount -= new SecondYearVacationList(this, pivotPlusOneDate, lastDate).getTotalCount();
        thirdCount -= new SickVacationList(this, firstDate, lastDate).getTotalCount();
        firstVacRemain.setText(convertMinuteToProperUnit((int) firstCount));
        secondVacRemain.setText(convertMinuteToProperUnit((int) secondCount));
        sickVacRemain.setText(convertMinuteToProperUnit((int) thirdCount));
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

    public void countDdayFromToday() {

        long count = user.getRemainServicePeriod();
        dDayTextView.setText(count > 0 ? "D-" + count : "소집해제");

    }
}