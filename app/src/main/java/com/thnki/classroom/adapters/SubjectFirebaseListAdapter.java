package com.thnki.classroom.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.thnki.classroom.model.Subjects;

public class SubjectFirebaseListAdapter extends FirebaseListAdapter<Subjects>
{
    public SubjectFirebaseListAdapter(Activity activity, Class<Subjects> modelClass, int modelLayout, DatabaseReference ref)
    {
        super(activity, modelClass, modelLayout, ref);
    }

    @Override
    protected void populateView(View v, Subjects model, int position)
    {
        ((TextView) v).setText(model.getSubjectName());
        v.setTag(model);
    }
}
