package com.thnki.classroom.model;

import android.text.TextUtils;

public abstract class User
{
    public static final String FULL_NAME = "fullName";
    public static final String PHOTO_URL = "photoUrl";
    public static final String PASSWORD = "password";
    private String userId;
    private String fullName;
    private String photoUrl;
    private String password;
    private String address;
    private String email;
    private String phone;

    public String getDob()
    {
        if (dob == null)
        {
            dob = "Not Set..";
        }
        return dob;
    }

    public void setDob(String dob)
    {
        this.dob = dob;
    }

    public String getPhone()
    {
        if (phone == null)
        {
            phone = "Not Set..";
        }
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getEmail()
    {
        if (email == null)
        {
            email = "Not Set..";
        }
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getAddress()
    {
        if (address == null)
        {
            address = "Not Set..";
        }
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    private String dob;

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

    public abstract String userType();
}
