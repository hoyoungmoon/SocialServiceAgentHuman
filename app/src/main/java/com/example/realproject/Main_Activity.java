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
import android.widget.ImageButton;
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
import java.util.Locale;

public class Main_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    vacationDBManager DBmanager = null;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    private static String[] userColumns = new String[]{"id", "nickName", "firstDate", "lastDate", "mealCost", "trafficCost"};
    private static String[] vacationColumns = new String[]{"id", "vacation", "startDate", "type", "count"};
    private TextView nickNameTextView;
    private TextView dDayTextView;
    private TextView servicePeriodTextView;
    private TextView salaryTextView;
    private TextView thisMonthOuting;
    private TextView thisMonthVac;
    private TextView thisMonthSickVac;
    private ProgressBar progressBar;
    String firstDate;
    String lastDate;

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


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton nav_button = findViewById(R.id.navigation_drawer_button);
        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        Cursor cursor_user = DBmanager.query(userColumns, vacationDBManager.TABLE_USER, null, null, null, null, null);
        Cursor cursor_vac = DBmanager.query(vacationColumns, vacationDBManager.TABLE_FIRST, null, null, null, null, null);
        cursor_user.moveToFirst();
        firstDate = cursor_user.getString(2);
        lastDate = cursor_user.getString(3);
        nickNameTextView.setText(cursor_user.getString(1));
        servicePeriodTextView.setText(firstDate + " ~ " + lastDate);
        dDayTextView.setText("D-" + countDdayfromToday(lastDate));
        progressBar.setProgress(getPercentage(firstDate, lastDate));
        cursor_user.close();


        Fragment frag2 = new Frag2();
        Bundle bundle = new Bundle(4);
        bundle.putString("firstDate", firstDate);
        bundle.putString("lastDate", lastDate);
        bundle.putString("firstRemain", getRemainVac(cursor_vac, 1));
        bundle.putString("secondRemain", getRemainVac(cursor_vac, 2));
        frag2.setArguments(bundle);
        cursor_vac.close();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, frag2);
        ft.commit();
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

    public int countDdayfromToday(String lastDate) {
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

    public String getRemainVac(Cursor c, int numberOfYear) {
        try {
            double firstCount = 0;
            double secondCount = 0;
            Calendar cal = Calendar.getInstance();
            cal.setTime(formatter.parse(firstDate));
            cal.add(Calendar.YEAR, 1);
            long firstTime = formatter.parse(firstDate).getTime();
            long lastTime = formatter.parse(lastDate).getTime();
            long pivotTime = cal.getTime().getTime();
            while (c.moveToNext()) {
                String startDate = c.getString(2);
                long startTime = formatter.parse(startDate).getTime();
                if (startTime - firstTime >= 0 && pivotTime - startTime >= 0) {
                    firstCount += c.getDouble(4);
                } else if (startTime - pivotTime > 0 && lastTime - startTime >= 0) {
                    secondCount += c.getDouble(4);
                }
            }
            if (numberOfYear == 1) {
                return "" + firstCount;
            } else {
                return "" + secondCount;
            }
        }
        catch(ParseException e){
            return " ";
        }
    }
}
