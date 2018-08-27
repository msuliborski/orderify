package com.amm.orderify.helpers;


import java.util.Calendar;
import java.util.TimeZone;

public class TimeAndDate {

    public static String getCurrentTime(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+02:00"));
        return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar.get(Calendar.MINUTE)) + ":" + String.valueOf(calendar.get(Calendar.SECOND));
    }

    public static String getCurrentDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+02:00"));
        return String.valueOf(calendar.get(Calendar.YEAR)) + "-" + String.valueOf(calendar.get(Calendar.MONTH)+1) + "-" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

}