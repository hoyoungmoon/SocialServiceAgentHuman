package com.project.realproject.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.kakao.adfit.ads.ba.BannerAdView;
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

public class SettingUserInfoFragment extends DialogFragment implements View.OnClickListener,
        NumberPickerFragment.NumberPickerSaveListener {

    private EditText nickNameEditText;
    private TextView firstDateEditText;
    private TextView lastDateEditText;
    private TextView mealCostEditText;
    private TextView trafficCostEditText;
    private TextView totalFirstVacEditText;
    private TextView totalSecondVacEditText;
    private TextView totalSickVacEditText;
    private Button saveButton;
    private ImageButton cancelButton;

    private int mealCost;
    private int trafficCost;
    private int totalFirstVac;
    private int totalSecondVac;
    private int totalSickVac;

    private Calendar calendar;
    private DBHelper DBmanager;

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
        View view = inflater.inflate(R.layout.fragment_setting_user_info, container, false);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        nickNameEditText = view.findViewById(R.id.et_nickName);
        nickNameEditText.setInputType(TYPE_CLASS_TEXT);
        firstDateEditText = view.findViewById(R.id.tv_firstDate);
        lastDateEditText = view.findViewById(R.id.tv_lastDate);
        mealCostEditText = view.findViewById(R.id.tv_mealCost);
        trafficCostEditText = view.findViewById(R.id.tv_trafficCost);
        totalFirstVacEditText = view.findViewById(R.id.tv_totalFirstVac);
        totalSecondVacEditText = view.findViewById(R.id.tv_totalSecondVac);
        totalSickVacEditText = view.findViewById(R.id.tv_totalSickVac);
        saveButton = view.findViewById(R.id.btn_save);
        cancelButton = view.findViewById(R.id.btn_cancel);


        if (DBmanager.getDataCount(TABLE_USER) != 0) {
            Cursor c = DBmanager.query(userColumns, DBHelper.TABLE_USER,
                    null, null, null, null, null);
            c.moveToFirst();

            mealCost = c.getInt(4);
            trafficCost = c.getInt(5);
            totalFirstVac = c.getInt(6);
            totalSecondVac = c.getInt(7);
            totalSickVac = c.getInt(8);

            nickNameEditText.setText(c.getString(1));
            firstDateEditText.setText(c.getString(2));
            lastDateEditText.setText(c.getString(3));
        } else {
            mealCost = 6000;
            trafficCost = 2700;
            totalFirstVac = 15;
            totalSecondVac = 13;
            totalSickVac = 30;

            cancelButton.setVisibility(View.GONE);
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
                    setDatePickerDialog(firstDateEditText, firstDateEditText.getText().toString()).show();
                    break;
                case R.id.tv_lastDate:
                    setDatePickerDialog(lastDateEditText, lastDateEditText.getText().toString()).show();
                    break;
                case R.id.btn_save:

                    if ((formatter.parse(lastDateEditText.getText().toString()).getTime() <=
                            formatter.parse(firstDateEditText.getText().toString()).getTime())) {
                        blankAlert("소집해제일을 다시 설정해주세요");
                    } else {
                        User user = new User(nickNameEditText.getText().toString(),
                                firstDateEditText.getText().toString(),
                                lastDateEditText.getText().toString(),
                                getTagOnlyInt(mealCostEditText.getText().toString()),
                                getTagOnlyInt(trafficCostEditText.getText().toString()),
                                getTagOnlyInt(totalFirstVacEditText.getText().toString()),
                                getTagOnlyInt(totalSecondVacEditText.getText().toString()),
                                getTagOnlyInt(totalSickVacEditText.getText().toString()));

                        if (DBmanager.getDataCount(TABLE_USER) == 0) {
                            DBmanager.insertUser(user);
                        } else {
                            Cursor c = DBmanager.query(userColumns, DBHelper.TABLE_USER,
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
                            0, 5000, 50).show(fg, "dialog");
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

