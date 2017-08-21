package com.thnki.classroom.model;

import com.thnki.classroom.utils.Otto;

public class ToastMsg
{
    private int mMsg;

    private ToastMsg(int msg)
    {
        mMsg = msg;
    }

    public int getMsg()
    {
        return mMsg;
    }

    public static void show(int msg)
    {
        ToastMsg toast = new ToastMsg(msg);
        Otto.post(toast);
    }
}
