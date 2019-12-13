package com.project.realproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.Calendar.DATE;

public class Frag2_save extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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
    private RadioGroup sickVacationTypeRadioGroup;
    private Spinner specialVacationTypeSpinner;
    private RelativeLayout vacationTypeRelative;
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

    public Frag2_save() {
    }

    public static Frag2_save newInstance(String param1, String param2, int param3, String param4) {
        Frag2_save dialog = new Frag2_save();
        Bundle bundle = new Bundle(4);
        bundle.putString("limitStartDate", param1);
        bundle.putString("limitLastDate", param2);
        bundle.putInt("numOfYear", param3);
        bundle.putString("searchStartDate", param4);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBmanager = vacationDBManager.getInstance(getActivity());
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        if (getArguments() != null) {
            limitStartDate = getArguments().getString("limitStartDate");
            limitLastDate = getArguments().getString("limitLastDate");
            numberOfYear = getArguments().getInt("numOfYear");
            searchStartDate = getArguments().getString("searchStartDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag2_save, container, false);

        startDateEditText = view.findViewById(R.id.et_startDate);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        outingLengthTextView = view.findViewById(R.id.et_outingLength);
        vacationLengthTextView = view.findViewById(R.id.et_vacationLength);
        vacationEditText = view.findViewById(R.id.et_vacation);
        saveButton = view.findViewById(R.id.button_save);
        cancelButton = view.findViewById(R.id.button_cancel);
        plusOutingButton = view.findViewById(R.id.button_outing_plus);
        minusOutingButton = view.findViewById(R.id.button_outing_minus);
        plusVacationButton = view.findViewById(R.id.button_vacation_plus);
        minusVacationButton = view.findViewById(R.id.button_vacation_minus);

        vacationTypeRadioGroup = view.findViewById(R.id.radioGroup_vacationType);
        sickVacationTypeRadioGroup = view.findViewById(R.id.radioGroup_sickVacationType);
        vacationTypeRadioGroup.check(R.id.radio_allDay);
        sickVacationTypeRadioGroup.check(R.id.radio_sickVac_allDay);
        specialVacationTypeSpinner = view.findViewById(R.id.radio_special_spinner);
        vacationTypeRelative = view.findViewById(R.id.relative_vacation);
        outingSetter = view.findViewById(R.id.linear_outingSetter);
        vacationSetter = view.findViewById(R.id.linear_vacationSetter);

        if (numberOfYear == 3) {
            vacationTypeRelative.setVisibility(GONE);
            sickVacationTypeRadioGroup.setVisibility(VISIBLE);
        } else {
            vacationTypeRelative.setVisibility(VISIBLE);
            sickVacationTypeRadioGroup.setVisibility(GONE);
        }

        vacationTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_outing) {
                    vacationSetter.setVisibility(GONE);
                    outingSetter.setVisibility(VISIBLE);
                } else if (i == R.id.radio_allDay || i == R.id.radio_special) {
                    vacationSetter.setVisibility(VISIBLE);
                    outingSetter.setVisibility(GONE);
                } else {
                    vacationSetter.setVisibility(GONE);
                    outingSetter.setVisibility(GONE);
                }
            }
        });
        sickVacationTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_sickVac_outing) {
                    vacationSetter.setVisibility(GONE);
                    outingSetter.setVisibility(VISIBLE);
                } else if (i == R.id.radio_sickVac_allDay) {
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
            long min = formatter.parse(limitStartDate).getTime();
            long max = formatter.parse(limitLastDate).getTime();
            if (min < max) {
                datePickerDialog.getDatePicker().setMinDate(min);
                datePickerDialog.getDatePicker().setMaxDate(max);
            } else {
                // 더 좋은 범위 설정 있는지 생각해보기 (지금으로써는 1년 이하로 복무기간을 설정했을때
                // 2년차 연가를 쓸 필요는 없지만 그래도 max(소집해제일) 설정만 해두었음.
                datePickerDialog.getDatePicker().setMaxDate(max);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
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

            if (numberOfYear == 3) {
                int radioButtonId = sickVacationTypeRadioGroup.getCheckedRadioButtonId();
                idx = sickVacationTypeRadioGroup.indexOfChild(sickVacationTypeRadioGroup.findViewById(radioButtonId));
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
                        firstVacation.setCount(Double.parseDouble(getOnlyNumber(outingLengthTextView.getText().toString())));
                        break;
                }
            } else {
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
                    // 새로 추가한것 오류 확인 !!
                    case 4:
                        firstVacation.setType(specialVacationTypeSpinner.getSelectedItem().toString());
                        firstVacation.setCount(480);
                        break;
                }
            }

            String getDate = startDateEditText.getText().toString().trim();
            if (getDate.equals("")) {
                blankAlert("시작일을 입력해주세요");
            } else {
                if (dateCalendar != null) {
                    firstVacation.setStartDate(dateCalendar.getTime());
                }

                // 병가 저장이 안댐 해결해겨랭ㄹㄴ럼ㅇ
                if ((idx == 0 || idx == 4) && vacationLength != 1) {
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

                ((Main_Activity) getActivity()).setRemainVac();
                ((Main_Activity) getActivity()).setThisMonthInfo(searchStartDate);

                if (numberOfYear == 1) {
                    ((Main_Activity) getActivity()).refreshListView(limitStartDate, limitLastDate,
                            1, "list1");
                } else if (numberOfYear == 2) {
                    ((Main_Activity) getActivity()).refreshListView(limitStartDate, limitLastDate,
                            2, "list2");
                } else {
                    ((Main_Activity) getActivity()).refreshListView(limitStartDate, limitLastDate,
                            3, "list3");
                }
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

    public void saveFirstVacation(FirstVacation firstVacation) {
        DBmanager.insertFirstVacation(firstVacation);

    }

    public String getOnlyNumber(String string) {
        return string.replaceAll("[^0-9]", "");
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
