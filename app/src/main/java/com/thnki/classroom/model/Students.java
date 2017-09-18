package com.thnki.classroom.model;

public class Students extends User
{
    public Students()
    {

    }

    @Override
    public String getUserType()
    {
        return STUDENTS;
    }
}
