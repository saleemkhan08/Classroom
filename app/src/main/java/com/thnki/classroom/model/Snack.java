package com.thnki.classroom.model;

import com.thnki.classroom.utils.Otto;

public class Snack
{
    private int mMsg;

    private Snack(int msg)
    {
        mMsg = msg;
    }

    public int getMsg()
    {
        return mMsg;
    }

    public static void show(int msg)
    {
        Snack snack = new Snack(msg);
        Otto.post(snack);
    }
}
