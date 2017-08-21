package com.thnki.classroom.model;

import com.thnki.classroom.utils.Otto;

public class Progress
{
    private int mMsg;
    private boolean toBeShown;

    private Progress(int msg)
    {
        mMsg = msg;
    }

    private Progress()
    {

    }

    public int getMsg()
    {
        return mMsg;
    }

    public static void show(int msg)
    {
        Progress progress = new Progress(msg);
        progress.toBeShown = true;
        Otto.post(progress);
    }

    public static void hide()
    {
        Progress progress = new Progress();
        progress.toBeShown = false;
        Otto.post(progress);
    }

    public boolean toBeShown()
    {
        return toBeShown;
    }
}
