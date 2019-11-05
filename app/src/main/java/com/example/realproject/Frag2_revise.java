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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Frag2_revise extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private EditText startDateEditText;
    private EditText vacationEditText;
    private Button saveButton;
    private Button cancelButton;
    private RadioGroup vacationTypeRadioGroup;
    private RadioGroup sickVacationTypeRadioGroup;
    private NumberPicker outingLengthPicker;
    private LinearLayout outingSetter;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);


    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;

    String limitStartDate;
    String limitLastDate;
    String firstDate;
    String lastDate;
    int numOfYear;
    FirstVacation firstVacation;
    int id;
    public vacationDBManager DBmanager = null;

    public Frag2_revise() {
    }

    public static Frag2_revise newInstance(String param1, String param2, String param3, String param4, int param5, FirstVacation param6, int param7){
        Frag2_revise dialog = new Frag2_revise();
        Bundle bundle = new Bundle(7);
        bundle.putString("limitStartDate", param1);
        bundle.putString("limitLastDate", param2);
        bundle.putString("firstDate", param3);
        bundle.putString("lastDate", param4);
        bundle.putInt("numOfYear", param5);
        bundle.putParcelable("firstVacation", param6);
        bundle.putInt("id", param7);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBmanager = vacationDBManager.getInstance(getActivity());
        if(getArguments() != null){
            limitStartDate = getArguments().getString("limitStartDate");
            limitLastDate = getArguments().getString("limitLastDate");
            firstDate = getArguments().getString("firstDate");
            lastDate = getArguments().getString("lastDate");
            numOfYear = getArguments().getInt("numOfYear");
            firstVacation = getArguments().getParcelable("firstVacation");
            id = getArguments().getInt("id");
        }
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
        outingSetter = view.findViewById(R.id.linear_outingSetter);

        if(numOfYear ==3){
            vacationTypeRadioGroup.setVisibility(GONE);
            sickVacationTypeRadioGroup.setVisibility(VISIBLE);
        }
        else{
            vacationTypeRadioGroup.setVisibility(VISIBLE);
            sickVacationTypeRadioGroup.setVisibility(GONE);
        }

        vacationTypeRadioGroup.setOnCheckedChangeListener(this);
        sickVacationTypeRadioGroup.setOnCheckedChangeListener(this);

        outingLengthPicker = view.findViewById(R.id.picker_outingLength);
        String minute[] = new String[48];
        for (int i = 0; i <= 47; i++) {
            minute[i] = ((i + 1) * 10) + "";
        }
        outingLengthPicker.setMinValue(0);
        outingLengthPicker.setMaxValue(47);
        outingLengthPicker.setDisplayedValues(minute);

        // 수정 전 data 미리 세팅
        vacationTypeRadioGroup.check(R.id.radio_allDay);
        sickVacationTypeRadioGroup.check(R.id.radio_sickVac_allDay);
        vacationEditText.setText(firstVacation.getVacation());
        startDateEditText.setText(formatter.format(firstVacation.getStartDate()));

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
            datePickerDialog.getDatePicker().setMinDate(formatter.parse(firstDate).getTime());
            datePickerDialog.getDatePicker().setMaxDate(formatter.parse(lastDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == startDateEditText) {
            datePickerDialog.show();
        } else if (view == saveButton) {
            String getDate = startDateEditText.getText().toString();

            if (getDate.getBytes().length <= 0) {
                blankAlert("시작일을 입력해주세요");
            }
            if(numOfYear == 3){
                int radioButtonId = sickVacationTypeRadioGroup.getCheckedRadioButtonId();
                int idx = sickVacationTypeRadioGroup.indexOfChild(sickVacationTypeRadioGroup.findViewById(radioButtonId));
                switch (idx) {
                    case 0:
                        firstVacation.setType("병가");
                        firstVacation.setCount(480);
                        break;
                    case 1:
                        firstVacation.setType("오전지참");
                        firstVacation.setCount(240);
                        break;
                    case 2:
                        firstVacation.setType("오후조퇴");
                        firstVacation.setCount(240);
                        break;
                    case 3:
                        firstVacation.setType("병가외출");
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

                revise(firstVacation);
                ((Main_Activity)getActivity()).setRemainVac();
                ((Main_Activity)getActivity()).setThisMonthInfo();
                if(numOfYear == 1) {
                    ((Main_Activity) getActivity()).refreshListView(R.id.fragment_container_1,
                            R.id.first_vacation_image, limitStartDate, limitLastDate, numOfYear);
                }
                else if(numOfYear == 2){
                    ((Main_Activity) getActivity()).refreshListView(R.id.fragment_container_2,
                            R.id.second_vacation_image, limitStartDate, limitLastDate, numOfYear);
                }
                else {
                    ((Main_Activity) getActivity()).refreshListView(R.id.fragment_container_3,
                            R.id.sick_vacation_image, limitStartDate, limitLastDate, 3);
                }
                Toast.makeText(getActivity(), "수정되었습니다", Toast.LENGTH_SHORT).show();
                dismiss();

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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(radioGroup == vacationTypeRadioGroup) {
            if (i == R.id.radio_outing) {
                outingSetter.setVisibility(VISIBLE);
            } else {
                outingSetter.setVisibility(GONE);
            }
        }
        else if(radioGroup == sickVacationTypeRadioGroup){
            if(i == R.id.radio_sickVac_outing){
                outingSetter.setVisibility(VISIBLE);
            }
            else{
                outingSetter.setVisibility(GONE);
            }
        }
    }
}
