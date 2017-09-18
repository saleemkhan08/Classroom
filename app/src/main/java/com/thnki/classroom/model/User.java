package com.thnki.classroom.model;

import android.text.TextUtils;

public abstract class User
{
    private String userId;
    private String fullName;
    private String photoUrl;
    private String password;

    public static final String STUDENTS = "students";
    public static final String STAFF = "staff";
    public static final String ADMIN = "admin";
    public static final String USER_ID = "userId";

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public void setPhotoUrl(String photoUrl)
    {
        this.photoUrl = photoUrl;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getFullName()
    {
        if (fullName == null || TextUtils.isEmpty(fullName))
        {
            return "Name isn't updated..";
        }
        return fullName;
    }

    public String getPhotoUrl()
    {
        if (photoUrl == null)
        {
            return "";
        }
        return photoUrl;
    }
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public abstract String getUserType();
}
