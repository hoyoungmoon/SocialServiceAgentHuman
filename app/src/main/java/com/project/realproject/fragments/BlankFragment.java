package com.project.realproject.fragments;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.realproject.R;
import com.project.realproject.activities.MainActivity;

import java.util.Calendar;

import kotlin.jvm.internal.MagicApiIntrinsics;

public class BlankFragment extends Fragment implements DialogInterface.OnDismissListener {


    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        //((MainActivity) getActivity()).setRemainVac();
        //((MainActivity) getActivity()).setMonthlyInfo(Calendar.getInstance().getTime());
    }
}
