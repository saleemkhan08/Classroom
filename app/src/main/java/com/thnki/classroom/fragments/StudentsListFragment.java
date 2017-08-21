package com.thnki.classroom.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.thnki.classroom.MainActivity;
import com.thnki.classroom.R;
import com.thnki.classroom.adapters.StudentsAdapter;
import com.thnki.classroom.dialogs.AddStudentsDialogFragment;
import com.thnki.classroom.dialogs.ViewStudentAttendanceDialogFragment;
import com.thnki.classroom.listeners.EventsListener;
import com.thnki.classroom.model.Classes;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.Students;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.ActionBarUtil;
import com.thnki.classroom.utils.NavigationDrawerUtil;
import com.thnki.classroom.utils.Otto;
import com.thnki.classroom.utils.TransitionUtil;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StudentsListFragment extends Fragment implements EventsListener, DatePickerDialog.OnDateSetListener
{
    private static final String TAG = "StudentsListFragment";

    @Bind(R.id.studentsListRecyclerView)
    RecyclerView mStudentsListRecyclerView;

    @Bind(R.id.recyclerProgress)
    View mProgress;

    @Bind(R.id.errorMsg)
    View mErrorMsg;

    @Bind(R.id.fabContainer)
    ViewGroup mFabContainer;

    @Bind(R.id.attendanceFab)
    View mTakeAttendanceFab;

    @Bind(R.id.savefab)
    View mSaveAttendanceFab;

    private DatabaseReference mRootRef;
    private Classes mCurrentClass;
    private StudentsAdapter mAdapter;
    private DatePickerDialog mDatePickerDialog;
    private DatabaseReference mStudentsDbRef;
    private DatabaseReference mClassesDbRef;

    public StudentsListFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View parentView = inflater.inflate(R.layout.fragment_students_list, container, false);
        ButterKnife.bind(this, parentView);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        return parentView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Otto.register(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Activity activity = getActivity();
        if (activity instanceof MainActivity)
        {
            ((MainActivity) activity).setToolBarTitle(mCurrentClass.getName());
            ((MainActivity) activity).updateEventsListener(this);
            Otto.post(ActionBarUtil.SHOW_INDEPENDENT_STUDENTS_MENU);
        }
        setUpRecyclerView();
        mDatePickerDialog = new DatePickerDialog(getActivity(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onStop()
    {
        super.onStop();
        Otto.unregister(this);
    }

    public static StudentsListFragment getInstance(Classes classCode)
    {
        StudentsListFragment fragment = new StudentsListFragment();
        fragment.mCurrentClass = classCode;
        return fragment;
    }

    private void setUpRecyclerView()
    {
        mStudentsDbRef = mRootRef.child(Students.STUDENTS).child(mCurrentClass.getCode());
        mClassesDbRef = mRootRef.child(Classes.CLASSES).child(mCurrentClass.getCode());
        mAdapter = StudentsAdapter.getInstance(mStudentsDbRef, getActivity());
        mStudentsListRecyclerView.setAdapter(mAdapter);
        mStudentsListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStudentsListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 || dy < 0 && mFabContainer.isShown())
                {
                    TransitionUtil.slideTransition(mFabContainer);
                    mFabContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            TransitionUtil.slideTransition(mFabContainer);
                            mFabContainer.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mStudentsDbRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d(TAG, "Data : " + dataSnapshot);
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
                Log.d(TAG, "databaseError : " + databaseError);
            }
        });
    }

    @OnClick(R.id.attendanceFab)
    public void takeAttendance(View view)
    {
        mAdapter.enableAttendance();
        mSaveAttendanceFab.setVisibility(View.VISIBLE);
        mTakeAttendanceFab.setVisibility(View.GONE);
    }

    @OnClick(R.id.savefab)
    public void saveAttendance(View view)
    {
        onBackPressed();
        StudentAttendanceListFragment fragment = StudentAttendanceListFragment.getInstance(mAdapter.mUnSelectedStudents, mCurrentClass.getCode());
        ((MainActivity) getActivity()).showFragment(fragment, true, StudentAttendanceListFragment.TAG);
    }

    @Override
    public boolean onBackPressed()
    {
        if (StudentsAdapter.isSelectionEnabled)
        {
            StudentsAdapter.isSelectionEnabled = false;
            mAdapter.notifyDataSetChanged();
            Otto.post(ActionBarUtil.SHOW_INDEPENDENT_STUDENTS_MENU);
            mSaveAttendanceFab.setVisibility(View.GONE);
            mTakeAttendanceFab.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    @Override
    public int getMenuItemId()
    {
        return R.id.admin_students;
    }

    @Override
    public String getTagName()
    {
        return NavigationDrawerUtil.STUDENTS_LIST_FRAGMENT;
    }

    @Subscribe
    public void onOptionItemClicked(Integer itemId)
    {
        switch (itemId)
        {
            case R.id.addNewStudent:
                showDialogFragment();
                break;
            case R.id.selectAll:
                mAdapter.setSelectAll();
                break;
            case R.id.viewStudentAttendance:
                mDatePickerDialog.show();
                break;
            case R.id.deleteStudents:
                Progress.show(R.string.deleting);
                for (String code : mAdapter.mSelectedStudents)
                {
                    mStudentsDbRef.getRef().child(code).removeValue();
                }
                mStudentsDbRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        mCurrentClass.setStudentCount((int) dataSnapshot.getChildrenCount());
                        mClassesDbRef.setValue(mCurrentClass)
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        Progress.hide();
                                        if (task.isSuccessful())
                                        {
                                            onBackPressed();
                                            ToastMsg.show(R.string.deleted);
                                        }
                                        else
                                        {
                                            ToastMsg.show(R.string.couldntUpdateStudentCount);
                                        }
                                    }
                                });
                        mStudentsDbRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Progress.hide();
                        ToastMsg.show(R.string.please_try_again);
                    }
                });
                break;
        }
    }

    public void showDialogFragment()
    {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        AddStudentsDialogFragment fragment = AddStudentsDialogFragment.getInstance(mCurrentClass);
        fragment.show(manager, AddStudentsDialogFragment.TAG);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day)
    {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        ViewStudentAttendanceDialogFragment fragment = ViewStudentAttendanceDialogFragment.getInstance(mCurrentClass.getCode(), year, month, day);
        fragment.show(manager, ViewStudentAttendanceDialogFragment.TAG);
    }
}
