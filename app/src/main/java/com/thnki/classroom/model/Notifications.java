package com.thnki.classroom.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.thnki.classroom.dialogs.MonthYearPickerDialog.MONTH_ARRAY;
import static com.thnki.classroom.model.Notes.AM_PM;

public class Notifications
{
    public static final String NOTIFICATIONS = "notifications";
    private String message;
    private String senderName;
    private String senderPhotoUrl;
    private String senderId;
    private long dateTime;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    public String getSenderPhotoUrl()
    {
        return senderPhotoUrl;
    }

    public void setSenderPhotoUrl(String senderPhotoUrl)
    {
        this.senderPhotoUrl = senderPhotoUrl;
    }

    public String getSenderId()
    {
        return senderId;
    }

    public void setSenderId(String senderId)
    {
        this.senderId = senderId;
    }

    public long getDateTime()
    {
        return dateTime;
    }

    public String dateTimeKey()
    {
        return "" + (-dateTime);
    }

    public void setDateTime(long dateTime)
    {
        this.dateTime = dateTime;
    }

    public String displayDate()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        try
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(dateTimeKey()));
            return MONTH_ARRAY[calendar.get(Calendar.MONTH)] + "-" +
                    calendar.get(Calendar.DAY_OF_MONTH) + " "
                    + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE)
                    + " " + AM_PM[calendar.get(Calendar.AM_PM)];
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
