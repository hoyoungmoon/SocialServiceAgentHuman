package com.project.realproject.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.project.realproject.R;
import com.project.realproject.Salary;
import com.project.realproject.User;
import com.project.realproject.helpers.DBHelper;
import com.project.realproject.helpers.FontChangeCrawler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.project.realproject.helpers.DBHelper.TABLE_USER;
import static com.project.realproject.helpers.Formatter.*;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class SalaryCalculatorFragment extends Fragment implements View.OnClickListener, NumberPickerFragment.NumberPickerSaveListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String[] columns = new String[]{"id", "nickName", "firstDate", "lastDate",
            "mealCost", "trafficCost", "totalFirstVac", "totalSecondVac", "totalSickVac"};
    private Date startDate;
    private Date endDate;
    private int baseSalary;
    private int monthlyPay;
    private int mealCost;
    private int trafficCost;
    private int mealSetValue;
    private int trafficSetValue;
    private int notWorkNum;
    private int morningNum;
    private boolean isOriginalPay = true;
    private boolean resultOpen = false;

    private LinearLayout entireLayout;
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

    private User user;
    private Salary salary;
    private DBHelper DBmanager;
    private AdView mAdView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SalaryCalculatorFragment() {
        // Required empty public constructor
    }

    public static SalaryCalculatorFragment newInstance(String param1, String param2) {
        SalaryCalculatorFragment fragment = new SalaryCalculatorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salary_calculator, container, false);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        DBmanager = new DBHelper(getContext());

        entireLayout = view.findViewById(R.id.calculatorLayout);
        calculateLinear = view.findViewById(R.id.cal_linear1);
        resultLinear = view.findViewById(R.id.cal_salary_result);
        startSearchDateTextView = view.findViewById(R.id.cal_first);
        endSearchDateTextView = view.findViewById(R.id.cal_last);
        payTextView = view.findViewById(R.id.cal_pay_text);
        payEditText = view.findViewById(R.id.cal_pay_edit);
        payEditText.setInputType(TYPE_CLASS_NUMBER);
        mealCostTextView = view.findViewById(R.id.cal_meal);
        trafficCostTextView = view.findViewById(R.id.cal_traffic);
        notWorkTextView = view.findViewById(R.id.cal_notWork);
        morningTextView = view.findViewById(R.id.cal_morning);
        calculateButton = view.findViewById(R.id.cal_calculate);
        payTypeRadioGroup = view.findViewById(R.id.cal_pay_type);

        resultWorkTextView = view.findViewById(R.id.cal_result_work);
        resultPayTextView = view.findViewById(R.id.cal_result_pay);
        resultMealTextView = view.findViewById(R.id.cal_result_meal);
        resultTrafficTextView = view.findViewById(R.id.cal_result_traffic);
        resultTotalTextView = view.findViewById(R.id.cal_result_total);

        setLayoutTransition(entireLayout);
        startSearchDateTextView.setOnClickListener(this);
        endSearchDateTextView.setOnClickListener(this);
        payTextView.setOnClickListener(this);
        mealCostTextView.setOnClickListener(this);
        trafficCostTextView.setOnClickListener(this);
        notWorkTextView.setOnClickListener(this);
        morningTextView.setOnClickListener(this);
        calculateButton.setOnClickListener(this);

        Calendar cal = Calendar.getInstance();
        cal.set(DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        startSearchDateTextView.setText(formatter.format(cal.getTime()));
        cal.set(DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endSearchDateTextView.setText(formatter.format(cal.getTime()));

        if (DBmanager.getDataCount(TABLE_USER) != 0) {
            user = new User(getContext());
            mealSetValue = user.getMealCost();
            trafficSetValue = user.getTrafficCost();
            mealCostTextView.setText(mealSetValue + " 원");
            trafficCostTextView.setText(trafficSetValue + " 원");
            payTextView.setText(decimalFormat.format(user.getBaseSalary(cal.getTime())) + " 원");

        } else {
            mealSetValue = 6000;
            trafficSetValue = 2700;
            mealCostTextView.setText("6000 원");
            trafficCostTextView.setText("2700 원");
        }

        payTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.cal_pay_type_exist) {
                    isOriginalPay = true;
                    payEditText.setVisibility(GONE);
                    payTextView.setVisibility(VISIBLE);
                    try {
                        payTextView.setText(decimalFormat.format(user.getBaseSalary(
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


        return view;
    }


    @Override
    public void onClick(View view) {
        FragmentManager fg = getFragmentManager();
        switch (view.getId()) {
            case R.id.cal_first:
                setDatePickerDialog(startSearchDateTextView).show();
                break;
            case R.id.cal_last:
                setDatePickerDialog(endSearchDateTextView).show();
                break;
            case R.id.cal_meal:
                NumberPickerFragment.newInstance(this, "mealCost", mealSetValue,
                        0, 10000, 500).show(fg, "dialog");
                break;
            case R.id.cal_traffic:
                NumberPickerFragment.newInstance(this, "trafficCost", trafficSetValue,
                        0, 5000, 100).show(fg, "dialog");
                break;
            case R.id.cal_notWork:
                NumberPickerFragment.newInstance(this, "workNum", 0,
                        0, 31, 1).show(fg, "dialog");
                break;
            case R.id.cal_morning:
                NumberPickerFragment.newInstance(this, "morningNum", 0,
                        0, 31, 1).show(fg, "dialog");
                break;
            case R.id.cal_calculate:
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
                    salaryCalculate();
                }
                break;
        }
    }

    private void salaryCalculate(){
        try {
            salary = new Salary(getContext(), formatter.parse(startSearchDateTextView.getText().toString()),
                    formatter.parse(endSearchDateTextView.getText().toString()));
            startDate = formatter.parse(startSearchDateTextView.getText().toString());
            endDate= formatter.parse(endSearchDateTextView.getText().toString());
            if (payTextView.getVisibility() == GONE) {
                monthlyPay = getTagOnlyInt(payEditText.getText().toString());
            }
            mealCost = getTagOnlyInt(mealCostTextView.getText().toString());
            trafficCost = getTagOnlyInt(trafficCostTextView.getText().toString());
            notWorkNum = getTagOnlyInt(notWorkTextView.getText().toString());
            morningNum = getTagOnlyInt(morningTextView.getText().toString());

            int workNum = getDateLengthExceptSaturdayAndSunday(startDate, endDate);

            int totalMealSalary = mealCost * (workNum - notWorkNum - morningNum);
            int totalTrafficSalary = trafficCost * (workNum - notWorkNum);
            resultWorkTextView.setText((workNum - notWorkNum) + " 일");
            resultMealTextView.setText(toMoneyUnit(mealCost) + " x " + (workNum - notWorkNum - morningNum) + "일 =   "
                    + toMoneyUnit(totalMealSalary));
            resultTrafficTextView.setText(toMoneyUnit(trafficCost) + " x " + (workNum - notWorkNum) + "일 =   "
                    + toMoneyUnit(totalTrafficSalary));



            // 기존 소집일 기준으로 월에 따라 진급할때 계산
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int previousMonth = calendar.get(MONTH);
            int count = 0;
            double totalBaseSalary = 0;
            int baseSalary = user.getBaseSalary(startDate);
            int totalMonthLength = getMonthLength(startDate);   // 분모
            int totalSearchLength = -1;  // 분자

            ArrayList<int[]> infoList = new ArrayList<>();

            while (calendar.getTime().compareTo(endDate) <= 0) {
                totalSearchLength++;
                if (previousMonth != calendar.get(MONTH)) {
                    int[] baseSalaryInfo = new int[3];
                    baseSalaryInfo[0] = (payTextView.getVisibility() == VISIBLE ? baseSalary : monthlyPay);
                    baseSalaryInfo[1] = totalMonthLength;
                    baseSalaryInfo[2] = totalSearchLength;
                    infoList.add(count, baseSalaryInfo);
                    count++;

                    totalSearchLength = 0;
                    totalMonthLength = getMonthLength(calendar.getTime());
                    previousMonth = calendar.get(MONTH);
                    baseSalary = user.getBaseSalary(calendar.getTime());
                }
                calendar.add(DATE, 1);
            }

            totalSearchLength++;
            int[] baseSalaryInfo = new int[3];
            baseSalaryInfo[0] = (payTextView.getVisibility() == VISIBLE ? baseSalary : monthlyPay);
            baseSalaryInfo[1] = totalMonthLength;
            baseSalaryInfo[2] = totalSearchLength;
            infoList.add(count, baseSalaryInfo);




            String baseSalaryString = "";
            int monthCount = 0;
            for (int i = 0; i < infoList.size(); i++) {

                Log.d("baseSalary", i + " : " + infoList.get(i)[0] + ", " + infoList.get(i)[1] + ", " + infoList.get(i)[2]);

                // 처음에 설정
                if (i == 0) {
                    baseSalary = infoList.get(i)[0];
                    baseSalaryString += toMoneyUnit(infoList.get(i)[0]) + " x [";
                }

                // 분자, 분모 비교하여 같으면 개월수++, 다르면 바로 분수로 입력시키기
                if (infoList.get(i)[1] == infoList.get(i)[2]) {
                    monthCount++;
                } else {
                    baseSalaryString += ("(" + infoList.get(i)[2] + "/" + infoList.get(i)[1] + ")개월 + ");
                    totalBaseSalary += (double) baseSalary * ((double) infoList.get(i)[2] / (double) infoList.get(i)[1]);
                }

                // 기본급이 바뀌면 개월수 입력시키고 설정 초기화
                if (baseSalary != infoList.get(i)[0]) {
                    if (monthCount != 0) baseSalaryString += (monthCount + "개월 + ");
                    totalBaseSalary += baseSalary * monthCount;
                    baseSalaryString += ("]\n" + toMoneyUnit(infoList.get(i)[0]) + " x [");
                    baseSalary = infoList.get(i)[0];
                    monthCount = 0;
                }

                // 마지막에 남은 개월수 입력시키기
                if (i == infoList.size() - 1) {
                    if (monthCount != 0) baseSalaryString += (monthCount + "개월 + ");
                    totalBaseSalary += baseSalary * monthCount;
                }
            }
            baseSalaryString += "]\n= " + toMoneyUnit(totalBaseSalary);
            baseSalaryString = baseSalaryString.replace(" + ]", "]");

            resultPayTextView.setText(baseSalaryString);
            resultTotalTextView.setText(toMoneyUnit(totalBaseSalary + totalMealSalary + totalTrafficSalary));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public DatePickerDialog setDatePickerDialog(TextView dateTextView) {
        final TextView someDateTextView = dateTextView;
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog returnDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year, monthOfYear, dayOfMonth);
                        someDateTextView.setText(formatter.format(dateCalendar
                                .getTime()));
                        if (payTextView.getVisibility() == VISIBLE) {
                            payTextView.setText(decimalFormat.format(user.getBaseSalary(dateCalendar.getTime())));
                        }
                    }
                }, newCalendar.get(YEAR),
                newCalendar.get(MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        return returnDialog;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "nanum_bareun_font.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }

}
