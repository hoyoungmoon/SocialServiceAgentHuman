package com.project.realproject.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.project.realproject.R;
import com.project.realproject.User;
import com.project.realproject.Vacation;
import com.project.realproject.activities.MainActivity;
import com.project.realproject.helpers.DBHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static com.project.realproject.helpers.DBHelper.TABLE_USER;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static com.project.realproject.helpers.Formatter.*;

public class SettingUserInfoFragment extends DialogFragment implements View.OnClickListener,
        NumberPickerFragment.NumberPickerSaveListener {

    private EditText nickNameEditText;
    private TextView firstDateTextView;
    private TextView lastDateTextView;
    private TextView mealCostTextView;
    private TextView trafficCostTextView;
    private TextView totalFirstVacTextView;
    private TextView totalSecondVacTextView;
    private TextView totalSickVacTextView;
    private TextView promotionDateTextView;
    private TextView payDayTextView;
    private TextView payDayExampleTextView;
    private Button saveButton;
    private ImageButton cancelButton;

    private int mealCost;
    private int trafficCost;
    private int totalFirstVac;
    private int totalSecondVac;
    private int totalSickVac;
    private int payDay;

    private Calendar calendar;
    private DBHelper DBmanager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SettingUserInfoFragment() {
    }

    public static SettingUserInfoFragment newInstance() {
        return new SettingUserInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        DBmanager = DBHelper.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_user_info, container, false);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        nickNameEditText = view.findViewById(R.id.et_nickName);
        nickNameEditText.setInputType(TYPE_CLASS_TEXT);
        firstDateTextView = view.findViewById(R.id.tv_firstDate);
        lastDateTextView = view.findViewById(R.id.tv_lastDate);
        mealCostTextView = view.findViewById(R.id.tv_mealCost);
        trafficCostTextView = view.findViewById(R.id.tv_trafficCost);
        totalFirstVacTextView = view.findViewById(R.id.tv_totalFirstVac);
        totalSecondVacTextView = view.findViewById(R.id.tv_totalSecondVac);
        totalSickVacTextView = view.findViewById(R.id.tv_totalSickVac);
        promotionDateTextView = view.findViewById(R.id.tv_promotionDate);
        payDayTextView = view.findViewById(R.id.tv_payDay);
        payDayExampleTextView = view.findViewById(R.id.tv_payDay_example);
        
        saveButton = view.findViewById(R.id.btn_save);
        cancelButton = view.findViewById(R.id.btn_cancel);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        if (DBmanager.getDataCount(TABLE_USER) != 0) {
            Cursor c = DBmanager.query(userColumns_ver_2, DBHelper.TABLE_USER,
                    null, null, null, null, null);
            c.moveToFirst();

            mealCost = c.getInt(4);
            trafficCost = c.getInt(5);
            totalFirstVac = c.getInt(6);
            totalSecondVac = c.getInt(7);
            totalSickVac = c.getInt(8);
            payDay = c.getInt(9);

            nickNameEditText.setText(c.getString(1));
            firstDateTextView.setText(c.getString(2));
            lastDateTextView.setText(c.getString(3));
        } else {
            mealCost = 6000;
            trafficCost = 2700;
            totalFirstVac = 15;
            totalSecondVac = 13;
            totalSickVac = 30;
            payDay = 1;

            cancelButton.setVisibility(View.GONE);
            calendar.add(YEAR, 1);
            calendar.add(MONTH, 9);
            firstDateTextView.setText(formatter.format(Calendar.getInstance().getTime()));
            lastDateTextView.setText(formatter.format(calendar.getTime()));
        }

        setSalaryPeriodExample(payDay);
        firstDateTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().equals("")){
                    try {
                        calendar.setTime(formatter.parse(charSequence.toString()));
                        calendar.add(YEAR, 1);
                        calendar.add(MONTH, 9);
                        lastDateTextView.setText(formatter.format(calendar.getTime()));
                        editor.putBoolean("isPromotionDateChanged", false);
                        editor.apply();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mealCostTextView.setText(mealCost + " 원");
        trafficCostTextView.setText(trafficCost + " 원");
        totalFirstVacTextView.setText(totalFirstVac + " 일");
        totalSecondVacTextView.setText(totalSecondVac + " 일");
        totalSickVacTextView.setText(totalSickVac + " 일");
        payDayTextView.setText(payDay + " 일");

        nickNameEditText.setOnClickListener(this);
        firstDateTextView.setOnClickListener(this);
        lastDateTextView.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        mealCostTextView.setOnClickListener(this);
        trafficCostTextView.setOnClickListener(this);
        totalFirstVacTextView.setOnClickListener(this);
        totalSecondVacTextView.setOnClickListener(this);
        totalSickVacTextView.setOnClickListener(this);
        promotionDateTextView.setOnClickListener(this);
        payDayTextView.setOnClickListener(this);

        return view;
    }

    private DatePickerDialog setDatePickerDialog(final TextView dateTextView, String currentDate) {
        final Calendar newCalendar = (Calendar)calendar.clone();
        try {
            newCalendar.setTime(formatter.parse(currentDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        newCalendar.set(year, monthOfYear, dayOfMonth);
                        dateTextView.setText(formatter.format(newCalendar.getTime()));
                    }
                }, newCalendar.get(YEAR),
                newCalendar.get(MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
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
            case "totalFirstVac":
                totalFirstVacTextView.setText(saveValue + " 일");
                break;
            case "totalSecondVac":
                totalSecondVacTextView.setText(saveValue + " 일");
                break;
            case "totalSickVac":
                totalSickVacTextView.setText(saveValue + " 일");
                break;
            case "payDay":
                payDayTextView.setText(saveValue + " 일");
                setSalaryPeriodExample(Integer.parseInt(saveValue));
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
                    setDatePickerDialog(firstDateTextView, firstDateTextView.getText().toString()).show();
                    break;
                case R.id.tv_lastDate:
                    setDatePickerDialog(lastDateTextView, lastDateTextView.getText().toString()).show();
                    break;
                case R.id.tv_promotionDate:
                    calendar.setTime(formatter.parse(firstDateTextView.getText().toString()));
                    FragmentManager fragmentManager = getChildFragmentManager();
                    SettingPromotionFragment infoFragment1 = SettingPromotionFragment.newInstance(calendar.getTimeInMillis());
                    infoFragment1.show(fragmentManager, "dialog");
                    break;
                case R.id.btn_save:

                    if ((formatter.parse(lastDateTextView.getText().toString()).getTime() <=
                            formatter.parse(firstDateTextView.getText().toString()).getTime())) {
                        blankAlert("소집해제일을 다시 설정해주세요");
                    } else {
                        User user = new User(nickNameEditText.getText().toString(),
                                firstDateTextView.getText().toString(),
                                lastDateTextView.getText().toString(),
                                getTagOnlyInt(mealCostTextView.getText().toString()),
                                getTagOnlyInt(trafficCostTextView.getText().toString()),
                                getTagOnlyInt(totalFirstVacTextView.getText().toString()),
                                getTagOnlyInt(totalSecondVacTextView.getText().toString()),
                                getTagOnlyInt(totalSickVacTextView.getText().toString()),
                                getTagOnlyInt(payDayTextView.getText().toString()));

                        if (DBmanager.getDataCount(TABLE_USER) == 0) {
                            DBmanager.insertUser(user);
                        } else {
                            Cursor c = DBmanager.query(userColumns_ver_2, DBHelper.TABLE_USER,
                                    null, null, null, null, null);
                            c.moveToFirst();
                            int id = c.getInt(0);
                            DBmanager.updateUser(id, user);
                        }

                        if (DBmanager.getDataCount(DBHelper.TABLE_USER) != 0) {
                            ((MainActivity) getActivity()).initialLoad();
                        }
                        dismiss();
                    }
                    break;
                case R.id.tv_mealCost:
                    NumberPickerFragment.newInstance(this, "mealCost", mealCost,
                            0, 10000, 500).show(fg, "dialog");
                    break;
                case R.id.tv_trafficCost:
                    NumberPickerFragment.newInstance(this, "trafficCost", trafficCost,
                            0, 10000, 50).show(fg, "dialog");
                    break;
                case R.id.tv_totalFirstVac:
                    NumberPickerFragment.newInstance(this, "totalFirstVac", totalFirstVac,
                            0, 30, 1).show(fg, "dialog");
                    break;
                case R.id.tv_totalSecondVac:
                    NumberPickerFragment.newInstance(this, "totalSecondVac", totalSecondVac,
                            0, 30, 1).show(fg, "dialog");
                    break;
                case R.id.tv_totalSickVac:
                    NumberPickerFragment.newInstance(this, "totalSickVac", totalSickVac,
                            0, 50, 1).show(fg, "dialog");
                    break;
                case R.id.tv_payDay:
                    NumberPickerFragment.newInstance(this, "payDay", payDay,
                            1, 26, 1).show(fg, "dialog");
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

    private int getTagOnlyInt(String tag) {
        String reTag = tag.replaceAll("[^0-9]", "");
        return Integer.parseInt(reTag);
    }

    private void setSalaryPeriodExample(int payDay){
        calendar.set(DATE, payDay);
        Date start = calendar.getTime();
        calendar.add(MONTH, 1);
        calendar.add(DATE, -1);
        Date end = calendar.getTime();

        payDayExampleTextView.setText("계산 예시  " + dateFormat_dot.format(start) + " ~ " + dateFormat_dot.format(end));
    }

    private void blankAlert(String alert) {
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

