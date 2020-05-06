package com.project.realproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.project.realproject.R;
import com.project.realproject.fragments.MonthPickerDialog;
import com.project.realproject.fragments.NumberPickerFragment;
import com.xw.repo.BubbleSeekBar;

import java.util.Calendar;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static com.project.realproject.helpers.Formatter.*;

public class SettingExtraActivity extends AppCompatActivity implements View.OnClickListener, NumberPickerFragment.NumberPickerSaveListener{

    private Switch percentSwitch;
    private Switch bootCampSwitch;
    private Switch savingsSwitch;

    private BubbleSeekBar bubbleSeekBar;
    private TextView decimalTextView;
    private TextView bootCampStartTextView;
    private TextView bootCampEndTextView;
    private TextView savingsStartMonthTextView;
    private TextView savingsEndMonthTextView;
    private TextView savingsPeriodTextView;
    private EditText savingsInterestEditText;
    private EditText savingsPaymentEditText;

    private LinearLayout interestRateLinear;
    private LinearLayout bootCampDateLinear;
    private LinearLayout savingsLinear;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        percentSwitch = findViewById(R.id.switch_percent);
        bootCampSwitch = findViewById(R.id.switch_bootCamp);
        bubbleSeekBar = findViewById(R.id.bubbleSeekBar_percent);
        bootCampStartTextView = findViewById(R.id.textView_bootCampStart);
        bootCampEndTextView = findViewById(R.id.textView_bootCampEnd);
        bootCampDateLinear = findViewById(R.id.linear_bootCampDate);
        decimalTextView = findViewById(R.id.textView_decimal);

        interestRateLinear = findViewById(R.id.linear_interestRate);
        savingsLinear = findViewById(R.id.linear_savings);
        savingsSwitch = findViewById(R.id.switch_savings);
        savingsStartMonthTextView = findViewById(R.id.tv_savingsStartMonth);
        savingsEndMonthTextView = findViewById(R.id.tv_savingsEndMonth);
        savingsPeriodTextView = findViewById(R.id.tv_savingsPeriod);
        savingsPaymentEditText = findViewById(R.id.et_savingsPayment);
        savingsInterestEditText = findViewById(R.id.et_interestRate);


