package com.project.realproject.fragments;


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
import com.project.realproject.R;
import com.project.realproject.User;
import com.project.realproject.activities.MainActivity;
import com.project.realproject.helpers.DBHelper;

import java.text.ParseException;
import java.util.Calendar;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static com.project.realproject.helpers.DBHelper.TABLE_USER;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static com.project.realproject.helpers.Formatter.*;

public class SettingUserInfoFragment extends DialogFragment implements View.OnClickListener, NumberPickerFragment.NumberPickerSaveListener {

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

    private int mealCost;
    private int trafficCost;
    private int totalFirstVac;
    private int totalSecondVac;
    private int totalSickVac;
    private int payDay;

    private static String[] columns = new String[]{"id", "nickName", "firstDate", "lastDate",
            "mealCost", "trafficCost", "totalFirstVac", "totalSecondVac", "totalSickVac", "payDay"};

    DatePickerDialog firstDatePickerDialog;
    DatePickerDialog lastDatePickerDialog;
    Calendar dateCalendar;
    DBHelper DBmanager;

    public SettingUserInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        DBmanager = new DBHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_frag_date_revise, container, false);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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


        if (DBmanager.getDataCount(TABLE_USER) != 0) {
            Cursor c = DBmanager.query(columns, DBHelper.TABLE_USER, null, null, null, null, null);
            c.moveToFirst();

            mealCost = c.getInt(4);
            trafficCost = c.getInt(5);
            totalFirstVac = c.getInt(6);
            totalSecondVac = c.getInt(7);
            totalSickVac = c.getInt(8);
            payDay = c.getInt(9);

            nickNameEditText.setText(c.getString(1));
            firstDateEditText.setText(c.getString(2));
            lastDateEditText.setText(c.getString(3));
        } else {
            mealCost = 6000;
            trafficCost = 2700;
            totalFirstVac = 15;
            totalSecondVac = 15;
            totalSickVac = 30;
            payDay = 1;

            cancelButton.setVisibility(View.GONE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(YEAR, 1);
            calendar.add(MONTH, 9);
            firstDateEditText.setText(formatter.format(Calendar.getInstance().getTime()));
            lastDateEditText.setText(formatter.format(calendar.getTime()));
        }

        mealCostEditText.setText(mealCost + " 원");
        trafficCostEditText.setText(trafficCost + " 원");
        totalFirstVacEditText.setText(totalFirstVac + " 일");
        totalSecondVacEditText.setText(totalSecondVac + " 일");
        totalSickVacEditText.setText(totalSickVac + " 일");
        payDayEditText.setText("매월 " + payDay + " 일");

        nickNameEditText.setOnClickListener(this);
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

    public void setNumberPickerDialog(String userInfo, int setValue, int minValue, int maxValue, int step, FragmentManager fg) {
        NumberPickerFragment dialog = new NumberPickerFragment(this);
        Bundle bundle = new Bundle(5);
        // 이미 세팅되어있던 값 넣기
        // 원래 세팅 되어있는 값(string)을 int로 받아서 index화 시켜야할듯
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
                mealCostEditText.setText(saveValue + " 원");
                break;
            case "trafficCost":
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
                payDayEditText.setText("매월 " + saveValue + " 일");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        FragmentManager fg = getFragmentManager();
        try {
            switch (view.getId()) {
                case R.id.et_nickName:
                    nickNameEditText.setSelection(nickNameEditText.length());
                    break;
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
                            Cursor c = DBmanager.query(columns, DBHelper.TABLE_USER, null, null, null, null, null);
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

                        if (DBmanager.getDataCount(DBHelper.TABLE_USER) != 0) {
                            ((MainActivity) getActivity()).load();
                        }
                        dismiss();
                    }
                    break;
                case R.id.tv_mealCost:
                    setNumberPickerDialog("mealCost", mealCost, 0, 10000, 500, fg);
                    break;
                case R.id.tv_trafficCost:
                    setNumberPickerDialog("trafficCost", trafficCost, 0, 5000, 100, fg);
                    break;
                case R.id.tv_totalFirstVac:
                    setNumberPickerDialog("totalFirstVac", totalFirstVac, 0, 30, 1, fg);
                    break;
                case R.id.tv_totalSecondVac:
                    setNumberPickerDialog("totalSecondVac", totalSecondVac, 0, 30, 1, fg);
                    break;
                case R.id.tv_totalSickVac:
                    setNumberPickerDialog("totalSickVac", totalSickVac, 0, 50, 1, fg);
                    break;
                case R.id.tv_payDay:
                    setNumberPickerDialog("payDay", payDay, 1, 26, 1, fg);
                    break;
                case R.id.btn_cancel:
                    ((MainActivity) getActivity()).resetTimer();
                    ((MainActivity) getActivity()).startTimer();
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

