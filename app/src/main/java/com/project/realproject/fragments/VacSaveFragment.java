package com.project.realproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import android.widget.Toast;

import com.kakao.adfit.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.ParseException;
import java.util.Calendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.Calendar.DATE;
import static com.project.realproject.helpers.Formatter.*;

import com.kakao.adfit.ads.ba.BannerAdView;
import com.project.realproject.FirstYearVacationList;
import com.project.realproject.R;
import com.project.realproject.SecondYearVacationList;
import com.project.realproject.SickVacationList;
import com.project.realproject.Vacation;
import com.project.realproject.VacationList;
import com.project.realproject.activities.MainActivity;
import com.project.realproject.activities.MainActivity.vacType;
import com.project.realproject.helpers.DBHelper;

public class VacSaveFragment extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView startDateEditText;
    private EditText vacationEditText;
    private TextView outingLengthTextView;
    private TextView vacationLengthTextView;
    private Button saveButton;
    private ImageButton cancelButton;
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

    private int vacationLength = 1;
    private int outingLength = 10;
    private VacationList vacationList;

    private Vacation vacation = null;
    public DBHelper DBmanager = null;

    public VacSaveFragment() {
    }

    public static VacSaveFragment newInstance(VacationList vacationList) {
        VacSaveFragment dialog = new VacSaveFragment();
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("vacationList", vacationList);
        dialog.setArguments(bundle);
        return dialog;
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

        window.setLayout((int) (size.x * 0.85), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBmanager = DBHelper.getInstance(getActivity());
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        if (getArguments() != null) {
            vacationList = getArguments().getParcelable("vacationList");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vac_save, container, false);


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

        if (vacationList instanceof SickVacationList) {
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

        long min = vacationList.getStartDateTime().getTime();
        long max = vacationList.getLastDateTime().getTime();
        if (max - min >= 0) {
            datePickerDialog.getDatePicker().setMinDate(min);
            datePickerDialog.getDatePicker().setMaxDate(max);
        } else {
            // 더 좋은 범위 설정 있는지 생각해보기 (지금으로써는 1년 이하로 복무기간을 설정했을때
            // 2년차 연가를 쓸 필요는 없지만 그래도 max(소집해제일) 설정만 해두었음.
            datePickerDialog.getDatePicker().setMaxDate(max);
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
            vacation = new Vacation();
            if (vacationList instanceof SickVacationList) {
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
            if (getDate.equals("시작일")) {
                blankAlert();
            } else if (idx == 4) {
                specialVacationAlert(idx);
            } else {
                saveVacation(idx);
            }
        } else if (view == cancelButton) {
            dismiss();
        }
    }

    private void saveVacation(int idx) {
        final boolean isFullVac = idx == 0;
        final boolean isSpecialVac = idx == 4;
        final boolean isVacationLengthLong = vacationLength != 1;

        if (dateCalendar != null) {
            vacation.setStartDate(dateCalendar.getTime());
        }

        if ((isFullVac || isSpecialVac) && isVacationLengthLong) {
            dateCalendar.add(DATE, -1);
            for (int i = 1; i <= vacationLength; i++) {
                vacation.setVacation(vacationEditText.getText().toString().trim() + " (" + i + "/" + vacationLength + ")");
                dateCalendar.add(DATE, 1);
                vacation.setStartDate(dateCalendar.getTime());
                saveFirstVacation(vacation);
            }
        } else {
            vacation.setVacation(vacationEditText.getText().toString().trim());
            saveFirstVacation(vacation);
        }

        dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (dateCalendar != null)
            outState.putLong("dateCalendar", dateCalendar.getTime().getTime());
    }

    public void saveFirstVacation(Vacation vacation) {
        DBmanager.insertVacation(vacation);
    }

    public String getOnlyNumber(String string) {
        return string.replaceAll("[^0-9]", "");
    }

    public void blankAlert() {
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

    private void specialVacationAlert(final int idx) {
        new AlertDialog.Builder(getActivity())
                .setMessage("기타(특별휴가, 청원휴가, 공가)는 연가에서 차감되지 않습니다")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveVacation(idx);
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

}
