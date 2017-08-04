package com.thnki.classroom.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thnki.classroom.R;


public class AdminDrawerFragment extends Fragment
{
    public AdminDrawerFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    public static AdminDrawerFragment getInstance()
    {
        return new AdminDrawerFragment();
    }
}
