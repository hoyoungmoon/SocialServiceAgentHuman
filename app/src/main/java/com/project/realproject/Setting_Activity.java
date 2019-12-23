package com.project.realproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.xw.repo.BubbleSeekBar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Setting_Activity extends AppCompatActivity {

    private Switch percentSwitch;
    private BubbleSeekBar bubbleSeekBar;
    private TextView decimalTextView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        //getSupportActionBar().setDisplayShowTitleEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        percentSwitch = findViewById(R.id.switch_percent);
        bubbleSeekBar = findViewById(R.id.bubbleSeekBar_percent);
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
                    editor.putBoolean("percentIsChange", true);
                    editor.apply();
                } else {
                    editor.putBoolean("percentIsChange", false);
                    editor.apply();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "변경된 설정이 적용되려면 설정창 상단의 뒤로가기 버튼을 클릭해주세요", Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }
}
