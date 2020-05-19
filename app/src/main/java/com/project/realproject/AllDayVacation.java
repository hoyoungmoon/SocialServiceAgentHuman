package com.project.realproject;

public class AllDayVacation extends VacationType {
    @Override
    public boolean isMealIncluded() {
        return true;
    }

    @Override
    public boolean isTrafficIncluded() {
        return true;
    }
}
