package com.project.realproject;

public class SpecialVacation extends VacationType {
    @Override
    public boolean isMealIncluded() {
        return false;
    }

    @Override
    public boolean isTrafficIncluded() {
        return false;
    }
}
