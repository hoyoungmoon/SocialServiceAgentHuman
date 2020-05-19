package com.project.realproject;

public abstract class VacationType {
    // 교통비, 식비 포함 or 제외 되는 조건
    public abstract boolean isMealIncluded();
    public abstract boolean isTrafficIncluded();
}
