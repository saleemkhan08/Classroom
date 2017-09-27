package com.thnki.classroom.model;

import android.text.TextUtils;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.thnki.classroom.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Leaves
{
    public static final String LEAVES = "leaves";
    public static final java.lang.String DB_DATE_FORMAT = "yyyyMMdd";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String TAG = "Leaves";
    private String fromDate;
    private String toDate;
    private String reason;
    private String approver;

    public String getFromDate()
    {
        return fromDate.trim();
    }

    public void setFromDate(String fromDate)
    {
        this.fromDate = fromDate;
    }

    public String getToDate()
    {
        return toDate.trim();
    }

    public void setToDate(String toDate)
    {
        this.toDate = toDate;
    }

    public String getReason()
    {
        return reason.trim();
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getApprover()
    {
        return approver.trim();
    }

    public void setApprover(String approver)
    {
        this.approver = approver;
    }

    public boolean validate()
    {
        if (TextUtils.isEmpty(reason))
        {
            ToastMsg.show(R.string.pleaseEnterAValidReason);
            return false;
        }
        else if (TextUtils.isEmpty(fromDate) || !validateDate(fromDate))
        {
            ToastMsg.show(R.string.pleaseEnterAValidFromDate);
            return false;
        }
        else if ((TextUtils.isEmpty(toDate) || !validateDate(toDate)))
        {
            ToastMsg.show(R.string.pleaseEnterAToValidDate);
            return false;
        }
        else
        {
            long numOfDays = numDaysBetweenDates();
            if (numOfDays < 0)
            {
                ToastMsg.show(R.string.pleaseEnterAToValidDate);
                return false;
            }
        }
        return true;
    }

    public long numDaysBetweenDates()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try
        {
            startDate = dateFormat.parse(fromDate);
            endDate = dateFormat.parse(toDate);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return numberOfDays;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit)
    {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    private boolean validateDate(String testDate)
    {
        if (testDate.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})"))
        {
            String[] dates = testDate.split("-");
            int day = Integer.parseInt(dates[0]);
            int month = Integer.parseInt(dates[1]);
            int year = Integer.parseInt(dates[2]);

            if (day > 31)
            {
                return false;
            }
            if (month > 12)
            {
                return false;
            }
            if (year < 1900 || year > 2100)
            {
                return false;
            }
            return true;
        }
        return false;
    }

    public static String getDbKeyDate(CalendarDay calendar)
    {
        return "" + calendar.getYear() + get2DigitNum(calendar.getMonth() + 1) + get2DigitNum(calendar.getDay());
    }

    public static String getDbKeyDate(Calendar calendar)
    {
        return "" + calendar.get(Calendar.YEAR) + get2DigitNum(calendar.get(Calendar.MONTH) + 1)
                + get2DigitNum(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getDbRetrieveKeyDate(CalendarDay calendar)
    {
        return "" + calendar.getYear() + get2DigitNum(calendar.getMonth()) + get2DigitNum(calendar.getDay());
    }

    public static String getDbRetrieveKeyDate(Calendar calendar)
    {
        return "" + calendar.get(Calendar.YEAR) + get2DigitNum(calendar.get(Calendar.MONTH))
                + get2DigitNum(calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static String get2DigitNum(int num)
    {
        return (num < 10) ? "0" + num : "" + num;
    }

    public static String getDisplayDate(Calendar calendar)
    {
        return get2DigitNum(calendar.get(Calendar.DAY_OF_MONTH)) + "/"
                + get2DigitNum(calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
    }

    public static String getDbKeyDate(int year, int month, int day)
    {
        return get2DigitNum(day) + "-" + get2DigitNum(month) + "-" + year;
    }

    public static String getDisplayDate(int year, int month, int day)
    {
        return get2DigitNum(day) + "/" + get2DigitNum(month) + "/" + year;
    }

    public static String getDbKeyStartDate(Calendar calendar)
    {
        return "" + calendar.get(Calendar.YEAR) + get2DigitNum(calendar.get(Calendar.MONTH))
                + "20";
    }

    public static String getDbKeyEndDate(Calendar calendar)
    {
        return "" + calendar.get(Calendar.YEAR) + get2DigitNum(calendar.get(Calendar.MONTH) + 2)
                + "10";
    }

    public static String getDbKeyStartDate(CalendarDay calendar)
    {
        return "" + calendar.getYear() + get2DigitNum(calendar.getMonth())
                + "20";
    }

    public static String getDbKeyEndDate(CalendarDay calendar)
    {
        return "" + calendar.getYear() + get2DigitNum(calendar.getMonth() + 2)
                + "10";
    }

    public static Calendar getCalendar(String date)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        try
        {
            calendar.setTime(format.parse(date));
        }
        catch (ParseException e)
        {
            Log.d(TAG, e.getMessage());
        }

        return calendar;
    }

    public static String getDbKeyDateTime(Calendar calendar)
    {
        return "" + calendar.get(Calendar.YEAR) + get2DigitNum(calendar.get(Calendar.MONTH) + 1)
                + get2DigitNum(calendar.get(Calendar.DAY_OF_MONTH)) + get2DigitNum(calendar.get(Calendar.MINUTE))
                + get2DigitNum(calendar.get(Calendar.SECOND));
    }
}
