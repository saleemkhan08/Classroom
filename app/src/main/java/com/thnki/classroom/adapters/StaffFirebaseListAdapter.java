package com.thnki.classroom.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.thnki.classroom.model.Staff;

public class StaffFirebaseListAdapter extends FirebaseListAdapter<Staff>
{
    public StaffFirebaseListAdapter(Activity activity, Class<Staff> modelClass, int modelLayout, DatabaseReference ref)
    {
        super(activity, modelClass, modelLayout, ref);
    }

    @Override
    protected void populateView(View v, Staff model, int position)
    {
        ((TextView) v).setText(model.getFullName());
        v.setTag(model) ;
    }
}
