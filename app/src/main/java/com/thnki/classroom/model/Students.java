package com.thnki.classroom.model;

public class Students extends User
{
    private String className;

    public Students()
    {

    }

    @Override
    public String userType()
    {
        return STUDENTS;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String classId()
    {
        return getUserId().substring(0, 3);
    }
}
