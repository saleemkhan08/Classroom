package com.thnki.classroom.model;

import java.util.ArrayList;

public class ClassAttendance
{
    public static final String ATTENDANCE = "attendance";
    public static final String ABSENTEES = "listOfAbsentees";
    private String staffId;
    private String staffName;
    private ArrayList<Students> listOfAbsentees;
    private String takenDate;

    public String getStaffId()
    {
        return staffId;
    }

    public void setStaffId(String staffId)
    {
        this.staffId = staffId;
    }

    public ArrayList<Students> getListOfAbsentees()
    {
        return listOfAbsentees;
    }

    public void setListOfAbsentees(ArrayList<Students>  listOfAbsentees)
    {
        this.listOfAbsentees = listOfAbsentees;
    }

    public String getTakenDate()
    {
        return takenDate;
    }

    public void setTakenDate(String takenDate)
    {
        this.takenDate = takenDate;
    }

}
