package com.project.realproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;

import java.util.Arrays;
import java.util.Date;

public class SickVacationList extends VacationList {

    private static final String[] satisfyingVacationType =
            new String[]{"병가", "오전지참", "오후조퇴", "병가외출"};

    public SickVacationList(){}

    public SickVacationList(Context context, String startDate, String lastDate) {
        super(context, startDate, lastDate);
    }

    public SickVacationList(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(CLASS_TYPE_SICK_VACATION);
        super.writeToParcel(dest, flags);
    }

    @Override
    public boolean isSatisfied(String type) {
        return Arrays.asList(satisfyingVacationType).contains(type);
    }
}
