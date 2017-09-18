package com.thnki.classroom.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.thnki.classroom.R;
import com.thnki.classroom.model.Leaves;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

public class LeavesDecorator implements DayViewDecorator
{
    private static final String TAG = "LeavesDecorator";
    private HashSet<CalendarDay> dates;
    private Context mContext;

    public LeavesDecorator(DataSnapshot leaves, Context context)
    {
        mContext = context;
        this.dates = new HashSet<>();
        Calendar calendar = Calendar.getInstance();
        for (DataSnapshot snapshot : leaves.getChildren())
        {
            try
            {
                String leaveDate = snapshot.getKey();
                SimpleDateFormat format = new SimpleDateFormat(Leaves.DB_DATE_FORMAT, Locale.ENGLISH);
                calendar.setTime(format.parse(leaveDate));
                CalendarDay day = CalendarDay.from(calendar);
                this.dates.add(day);
            }
            catch (ParseException e)
            {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day)
    {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view)
    {
        if (Build.VERSION.SDK_INT > 20)
        {
            view.setBackgroundDrawable(mContext.getDrawable(R.drawable.leave_bg_drawable));
        }
        else
        {
            view.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.leave_bg_drawable));
        }
    }
}