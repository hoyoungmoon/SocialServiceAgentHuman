package com.project.realproject.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.project.realproject.R;
import com.project.realproject.Savings;

import static com.project.realproject.helpers.Formatter.*;

public class SavingsCalculatorFragment extends Fragment {
    private EditText savingAmountEditText;
    private EditText savingPeriodEditText;
    private EditText interestRateEditText;
    private TextView totalSavingsTextView;
    private TextView principalSumTextView;
    private TextView totalInterestTextView;
    private AdView mAdView;




    public SavingsCalculatorFragment() {
        // Required empty public constructor
    }

    public static SavingsCalculatorFragment newInstance() {
        SavingsCalculatorFragment fragment = new SavingsCalculatorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_savings_calculator, container, false);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        savingPeriodEditText = view.findViewById(R.id.et_savingPeriod);
        savingAmountEditText = view.findViewById(R.id.et_savingAmount);
        interestRateEditText = view.findViewById(R.id.et_interestRate);
        totalSavingsTextView = view.findViewById(R.id.tv_totalSavings);
        principalSumTextView = view.findViewById(R.id.tv_principalSum);
        totalInterestTextView = view.findViewById(R.id.tv_totalInterest);

        savingPeriodEditText.addTextChangedListener(textWatcher);
        savingAmountEditText.addTextChangedListener(textWatcher);
        interestRateEditText.addTextChangedListener(textWatcher);

        return view;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            long amount, period;
            double interest;

            String amountString = savingAmountEditText.getText().toString();
            String periodString = savingPeriodEditText.getText().toString();
            String interestString = interestRateEditText.getText().toString();

            if(interestString.equals(".")){
                interestString = "";
                interestRateEditText.setText("");
            }

            amount = amountString.equals("") ? 0 : Long.parseLong(amountString);
            period = periodString.equals("") ? 1 : Long.parseLong(periodString);
            interest = interestString.equals("") ? 0 : Double.parseDouble(interestString);

            Savings savings = new Savings(amount, period, interest);
            totalSavingsTextView.setText(decimalFormat.format(savings.calculateTotalSavings()));
            principalSumTextView.setText(decimalFormat.format(savings.calculateTotalPrincipalSum()));
            totalInterestTextView.setText(decimalFormat.format(savings.calculateTotalInterest()));

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
