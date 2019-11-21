package com.example.realproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.Calendar.DATE;

import android.os.Bundle;

public class Save_Activity extends AppCompatActivity implements View.OnClickListener {
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    private EditText startDateEditText;
    private EditText vacationEditText;
    private TextView outingLengthTextView;
    private TextView vacationLengthTextView;
    private Button saveButton;
    private Button cancelButton;
    private ImageButton plusOutingButton;
    private ImageButton minusOutingButton;
    private ImageButton plusVacationButton;
    private ImageButton minusVacationButton;
    private RadioGroup vacationTypeRadioGroup;
    private LinearLayout outingSetter;
    private LinearLayout vacationSetter;

    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;

    private String limitStartDate;
    private String limitLastDate;
    private int numberOfYear;
    private int vacationLength = 1;
    private int outingLength = 10;
    private String searchStartDate;

    private FirstVacation firstVacation = null;
    public vacationDBManager DBmanager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_activity);

        DBmanager = vacationDBManager.getInstance(this);

        Intent intent = getIntent();
        limitStartDate = intent.getExtras().getString("limitStartDate");
        limitLastDate = intent.getExtras().getString("limitLastDate");
        searchStartDate = intent.getExtras().getString("searchStartDate");


        startDateEditText = findViewById(R.id.et_startDate);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        outingLengthTextView = findViewById(R.id.et_outingLength);
        vacationLengthTextView = findViewById(R.id.et_vacationLength);
        vacationEditText = findViewById(R.id.et_vacation);
        saveButton = findViewById(R.id.button_save);
        cancelButton = findViewById(R.id.button_cancel);
        plusOutingButton = findViewById(R.id.button_outing_plus);
        minusOutingButton = findViewById(R.id.button_outing_minus);
        plusVacationButton = findViewById(R.id.button_vacation_plus);
        minusVacationButton = findViewById(R.id.button_vacation_minus);

        vacationTypeRadioGroup = findViewById(R.id.radioGroup_vacationType);
        vacationTypeRadioGroup.check(R.id.radio_allDay);
        outingSetter = findViewById(R.id.linear_outingSetter);
        vacationSetter = findViewById(R.id.linear_vacationSetter);


        vacationTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_outing) {
                    vacationSetter.setVisibility(GONE);
                    outingSetter.setVisibility(VISIBLE);
                } else if (i == R.id.radio_allDay) {
                    vacationSetter.setVisibility(VISIBLE);
                    outingSetter.setVisibility(GONE);
                } else {
                    vacationSetter.setVisibility(GONE);
                    outingSetter.setVisibility(GONE);
                }
            }
        });


        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        plusOutingButton.setOnClickListener(this);
        minusOutingButton.setOnClickListener(this);
        plusVacationButton.setOnClickListener(this);
        minusVacationButton.setOnClickListener(this);
        startDateEditText.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year, monthOfYear, dayOfMonth);
                        startDateEditText.setText(formatter.format(dateCalendar
                                .getTime()));
                    }
                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        try {
            datePickerDialog.getDatePicker().setMinDate(formatter.parse(limitStartDate).getTime());
            datePickerDialog.getDatePicker().setMaxDate(formatter.parse(limitLastDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == startDateEditText) {
            datePickerDialog.show();
        } else if (view == plusOutingButton) {
            if (outingLength < 480) {
                outingLength += 10;
                outingLengthTextView.setText(outingLength + "분");
            }
        } else if (view == minusOutingButton) {
            if (outingLength > 10) {
                outingLength -= 10;
                outingLengthTextView.setText(outingLength + "분");
            }
        } else if (view == plusVacationButton) {
            if (vacationLength < 7) {
                vacationLength += 1;
                vacationLengthTextView.setText(vacationLength + "일");
            }
        } else if (view == minusVacationButton) {
            if (vacationLength > 1) {
                vacationLength -= 1;
                vacationLengthTextView.setText(vacationLength + "일");
            }
        } else if (view == saveButton) {
            int idx;
            firstVacation = new FirstVacation();

            int radioButtonId = vacationTypeRadioGroup.getCheckedRadioButtonId();
            idx = vacationTypeRadioGroup.indexOfChild(vacationTypeRadioGroup.findViewById(radioButtonId));
            switch (idx) {
                case 0:
                    firstVacation.setType("연가");
                    firstVacation.setCount(480);
                    break;
                case 1:
                    firstVacation.setType("오전반가");
                    firstVacation.setCount(240);
                    break;
                case 2:
                    firstVacation.setType("오후반가");
                    firstVacation.setCount(240);
                    break;
                case 3:
                    firstVacation.setType("외출");
                    firstVacation.setCount(Double.parseDouble(getOnlyNumber(outingLengthTextView.getText().toString())));
                    break;
            }

            String getDate = startDateEditText.getText().toString().trim();
            if (getDate.equals("")) {
                blankAlert("시작일을 입력해주세요");
            } else {
                if (dateCalendar != null) {
                    firstVacation.setStartDate(dateCalendar.getTime());
                }

                if (vacationLength != 1 && idx == 0) {
                    dateCalendar.add(DATE, -1);
                    for (int i = 1; i <= vacationLength; i++) {
                        firstVacation.setVacation(vacationEditText.getText().toString().trim() + " (" + i + "/" + vacationLength + ")");
                        dateCalendar.add(DATE, 1);
                        firstVacation.setStartDate(dateCalendar.getTime());
                        saveFirstVacation(firstVacation);
                    }
                } else {
                    firstVacation.setVacation(vacationEditText.getText().toString().trim());
                    saveFirstVacation(firstVacation);
                }

                ((Main_Activity) Main_Activity.mContext).setRemainVac();
                ((Main_Activity) Main_Activity.mContext).setThisMonthInfo(searchStartDate);

                finish();
            }
        } else if (view == cancelButton) {
            finish();
        }
    }


    public void saveFirstVacation(FirstVacation firstVacation) {
        DBmanager.insertFirstVacation(firstVacation);
    }

    public String getOnlyNumber(String string) {
        return string.replaceAll("[^0-9]", "");
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
