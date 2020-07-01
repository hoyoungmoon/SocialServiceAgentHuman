package com.project.realproject.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.realproject.R;
import com.project.realproject.User;
import com.project.realproject.helpers.DBHelper;

import java.text.ParseException;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import static com.project.realproject.helpers.Formatter.*;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingPromotionFragment extends DialogFragment implements View.OnClickListener, MonthPickerDialog.NumberPickerSaveListener {

    private TextView firstPromotionTextView;
    private TextView secondPromotionTextView;
    private TextView thirdPromotionTextView;
    private Button saveButton;
    private ImageButton cancelButton;

    private Calendar calendar = Calendar.getInstance();
    private Date startDate;
    private long startDateTime;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private User user;
    private Context mContext;

    private int firstPromotionPeriod;
    private int secondPromotionPeriod;
    private int thirdPromotionPeriod;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public SettingPromotionFragment(){

    }

    public SettingPromotionFragment(String startDate) {
        try {
            this.startDate = formatter.parse(startDate);
        } catch (ParseException e) {
            this.startDate = Calendar.getInstance().getTime();
            e.printStackTrace();
        }
    }

    public static SettingPromotionFragment newInstance(long startDateTime) {
        SettingPromotionFragment fragment = new SettingPromotionFragment();
        Bundle args = new Bundle();
        args.putLong("startDateTime", startDateTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_promotion, container, false);

        user = new User(mContext);
        if (getArguments() != null) {
            startDateTime = getArguments().getLong("startDateTime");
        }
        calendar.setTime(new Date(startDateTime));
        startDate = calendar.getTime();

        firstPromotionTextView = view.findViewById(R.id.tv_firstPromotion);
        secondPromotionTextView = view.findViewById(R.id.tv_secondPromotion);
        thirdPromotionTextView = view.findViewById(R.id.tv_thirdPromotion);
        saveButton = view.findViewById(R.id.btn_save);
        cancelButton = view.findViewById(R.id.btn_cancel);

        try {
            firstPromotionPeriod = user.getMonthsOfService(startDate,
                    dateFormat_dot.parse(firstPromotionTextView.getText().toString()));
            secondPromotionPeriod = user.getMonthsOfService(startDate,
                    dateFormat_dot.parse(secondPromotionTextView.getText().toString()));
            thirdPromotionPeriod = user.getMonthsOfService(startDate,
                    dateFormat_dot.parse(thirdPromotionTextView.getText().toString()));
        } catch (ParseException e) {
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        calendar.setTime(startDate);
        calendar.set(DATE, 1);
        firstPromotionTextView.setText(dateFormat_dot.format(user.getPromotionDate(startDate,"second")));
        secondPromotionTextView.setText(dateFormat_dot.format(user.getPromotionDate(startDate,"third")));
        thirdPromotionTextView.setText(dateFormat_dot.format(user.getPromotionDate(startDate,"fourth")));

        firstPromotionTextView.setOnClickListener(this);
        secondPromotionTextView.setOnClickListener(this);
        thirdPromotionTextView.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onSaveBtnClick(String userInfo, int year, int month) {
        switch (userInfo) {
            // 값이 바뀐것이 확인되면 isPromotionChanged ture로 바꾸자
            case "firstPromotion":
                firstPromotionTextView.setText(year + "." + month + ".01");
                break;
            case "secondPromotion":
                secondPromotionTextView.setText(year + "." + month + ".01");
                break;
            case "thirdPromotion":
                thirdPromotionTextView.setText(year + "." + month + ".01");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_firstPromotion:
                getMonthPickerDialog(firstPromotionTextView, "firstPromotion");
                break;

            case R.id.tv_secondPromotion:
                getMonthPickerDialog(secondPromotionTextView, "secondPromotion");
                break;

            case R.id.tv_thirdPromotion:
                getMonthPickerDialog(thirdPromotionTextView, "thirdPromotion");
                break;

            case R.id.btn_cancel:
                dismiss();
                break;

            case R.id.btn_save:
                try {
                    int firstPromotionPeriod = user.getMonthsOfService(startDate,
                            dateFormat_dot.parse(firstPromotionTextView.getText().toString()));
                    int secondPromotionPeriod = user.getMonthsOfService(startDate,
                            dateFormat_dot.parse(secondPromotionTextView.getText().toString()));
                    int thirdPromotionPeriod = user.getMonthsOfService(startDate,
                            dateFormat_dot.parse(thirdPromotionTextView.getText().toString()));
                    if (!(firstPromotionPeriod <= secondPromotionPeriod && secondPromotionPeriod <= thirdPromotionPeriod)){
                        blankAlert(getActivity(), "진급월 순서를 다시 설정해주세요");
                        break;
                    }
                    if (firstPromotionPeriod != this.firstPromotionPeriod ||
                            secondPromotionPeriod != this.secondPromotionPeriod ||
                            thirdPromotionPeriod != this.thirdPromotionPeriod) {
                        editor.putBoolean("isPromotionDateChanged", true);
                        editor.putInt("firstPromotion", firstPromotionPeriod);
                        editor.putInt("secondPromotion", secondPromotionPeriod);
                        editor.putInt("thirdPromotion", thirdPromotionPeriod);
                        editor.apply();
                        dismiss();
                    } else {
                        dismiss();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void modifyPromotionDate(){

    }

    private void getMonthPickerDialog(TextView textView, String tag){
        String[] date = textView.getText().toString().split("\\.");
        MonthPickerDialog monthPickerDialog =
                new MonthPickerDialog(this, tag, Integer.parseInt(date[0]), Integer.parseInt(date[1]));
        monthPickerDialog.show(getFragmentManager(), tag);
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
        //window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        Display display = window.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

}
