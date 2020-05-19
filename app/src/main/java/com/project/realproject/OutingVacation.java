package com.project.realproject;

public class OutingVacation extends VacationType {
    @Override
    public boolean isMealIncluded() {
        return false;
    }

    @Override
    public boolean isTrafficIncluded() {
        return false;
    }
}
