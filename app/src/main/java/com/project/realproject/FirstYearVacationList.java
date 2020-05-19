package com.project.realproject;

import android.content.Context;
import android.os.Parcel;

import java.util.Arrays;
import java.util.Date;

public class FirstYearVacationList extends VacationList {

    private static final String[] satisfyingVacationType =
            new String[]{"연가", "오전반가", "오후반가", "외출", "특별휴가", "청원휴가", "공가"};

    public FirstYearVacationList(){}

    public FirstYearVacationList(Context context, String startDate, String lastDate) {
        super(context, startDate, lastDate);
    }

    public FirstYearVacationList(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(CLASS_TYPE_FIRST_VACATION);
        super.writeToParcel(dest, flags);
    }

    @Override
    public boolean isSatisfied(String type) {
        return Arrays.asList(satisfyingVacationType).contains(type);
    }
}
