package com.example.realproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

// 밝은회색 3c5c75
// 진한회색 0b1c2d
// 회색글씨 404855, 너무 연하면 3b455b
// 바탕 e6e729
// 작은글씨 a3aaab

import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.sephiroth.android.library.tooltip.Tooltip;

import static android.icu.text.DateTimePatternGenerator.DAY;
import static android.view.View.FOCUS_UP;
import static android.view.View.VISIBLE;
import static java.lang.String.valueOf;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getAvailableCalendarTypes;

public class Main_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    vacationDBManager DBmanager = null;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyyMMdd", Locale.ENGLISH);
    private static final SimpleDateFormat dateFormat_dot = new SimpleDateFormat(
            "yyyy.MM.dd", Locale.ENGLISH);
    private static final DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private static String[] userColumns = new String[]{"id", "nickName", "firstDate", "lastDate",
            "mealCost", "trafficCost", "totalFirstVac", "totalSecondVac", "totalSickVac", "payDay"};
    private static String[] vacationColumns = new String[]{"id", "vacation", "startDate", "type", "count"};
    private static final int[] payDependsOnRank2019 = new int[]{306100, 331300, 366200, 405700};
    private static String[] listOfHoliday = new String[]{"0101", "0301", "0505", "0512", "0606", "0815",
            "1003", "1009", "1225"};
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

    private ScrollView scrollView;
    private CardView vacCard1;
    private CardView vacCard2;
    private CardView vacCard3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        DBmanager = vacationDBManager.getInstance(this);


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        scrollView = findViewById(R.id.scrollView);
        vacCard1 = findViewById(R.id.vacCardView_1);
        vacCard2 = findViewById(R.id.vacCardView_2);
        vacCard3 = findViewById(R.id.vacCardView_3);

        LinearLayout listButton_1 = findViewById(R.id.listButton);
        LinearLayout listButton_2 = findViewById(R.id.listButton_2);
        LinearLayout listButton_3 = findViewById(R.id.listButton_3);

        Button spendButton = findViewById(R.id.spendButton);
        Button spendButton_2 = findViewById(R.id.spendButton_2);
        Button spendButton_3 = findViewById(R.id.spendButton_3);
        firstVacRemain = findViewById(R.id.first_vacation_remain);
        secondVacRemain = findViewById(R.id.second_vacation_remain);
        sickVacRemain = findViewById(R.id.sick_vacation_remain);
        firstVacTotal = findViewById(R.id.first_vacation_total);
        secondVacTotal = findViewById(R.id.second_vacation_total);
        sickVacTotal = findViewById(R.id.sick_vacation_total);

        listButton_1.setOnClickListener(this);
        listButton_2.setOnClickListener(this);
        listButton_3.setOnClickListener(this);
        spendButton.setOnClickListener(this);
        spendButton_2.setOnClickListener(this);
        spendButton_3.setOnClickListener(this);
        goToNextPeriod.setOnClickListener(this);
        goToPreviousPeriod.setOnClickListener(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView nav_button = findViewById(R.id.navigation_drawer_button);
        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        setUserProfile();
        if (DBmanager.getDataCount(vacationDBManager.TABLE_USER) != 0) {
            setRemainVac();
            searchStartDate = dateFormat.format(Calendar.getInstance().getTime());
            setThisMonthInfo(searchStartDate);
        }

        progressPercentage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int barWidth = progressBar.getRight() - progressBar.getLeft();
                float position_X = (progressBar.getX() + (barWidth * getPercentage() / 100)) - 40;
                progressPercentage.setText(String.format("%.3f",getPercentage()) + "%");
                if(position_X < 80){
                    progressPercentage.setX(80);
                }
                else if(position_X > barWidth - 160){
                    progressPercentage.setX(barWidth - 160);
                }
                else {
                    progressPercentage.setX((progressBar.getX() + (barWidth * getPercentage() / 100)) - 40);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        Calendar calendar = Calendar.getInstance();
        FragmentManager fg = getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        Frag2_save dialog;

        switch (view.getId()) {
            case R.id.listButton:
                setListView(R.id.fragment_container_1, R.id.first_vacation_image, fg, ft,
                        firstDate, pivotDate, 1, "list1");
                break;

            case R.id.listButton_2:
                setListView(R.id.fragment_container_2, R.id.second_vacation_image, fg, ft,
                        pivotPlusOneDate, lastDate, 2, "list2");
                break;

            case R.id.listButton_3:
                setListView(R.id.fragment_container_3, R.id.sick_vacation_image, fg, ft,
                        firstDate, lastDate, 3, "list3");
                break;

            case R.id.spendButton:
                dialog = new Frag2_save().newInstance(firstDate, pivotDate, 1, searchStartDate);
                dialog.show(fg, "dialog");
                break;

            case R.id.spendButton_2:
                dialog = new Frag2_save().newInstance(pivotPlusOneDate, lastDate, 2, searchStartDate);
                dialog.show(fg, "dialog");
                break;

            case R.id.spendButton_3:
                dialog = new Frag2_save().newInstance(firstDate, lastDate, 3, searchStartDate);
                dialog.show(fg, "dialog");
                break;

            case R.id.iv_search_next_period:
                try {
                    calendar.setTime(dateFormat.parse(searchStartDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(MONTH, 1);
                searchStartDate = dateFormat.format(calendar.getTime());
                if(!setThisMonthInfo(searchStartDate)) {
                    calendar.add(MONTH, -1);
                    searchStartDate = dateFormat.format(calendar.getTime());
                }
                break;

            case R.id.iv_search_previous_period:
                try {
                    calendar.setTime(dateFormat.parse(searchStartDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(MONTH, -1);
                searchStartDate = dateFormat.format(calendar.getTime());
                if(!setThisMonthInfo(searchStartDate)) {
                    calendar.add(MONTH, 1);
                    searchStartDate = dateFormat.format(calendar.getTime());
                }
                break;

        }
    }

    public void toolTipRank(View view) {
        ImageView toolTipRank = findViewById(R.id.btn_rank_info);
        ImageView toolTipPay = findViewById(R.id.btn_pay_info);
        try {
            if (view == toolTipRank) {
                Calendar calendar = Calendar.getInstance();
                Calendar today = Calendar.getInstance();
                calendar.setTime(formatter.parse(firstDate));
                calendar.set(DATE, 1);
                int currentPay = payDependsOnMonth(today.getTime());
                String rank = null;
                if (currentPay == payDependsOnRank2019[0]) {
                    calendar.add(MONTH, 3);
                    rank = "이등병";
                } else if (currentPay == payDependsOnRank2019[1]) {
                    calendar.add(MONTH, 10);
                    rank = "일병";
                } else if (currentPay == payDependsOnRank2019[2]) {
                    calendar.add(MONTH, 17);
                    rank = "상병";
                } else if (currentPay == payDependsOnRank2019[3]) {
                    calendar.setTime(formatter.parse(lastDate));
                    rank = "병장";
                }
                toolTipText = ("<b>계급</b> | " + rank + "<br><br>" + "<b>현재 기본급</b> | "
                        + decimalFormat.format(currentPay) + "원<br><br>" + "<b>다음 진급일</b> | " + dateFormat_dot.format(calendar.getTime()));
            } else if (view == toolTipPay) {
                setThisMonthInfo(searchStartDate);
            }

            Tooltip.make(this,
                    new Tooltip.Builder(101)
                            .withStyleId(R.style.ToolTipLayoutCustomStyle)
                            .anchor(view, Tooltip.Gravity.BOTTOM)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 10000)
                            .text(toolTipText)
                            .maxWidth(1000)
                            .withArrow(true)
                            .withOverlay(true).build()
            ).show();
        }catch(ParseException e){}
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        DialogFrag_DateRevise dialog = new DialogFrag_DateRevise();
        FragmentManager fg = getSupportFragmentManager();
        switch (menuItem.getItemId()) {
            // 소집일 식비 각각 gone 으로 없앨지 말지
            case R.id.profile:
                dialog.show(fg, "dialog");
                break;
            case R.id.date:
                dialog.show(fg, "dialog");
                break;
            case R.id.cost:
                dialog.show(fg, "dialog");
                break;
            case R.id.vacation:
                break;
        }
        return false;
    }

    public void setUserProfile() {
        if(DBmanager.getDataCount(vacationDBManager.TABLE_USER) != 0) {
            Cursor c = DBmanager.query(userColumns, vacationDBManager.TABLE_USER,
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
            dDayTextView.setText(countDdayFromToday());
            progressBar.setProgress((int)getPercentage());

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
        else{
            DialogFrag_DateRevise dialog = new DialogFrag_DateRevise();
            FragmentManager fg = getSupportFragmentManager();
            dialog.show(fg, "dialog");
        }
    }

    public int payDependsOnMonth(Date searchDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        int firstYear = Integer.parseInt(firstDate.substring(0, 4));
        int firstMonth = Integer.parseInt(firstDate.substring(5, 7));
        int year = calendar.get(YEAR);
        int month = calendar.get(MONTH) + 1;
        int diff = ((year - firstYear) * 12) + (month - firstMonth);

        if(diff < 3) return payDependsOnRank2019[0];
        else if(diff >= 3 && diff < 10) return payDependsOnRank2019[1];
        else if(diff >= 10 && diff < 17) return payDependsOnRank2019[2];
        else return payDependsOnRank2019[3];
    }

    public void setRemainVac() {
        Cursor c =  DBmanager.query(vacationColumns, vacationDBManager.TABLE_FIRST,
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
                if (type.equals("병가") || type.equals("오전지참")
                        || type.equals("오후조퇴") || type.equals("병가외출")) {
                    thirdCount -= c.getDouble(4);
                } else if (startTime - firstTime >= 0 && pivotTime - startTime >= 0) {
                    firstCount -= c.getDouble(4);
                } else if (startTime - pivotTime > 0 && lastTime - startTime >= 0) {
                    secondCount -= c.getDouble(4);
                }
            }
            c.close();
            firstVacRemain.setText(convertMinuteToProperUnit((int) firstCount));
            secondVacRemain.setText(convertMinuteToProperUnit((int) secondCount));
            sickVacRemain.setText(convertMinuteToProperUnit((int) thirdCount));
        }
        catch(ParseException e){
        }
    }


    public boolean setThisMonthInfo(String searchDate){
        int first, last, entireLength;
        Calendar cal = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
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
            if(last - first >= 0) {
                searchPeriodTextView.setText(intToDateFormatString(first) + " ~ " + intToDateFormatString(last));
                setThisMonthSpendVac(first, last, entireLength);
                return true;
            }else{
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setThisMonthSpendVac(int first_tmp, int last_tmp, int entireLength) throws ParseException{
        // 소집일 소집해제일이 끼어있는지 주의
        Calendar cal = Calendar.getInstance();
        double sum_sickVac = 0;
        double sum_Vac = 0;
        double sum_outing = 0;
        // 출근횟수 구하기
        int numberOfEntire; // 전체 날짜수
        int numberOfWork; // 출근횟수 넣기 (토, 일, 공휴일 빼고 카운트)
        int numberOfMeal;
        int numberOfTraffic;
        double pay = 0;
        boolean isPromoted = false;
        int payBeforePromotion;
        int payAfterPromotion = 0;
        int numberOfAfterPromotion = 0;

        Date first_payDate = dateFormat.parse(first_tmp + "");
        Date last_payDate = dateFormat.parse(last_tmp + "");
        numberOfEntire = getDateLength(first_tmp, last_tmp);
        numberOfWork = numberOfEntire;
        Date searchDate = first_payDate;

        payBeforePromotion = payDependsOnMonth(searchDate);
        double checkPromotion = (double)(payDependsOnMonth(searchDate)/entireLength);

        while(searchDate.compareTo(last_payDate) <= 0){
            cal.setTime(searchDate);
            String onlyMonthAndDate = dateFormat.format(searchDate).substring(4);
            if(cal.get(Calendar.DAY_OF_WEEK) == SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == SUNDAY ||
                    Arrays.asList(listOfHoliday).contains(onlyMonthAndDate)){
                numberOfWork--;
            }
            double payPerDay = (double)(payDependsOnMonth(searchDate)/entireLength);
            if((checkPromotion != payPerDay) || isPromoted){
                isPromoted = true;
                payAfterPromotion = payDependsOnMonth(searchDate);
                numberOfAfterPromotion++;
            }
            pay += payPerDay;
            cal.add(DATE, 1);
            searchDate = cal.getTime();
        }

        numberOfMeal = numberOfWork;
        numberOfTraffic = numberOfWork;

        Cursor c = DBmanager.query(vacationColumns, vacationDBManager.TABLE_FIRST, null, null, null, null, null);
        while(c.moveToNext()){
            String type = c.getString(3);
            double count = c.getDouble(4);
            Date startDate = formatter.parse(c.getString(2));
            long lowerDiff = startDate.getTime() - first_payDate.getTime();
            long upperDiff = last_payDate.getTime() - startDate.getTime();
            if(lowerDiff >= 0 && upperDiff >= 0){
                if(type.equals("연가")){
                    numberOfMeal--;
                    numberOfTraffic--;
                    sum_Vac += count;
                }
                else if(type.equals("오전반가")){
                    numberOfMeal--;
                    sum_Vac += count;
                }
                else if(type.equals("오후반가")){
                    sum_Vac += count;
                }
                else if(type.equals("외출")){
                    sum_outing += count;
                }
                else if(type.equals("병가")){
                    numberOfMeal--;
                    numberOfTraffic--;
                    sum_sickVac += count;
                }
                else if(type.equals("오전지참")){
                    numberOfMeal--;
                    sum_sickVac += count;
                }
                else if(type.equals("오후조퇴") || type.equals("병가외출")){
                    sum_sickVac += count;
                }
            }
        }

        thisMonthVac.setText(convertMinuteToProperUnit((int)sum_Vac));
        thisMonthSickVac.setText(convertMinuteToProperUnit((int)sum_sickVac));
        thisMonthOuting.setText(convertMinuteToProperUnit((int)sum_outing));
        int sumOfPay = (int) Math.round((pay+ (mealCost * numberOfMeal) + (trafficCost * numberOfTraffic))/100.0) * 100;
        salaryTextView.setText(decimalFormat.format(sumOfPay) + " KRW");
        int sumOfMeal = mealCost * numberOfMeal;
        int sumOfTraffic = trafficCost * numberOfTraffic;
        int sumOfBeforePromotion = payBeforePromotion * (numberOfEntire - numberOfAfterPromotion) / entireLength;
        int sumOfAfterPromotion = payAfterPromotion * numberOfAfterPromotion / entireLength;
        int sumOfNoPromotion = payBeforePromotion * numberOfEntire / entireLength;

        if(isPromoted){
            toolTipText = ("<b>진급전</b><br>" + decimalFormat.format(sumOfBeforePromotion) + "원 <b>|</b> "
                    + decimalFormat.format(payBeforePromotion) +"원 x " + "(" + (numberOfEntire-numberOfAfterPromotion)
                    + "/" + entireLength + "일)" + "<br><br>" + "<b>진급후</b><br>"+ decimalFormat.format(sumOfAfterPromotion)
                    + "원 <b>|</b> " + decimalFormat.format(payAfterPromotion) +"원 x " + "(" + (numberOfAfterPromotion)
                    + "/" + entireLength + "일)" + "<br><br>" + "<b>식비</b><br>" + decimalFormat.format(sumOfMeal) + "원 <b>|</b> "
                    + decimalFormat.format(mealCost) + "원 x " + numberOfMeal + "일" + "<br><br>" + "<b>교통비</b><br>" + decimalFormat.format(sumOfTraffic) + "원 <b>|</b> "
                    + decimalFormat.format(trafficCost) + "원 * " + numberOfTraffic + "일");
        }
        else {
            toolTipText = ("<b>기본급여</b><br>"+ decimalFormat.format(sumOfNoPromotion) + "원 <b>|</b> " + decimalFormat.format(payBeforePromotion) +"원 x "
                    + "(" + numberOfEntire + "/" + entireLength + "일)" + "<br><br>" + "<b>식비</b><br>" + decimalFormat.format(sumOfMeal) + "원 <b>|</b> "
                    + decimalFormat.format(mealCost) + "원 x " + numberOfMeal + "일" + "<br><br>" + "<b>교통비</b><br>" + decimalFormat.format(sumOfTraffic) + "원 <b>|</b> "
                    + decimalFormat.format(trafficCost) + "원 x " + numberOfTraffic + "일");
        }
    }

    public int getDateLength(int first, int last){
        Date first_payDate = null;
        Date last_payDate = null;
        try {
            first_payDate = dateFormat.parse(first + "");
            last_payDate = dateFormat.parse(last + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int)(((last_payDate.getTime() - first_payDate.getTime()) / (24 * 60 * 60 * 1000)) + 1);
    }

    public int checkFirstDayInclude(int first){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(firstDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int firstDateToInt = ((calendar.get(YEAR) * 10000) + ((calendar.get(MONTH) + 1) * 100)) + calendar.get(DATE);
        if(first - firstDateToInt > 0){
            return first;
        }
        else{
            return firstDateToInt;
        }
    }

    public int checkLastDayInclude(int last){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(lastDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int lastDateToInt = ((calendar.get(YEAR) * 10000) + ((calendar.get(MONTH) + 1) * 100)) + calendar.get(DATE);
        if(last - lastDateToInt < 0){
            return last;
        }
        else{
            return lastDateToInt;
        }
    }

    public String intToDateFormatString(int date){
        String tmp = Integer.toString(date);
        return tmp.substring(0,4) + "." + tmp.substring(4,6) + "." + tmp.substring(6);
    }

    String convertMinuteToProperUnit(int minute){
        if(minute < 0) return "남은휴가없음";
        else {
            int day = minute / 480;
            minute %= 480;
            if(day != 0) {
                if (minute == 240) return ((double) day + 0.5) + "일";
                else {
                    int hour = minute / 60;
                    minute %= 60;
                    if (hour != 0) return minute != 0 ? day + "일 " + hour + "시간 "
                            + minute + "분" : day + "일" + hour + "시간";
                    else return minute != 0 ? day + "일 " + minute + "분" : day + "일";
                }
            }
            else{
                if (minute == 240) return "0.5일";
                else {
                    int hour = minute / 60;
                    minute %= 60;
                    if (hour != 0) return minute != 0 ?  hour + "시간 "
                            + minute + "분" : hour + "시간";
                    else return minute != 0 ? minute + "분" : "0일";
                }
            }
        }
    }
    // ====================== 고치기 ================================
    // 넣은 view의 높이 만큼 위로 올라감
    private void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }
    // ======================================================


    public void setListView(int viewId, int imageId, FragmentManager fg, FragmentTransaction ft,
                            String lowerBoundDate, String upperBoundDate, int numOfYear, String tag){
        ImageView imageView = findViewById(imageId);
        Fragment fragment;
        Frag2_listview listFrag = (Frag2_listview) fg.findFragmentByTag(tag);
        BlankFragment blankFrag = (BlankFragment) fg.findFragmentByTag(tag + "blank");

        if(listFrag == null && blankFrag == null){
            imageView.setImageResource(R.drawable.ic_expand_less);
            fragment = new Frag2_listview().newInstance(lowerBoundDate, upperBoundDate, firstDate,
                    lastDate, numOfYear, searchStartDate);
            ft.replace(viewId, fragment, tag);
            Log.d("getTop", scrollView.getTop() +" "+imageView.getTop()+" "+vacCard1.getTop()+" "+vacCard2.getTop()+" "+vacCard3.getTop()+" ");
        }
        else if(listFrag == null && blankFrag != null){
            imageView.setImageResource(R.drawable.ic_expand_less);
            fragment = new Frag2_listview().newInstance(lowerBoundDate, upperBoundDate, firstDate,
                    lastDate, numOfYear, searchStartDate);
            ft.replace(viewId, fragment, tag);
            Log.d("getTop", scrollView.getTop() +" "+imageView.getTop()+" "+vacCard1.getTop()+" "+vacCard2.getTop()+" "+vacCard3.getTop()+" ");
        }
        else{
            imageView.setImageResource(R.drawable.ic_expand_more);
            fragment = new BlankFragment();
            ft.replace(viewId, fragment,tag + "blank");
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public void refreshListView(int viewId, int imageId, String limitStartDate, String limitLastDate, int numOfYear, String tag){
        FragmentManager fg = getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        ImageView imageView =findViewById(imageId);
        imageView.setImageResource(R.drawable.ic_expand_less);
        Frag2_listview fragment = new Frag2_listview().newInstance(limitStartDate, limitLastDate, firstDate,
                lastDate, numOfYear, searchStartDate);
        ft.replace(viewId, fragment, tag);
        ft.commit();
    }

    public String countDdayFromToday(){
        Calendar today = Calendar.getInstance();
        long todayTime = today.getTime().getTime();
        long lastTime = 0;
        try {
            lastTime = formatter.parse(lastDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long count = (lastTime - todayTime);
        return count > 0 ? "D-" + (int) ((count / (60 * 60 * 24 * 1000)) + 1) : "소집해제";
    }

    public float getPercentage() {
        Calendar today = Calendar.getInstance();
        long todayTime = today.getTime().getTime();
        try {
            long firstTime = formatter.parse(firstDate).getTime();
            long lastTime = formatter.parse(lastDate).getTime();
            float entire = (lastTime - firstTime) / (60 * 60 * 24 * 1000);
            float part = (todayTime - firstTime) / (60 * 60 * 24 * 1000);
            if(entire > 0) {
                return (100 * part / entire);
            }
            else{
                return 100;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

}