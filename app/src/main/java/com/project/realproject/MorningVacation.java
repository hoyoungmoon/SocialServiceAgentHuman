package com.project.realproject;

public class MorningVacation extends VacationType {
    @Override
    public boolean isMealIncluded() {
        return true;
    }

    @Override
    public boolean isTrafficIncluded() {
        return false;
    }
}
