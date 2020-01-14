package com.application.socialagent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.application.socialagent.vacationDBManager.TABLE_USER;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class Calculator_Activity extends AppCompatActivity implements View.OnClickListener, NumberPickerDialog.NumberPickerSaveListener {

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    private static final DecimalFormat decimalFormat = new DecimalFormat("###,###,###,### 원");
    private static String[] columns = new String[]{"id", "nickName", "firstDate", "lastDate",
            "mealCost", "trafficCost", "totalFirstVac", "totalSecondVac", "totalSickVac", "payDay"};
    private Date startDate;
    private Date endDate;
    private int monthlyPay;
    private int mealCost;
    private int trafficCost;
    private int mealSetValue;
    private int trafficSetValue;
    private int notWorkNum;
    private int morningNum;
    private boolean isOriginalPay = true;
    private boolean resultOpen = false;

    private LinearLayout calculateLinear;
    private LinearLayout resultLinear;
    private TextView startSearchDateTextView;
    private TextView endSearchDateTextView;
    private TextView payTextView;
    private EditText payEditText;
    private TextView mealCostTextView;
    private TextView trafficCostTextView;
    private TextView notWorkTextView;
    private TextView morningTextView;
    private Button calculateButton;
    private Calendar dateCalendar;
    private RadioGroup payTypeRadioGroup;

    private TextView resultWorkTextView;
    private TextView resultPayTextView;
    private TextView resultMealTextView;
    private TextView resultTrafficTextView;
    private TextView resultTotalTextView;

    private AdView mAdView;

    vacationDBManager DBmanager;
    PayDependsOnMonth pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator_activity);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        DBmanager = vacationDBManager.getInstance(this);


        calculateLinear = findViewById(R.id.cal_linear1);
        resultLinear = findViewById(R.id.cal_linear2);
        startSearchDateTextView = findViewById(R.id.cal_first);
        endSearchDateTextView = findViewById(R.id.cal_last);
        payTextView = findViewById(R.id.cal_pay_text);
        payEditText = findViewById(R.id.cal_pay_edit);
        payEditText.setInputType(TYPE_CLASS_NUMBER);
        mealCostTextView = findViewById(R.id.cal_meal);
        trafficCostTextView = findViewById(R.id.cal_traffic);
        notWorkTextView = findViewById(R.id.cal_notWork);
        morningTextView = findViewById(R.id.cal_morning);
        calculateButton = findViewById(R.id.cal_calculate);
        payTypeRadioGroup = findViewById(R.id.cal_pay_type);

        resultWorkTextView = findViewById(R.id.cal_result_work);
        resultPayTextView = findViewById(R.id.cal_result_pay);
        resultMealTextView = findViewById(R.id.cal_result_meal);
        resultTrafficTextView = findViewById(R.id.cal_result_traffic);
        resultTotalTextView = findViewById(R.id.cal_result_total);

        startSearchDateTextView.setOnClickListener(this);
        endSearchDateTextView.setOnClickListener(this);
        payTextView.setOnClickListener(this);
        mealCostTextView.setOnClickListener(this);
        trafficCostTextView.setOnClickListener(this);
        notWorkTextView.setOnClickListener(this);
        morningTextView.setOnClickListener(this);
        calculateButton.setOnClickListener(this);


        if (DBmanager.getDataCount(TABLE_USER) != 0) {
            Cursor c = DBmanager.query(columns, vacationDBManager.TABLE_USER, null, null, null, null, null);
            c.moveToFirst();
            mealSetValue = c.getInt(4);
            trafficSetValue = c.getInt(5);
            Calendar cal = Calendar.getInstance();
            pay = PayDependsOnMonth.getInstance(c.getString(2));
            startSearchDateTextView.setText(formatter.format(cal.getTime()));
            payTextView.setText(decimalFormat.format(pay.payDependsOnMonth(cal.getTime())));
            cal.add(MONTH, 1);
            cal.add(DATE, -1);
            endSearchDateTextView.setText(formatter.format(cal.getTime()));
            mealCostTextView.setText(mealSetValue + " 원");
            trafficCostTextView.setText(trafficSetValue + " 원");

        } else {
            mealCostTextView.setText("6000 원");
            trafficCostTextView.setText("2700 원");
        }
        notWorkTextView.setText("0 일");
        morningTextView.setText("0 일");

        payTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.cal_pay_type_exist) {
                    isOriginalPay = true;
                    payEditText.setVisibility(GONE);
                    payTextView.setVisibility(VISIBLE);
                    try {
                        payTextView.setText(decimalFormat.format(pay.payDependsOnMonth(
                                formatter.parse(startSearchDateTextView.getText().toString()))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else if (i == R.id.cal_pay_type_input) {
                    isOriginalPay = false;
                    payEditText.setVisibility(VISIBLE);
                    payTextView.setVisibility(GONE);
                }
            }
        });
    }


    public DatePickerDialog setDatePickerDialog(TextView dateTextView) {
        final TextView someDateTextView = dateTextView;
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog returnDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year, monthOfYear, dayOfMonth);
                        someDateTextView.setText(formatter.format(dateCalendar
                                .getTime()));
                        if (payTextView.getVisibility() == VISIBLE) {
                            payTextView.setText(decimalFormat.format(pay.payDependsOnMonth(dateCalendar.getTime())));
                        }
                    }
                }, newCalendar.get(YEAR),
                newCalendar.get(MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        return returnDialog;
    }

    public void setNumberPickerDialog(String userInfo, int setValue, int minValue, int maxValue, int step, FragmentManager fg) {
        NumberPickerDialog dialog = new NumberPickerDialog(this);
        Bundle bundle = new Bundle(5);
        // 이미 세팅되어있던 값 넣기
        bundle.putString("userInfo", userInfo);
        bundle.putInt("setValue", setValue);
        bundle.putInt("minValue", minValue);
        bundle.putInt("maxValue", maxValue);
        bundle.putInt("step", step);
        dialog.setArguments(bundle);
        dialog.show(fg, "dialog");
    }

    @Override
    public void onSaveBtnClick(String userInfo, String saveValue) {
        switch (userInfo) {
            case "mealCost":
                mealCostTextView.setText(saveValue + " 원");
                break;
            case "trafficCost":
                trafficCostTextView.setText(saveValue + " 원");
                break;
            case "workNum":
                notWorkTextView.setText(saveValue + " 일");
                break;
            case "morningNum":
                morningTextView.setText(saveValue + "일");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        FragmentManager fg = getSupportFragmentManager();
        switch (view.getId()) {
            case R.id.cal_first:
                setDatePickerDialog(startSearchDateTextView).show();
                break;
            case R.id.cal_last:
                try {
                    Calendar cal = Calendar.getInstance();
                    DatePickerDialog dialog = setDatePickerDialog(endSearchDateTextView);
                    startDate = formatter.parse(startSearchDateTextView.getText().toString());
                    cal.setTime(startDate);
                    cal.add(MONTH, 1);
                    cal.add(DATE, -1);
                    dialog.getDatePicker().setMinDate(startDate.getTime());
                    dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                    dialog.show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cal_meal:
                setNumberPickerDialog("mealCost", mealSetValue, 0, 10000, 500, fg);
                break;
            case R.id.cal_traffic:
                setNumberPickerDialog("trafficCost", trafficSetValue, 0, 5000, 100, fg);
                break;
            case R.id.cal_notWork:
                setNumberPickerDialog("workNum", 0,0, 31, 1, fg);
                break;
            case R.id.cal_morning:
                setNumberPickerDialog("morningNum", 0,0, 31, 1, fg);
                break;
            case R.id.cal_calculate:
                if (isLessThanOneMonth()) {

                    if (resultOpen) {
                        resultOpen = false;
                        resultLinear.setVisibility(GONE);
                        calculateLinear.setVisibility(VISIBLE);
                        calculateButton.setText("계산하기");
                    } else {
                        resultOpen = true;
                        resultLinear.setVisibility(VISIBLE);
                        calculateLinear.setVisibility(GONE);
                        calculateButton.setText("다시 계산하기");
                        calculate();
                    }
                } else {
                    blankAlert("1달 미만의 기간만 검색 가능합니다");
                }
                break;
        }
    }

    public void calculate() {
        Calendar cal = Calendar.getInstance();
        try {
            startDate = formatter.parse(startSearchDateTextView.getText().toString());
            endDate = formatter.parse(endSearchDateTextView.getText().toString());
            if (payTextView.getVisibility() == VISIBLE) {
                monthlyPay = getTagOnlyInt(payTextView.getText().toString());
            } else {
                monthlyPay = getTagOnlyInt(payEditText.getText().toString());
            }
            mealCost = getTagOnlyInt(mealCostTextView.getText().toString());
            trafficCost = getTagOnlyInt(trafficCostTextView.getText().toString());
            notWorkNum = getTagOnlyInt(notWorkTextView.getText().toString());
            morningNum = getTagOnlyInt(morningTextView.getText().toString());

            boolean isPromoted = false;
            double payBeforePromotion = 0;
            double payAfterPromotion = 0;
            int dayAfterPromotion = 0;
            int workNum = 0;
            double finalPay;
            Date searchDate = startDate;
            int monthLength = getMonthLength(searchDate);
            double checkPromotion = (double) pay.payDependsOnMonth(searchDate) / monthLength;
            while (searchDate.compareTo(endDate) <= 0) {
                cal.setTime(searchDate);
                if (isOriginalPay && checkPromotion != (double) pay.payDependsOnMonth(searchDate) / monthLength) {
                    isPromoted = true;
                    dayAfterPromotion++;
                    payAfterPromotion += (double) pay.payDependsOnMonth(searchDate) / monthLength;
                } else {
                    payBeforePromotion += (double) monthlyPay / monthLength;
                }
                if (cal.get(Calendar.DAY_OF_WEEK) != SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != SUNDAY) {
                    workNum++;
                }
                cal.add(DATE, 1);
                searchDate = cal.getTime();
            }
            finalPay = payAfterPromotion + payBeforePromotion + mealCost * (workNum - notWorkNum - morningNum)
                    + trafficCost * (workNum - notWorkNum);
            if (isPromoted) {
                resultPayTextView.setText("인상전   " + toMoneyUnit(pay.payDependsOnMonth(startDate)) + " x (" + (monthLength - dayAfterPromotion) + "일 / " + monthLength + "일) =   " +
                        toMoneyUnit(payBeforePromotion) + "\n인상후   " + toMoneyUnit(pay.payDependsOnMonth(endDate)) + " x (" + dayAfterPromotion + "일 / " + monthLength + "일) =   " +
                        toMoneyUnit(payAfterPromotion));
            } else {
                resultPayTextView.setText(toMoneyUnit(monthlyPay) + " x (" + getLength(startDate, endDate) + "일/" + monthLength + "일) =   " +
                        toMoneyUnit(payBeforePromotion));
            }
            resultWorkTextView.setText((workNum - notWorkNum) + " 일");
            resultMealTextView.setText(mealCost + "원 x " + (workNum - notWorkNum - morningNum) + "일 =   "
                    + toMoneyUnit(mealCost * (workNum - notWorkNum - morningNum)));
            resultTrafficTextView.setText(trafficCost + "원 x " + (workNum - notWorkNum) + "일 =   "
                    + toMoneyUnit(trafficCost * (workNum - notWorkNum)));
            resultTotalTextView.setText(toMoneyUnit(finalPay));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isLessThanOneMonth(){
        try {
            Calendar calendar = getInstance();
            startDate = formatter.parse(startSearchDateTextView.getText().toString());
            endDate = formatter.parse(endSearchDateTextView.getText().toString());
            calendar.setTime(startDate);
            calendar.add(MONTH, 1);
            Date pivotDate = calendar.getTime();
            if(endDate.compareTo(pivotDate) < 0) return true;
            else return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getMonthLength(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(MONTH, 1);
        calendar.add(DATE, -1);
        Date lastDate = calendar.getTime();

        return (int) ((lastDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000)) + 1;
    }

    public int getLength(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000)) + 1;
    }

    public String toMoneyUnit(double money) {
        return decimalFormat.format(Math.round((money) / 100.0) * 100);
    }

    public int getTagOnlyInt(String tag) {
        String reTag = tag.replaceAll("[^0-9]", "");
        return Integer.parseInt(reTag);
    }

    public void blankAlert(String alert) {
        new AlertDialog.Builder(this)
                .setMessage(alert)
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
