package com.thnki.classroom.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thnki.classroom.R;


public class StudentListFragment extends Fragment
{
    public StudentListFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View parentView = inflater.inflate(R.layout.fragment_student_list, container, false);
        FloatingActionButton fab = (FloatingActionButton) parentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getActivity(), "Add", Toast.LENGTH_SHORT).show();
            }
        });
        return parentView;
    }

    public static StudentListFragment getInstance()
    {
        return new StudentListFragment();
    }
}