        // 복무율 퍼센트 초기설정
        percentSwitch.setChecked(preferences.getBoolean("percentIsChange", true));
        percentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    Toast.makeText(SettingExtraActivity.this, "퍼센트 움직임 사용", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("percentIsChange", true);
                    editor.apply();
                } else {
                    Toast.makeText(SettingExtraActivity.this, "퍼센트 움직임 사용하지 않음", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("percentIsChange", false);
                    editor.apply();
                }
            }
        });

        decimalTextView.setText("소수점 " + preferences.getInt("decimalPlaces", 7) + "번째");
        bubbleSeekBar.setProgress(preferences.getInt("decimalPlaces", 7));
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                decimalTextView.setText("소수점 " + progress + "번째");
                editor.putInt("decimalPlaces", progress);
                editor.apply();
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });


        // 훈련소 관련 초기설정
        bootCampDateLinear.setVisibility(preferences.getBoolean("bootCampInclude", false) ? VISIBLE : GONE);
        bootCampSwitch.setChecked(preferences.getBoolean("bootCampInclude", false));
        bootCampSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    Toast.makeText(SettingExtraActivity.this, "월급계산에 훈련소 포함", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("bootCampInclude", true);
                    editor.apply();
                    bootCampDateLinear.setVisibility(VISIBLE);
                } else {
                    Toast.makeText(SettingExtraActivity.this, "월급계산에 훈련소 포함하지 않음", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("bootCampInclude", false);
                    editor.apply();
                    bootCampDateLinear.setVisibility(GONE);
                }
            }
        });

        bootCampStartTextView.setText(preferences.getString("bootCampStart", "2020-01-01"));
        bootCampEndTextView.setText(preferences.getString("bootCampEnd", "2020-01-29"));


        // 적금 관련 초기 설정
        savingsLinear.setVisibility(preferences.getBoolean("savingsInclude", false) ? VISIBLE : GONE);
        savingsSwitch.setChecked(preferences.getBoolean("savingsInclude", false));
        savingsPeriodTextView.setText(preferences.getInt("savingsPeriod", 21) + " 개월");
        savingsStartMonthTextView.setText(preferences.getInt("savingsYear", 2020) + "년 "
                + preferences.getInt("savingsMonth", 1) + "월");
        savingsInterestEditText.setText(preferences.getFloat("savingsInterestRate", (float)1.5)+"");
        savingsPaymentEditText.setText(preferences.getInt("savingsPayment", 200000)+"");
        setSavingsEndMonth(preferences.getInt("savingsYear", 2020), preferences.getInt("savingsMonth", 1));

        savingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    Toast.makeText(SettingExtraActivity.this, "월별 적금 계산 포함", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("savingsInclude", true);
                    editor.apply();
                    savingsLinear.setVisibility(VISIBLE);
                } else {
                    Toast.makeText(SettingExtraActivity.this, "월별 적금 계산 포함하지 않음", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("savingsInclude", false);
                    editor.apply();
                    savingsLinear.setVisibility(GONE);
                }
            }
        });
        savingsInterestEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.isFocused()) {
                    savingsInterestEditText.setCursorVisible(true);
                    //savingsInterestEditText.setSelection(savingsInterestEditText.length());
                    Log.d("selection", savingsInterestEditText.getText().length() + ", " + savingsInterestEditText.length());
                }
            }
        });

        savingsInterestEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putFloat("savingsInterestRate", Float.parseFloat(charSequence.toString()));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        savingsPaymentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putInt("savingsPayment", Integer.parseInt(charSequence.toString()));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bootCampStartTextView.setOnClickListener(this);
        bootCampEndTextView.setOnClickListener(this);
        savingsPeriodTextView.setOnClickListener(this);
        savingsStartMonthTextView.setOnClickListener(this);
        savingsInterestEditText.setOnClickListener(this);
        savingsPaymentEditText.setOnClickListener(this);
        interestRateLinear.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        FragmentManager fg = getSupportFragmentManager();
        switch(view.getId()){
            case R.id.textView_bootCampStart:
                setDatePickerDialog(bootCampStartTextView, "bootCampStart").show();
                break;

            case R.id.textView_bootCampEnd:
                setDatePickerDialog(bootCampEndTextView, "bootCampEnd").show();
                break;

            case R.id.tv_savingsPeriod:
                NumberPickerFragment.newInstance(this, "savingsPeriod",
                        preferences.getInt("savingsPeriod", 21), 1, 24, 1).show(fg, "dialog");
                break;

            case R.id.tv_savingsStartMonth:
                MonthPickerDialog monthPickerDialog =
                        new MonthPickerDialog(preferences.getInt("savingsYear", 2020), preferences.getInt("savingsMonth", 1));
                monthPickerDialog.setListener(dateSetListener);
                monthPickerDialog.show(getSupportFragmentManager(), "savingsStartMonth");
                break;

//            case R.id.et_interestRate:
//                savingsInterestEditText.requestFocus();
//                savingsInterestEditText.setSelection(savingsInterestEditText.length());
//                break;

//            case R.id.linear_interestRate:
//                savingsInterestEditText.setCursorVisible(true);
//                savingsInterestEditText.setSelection(savingsInterestEditText.length());
//                break;
        }
    }


    public DatePickerDialog setDatePickerDialog(TextView dateTextView, String tag) {
        final TextView someDateTextView = dateTextView;
        final String someTag = tag;
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog returnDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year, monthOfYear, dayOfMonth);
                        someDateTextView.setText(formatter.format(dateCalendar
                                .getTime()));
                        editor.putString(someTag, formatter.format(dateCalendar.getTime()));
                        editor.apply();
                    }
                }, newCalendar.get(YEAR),
                newCalendar.get(MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        return returnDialog;
    }

    private void setSavingsEndMonth(int year, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        calendar.add(MONTH, preferences.getInt("savingsPeriod", 21) - 1);
        savingsEndMonthTextView.setText("만기일자  " + calendar.get(YEAR) + "년 " + (calendar.get(MONTH) + 1) + "월");
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            editor.putInt("savingsYear", year);
            editor.putInt("savingsMonth", monthOfYear);
            editor.apply();
            savingsStartMonthTextView.setText(year + "년 " + monthOfYear + "월");
            setSavingsEndMonth(year, monthOfYear);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveBtnClick(String userInfo, String saveValue) {
        switch (userInfo) {
            case "savingsPeriod":
                editor.putInt("savingsPeriod", Integer.parseInt(saveValue));
                editor.apply();
                savingsPeriodTextView.setText(saveValue + " 개월");
                setSavingsEndMonth(preferences.getInt("savingsYear", 2020), preferences.getInt("savingsMonth", 1));
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    ((EditText) v).setCursorVisible(false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}
