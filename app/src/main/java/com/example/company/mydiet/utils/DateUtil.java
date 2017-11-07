package com.example.company.mydiet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mohamed Sayed on 11/3/2017.
 */

public class DateUtil {
    private static DateUtil dateUtil;
    String pattern = "MM/dd/yyyy";
    SimpleDateFormat format = new SimpleDateFormat(pattern);

    public static DateUtil getInstance() {
        if (dateUtil == null) {
            dateUtil = new DateUtil();
        }
        return dateUtil;
    }

    private DateUtil() {
    }

    public String getDate() {
        return format.format(new Date());
    }

    public Date getDate(String date) {
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public Date getSystemDate() {
        try {
            return format.parse(new Date().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public int getDays(String date) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(getDate(date));
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date());
        long milliSecond1 = calendar1.getTimeInMillis();
        long milliSecond2 = calendar2.getTimeInMillis();
        long diff = milliSecond2 - milliSecond1;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return (int) diffDays;
    }
}
