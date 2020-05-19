package com.project.realproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.kakao.adfit.ads.ba.BannerAdView;
import com.project.realproject.R;
import com.project.realproject.adapters.ContentViewPagerAdapter;
import com.project.realproject.fragments.SalaryCalculatorFragment;
import com.project.realproject.fragments.SavingsCalculatorFragment;

import static java.util.Calendar.getInstance;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


    }

    private void setupTabIcons() {

        View viewFirst = getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView txtFirst = viewFirst.findViewById(R.id.txt_tab);
        txtFirst.setText("월급계산기");
        tabLayout.getTabAt(0).setCustomView(viewFirst);

        View viewSecond = getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView txtSecond = viewSecond.findViewById(R.id.txt_tab);
        txtSecond.setText("적금계산기");
        tabLayout.getTabAt(1).setCustomView(viewSecond);

    }


    private void setupViewPager(ViewPager viewPager) {
        ContentViewPagerAdapter adapter = new ContentViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SalaryCalculatorFragment(), "First");
        adapter.addFragment(new SavingsCalculatorFragment(), "Second");

        viewPager.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {

    }


    /*
    @Override
    public void onResume() {
        super.onResume();

        // lifecycle 사용이 불가능한 경우
        if (mAdView == null) return;
        mAdView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        // lifecycle 사용이 불가능한 경우
        if (mAdView == null) return;
        mAdView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // lifecycle 사용이 불가능한 경우
        if (mAdView == null) return;
        mAdView.destroy();
    }

     */
}
