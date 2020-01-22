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

    public Vacation(){
        super();
    }

    private Vacation(Parcel in){
        super();
        this.id = in.readInt();
        this.type = in.readString();
        this.vacation = in.readString();
        this.startDate = new Date(in.readLong());
        this.count = in.readDouble();
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

    public static final Parcelable.Creator<Vacation> CREATOR = new Parcelable.Creator<Vacation>() {
        public Vacation createFromParcel(Parcel in) {
            return new Vacation(in);
        }

        public Vacation[] newArray(int size) {
            return new Vacation[size];
        }
    };

}
