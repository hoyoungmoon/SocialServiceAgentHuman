package com.project.realproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Vacation implements Parcelable  {

    private int id;
    private String type;
    private String vacation;
    private Date startDate;
    private double count;
    private VacationType vacationType;

    public Vacation(){

    }

    public Vacation(int id, String vacation, Date startDate, String type, double count){
        this.id = id;
        this.type = type;
        this.vacation = vacation;
        this.startDate = startDate;
        this.count = count;
        this.vacationType = convertIntoVacationType(type);
    }

    private Vacation(Parcel in){
        this.id = in.readInt();
        this.type = in.readString();
        this.vacation = in.readString();
        this.startDate = new Date(in.readLong());
        this.count = in.readDouble();
    }

    private VacationType convertIntoVacationType(String vacationType){
        if(vacationType.equals("연가") || vacationType.equals("병가")){
            return new AllDayVacation();
        } else if (vacationType.equals("오전반가") || vacationType.equals("오전지참")) {
            return new MorningVacation();
        } else if (vacationType.equals("오후반가") || vacationType.equals("오후조퇴")) {
            return new AfternoonVacation();
        } else if (vacationType.equals("외출") || vacationType.equals("병가외출")) {
            return new OutingVacation();
        } else {
            return new SpecialVacation();
        }

    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getVacation(){
        return vacation;
    }

    public void setVacation(String vacation){
        this.vacation = vacation;
    }

    public double getCount(){
        return count;
    }

    public void setCount(double count){
        this.count = count;
    }

    public Date getStartDate(){
        return startDate;
    }

    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }

    public VacationType getVacationType() {
        return vacationType;
    }

    public void setVacationType(VacationType vacationType) {
        this.vacationType = vacationType;
    }


    @Override
    public String toString() {
        return "Vacation [id=" + id + ", vacation=" + vacation + ", startDate="
                + startDate + ", type=" + type + ", count=" + count + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vacation other = (Vacation) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getId());
        parcel.writeString(getType());
        parcel.writeString(getVacation());
        parcel.writeLong(getStartDate().getTime());
        parcel.writeDouble(getCount());
    }

    public static final Creator<Vacation> CREATOR = new Creator<Vacation>() {
        public Vacation createFromParcel(Parcel in) {
            return new Vacation(in);
        }

        public Vacation[] newArray(int size) {
            return new Vacation[size];
        }
    };
}
