package com.thnki.classroom.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.thnki.classroom.MainActivity;
import com.thnki.classroom.R;
import com.thnki.classroom.adapters.StaffAdapter;
import com.thnki.classroom.dialogs.AddOrEditStaffDialogFragment;
import com.thnki.classroom.dialogs.ViewStaffAttendanceDialogFragment;
import com.thnki.classroom.listeners.EventsListener;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.utils.ActionBarUtil;
import com.thnki.classroom.utils.NavigationDrawerUtil;
import com.thnki.classroom.utils.Otto;
import com.thnki.classroom.utils.TransitionUtil;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StaffListFragment extends Fragment implements EventsListener, DatePickerDialog.OnDateSetListener
{
    private static final String TAG = "StaffListFragment";

    @Bind(R.id.staffListRecyclerView)
    RecyclerView mStaffListRecyclerView;

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
    private StaffAdapter mAdapter;
    private DatePickerDialog mDatePickerDialog;


    public StaffListFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View parentView = inflater.inflate(R.layout.fragment_staff_list, container, false);
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
            ((MainActivity) activity).setToolBarTitle(getString(R.string.staff));
            ((MainActivity) activity).updateEventsListener(this);
            Otto.post(ActionBarUtil.SHOW_INDEPENDENT_STAFF_MENU);
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

    private void setUpRecyclerView()
    {
        DatabaseReference staffDbRef = mRootRef.child(Staff.STAFF);
        mAdapter = StaffAdapter.getInstance(staffDbRef, (AppCompatActivity) getActivity());
        mStaffListRecyclerView.setAdapter(mAdapter);
        mStaffListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStaffListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
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
        staffDbRef.addValueEventListener(new ValueEventListener()
        {
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
        StaffAttendanceListFragment fragment = StaffAttendanceListFragment.getInstance(mAdapter.mUnSelectedStaff);
        ((MainActivity) getActivity()).showFragment(fragment, true, StaffAttendanceListFragment.TAG);
    }

    @Override
    public boolean onBackPressed()
    {
        if (StaffAdapter.isSelectionEnabled)
        {
            StaffAdapter.isSelectionEnabled = false;
            mAdapter.notifyDataSetChanged();
            Otto.post(ActionBarUtil.SHOW_INDEPENDENT_STAFF_MENU);
            mSaveAttendanceFab.setVisibility(View.GONE);
            mTakeAttendanceFab.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    @Override
    public int getMenuItemId()
    {
        return R.id.admin_staff;
    }

    @Override
    public String getTagName()
    {
        return NavigationDrawerUtil.STAFF_LIST_FRAGMENT;
    }

    @Subscribe
    public void onOptionItemClicked(Integer itemId)
    {
        switch (itemId)
        {
            case R.id.addNewStaff:
                showDialogFragment();
                break;
            case R.id.viewStaffAttendance:
                mDatePickerDialog.show();
                break;
            case R.id.selectAll:
                mAdapter.setSelectAll();
                break;
        }
    }

    public void showDialogFragment()
    {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        AddOrEditStaffDialogFragment fragment = AddOrEditStaffDialogFragment.getInstance(null);
        fragment.show(manager, AddOrEditStaffDialogFragment.TAG);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day)
    {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        ViewStaffAttendanceDialogFragment fragment = ViewStaffAttendanceDialogFragment.getInstance(year, month, day);
        fragment.show(manager, ViewStaffAttendanceDialogFragment.TAG);
    }
}
