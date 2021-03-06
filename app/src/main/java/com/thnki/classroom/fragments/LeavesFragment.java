package com.thnki.classroom.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.squareup.otto.Subscribe;
import com.thnki.classroom.MainActivity;
import com.thnki.classroom.R;
import com.thnki.classroom.dialogs.AddLeavesDialogFragment;
import com.thnki.classroom.dialogs.LeavesDetailDialogFragment;
import com.thnki.classroom.dialogs.MonthYearPickerDialog;
import com.thnki.classroom.dialogs.RequestedLeavesDialogFragment;
import com.thnki.classroom.dialogs.SelectStaffDialogFragment;
import com.thnki.classroom.dialogs.SelectStudentDialogFragment;
import com.thnki.classroom.listeners.EventsListener;
import com.thnki.classroom.model.Leaves;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.model.Students;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.model.User;
import com.thnki.classroom.utils.ActionBarUtil;
import com.thnki.classroom.utils.ImageUtil;
import com.thnki.classroom.utils.LeavesDecorator;
import com.thnki.classroom.utils.NavigationDrawerUtil;
import com.thnki.classroom.utils.Otto;
import com.thnki.classroom.utils.TransitionUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.thnki.classroom.R.string.leaves;
import static com.thnki.classroom.model.Leaves.MY_LEAVES;
import static com.thnki.classroom.model.Leaves.getCalendar;

