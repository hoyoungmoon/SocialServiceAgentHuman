package com.project.realproject;

import android.content.Context;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.project.realproject.helpers.Formatter.listOfSickVac;

public class SpecificPeriodVacationList extends VacationList {

    private ArrayList<Vacation> vacations;

    public SpecificPeriodVacationList(Context context, String startDate, String lastDate) {
        super(context, startDate, lastDate);
        this.vacations = getVacations();
    }

    @Override
    public boolean isSatisfied(String type) {
        return true;
    }

    public SpecificPeriodVacationList(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(CLASS_TYPE_MONTHLY_VACATION);
        super.writeToParcel(dest, flags);
    }

    public double getSickVacCount(){
        double total = 0;
        for(Vacation vacation : vacations){
            if(Arrays.asList(new String[]{"병가", "오전지참", "오후조퇴", "병가외출"}).contains(vacation.getType())) {
                total += vacation.getCount();
            }
        }
        return total;
    }

    public double getGeneralVacCount(){
        double total = 0;
        for(Vacation vacation : vacations){
            if(Arrays.asList(new String[]{"연가", "오전반가", "오후반가", "공가", "청원휴가", "특별휴가"}).contains(vacation.getType())) {
                total += vacation.getCount();
            }
        }
        return total;
    }

    public double getOutingVacCount(){
        double total = 0;
        for(Vacation vacation : vacations){
            if(Arrays.asList(new String[]{"외출"}).contains(vacation.getType())) {
                total += vacation.getCount();
            }
        }
        return total;
    }
}
