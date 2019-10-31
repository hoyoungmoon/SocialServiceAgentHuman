package com.example.realproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

// 밝은회색 3c5c75
// 진한회색 0b1c2d
// 회색글씨 404855, 너무 연하면 3b455b
// 바탕 e6e729
// 작은글씨 a3aaab

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Main_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    vacationDBManager DBmanager = null;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    private static String[] userColumns = new String[]{"id", "nickName", "firstDate", "lastDate", "mealCost", "trafficCost"};
    private static String[] vacationColumns = new String[]{"id", "vacation", "startDate", "type", "count"};

    private String firstDate;
    private String lastDate;
    private String pivotDate;
    private String firstRemain;
    private String secondRemain;

    TextView firstVacRemain;
    TextView secondVacRemain;
    TextView sickVacRemain;

    private TextView nickNameTextView;
    private TextView dDayTextView;
    private TextView servicePeriodTextView;
    private TextView salaryTextView;
    private TextView thisMonthOuting;
    private TextView thisMonthVac;
    private TextView thisMonthSickVac;
    private ProgressBar progressBar;

    Cursor cursor_user;
    Cursor cursor_vac;

    // Main_Activity에 모두 다 넣어야한다
    // Frag2(cardview), Frag2_listview(ListView) main_activity에 넣고
    // databasehelper 다시 수정
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        DBmanager = vacationDBManager.getInstance(this);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        nickNameTextView = findViewById(R.id.tv_nickName);
        dDayTextView = findViewById(R.id.tv_dDay);
        servicePeriodTextView = findViewById(R.id.tv_servicePeriod);
        salaryTextView = findViewById(R.id.tv_salary);
        thisMonthOuting = findViewById(R.id.thisMonthOuting);
        thisMonthVac = findViewById(R.id.thisMonthVac);
        thisMonthSickVac = findViewById(R.id.thisMonthSickVac);
        progressBar = findViewById(R.id.progressBar);

        LinearLayout listButton_1 = findViewById(R.id.listButton);
        LinearLayout listButton_2 = findViewById(R.id.listButton_2);
        LinearLayout listButton_3 = findViewById(R.id.listButton_3);
        Button spendButton = findViewById(R.id.spendButton);
        Button spendButton_2 = findViewById(R.id.spendButton_2);
        Button spendButton_3 = findViewById(R.id.spendButton_3);
        firstVacRemain = findViewById(R.id.first_vacation_remain);
        secondVacRemain = findViewById(R.id.second_vacation_remain);
        sickVacRemain = findViewById(R.id.sick_vacation_remain);

        listButton_1.setOnClickListener(this);
        listButton_2.setOnClickListener(this);
        listButton_3.setOnClickListener(this);
        spendButton.setOnClickListener(this);
        spendButton_2.setOnClickListener(this);
        spendButton_3.setOnClickListener(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton nav_button = findViewById(R.id.navigation_drawer_button);
        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        setUserProfile();
        setRemainVac();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        DialogFrag_DateRevise dialog = new DialogFrag_DateRevise();
        FragmentManager fg = getSupportFragmentManager();
        switch (menuItem.getItemId()) {
            // 소집일 식비 각각 gone 으로 없앨지 말지
            case R.id.date:
                dialog.show(fg, "dialog");
                break;
            case R.id.cost:
                dialog.show(fg, "dialog");
                break;
        }
        return false;
    }

    public void setUserProfile(){
        Cursor c = DBmanager.query(userColumns, vacationDBManager.TABLE_USER, null, null, null, null, null);
        c.moveToFirst();
        firstDate = c.getString(2);
        lastDate = c.getString(3);
        nickNameTextView.setText(c.getString(1));
        servicePeriodTextView.setText(firstDate + " ~ " + lastDate);
        dDayTextView.setText("D-" + countDdayFromToday(lastDate));
        progressBar.setProgress(getPercentage(firstDate, lastDate));

        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(formatter.parse(firstDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.YEAR, 1);
        Date pivotTime = cal.getTime();
        pivotDate = formatter.format(pivotTime);
    }

    public void setRemainVac() {
        try {
            Cursor c =  DBmanager.query(vacationColumns, vacationDBManager.TABLE_FIRST, null, null, null, null, null);
            double firstCount = 0;
            double secondCount = 0;
            long firstTime = formatter.parse(firstDate).getTime();
            long lastTime = formatter.parse(lastDate).getTime();
            long pivotTime = formatter.parse(pivotDate).getTime();
            while (c.moveToNext()) {
                String startDate = c.getString(2);
                long startTime = formatter.parse(startDate).getTime();
                if (startTime - firstTime >= 0 && pivotTime - startTime >= 0) {
                    firstCount += c.getDouble(4);
                } else if (startTime - pivotTime > 0 && lastTime - startTime >= 0) {
                    secondCount += c.getDouble(4);
                }
            }
            c.close();
            firstVacRemain.setText(converMinuteToProperUnit((int)firstCount));
            secondVacRemain.setText(converMinuteToProperUnit((int)secondCount));
        }
        catch(ParseException e){
        }
    }

    // 나중에 년차 별로 받는 연가 수량 넣어야함
    String converMinuteToProperUnit(int useMinute){
        int minute = (15 * 480) - useMinute;
        if(minute < 0) return "남은휴가없음";
        else {
            int day = minute / 480;
            minute %= 480;
            if (minute == 240) {
                return ((double)day + 0.5) + "일";
            } else {
                int hour = minute / 60;
                minute %= 60;
                if (hour != 0) {
                    if (minute != 0) {
                        return day + "일 " + hour + "시간 " + minute + "분";
                    } else {
                        return day + "일" + hour + "시간";
                    }
                } else {
                    if (minute != 0) {
                        return day + "일 " + minute + "분";
                    } else {
                        return day + "일";
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

        FragmentManager fg = getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        Frag2_save dialog;

        switch (view.getId()) {
            // 1년차 휴가 2년차 휴가 listview에 나타나는것 다르게
            case R.id.listButton:
                setListView(R.id.fragment_container_1, R.id.first_vacation_image, fg, ft,
                        firstDate, pivotDate, false);
                break;

            case R.id.listButton_2:
                setListView(R.id.fragment_container_2, R.id.second_vacation_image, fg, ft,
                        pivotDate, lastDate, false);
                break;

            case R.id.listButton_3:
                setListView(R.id.fragment_container_3, R.id.sick_vacation_image, fg, ft,
                        firstDate, lastDate, true);
                break;

            // 1년차 연가, 2년차 연가 datePickerDialog 제한 어떻게 할지 결정하자. (그냥 소집~소집해제 or 소집~(소집+1)~소집해제)
            case R.id.spendButton:
                dialog = new Frag2_save().newInstance(firstDate, pivotDate);
                dialog.show(fg, "dialog");
                break;

            case R.id.spendButton_2:
                dialog = new Frag2_save().newInstance(pivotDate, lastDate);
                dialog.show(fg, "dialog");
                break;

            case R.id.spendButton_3:
                break;
        }
    }

    public void setListView(int viewId, int imageId, FragmentManager fg, FragmentTransaction ft,
                            String lowerBoundDate, String upperBoundDate, boolean isSickListView){
        ImageView imageView = findViewById(imageId);
        Fragment fragment;
        Frag2_listview frag = (Frag2_listview) fg.findFragmentByTag("filled");
        if(frag == null){
            imageView.setImageResource(R.drawable.ic_expand_less);
            fragment = new Frag2_listview(lowerBoundDate, upperBoundDate, isSickListView);
            ft.replace(viewId, fragment, "filled");
        }
        else{
            imageView.setImageResource(R.drawable.ic_expand_more);
            fragment = new BlankFragment();
            ft.replace(viewId, fragment,"unfilled");
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public int countDdayFromToday(String lastDate) {
        Calendar today = Calendar.getInstance();
        long todayTime = today.getTime().getTime();
        try {
            long lastTime = formatter.parse(lastDate).getTime();
            long count = (lastTime - todayTime);
            return (int) (count / (60 * 60 * 24 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getPercentage(String firstDate, String lastDate) {
        Calendar today = Calendar.getInstance();
        long todayTime = today.getTime().getTime();
        try {
            long firstTime = formatter.parse(firstDate).getTime();
            long lastTime = formatter.parse(lastDate).getTime();
            long entire = (lastTime - firstTime) / (60 * 60 * 24 * 1000);
            long part = (todayTime - firstTime) / (60 * 60 * 24 * 1000);
            return (int) (100 * part / entire);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

}