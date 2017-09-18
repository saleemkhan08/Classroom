package com.thnki.classroom.model;

import com.thnki.classroom.utils.Otto;

public class ToastMsg
{
    private int mMsg = -9999;
    private String mTxtMsg;

    private ToastMsg(int msg)
    {
        mMsg = msg;
    }

    private ToastMsg(String msg)
    {
        mTxtMsg = msg;
    }

    public int getMsg()
    {
        return mMsg;
    }

    public String getTxtMsg()
    {
        return mTxtMsg;
    }

    public static void show(int msg)
    {
        ToastMsg toast = new ToastMsg(msg);
        Otto.post(toast);
    }

    public static void show(String msg)
    {
        ToastMsg toast = new ToastMsg(msg);
        Otto.post(toast);
    }
}
