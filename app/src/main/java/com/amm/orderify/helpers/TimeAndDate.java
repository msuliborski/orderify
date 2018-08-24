package com.amm.orderify.helpers;


import java.util.Calendar;

public class TimeAndDate {

    public static String getCurrentTime(){
        return String.valueOf(Calendar.getInstance().get(Calendar.HOUR)) + ":" + String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)) + ":" + String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
    }

    public static String getCurrentDate(){
        return String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + "-" + String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1) + "-" + String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

}
