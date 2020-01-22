package com.project.realproject.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import com.project.realproject.Vacation;
import com.project.realproject.R;
import com.project.realproject.activities.MainActivity;
import com.project.realproject.activities.MainActivity.vacType;
import com.project.realproject.DBHelper;

public class VacReviseFragment extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private EditText startDateEditText;
    private EditText vacationEditText;
    private TextView outingLengthTextView;
    private Button saveButton;
    private Button cancelButton;
    private ImageButton plusOutingButton;
    private ImageButton minusOutingButton;
    private RadioGroup vacationTypeRadioGroup;
    private RadioGroup sickVacationTypeRadioGroup;
    private Spinner specialVacationTypeSpinner;
    private RelativeLayout vacationTypeRelative;
    private LinearLayout outingSetter;
    private AdView mAdView;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;

    private String limitStartDate;
    private String limitLastDate;
    private String firstDate;
    private String lastDate;
    private vacType typeOfVac;
    private int outingLength = 10;
    private Vacation vacation;
    private int id;
    private String searchStartDate;
    public DBHelper DBmanager = null;

    public VacReviseFragment() {
    }

    public static VacReviseFragment newInstance(String param1, String param2, String param3, String param4,
                                                vacType param5, Vacation param6, int param7, String param8) {
        VacReviseFragment dialog = new VacReviseFragment();
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle bundle = new Bundle(8);
        bundle.putString("limitStartDate", param1);
        bundle.putString("limitLastDate", param2);
        bundle.putString("firstDate", param3);
        bundle.putString("lastDate", param4);
        bundle.putSerializable("typeOfVac", param5);
        bundle.putParcelable("vacation", param6);
        bundle.putInt("id", param7);
        bundle.putString("searchStartDate", param8);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBmanager = DBHelper.getInstance(getActivity());
        if (getArguments() != null) {
            limitStartDate = getArguments().getString("limitStartDate");
            limitLastDate = getArguments().getString("limitLastDate");
            firstDate = getArguments().getString("firstDate");
            lastDate = getArguments().getString("lastDate");
            typeOfVac = (vacType) getArguments().getSerializable("typeOfVac");
            vacation = getArguments().getParcelable("vacation");
            id = getArguments().getInt("id");
            searchStartDate = getArguments().getString("searchStartDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag2_save, container, false);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        startDateEditText = view.findViewById(R.id.et_startDate);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        vacationEditText = view.findViewById(R.id.et_vacation);
        outingLengthTextView = view.findViewById(R.id.et_outingLength);

        saveButton = view.findViewById(R.id.button_save);
        cancelButton = view.findViewById(R.id.button_cancel);
        plusOutingButton = view.findViewById(R.id.button_outing_plus);
        minusOutingButton = view.findViewById(R.id.button_outing_minus);

        vacationTypeRadioGroup = view.findViewById(R.id.radioGroup_vacationType);
        sickVacationTypeRadioGroup = view.findViewById(R.id.radioGroup_sickVacationType);
        vacationTypeRelative = view.findViewById(R.id.relative_vacation);
        specialVacationTypeSpinner = view.findViewById(R.id.radio_special_spinner);
        outingSetter = view.findViewById(R.id.linear_outingSetter);

        if (typeOfVac == vacType.sickVac) {
            vacationTypeRelative.setVisibility(GONE);
            sickVacationTypeRadioGroup.setVisibility(VISIBLE);
        } else {
            vacationTypeRelative.setVisibility(VISIBLE);
            sickVacationTypeRadioGroup.setVisibility(GONE);
        }

        vacationTypeRadioGroup.setOnCheckedChangeListener(this);
        sickVacationTypeRadioGroup.setOnCheckedChangeListener(this);


        // 수정 전 data 미리 세팅
        LinearLayout vacationSetter = view.findViewById(R.id.linear_vacationSetter);
        vacationSetter.setVisibility(GONE);
        vacationTypeRadioGroup.check(R.id.radio_allDay);
        sickVacationTypeRadioGroup.check(R.id.radio_sickVac_allDay);
        vacationEditText.setText(vacation.getVacation());
        startDateEditText.setText(formatter.format(vacation.getStartDate()));

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        plusOutingButton.setOnClickListener(this);
        minusOutingButton.setOnClickListener(this);
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
            // VacSaveFragment 와 달리 수정은 모든 구간(복무일 ~ 복무해제일)에서 선택가능하도록 하기 위해
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
        } else if (view == saveButton) {
            int idx;
            if (typeOfVac == vacType.sickVac) {
                int radioButtonId = sickVacationTypeRadioGroup.getCheckedRadioButtonId();
                idx = sickVacationTypeRadioGroup.indexOfChild(sickVacationTypeRadioGroup.findViewById(radioButtonId));
                switch (idx) {
                    case 0:
                        vacation.setType("병가");
                        vacation.setCount(480);
                        break;
                    case 1:
                        vacation.setType("오전지참");
                        vacation.setCount(240);
                        break;
                    case 2:
                        vacation.setType("오후조퇴");
                        vacation.setCount(240);
                        break;
                    case 3:
                        vacation.setType("병가외출");
                        vacation.setCount(Double.parseDouble(getOnlyNumber(outingLengthTextView.getText().toString())));
                        break;
                }
            } else {
                int radioButtonId = vacationTypeRadioGroup.getCheckedRadioButtonId();
                idx = vacationTypeRadioGroup.indexOfChild(vacationTypeRadioGroup.findViewById(radioButtonId));
                switch (idx) {
                    case 0:
                        vacation.setType("연가");
                        vacation.setCount(480);
                        break;
                    case 1:
                        vacation.setType("오전반가");
                        vacation.setCount(240);
                        break;
                    case 2:
                        vacation.setType("오후반가");
                        vacation.setCount(240);
                        break;
                    case 3:
                        vacation.setType("외출");
                        vacation.setCount(Double.parseDouble(getOnlyNumber(outingLengthTextView.getText().toString())));
                        break;
                    case 4:
                        vacation.setType(specialVacationTypeSpinner.getSelectedItem().toString());
                        vacation.setCount(480);
                        break;
                }
            }

            String getDate = startDateEditText.getText().toString().trim();
            if (getDate.equals("")) {
                blankAlert();
            } else if (idx == 4) {
                specialVacationAlert();
            } else {
                saveVacation();
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

    private void saveVacation() {
        vacation.setVacation(vacationEditText.getText().toString().trim());
        if (dateCalendar != null) {
            vacation.setStartDate(dateCalendar.getTime());
        }

        revise(vacation);
        ((MainActivity) getActivity()).setRemainVac();
        ((MainActivity) getActivity()).setThisMonthInfo(searchStartDate);
        if (typeOfVac == vacType.firstYearVac) {
            ((MainActivity) getActivity()).refreshListView(limitStartDate, limitLastDate,
                    MainActivity.vacType.firstYearVac);
        } else if (typeOfVac == vacType.secondYearVac) {
            ((MainActivity) getActivity()).refreshListView(limitStartDate, limitLastDate,
                    MainActivity.vacType.secondYearVac);
        } else {
            ((MainActivity) getActivity()).refreshListView(limitStartDate, limitLastDate,
                    MainActivity.vacType.sickVac);
        }
        dismiss();
    }

    private void revise(Vacation vacation) {
        ContentValues values = new ContentValues();
        values.put("vacation", vacation.getVacation());
        values.put("startDate", formatter.format(vacation.getStartDate()));
        values.put("type", vacation.getType());
        values.put("count", vacation.getCount());
        DBmanager.updateFirstVacation(id, values);
    }

    private String getOnlyNumber(String string) {
        return string.replaceAll("[^0-9]", "");
    }

    private void blankAlert() {
        new AlertDialog.Builder(getActivity())
                .setMessage("시작일을 입력해주세요")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void specialVacationAlert() {
        new AlertDialog.Builder(getActivity())
                .setMessage("기타(특별휴가, 청원휴가, 공가)는 연가에서 차감되지 않습니다")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveVacation();
                    }
                })
                .show();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup == vacationTypeRadioGroup) {
            if (i == R.id.radio_outing) {
                outingSetter.setVisibility(VISIBLE);
            } else {
                outingSetter.setVisibility(GONE);
            }
        } else if (radioGroup == sickVacationTypeRadioGroup) {
            if (i == R.id.radio_sickVac_outing) {
                outingSetter.setVisibility(VISIBLE);
            } else {
                outingSetter.setVisibility(GONE);
            }
        }
    }

}
