package com.example.realproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Frag2_save extends DialogFragment implements View.OnClickListener {

    private EditText startDateEditText;
    private EditText vacationEditText;
    private Button saveButton;
    private Button cancelButton;
    private RadioGroup vacationTypeRadioGroup;
    private RadioGroup sickVacationTypeRadioGroup;
    private NumberPicker outingLengthPicker;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;

    private String limitStartDate;
    private String limitLastDate;
    private int numberOfYear;

    private FirstVacation firstVacation = null;
    public vacationDBManager DBManger = null;

    public Frag2_save() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBManger = vacationDBManager.getInstance(getActivity());
        if(getArguments() != null){
            limitStartDate = getArguments().getString("limitStartDate");
            limitLastDate = getArguments().getString("limitLastDate");
            numberOfYear = getArguments().getInt("numOfYear");
        }
        firstVacation = new FirstVacation(); // 이거 onCreate 때 넣어야하나?
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag2_save, container, false);

        startDateEditText = view.findViewById(R.id.et_startDate);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        vacationEditText = view.findViewById(R.id.et_vacation);

        saveButton = view.findViewById(R.id.button_save);
        cancelButton = view.findViewById(R.id.button_cancel);

        vacationTypeRadioGroup = view.findViewById(R.id.radioGroup_vacationType);
        sickVacationTypeRadioGroup = view.findViewById(R.id.radioGroup_sickVacationType);
        vacationTypeRadioGroup.check(R.id.radio_allDay);
        sickVacationTypeRadioGroup.check(R.id.radio_sickVac_allDay);
        final LinearLayout outingSetter = view.findViewById(R.id.linear_outingSetter);

        if(numberOfYear ==3){
            vacationTypeRadioGroup.setVisibility(GONE);
            sickVacationTypeRadioGroup.setVisibility(VISIBLE);
        }
        else{
            vacationTypeRadioGroup.setVisibility(VISIBLE);
            sickVacationTypeRadioGroup.setVisibility(GONE);
        }

        vacationTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radio_outing){
                    outingSetter.setVisibility(VISIBLE);
                }
                else{
                    outingSetter.setVisibility(GONE);
                }
            }
        });
        sickVacationTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radio_sickVac_outing){
                    outingSetter.setVisibility(VISIBLE);
                }
                else{
                    outingSetter.setVisibility(GONE);
                }
            }
        });

       outingLengthPicker = view.findViewById(R.id.picker_outingLength);
        String minute[] = new String[48];
        for(int i = 0; i <= 47; i++){
            minute[i] = ((i + 1) * 10) + "";
        }
        outingLengthPicker.setMinValue(0);
        outingLengthPicker.setMaxValue(47);
        outingLengthPicker.setDisplayedValues(minute);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        startDateEditText.setOnClickListener(this);
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
        try {
            datePickerDialog.getDatePicker().setMinDate(formatter.parse(limitStartDate).getTime());
            datePickerDialog.getDatePicker().setMaxDate(formatter.parse(limitLastDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == startDateEditText) {
            datePickerDialog.show();
        }
        else if(view == saveButton){
            firstVacation = new FirstVacation();
            String getDate = startDateEditText.getText().toString();

            if(getDate.getBytes().length <= 0){
               blankAlert("시작일을 입력해주세요");
            }

            if(numberOfYear == 3){
                firstVacation.setType("병가");
                // 병가 종류에 따라 count 넣기
                int radioButtonId = sickVacationTypeRadioGroup.getCheckedRadioButtonId();
                int idx = sickVacationTypeRadioGroup.indexOfChild(sickVacationTypeRadioGroup.findViewById(radioButtonId));
                switch (idx) {
                    case 0:
                        firstVacation.setCount(480);
                        break;
                    case 1:
                        firstVacation.setCount(240);
                        break;
                    case 2:
                        int index = outingLengthPicker.getValue();
                        firstVacation.setCount(Double.parseDouble(outingLengthPicker.getDisplayedValues()[index]));
                        break;
                }
            }
            else {
                int radioButtonId = vacationTypeRadioGroup.getCheckedRadioButtonId();
                int idx = vacationTypeRadioGroup.indexOfChild(vacationTypeRadioGroup.findViewById(radioButtonId));
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
                        int index = outingLengthPicker.getValue();
                        firstVacation.setCount(Double.parseDouble(outingLengthPicker.getDisplayedValues()[index]));
                        break;
                }
            }

            firstVacation.setVacation(vacationEditText.getText().toString().trim());
                if (dateCalendar != null) {
                    firstVacation.setStartDate(dateCalendar.getTime());
                }

                saveFirstVacation(firstVacation);

                Toast.makeText(getActivity(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                ((Main_Activity)getActivity()).setRemainVac();

                if(numberOfYear == 1) {
                    ((Main_Activity) getActivity()).refreshListView(R.id.fragment_container_1,
                            R.id.first_vacation_image, limitStartDate, limitLastDate, 1);
                }
                else if(numberOfYear == 2){
                    ((Main_Activity) getActivity()).refreshListView(R.id.fragment_container_2,
                            R.id.first_vacation_image, limitStartDate, limitLastDate, 2);
                }
                else {
                    ((Main_Activity) getActivity()).refreshListView(R.id.fragment_container_3,
                            R.id.sick_vacation_image, limitStartDate, limitLastDate, 3);
                }
                dismiss();

        }
        else if(view == cancelButton){
            dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (dateCalendar != null)
            outState.putLong("dateCalendar", dateCalendar.getTime().getTime());
    }

    public void saveFirstVacation(FirstVacation firstVacation) {
        DBManger.insertFirstVacation(firstVacation);

    }

    public void blankAlert(String alert){
        new AlertDialog.Builder(getActivity())
                .setMessage(alert)
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static Frag2_save newInstance(String param1, String param2, int param3){
        Frag2_save dialog = new Frag2_save();
        Bundle bundle = new Bundle(3);
        bundle.putString("limitStartDate", param1);
        bundle.putString("limitLastDate", param2);
        bundle.putInt("numOfYear", param3);
        dialog.setArguments(bundle);
        return dialog;
    }
}


