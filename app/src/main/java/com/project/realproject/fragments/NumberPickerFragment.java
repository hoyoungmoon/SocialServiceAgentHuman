package com.project.realproject.fragments;


import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.project.realproject.R;

public class NumberPickerFragment extends DialogFragment implements View.OnClickListener {

    private Button cancelButton;
    private Button saveButton;
    private NumberPicker numberPicker;

    private int setValue;
    private int setIndex;
    private int minValue;
    private int maxValue;
    private int step = 1;
    private String userInfo;
    private int number_of_array;
    private String[] result;

    public NumberPickerFragment() {
        // Required empty public constructor
    }

    public static NumberPickerFragment newInstance(NumberPickerSaveListener saveListener,
                                                   String userInfo, int setValue, int minValue,
                                                   int maxValue, int step){
        NumberPickerFragment numberPickerFragment = new NumberPickerFragment(saveListener);
        Bundle bundle = new Bundle(5);
        bundle.putString("userInfo", userInfo);
        bundle.putInt("setValue", setValue);
        bundle.putInt("minValue", minValue);
        bundle.putInt("maxValue", maxValue);
        bundle.putInt("step", step);
        numberPickerFragment.setArguments(bundle);
        return numberPickerFragment;
    }


    // interface
    public interface NumberPickerSaveListener{
        void onSaveBtnClick(String userInfo, String saveValue);
    }

    private NumberPickerSaveListener numberPickerSaveListener;

    private NumberPickerFragment(NumberPickerSaveListener saveListener){
        this.numberPickerSaveListener = saveListener;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments() != null){
            userInfo = getArguments().getString("userInfo");
            setValue = getArguments().getInt("setValue");
            minValue = getArguments().getInt("minValue");
            maxValue = getArguments().getInt("maxValue");
            step = getArguments().getInt("step");
        }
        View view = inflater.inflate(R.layout.dialog_number_picker, container, false);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        saveButton = view.findViewById(R.id.numberPicker_save);
        cancelButton = view.findViewById(R.id.numberPicker_cancel);
        numberPicker = view.findViewById(R.id.numberPicker);

        number_of_array = ((maxValue - minValue) / step) + 1;
        result = new String[number_of_array];
        for (int i = 0; i < number_of_array; i++) {
            result[i] = String.valueOf(minValue + step * i);
        }
        setIndex = (setValue - minValue) / step;

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue((maxValue - minValue) / step);
        numberPicker.setDisplayedValues(result);
        numberPicker.setValue(setIndex);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.numberPicker_save:
                if (numberPickerSaveListener != null) {
                    numberPickerSaveListener.onSaveBtnClick(userInfo, result[numberPicker.getValue()]);
                }
                dismiss();
                break;

            case R.id.numberPicker_cancel:
                dismiss();
                break;
        }
    }
}
