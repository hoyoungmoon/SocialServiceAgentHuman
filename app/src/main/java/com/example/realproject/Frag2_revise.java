package com.example.realproject;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Frag2_revise extends DialogFragment implements View.OnClickListener {

    private EditText startDateEditText;
    private EditText vacationEditText;
    private EditText outingStartEditText;
    private Button saveButton;
    private Button cancelButton;
    private RadioGroup vacationTypeRadioGroup;
    private NumberPicker outingLengthPicker;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);


    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Calendar dateCalendar;

    FirstVacation firstVacation = null;
    public vacationDBManager DBmanager = null;
    int id;

    public Frag2_revise(FirstVacation firstVacation, int id) {
        this.firstVacation = firstVacation;
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBmanager = vacationDBManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag2_save, container, false);


        startDateEditText = view.findViewById(R.id.et_startDate);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        vacationEditText = view.findViewById(R.id.et_vacation);

        outingStartEditText = view.findViewById(R.id.et_outingStart);
        outingStartEditText.setInputType(InputType.TYPE_NULL);

        saveButton = view.findViewById(R.id.button_save);
        cancelButton = view.findViewById(R.id.button_cancel);

        vacationTypeRadioGroup = view.findViewById(R.id.radioGroup_vacationType);
        final LinearLayout outingSetter = view.findViewById(R.id.linear_outingSetter);

        vacationTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_outing) {
                    outingSetter.setVisibility(VISIBLE);
                } else {
                    outingSetter.setVisibility(GONE);
                }
            }
        });

        outingLengthPicker = view.findViewById(R.id.picker_outingLength);
        String minute[] = new String[48];
        for (int i = 0; i <= 47; i++) {
            minute[i] = ((i + 1) * 10) + "";
        }
        outingLengthPicker.setMinValue(0);
        outingLengthPicker.setMaxValue(47);
        outingLengthPicker.setDisplayedValues(minute);

        // 수정 전 data 미리 세팅
        vacationEditText.setText(firstVacation.getVacation());
        startDateEditText.setText(formatter.format(firstVacation.getStartDate()));


        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        startDateEditText.setOnClickListener(this);
        outingStartEditText.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(),
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

        timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int h = hourOfDay;
                        int m = minute;
                        outingStartEditText.setText(h + "시 " + m + "분");
                    }
                }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == startDateEditText) {
            datePickerDialog.show();
        } else if (view == outingStartEditText) {
            timePickerDialog.show();
        } else if (view == saveButton) {
            String getEdit = vacationEditText.getText().toString();
            String getDate = startDateEditText.getText().toString();

            if (getDate.getBytes().length <= 0) {
                blankAlert("시작일을 입력해주세요");
            } else if (getEdit.getBytes().length <= 0) {
                blankAlert("휴가사유를 입력해주세요");
            } else {
                int radioButtonId = vacationTypeRadioGroup.getCheckedRadioButtonId();
                int idx = vacationTypeRadioGroup.indexOfChild(vacationTypeRadioGroup.findViewById(radioButtonId));
                switch (idx) {
                    case 0:
                        firstVacation.setType("연가");
                        break;
                    case 1:
                        firstVacation.setType("오전반가");
                        break;
                    case 2:
                        firstVacation.setType("오후반가");
                        break;
                    case 3:
                        firstVacation.setType("외출");
                        break;
                }
                firstVacation.setVacation(vacationEditText.getText().toString().trim());

                if (radioButtonId == R.id.radio_allDay) {
                    firstVacation.setCount(480);  // 8시간 * 60(분/시간) = 480분
                } else if (radioButtonId == R.id.radio_halfAfternoon || radioButtonId == R.id.radio_halfMorning) {
                    firstVacation.setCount(240); // 4시간 * 60(분/시간) = 240분
                } else {
                    int index = outingLengthPicker.getValue();
                    firstVacation.setCount(Double.parseDouble(outingLengthPicker.getDisplayedValues()[index]));
                }

                if (dateCalendar != null) {
                    firstVacation.setStartDate(dateCalendar.getTime());
                }
                revise(firstVacation);
                ((Main_Activity)getActivity()).setRemainVac();



                Toast.makeText(getActivity(), "수정되었습니다", Toast.LENGTH_LONG).show();
                dismiss();
            }
        } else if (view == cancelButton) {
            dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (dateCalendar != null)
            outState.putLong("dateCalendar", dateCalendar.getTime().getTime());
    }

    public void revise(FirstVacation firstVacation) {
        ContentValues values = new ContentValues();
        values.put("vacation", firstVacation.getVacation());
        values.put("startDate", formatter.format(firstVacation.getStartDate()));
        values.put("type", firstVacation.getType());
        values.put("count", firstVacation.getCount());
        DBmanager.updateFirstVacation(id, values);
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
