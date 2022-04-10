package com.example.utehystudent.helper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateHelper {
    public String getDayString(){
        Locale locale = Locale.getDefault();
        LocalDate date = LocalDate.now();
        DayOfWeek day = date.getDayOfWeek();
        return day.getDisplayName(TextStyle.FULL, locale);
    }
}
