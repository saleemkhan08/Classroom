package com.thnki.classroom.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thnki.classroom.R;
import com.thnki.classroom.adapters.AttendanceFbAdapter;
import com.thnki.classroom.model.ClassAttendance;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.Otto;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewStudentAttendanceDialogFragment extends DialogFragment implements ValueEventListener
{
    public static final String TAG = "ViewStudentAttendance";
    int mYear;
    int mMonth;
    int mDay;
    private DatabaseReference mAttendanceRef;

    @Bind(R.id.attendanceListRecyclerView)
    RecyclerView mAttendanceListRecyclerView;

    @Bind(R.id.errorMsg)
    View mErrorMsg;

    @Bind(R.id.recyclerProgress)
    View mProgress;

    @Bind(R.id.dialogSubTitle)
    TextView mDateTextView;
    private String mClassCode;


    public static ViewStudentAttendanceDialogFragment getInstance(String classCode, int year, int month, int day)
    {
        ViewStudentAttendanceDialogFragment fragment = new ViewStudentAttendanceDialogFragment();
        fragment.mDay = day;
        fragment.mMonth = month;
        fragment.mYear = year;
        fragment.mClassCode = classCode;
        return fragment;
    }

    public ViewStudentAttendanceDialogFragment()
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

        View parentView = inflater.inflate(R.layout.fragment_view_attendance, container, false);
        ButterKnife.bind(this, parentView);
        Otto.register(this);
        mAttendanceRef = FirebaseDatabase.getInstance().getReference().child(ClassAttendance.ATTENDANCE)
                .child(mClassCode).child("" + mYear + mMonth + mDay).child(ClassAttendance.ABSENTEES);
        mAttendanceRef.addValueEventListener(this);
        return parentView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mDateTextView.setText(getFormattedDate());
        mAttendanceListRecyclerView.setAdapter(AttendanceFbAdapter.getInstance(mAttendanceRef));
        mAttendanceListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    private String getFormattedDate()
    {
        return mDay + "/" + (mMonth + 1) + "/" + mYear;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        mProgress.setVisibility(View.GONE);
        if (dataSnapshot.getChildrenCount() <= 0)
        {
            mErrorMsg.setVisibility(View.VISIBLE);
        }
        else
        {
            mErrorMsg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
        mProgress.setVisibility(View.GONE);
        ToastMsg.show(R.string.couldntLoadTheList);
    }
}