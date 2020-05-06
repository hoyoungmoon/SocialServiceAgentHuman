package com.project.realproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
    private BannerAdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        /*
        mAdView = findViewById(R.id.banner_ad);  // 배너 광고 뷰
        mAdView.setClientId("DAN-rl2aq9y534va");  // 할당 받은 광고 단위(clientId) 설정
        mAdView.setAdListener(new AdListener() {  // 광고 수신 리스너 설정

            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailed(int errorCode) {
            }

            @Override
            public void onAdClicked() {
            }
        });

        mAdView.loadAd();

         */

        //getSupportActionBar().setDisplayShowTitleEnabled(false);

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
