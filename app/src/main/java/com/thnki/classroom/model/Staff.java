package com.thnki.classroom.model;

import android.text.TextUtils;

public class Staff
{
    public static final String STAFF = "staff";
    private String userId;
    private String fullName;
    private String photoUrl;
    private String qualification;
    private String designation;
    private boolean isAdmin;
    private String password;

    public Staff()
    {

    }

    public String getDesignation()
    {
        return designation;
    }

    public void setDesignation(String designation)
    {
        this.designation = designation;
    }

    public boolean getIsAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(boolean admin)
    {
        isAdmin = admin;
    }

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

    public void setQualification(String qualification)
    {
        this.qualification = qualification;
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

    public String getQualification()
    {
        if (qualification == null || TextUtils.isEmpty(qualification))
        {
            return "Qualification isn't updated..";
        }
        return qualification;
    }

    public String getPhotoUrl()
    {
        if (photoUrl == null)
        {
            return "";
        }
        return photoUrl;
    }

    public static final String USER_ID = "userId";

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
