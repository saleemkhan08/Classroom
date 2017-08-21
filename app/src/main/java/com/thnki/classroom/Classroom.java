package com.thnki.classroom;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class Classroom extends MultiDexApplication
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty())
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}