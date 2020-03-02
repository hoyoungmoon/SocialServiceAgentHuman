package com.project.realproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.project.realproject.R;
import com.xw.repo.BubbleSeekBar;

import java.util.Calendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static com.project.realproject.helpers.Formatter.*;

public class SettingExtraActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch percentSwitch;
    private Switch bootCampSwitch;
    private BubbleSeekBar bubbleSeekBar;
    private TextView decimalTextView;
    private TextView bootCampStartTextView;
    private TextView bootCampEndTextView;
    private LinearLayout bootCampDateLinear;
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
        decimalTextView.setText("소숫점 " + preferences.getInt("decimalPlaces", 7) + "번째");

        bubbleSeekBar.setProgress(preferences.getInt("decimalPlaces", 7));
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                decimalTextView.setText("소숫점 " + progress + "번째");
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

        if(preferences.getBoolean("bootCampInclude", false)){
            bootCampDateLinear.setVisibility(VISIBLE);
        }else{
            bootCampDateLinear.setVisibility(GONE);
        }
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
        bootCampStartTextView.setOnClickListener(this);
        bootCampEndTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.textView_bootCampStart:
                setDatePickerDialog(bootCampStartTextView, "bootCampStart").show();
                break;

            case R.id.textView_bootCampEnd:
                setDatePickerDialog(bootCampEndTextView, "bootCampEnd").show();
                break;
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

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "변경된 설정이 적용되려면 설정창 상단의 뒤로가기 버튼을 클릭해주세요", Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }

}
