package com.project.realproject.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.project.realproject.helpers.Formatter.*;

import com.project.realproject.R;
import com.project.realproject.User;
import com.project.realproject.Vacation;
import com.project.realproject.activities.MainActivity;
import com.project.realproject.activities.MainActivity.vacType;
import com.project.realproject.helpers.DBHelper;

public class VacReviseFragment extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private TextView startDateEditText;
    private EditText vacationEditText;
    private TextView outingLengthTextView;
    private Button saveButton;
    private ImageButton cancelButton;
    private ImageButton plusOutingButton;
    private ImageButton minusOutingButton;
    private RadioGroup vacationTypeRadioGroup;
    private RadioGroup sickVacationTypeRadioGroup;
    private Spinner specialVacationTypeSpinner;
    private RelativeLayout vacationTypeRelative;
    private LinearLayout outingSetter;

    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;

    private Date firstDate;
    private Date lastDate;
    private vacType typeOfVac;
    private int outingLength = 10;
    private Vacation vacation;
    private DBHelper DBmanager = null;
    private User user;

    public VacReviseFragment() {
    }

    public static VacReviseFragment newInstance(vacType vacType, Vacation vacation) {
        VacReviseFragment dialog = new VacReviseFragment();
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle bundle = new Bundle(2);
        bundle.putSerializable("typeOfVac", vacType);
        bundle.putParcelable("vacation", vacation);
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
        user = new User(getActivity());
        firstDate = user.getFirstDateTime();
        lastDate = user.getLastDateTime();

        if (getArguments() != null) {
            typeOfVac = (vacType) getArguments().getSerializable("typeOfVac");
            vacation = getArguments().getParcelable("vacation");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vac_save, container, false);


        startDateEditText = view.findViewById(R.id.et_startDate);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        vacationEditText = view.findViewById(R.id.et_vacation);
        outingLengthTextView = view.findViewById(R.id.et_outingLength);

        saveButton = view.findViewById(R.id.button_save);
        cancelButton = view.findViewById(R.id.button_cancel);
        saveButton.setText("수 정");
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
        newCalendar.setTime(vacation.getStartDate());
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

        // VacSaveFragment 와 달리 수정은 모든 구간(복무일 ~ 복무해제일)에서 선택가능하도록 하기 위해
        datePickerDialog.getDatePicker().setMinDate(firstDate.getTime());
        datePickerDialog.getDatePicker().setMaxDate(lastDate.getTime());

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
        dismiss();
    }

    private void revise(Vacation vacation) {
        ContentValues values = new ContentValues();
        values.put("vacation", vacation.getVacation());
        values.put("startDate", formatter.format(vacation.getStartDate()));
        values.put("type", vacation.getType());
        values.put("count", vacation.getCount());
        DBmanager.updateVacation(vacation.getId(), values);
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
