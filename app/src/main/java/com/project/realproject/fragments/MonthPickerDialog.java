package com.project.realproject.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import com.project.realproject.R;
import com.project.realproject.User;

import java.util.Calendar;
import java.util.Date;

public class MonthPickerDialog extends DialogFragment {
    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 2010;
    private int setYear;
    private int setMonth;
    private String userInfo;
    private NumberPickerSaveListener numberPickerSaveListener;

    public MonthPickerDialog(){
    }

    public MonthPickerDialog(NumberPickerSaveListener saveListener, String userInfo, int setYear, int setMonth){
        this.numberPickerSaveListener = saveListener;
        this.userInfo = userInfo;
        this.setYear = setYear;
        this.setMonth = setMonth;
    }

    public interface NumberPickerSaveListener{
        void onSaveBtnClick(String userInfo, int year, int month);
    }

    Button saveButton;
    Button cancelButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_month_picker, null);

        saveButton = view.findViewById(R.id.btn_save);
        cancelButton = view.findViewById(R.id.btn_cancel);

        final NumberPicker monthPicker = view.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = view.findViewById(R.id.picker_year);

        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberPickerSaveListener != null) {
                    numberPickerSaveListener.onSaveBtnClick(userInfo, yearPicker.getValue(), monthPicker.getValue());
                }
                dismiss();
            }
        });

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(setMonth);

        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(setYear);

        builder.setView(view);

        return builder.create();
    }
}
