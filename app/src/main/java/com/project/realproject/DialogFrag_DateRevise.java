package com.project.realproject;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static com.project.realproject.vacationDBManager.TABLE_USER;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class DialogFrag_DateRevise extends DialogFragment implements View.OnClickListener, NumberPickerDialog.NumberPickerSaveListener {

    private EditText nickNameEditText;
    private TextView firstDateEditText;
    private TextView lastDateEditText;
    private TextView mealCostEditText;
    private TextView trafficCostEditText;
    private TextView totalFirstVacEditText;
    private TextView totalSecondVacEditText;
    private TextView totalSickVacEditText;
    private TextView payDayEditText;
    private Button saveButton;
    private Button cancelButton;
    private AdView mAdView;

    private static String[] columns = new String[]{"id", "nickName", "firstDate", "lastDate",
            "mealCost", "trafficCost", "totalFirstVac", "totalSecondVac", "totalSickVac", "payDay"};
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    DatePickerDialog firstDatePickerDialog;
    DatePickerDialog lastDatePickerDialog;
    Calendar dateCalendar;
    vacationDBManager DBmanager;

    public DialogFrag_DateRevise() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        DBmanager = vacationDBManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_frag_date_revise, container, false);

        nickNameEditText = view.findViewById(R.id.et_nickName);
        nickNameEditText.setInputType(TYPE_CLASS_TEXT);
        firstDateEditText = view.findViewById(R.id.tv_firstDate);
        lastDateEditText = view.findViewById(R.id.tv_lastDate);
        mealCostEditText = view.findViewById(R.id.tv_mealCost);
        trafficCostEditText = view.findViewById(R.id.tv_trafficCost);
        totalFirstVacEditText = view.findViewById(R.id.tv_totalFirstVac);
        totalSecondVacEditText = view.findViewById(R.id.tv_totalSecondVac);
        totalSickVacEditText = view.findViewById(R.id.tv_totalSickVac);
        payDayEditText = view.findViewById(R.id.tv_payDay);
        saveButton = view.findViewById(R.id.btn_save);
        cancelButton = view.findViewById(R.id.btn_cancel);


        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        if (DBmanager.getDataCount(TABLE_USER) != 0) {
            Cursor c = DBmanager.query(columns, vacationDBManager.TABLE_USER, null, null, null, null, null);
            c.moveToFirst();
            nickNameEditText.setText(c.getString(1));
            firstDateEditText.setText(c.getString(2));
            lastDateEditText.setText(c.getString(3));
            mealCostEditText.setText(c.getString(4) + " 원");
            trafficCostEditText.setText(c.getString(5) + " 원");
            totalFirstVacEditText.setText(c.getString(6) + " 일");
            totalSecondVacEditText.setText(c.getString(7) + " 일");
            totalSickVacEditText.setText(c.getString(8) + " 일");
            payDayEditText.setText("매월 " + c.getString(9) + " 일");
        } else {
            cancelButton.setVisibility(View.GONE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(YEAR, 1);
            calendar.add(MONTH, 9);
            firstDateEditText.setText(formatter.format(Calendar.getInstance().getTime()));
            lastDateEditText.setText(formatter.format(calendar.getTime()));
            mealCostEditText.setText("6000 원");
            trafficCostEditText.setText("2700 원");
            totalFirstVacEditText.setText("15 일");
            totalSecondVacEditText.setText("15 일");
            totalSickVacEditText.setText("30 일");
            payDayEditText.setText("매월 1 일");
        }

        firstDateEditText.setOnClickListener(this);
        lastDateEditText.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        mealCostEditText.setOnClickListener(this);
        trafficCostEditText.setOnClickListener(this);
        totalFirstVacEditText.setOnClickListener(this);
        totalSecondVacEditText.setOnClickListener(this);
        totalSickVacEditText.setOnClickListener(this);
        payDayEditText.setOnClickListener(this);

        firstDatePickerDialog = setDatePickerDialog(firstDateEditText);
        lastDatePickerDialog = setDatePickerDialog(lastDateEditText);

        return view;
    }

    public DatePickerDialog setDatePickerDialog(TextView dateTextView) {
        final TextView someDateTextView = dateTextView;
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog returnDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year, monthOfYear, dayOfMonth);
                        someDateTextView.setText(formatter.format(dateCalendar
                                .getTime()));
                    }
                }, newCalendar.get(YEAR),
                newCalendar.get(MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        return returnDialog;
    }

    public void setNumberPickerDialog(String userInfo, int minValue, int maxValue, int step, FragmentManager fg) {
        NumberPickerDialog dialog = new NumberPickerDialog(this);
        Bundle bundle = new Bundle(4);
        // 이미 세팅되어있던 값 넣기
        bundle.putString("userInfo", userInfo);
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
                mealCostEditText.setText(saveValue + " 원");
                break;
            case "tarfficCost":
                trafficCostEditText.setText(saveValue + " 원");
                break;
            case "totalFirstVac":
                totalFirstVacEditText.setText(saveValue + " 일");
                break;
            case "totalSecondVac":
                totalSecondVacEditText.setText(saveValue + " 일");
                break;
            case "totalSickVac":
                totalSickVacEditText.setText(saveValue + " 일");
                break;
            case "payDay":
                payDayEditText.setText(saveValue + " 일");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        FragmentManager fg = getFragmentManager();
        try {
            switch (view.getId()) {
                case R.id.tv_firstDate:
                    firstDatePickerDialog.show();
                    break;
                case R.id.tv_lastDate:
                    lastDatePickerDialog.show();
                    break;
                case R.id.btn_save:
                    if ((formatter.parse(lastDateEditText.getText().toString()).getTime() -
                            formatter.parse(firstDateEditText.getText().toString()).getTime()) / (24 * 60 * 60 * 1000) < 367) {
                        blankAlert("복무기간이 1년을 초과하도록 설정해주세요");
                    } else {
                        User user = new User();
                        user.setNickName(nickNameEditText.getText().toString());
                        user.setFirstDate(formatter.parse(firstDateEditText.getText().toString()));
                        user.setLastDate(formatter.parse(lastDateEditText.getText().toString()));
                        user.setMealCost(getTagOnlyInt(mealCostEditText.getText().toString()));
                        user.setTrafficCost(getTagOnlyInt(trafficCostEditText.getText().toString()));
                        user.setTotalFirstVac(getTagOnlyInt(totalFirstVacEditText.getText().toString()));
                        user.setTotalSecondVac(getTagOnlyInt(totalSecondVacEditText.getText().toString()));
                        user.setTotalSickVac(getTagOnlyInt(totalSickVacEditText.getText().toString()));
                        user.setPayDay(getTagOnlyInt(payDayEditText.getText().toString()));

                        if (DBmanager.getDataCount(TABLE_USER) == 0) {
                            DBmanager.insertUser(user);
                        } else {
                            Cursor c = DBmanager.query(columns, vacationDBManager.TABLE_USER, null, null, null, null, null);
                            c.moveToFirst();
                            int id = c.getInt(0);
                            ContentValues values = new ContentValues();
                            values.put("nickName", user.getNickName());
                            values.put("firstDate", formatter.format(user.getFirstDate()));
                            values.put("lastDate", formatter.format(user.getLastDate()));
                            values.put("mealCost", user.getMealCost());
                            values.put("trafficCost", user.getTrafficCost());
                            values.put("totalFirstVac", user.getTotalFirstVac());
                            values.put("totalSecondVac", user.getTotalSecondVac());
                            values.put("totalSickVac", user.getTotalSickVac());
                            values.put("payDay", user.getPayDay());
                            DBmanager.updateUser(id, values);
                        }
                        // 오류 없는지 확인
                        if (DBmanager.getDataCount(vacationDBManager.TABLE_USER) != 0) {
                            ((Main_Activity) getActivity()).load();
                            ((Main_Activity) getActivity()).resetTimer();
                            ((Main_Activity) getActivity()).startTimer();
                        }
                        dismiss();
                    }
                    break;
                case R.id.tv_mealCost:
                    setNumberPickerDialog("mealCost", 5000, 10000, 1000, fg);
                    break;
                case R.id.tv_trafficCost:
                    setNumberPickerDialog("trafficCost", 2000, 5000, 100, fg);
                    break;
                case R.id.tv_totalFirstVac:
                    setNumberPickerDialog("totalFirstVac", 10, 30, 1, fg);
                    break;
                case R.id.tv_totalSecondVac:
                    setNumberPickerDialog("totalSecondVac", 10, 30, 1, fg);
                    break;
                case R.id.tv_totalSickVac:
                    setNumberPickerDialog("totalSickVac", 20, 40, 1, fg);
                    break;
                case R.id.tv_payDay:
                    setNumberPickerDialog("payDay", 1, 26, 1, fg);
                    break;
                case R.id.btn_cancel:
                    ((Main_Activity) getActivity()).resetTimer();
                    ((Main_Activity) getActivity()).startTimer();
                    dismiss();
                    break;
            }
        } catch (ParseException e) {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public int getTagOnlyInt(String tag) {
        String reTag = tag.replaceAll("[^0-9]", "");
        return Integer.parseInt(reTag);
    }

    public void blankAlert(String alert) {
        new AlertDialog.Builder(getActivity())
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

