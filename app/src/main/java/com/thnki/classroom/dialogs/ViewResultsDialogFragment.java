package com.thnki.classroom.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.thnki.classroom.R;
import com.thnki.classroom.utils.Otto;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewResultsDialogFragment extends DialogFragment
{
    public static final String TAG = "AddClassDialogFragment";

    public ViewResultsDialogFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Window window = getDialog().getWindow();
        if (window != null)
        {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }

        View parentView = inflater.inflate(R.layout.fragment_add_staffs, container, false);
        ButterKnife.bind(this, parentView);
        Otto.register(this);
        return parentView;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        Otto.unregister(this);
    }

    @OnClick(R.id.closeDialog)
    public void close()
    {
        dismiss();
    }

    @OnClick(R.id.saveClassButton)
    public void save()
    {

    }
}