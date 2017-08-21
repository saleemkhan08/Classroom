package com.thnki.classroom.utils;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;

import com.squareup.otto.Subscribe;
import com.thnki.classroom.MainActivity;
import com.thnki.classroom.R;

public class ActionBarUtil
{
    public static final String NO_MENU = "showClassesMenu";
    public static final String SHOW_INDEPENDENT_STUDENTS_MENU = "showIndependentStudentsMenu";
    public static final String SHOW_MULTIPLE_STUDENT_MENU = "showMultipleStudentMenu";
    public static final String SHOW_ATTENDANCE_MENU = "showAttendanceMenu";
    Activity mActivity;
    MenuInflater mMenuInflater;
    Menu mMenu;
    public static final String SHOW_MULTIPLE_STAFF_MENU = "showMultipleStaffMenu";
    public static final String SHOW_INDEPENDENT_STAFF_MENU = "showIndependentStaffMenu";

    public ActionBarUtil(MainActivity activity, Menu menu)
    {
        mActivity = activity;
        mMenuInflater = mActivity.getMenuInflater();
        mMenu = menu;
        Otto.register(this);
    }

    @Subscribe
    public void handleActionBar(String menuName)
    {
        switch (menuName)
        {
            case SHOW_MULTIPLE_STAFF_MENU:
                loadNewMenu(R.menu.multiple_staff_menu);
                break;
            case SHOW_INDEPENDENT_STAFF_MENU:
                loadNewMenu(R.menu.independent_staff_menu);
                break;
            case SHOW_MULTIPLE_STUDENT_MENU:
                loadNewMenu(R.menu.multiple_student_menu);
                break;
            case SHOW_INDEPENDENT_STUDENTS_MENU:
                loadNewMenu(R.menu.independent_student_menu);
                break;
            case SHOW_ATTENDANCE_MENU:
                loadNewMenu(R.menu.attendance_menu);
                break;
            case NO_MENU:
                mMenu.clear();
                break;
        }

    }

    private void loadNewMenu(int resId)
    {
        mMenu.clear();
        mMenuInflater.inflate(resId, mMenu);
    }

    public void unRegisterOtto()
    {
        Otto.unregister(this);
    }
}
