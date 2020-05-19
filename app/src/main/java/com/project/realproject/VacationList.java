package com.project.realproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.project.realproject.helpers.DBHelper;

import static com.project.realproject.helpers.Formatter.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public abstract class VacationList implements Parcelable {

    public static final int CLASS_TYPE_FIRST_VACATION = 1;
    public static final int CLASS_TYPE_SECOND_VACATION = 2;
    public static final int CLASS_TYPE_SICK_VACATION = 3;
    public static final int CLASS_TYPE_MONTHLY_VACATION = 4;

    private Context context;
    private DBHelper dbHelper;
    private String startDate;
    private String lastDate;
    private ArrayList<Vacation> vacations;

    public VacationList(){}

    public VacationList(Context context, String startDate, String lastDate) {
        this.context = context;
        this.startDate = startDate;
        this.lastDate = lastDate;
        this.vacations = initializeVacationList(context);
    }

    public void refreshVacationList(){
        vacations = initializeVacationList(context);
    }

    private ArrayList<Vacation> initializeVacationList(Context context) {
        try {
            dbHelper = new DBHelper(context);
            ArrayList<Vacation> initialVacationList = new ArrayList<>();
            Cursor c = dbHelper.query(vacationColumns, DBHelper.TABLE_VACATION,
                    null, null, null, null, null);
            c.moveToFirst();
            Date vacationDate;
            do {
                if (c.getCount() == 0) break;

                vacationDate = formatter.parse(c.getString(2));
                if (formatter.parse(startDate).compareTo(vacationDate) <= 0
                        && formatter.parse(lastDate).compareTo(vacationDate) >= 0) {

                    if (isSatisfied(c.getString(3))) {
                        initialVacationList.add(new Vacation(c.getInt(0), c.getString(1),
                                formatter.parse(c.getString(2)), c.getString(3), c.getDouble(4)));
                    }
                }
            } while (c.moveToNext());
            c.close();
            return initialVacationList;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract boolean isSatisfied(String type);

    // 특별휴가, 청원, 공가는 빼고 카운트 (사용한 연가일수 계산위해)
    public double getTotalCount(){
        double total = 0;
        for(Vacation vacation : vacations){
            if(!(vacation.getVacationType() instanceof SpecialVacation)) {
                total += vacation.getCount();
            }
        }
        return total;
    }

    public String getStartDate() {
        return startDate;
    }

    public Date getStartDateTime(){
        try {
            return formatter.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public Date getLastDateTime(){
        try {
            return formatter.parse(lastDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public ArrayList<Vacation> getVacations() {
        return vacations;
    }

    public void setVacations(ArrayList<Vacation> vacations) {
        this.vacations = vacations;
    }

    protected VacationList(Parcel in) {
        startDate = in.readString();
        lastDate = in.readString();
        vacations = in.createTypedArrayList(Vacation.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startDate);
        dest.writeString(lastDate);
        dest.writeTypedList(vacations);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VacationList> CREATOR = new Creator<VacationList>() {
        @Override
        public VacationList createFromParcel(Parcel in) {
            return getConcreteClass(in);
        }

        @Override
        public VacationList[] newArray(int size) {
            return new VacationList[size];
        }
    };

    public static VacationList getConcreteClass(Parcel source) {

        switch (source.readInt()) {
            case CLASS_TYPE_FIRST_VACATION:
                return new FirstYearVacationList(source);
            case CLASS_TYPE_SECOND_VACATION:
                return new SecondYearVacationList(source);
            case CLASS_TYPE_SICK_VACATION:
                return new SickVacationList(source);
            case CLASS_TYPE_MONTHLY_VACATION:
                return new SpecificPeriodVacationList(source);
            default:
                return null;
        }
    }
}
