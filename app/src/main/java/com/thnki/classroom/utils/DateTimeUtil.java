package com.thnki.classroom.utils;

public class DateTimeUtil
{
    public static String getKey()
    {
        return "" + ((-1) * System.currentTimeMillis());
    }
}