public class LeavesFragment extends Fragment implements EventsListener, ValueEventListener,
        OnDateSelectedListener, DatePickerDialog.OnDateSetListener, OnMonthChangedListener
{
    public static final String TAG = "LeavesFragment";

    @Bind(R.id.leavesList)
    MaterialCalendarView mLeavesCalender;

    @Bind(R.id.userDetails)
    View userDetails;

    @Bind(R.id.profileName)
    TextView mProfileName;

    @Bind(R.id.profileId)
    TextView mDesignation;

    @Bind(R.id.profileImg)
    ImageView mProfileImg;

    @Bind(R.id.leavesProgress)
    View mProgress;

    ViewGroup calenderPager;

    private DatabaseReference mLeavesRootRef;
    private String mDateStartText;
    private String mDateEndText;

    private HashMap<String, Leaves> mLeavesList;
    private String mCurrentUserId;
    private Leaves mLeave;
    private String leaveId;
    private Query currentLeavesQuery;

    private Calendar currentCalendar;

    public LeavesFragment()
    {
    }

    public static LeavesFragment getInstance(String leaveId, String userId, String leaveRefType)
    {
        LeavesFragment fragment = new LeavesFragment();
        fragment.leaveId = leaveId;
        fragment.mCurrentUserId = userId;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View parentView = commonFlow(inflater, container);

        mLeavesCalender.setOnMonthChangedListener(this);
        LinearLayout titleContainer = (LinearLayout) mLeavesCalender.getChildAt(0);
        titleContainer.setBackgroundResource(R.color.grey);
        TextView title = (TextView) titleContainer.getChildAt(1);
        title.setBackgroundResource(R.drawable.bg_drawable);
        title.setClickable(true);
        title.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectMonth();
            }
        });

        calenderPager = (ViewGroup) mLeavesCalender.getChildAt(1);

        View prevButton = titleContainer.getChildAt(0);
        prevButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                currentCalendar.add(Calendar.MONTH, -1);
                animateCalender();

            }
        });
        View nextButton = titleContainer.getChildAt(2);
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                currentCalendar.add(Calendar.MONTH, 1);
                animateCalender();
            }
        });
        Log.d("LeavesCrash", "mCurrentUserId : " + mCurrentUserId);
        if (mCurrentUserId == null)
        {
            normalFlow();
        }
        else
        {
            notificationFlow();
        }
        showCurrentUserDetails();
        return parentView;
    }

    private void animateCalender()
    {
        calenderPager.setVisibility(View.INVISIBLE);
        TransitionUtil.defaultTransition(calenderPager);
        mLeavesCalender.setCurrentDate(currentCalendar);
        calenderPager.setVisibility(View.VISIBLE);
        TransitionUtil.defaultTransition(calenderPager);
    }

    private void showCurrentUserDetails()
    {
        User.getRef(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild(Staff.IS_ADMIN))
                {
                    showStaffDetails(dataSnapshot.getValue(Staff.class));
                }
                else
                {
                    showStudentDetails(dataSnapshot.getValue(Students.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private View commonFlow(LayoutInflater inflater, ViewGroup container)
    {
        View parentView = inflater.inflate(R.layout.fragment_leaves, container, false);
        ButterKnife.bind(this, parentView);

        mLeavesRootRef = FirebaseDatabase.getInstance().getReference().child(Leaves.LEAVES);

        mLeavesCalender.setOnDateChangedListener(this);
        mLeavesCalender.setPagingEnabled(false);
        mLeavesCalender.setAllowClickDaysOutsideCurrentMonth(false);

        Activity activity = getActivity();
        if (activity instanceof MainActivity)
        {
            ((MainActivity) activity).setToolBarTitle(getString(leaves));
            ((MainActivity) activity).updateEventsListener(this);
            if (NavigationDrawerUtil.isStudent)
            {
                Otto.post(ActionBarUtil.NO_MENU);
                userDetails.setVisibility(View.GONE);
            }
            else
            {
                Otto.post(ActionBarUtil.SHOW_ADMIN_LEAVES_MENU);
            }
        }

        return parentView;
    }

    private void normalFlow()
    {
        setCurrentMonth(Calendar.getInstance());
        mCurrentUserId = NavigationDrawerUtil.mCurrentUser.getUserId();
    }

    private void notificationFlow()
    {
        Log.d("LeavesCrash", "leaveId : " + leaveId);
        if (leaveId != null)
        {
            Progress.show(R.string.loading);
            mLeavesRootRef.child(mCurrentUserId).child(MY_LEAVES).child(leaveId)
                    .addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            mLeave = dataSnapshot.getValue(Leaves.class);
                            Progress.hide();
                            Log.d("LeavesCrash", "mLeave : " + mLeave);
                            if (mLeave != null)
                            {
                                Calendar calendar = Leaves.getCalendar(mLeave.getFromDate());
                                setCurrentMonth(calendar);
                                onDateSet(null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 0);
                                if (mLeave.getStatus() == Leaves.STATUS_APPLIED)
                                {
                                    RequestedLeavesDialogFragment.getInstance(mLeave)
                                            .show(getFragmentManager(), RequestedLeavesDialogFragment.TAG);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
        }
    }

    private void showCurrentUserLeaves()
    {
        if (currentLeavesQuery != null)
        {
            currentLeavesQuery.removeEventListener(this);
        }
        Log.d("LeavesCrash", "mDateStartText : " + mDateStartText);
        Log.d("LeavesCrash", "mDateEndText : " + mDateEndText);
        Log.d("LeavesCrash", "mCurrentUserId : " + mCurrentUserId);
        currentLeavesQuery = mLeavesRootRef.child(mCurrentUserId).child(MY_LEAVES).orderByKey()
                .startAt(mDateStartText).endAt(mDateEndText);

        currentLeavesQuery.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        ToastMsg.show(dataSnapshot.getChildrenCount() + "");
        mLeavesCalender.removeDecorators();
        Calendar calendar = Calendar.getInstance();

        if (mLeavesList == null)
        {
            mLeavesList = new HashMap<>();
        }
        else
        {
            mLeavesList.clear();
        }
        for (DataSnapshot snapshot : dataSnapshot.getChildren())
        {
            try
            {
                String leaveDate = snapshot.getKey();
                Leaves leave = snapshot.getValue(Leaves.class);
                SimpleDateFormat format = new SimpleDateFormat(Leaves.DB_DATE_FORMAT, Locale.ENGLISH);
                calendar.setTime(format.parse(leaveDate));
                mLeavesList.put(CalendarDay.from(calendar).toString(), leave);
            }
            catch (ParseException e)
            {
                Log.d(TAG, e.getMessage());
            }
        }
        mLeavesCalender.addDecorators(LeavesDecorator.getDecorators(mLeavesList, getActivity()));
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
    }

    public void selectMonth()
    {
        MonthYearPickerDialog pd = MonthYearPickerDialog.getInstance(currentCalendar);
        pd.setListener(this);
        pd.show(getFragmentManager(), "MonthYearPickerDialog");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date)
    {
        Log.d("LeavesCrash", "year : " + year + ", month : " + month);
        currentCalendar.set(year, month, 1);
        animateCalender();
        mDateStartText = Leaves.getDbKeyStartDate(currentCalendar);
        mDateEndText = Leaves.getDbKeyEndDate(currentCalendar);
        showCurrentUserLeaves();
    }

    @Subscribe
    public void showStaffLeaves(Staff staff)
    {
        showStaffDetails(staff);
    }

    @Subscribe
    public void showStudentLeaves(Students students)
    {
        showStudentDetails(students);
    }

    @Subscribe
    public void showCurrentUserLeaves(Leaves leave)
    {
        setStartEndDateText(getCalendar(leave.getFromDate()));
        showCurrentUserLeaves();
    }

    private void setCurrentMonth(Calendar calendar)
    {
        Log.d("LeavesCrash", "mLeave : " + mLeave);
        setStartEndDateText(calendar);
        currentCalendar = calendar;
    }

    private void setStartEndDateText(Calendar calendar)
    {
        mDateStartText = Leaves.getDbKeyStartDate(calendar);
        mDateEndText = Leaves.getDbKeyEndDate(calendar);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Otto.register(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Otto.unregister(this);
    }

    private void showStudentDetails(Students student)
    {
        mDesignation.setText(student.getClassName());
        showUserDetails(student);
    }

    private void showStaffDetails(Staff staff)
    {
        mDesignation.setText(staff.getDesignation());
        showUserDetails(staff);
    }

    private void showUserDetails(User user)
    {
        ImageUtil.loadCircularImg(getActivity(), user.getPhotoUrl(), mProfileImg);
        mProfileName.setText(user.getFullName());
        mCurrentUserId = user.getUserId();
        showCurrentUserLeaves();
    }

    @OnClick(R.id.addLeaves)
    public void addLeaves(View view)
    {
        AddLeavesDialogFragment.getInstance().show(getActivity()
                .getSupportFragmentManager(), AddLeavesDialogFragment.TAG);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected)
    {
        String key = Leaves.getFirstDateDbKey(mLeavesList, date);
        if (key != null)
        {
            LeavesDetailDialogFragment.getInstance(mCurrentUserId, key)
                    .show(getFragmentManager(), LeavesDetailDialogFragment.TAG);
        }
    }

    @Override
    public String getTagName()
    {
        return NavigationDrawerUtil.LEAVES_LIST_FRAGMENT;
    }

    @Subscribe
    public void onOptionItemClicked(Integer itemId)
    {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        switch (itemId)
        {
            case R.id.myLeaves:
                showCurrentUserLeaves();
                break;
            case R.id.requestedLeaves:
                RequestedLeavesDialogFragment.getInstance()
                        .show(manager, RequestedLeavesDialogFragment.TAG);
                break;
            case R.id.staffLeaves:
                SelectStaffDialogFragment.getInstance()
                        .show(manager, SelectStaffDialogFragment.TAG);
                break;
            case R.id.studentLeaves:
                SelectStudentDialogFragment.getInstance()
                        .show(manager, SelectStudentDialogFragment.TAG);
                break;
        }
    }

    @Override
    public boolean onBackPressed()
    {
        return true;
    }

    @Override
    public int getMenuItemId()
    {
        return R.id.launch_leaves_fragment;
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date)
    {
        onDateSet(null, date.getYear(), date.getMonth(), date.getDay());
    }
}