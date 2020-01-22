package com.project.realproject.helpers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Formatter {
    public static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyyMMdd", Locale.ENGLISH);
    public static final SimpleDateFormat dateFormat_dot = new SimpleDateFormat(
            "yyyy.MM.dd", Locale.ENGLISH);
    public static final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    public static final DecimalFormat decimalFormat2 = new DecimalFormat("###,###,###원");

}
